package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        EditText emailLogin = findViewById(R.id.emailLogin);
        EditText passwordLogin = findViewById(R.id.passwordLogin);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);

        loginButton.setOnClickListener(v -> {
            String email = emailLogin.getText().toString();
            String password = passwordLogin.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(MainActivity.this, SummaryActivity.class));
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }
}
