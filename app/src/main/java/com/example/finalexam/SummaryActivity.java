package com.example.finalexam;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class SummaryActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView userNameTextView, totalCreditsTextView, enrolledSubjectsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Button enrollButton = findViewById(R.id.enrollButton);
        enrollButton.setOnClickListener(v -> {
            Intent intent = new Intent(SummaryActivity.this, EnrollmentActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userNameTextView = findViewById(R.id.userNameTextView);
        totalCreditsTextView = findViewById(R.id.totalCreditsTextView);
        enrolledSubjectsTextView = findViewById(R.id.enrolledSubjectsTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("students").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        Long totalCredits = documentSnapshot.getLong("totalCredits");
                        List<String> enrolledSubjects = (List<String>) documentSnapshot.get("enrolledSubjects");

                        userNameTextView.setText("Name: " + name);
                        totalCreditsTextView.setText("Total Credits: " + (totalCredits != null ? totalCredits : 0));

                        if (enrolledSubjects == null || enrolledSubjects.isEmpty()) {
                            enrolledSubjectsTextView.setText("No subjects enrolled in yet.");
                        } else {
                            loadSubjects(enrolledSubjects);
                        }
                    } else {
                        Toast.makeText(SummaryActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SummaryActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSubjects(List<String> subjectIds) {
        StringBuilder subjectsList = new StringBuilder();

        for (String subjectId : subjectIds) {
            db.collection("subjects").document(subjectId).get()
                    .addOnSuccessListener(subjectSnapshot -> {
                        if (subjectSnapshot.exists()) {
                            String title = subjectSnapshot.getString("name");
                            Long credits = subjectSnapshot.getLong("credits");
                            subjectsList.append("- ").append(title).append(" (Credits: ").append(credits).append(")\n");
                            enrolledSubjectsTextView.setText(subjectsList.toString());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SummaryActivity.this, "Error loading subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
