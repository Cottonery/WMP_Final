package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText emailRegister = findViewById(R.id.emailRegister);
        EditText nameRegister = findViewById(R.id.nameRegister);
        EditText passwordRegister = findViewById(R.id.passwordRegister);
        EditText confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String email = emailRegister.getText().toString();
            String name = nameRegister.getText().toString();
            String password = passwordRegister.getText().toString();
            String confirmPassword = confirmPasswordRegister.getText().toString();

            if (!email.isEmpty() && !name.isEmpty() && !password.isEmpty() && password.equals(confirmPassword)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    saveUserToFirestore(user, name, email);
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Please fill all fields and ensure passwords match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToFirestore(FirebaseUser user, String name, String email) {
        Map<String, Object> student = new HashMap<>();
        student.put("name", name);
        student.put("email", email);
        student.put("totalCredits", 0);

        db.collection("students")
                .document(user.getUid())
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(RegisterActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}

