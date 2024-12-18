package com.example.finalexam;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;

public class EnrollmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView subjectsRecyclerView;
    private TextView currentCreditsTextView;
    private Button confirmEnrollmentButton;

    private List<Subject> subjectsList = new ArrayList<>();
    private List<Subject> selectedSubjects = new ArrayList<>();
    private List<String> alreadyEnrolledSubjectIds = new ArrayList<>();
    private int currentTotalCredits = 0;
    private final int CREDIT_LIMIT = 24;

    private SubjectAdapter subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        db = FirebaseFirestore.getInstance();

        subjectsRecyclerView = findViewById(R.id.subjectsRecyclerView);
        currentCreditsTextView = findViewById(R.id.currentCreditsTextView);
        confirmEnrollmentButton = findViewById(R.id.confirmEnrollmentButton);

        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectAdapter = new SubjectAdapter(subjectsList, this::onSubjectSelected);
        subjectsRecyclerView.setAdapter(subjectAdapter);

        loadUserEnrolledSubjects();
        confirmEnrollmentButton.setOnClickListener(v -> confirmEnrollment());
    }

    private void loadUserEnrolledSubjects() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("students").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        alreadyEnrolledSubjectIds = (List<String>) doc.get("enrolledSubjects");
                        if (alreadyEnrolledSubjectIds == null) {
                            alreadyEnrolledSubjectIds = new ArrayList<>();
                        }
                        currentTotalCredits = doc.getLong("totalCredits").intValue();
                        currentCreditsTextView.setText("Total Enrolled Credits: " + currentTotalCredits);
                        loadSubjectsFromFirestore();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadSubjectsFromFirestore() {
        db.collection("subjects").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjectsList.clear();
                    selectedSubjects.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        long credits = doc.getLong("credits");

                        Subject subject = new Subject(id, name, (int) credits);

                        if (alreadyEnrolledSubjectIds.contains(id)) {
                            subject.setSelected(true);
                            selectedSubjects.add(subject);
                        }
                        subjectsList.add(subject);
                    }
                    subjectAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void onSubjectSelected(Subject subject, boolean isSelected) {
        if (isSelected) {
            if (currentTotalCredits + subject.getCredits() <= CREDIT_LIMIT) {
                selectedSubjects.add(subject);
                currentTotalCredits += subject.getCredits();
            } else {
                Toast.makeText(this, "Credit limit exceeded!", Toast.LENGTH_SHORT).show();
                subjectAdapter.deselectSubject(subject);
            }
        } else {
            selectedSubjects.remove(subject);
            currentTotalCredits -= subject.getCredits();
        }
        currentCreditsTextView.setText("Total Enrolled Credits: " + currentTotalCredits);
    }

    private void confirmEnrollment() {
        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "No subjects selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        List<String> enrolledSubjectIds = new ArrayList<>();
        int totalCredits = 0;

        for (Subject subject : selectedSubjects) {
            enrolledSubjectIds.add(subject.getId());
            totalCredits += subject.getCredits();
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("enrolledSubjects", enrolledSubjectIds);
        updates.put("totalCredits", totalCredits);

        db.collection("students").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Enrollment updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error updating enrollment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
