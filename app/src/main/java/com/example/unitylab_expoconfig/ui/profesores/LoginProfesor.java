package com.example.unitylab_expoconfig.ui.profesores;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
public class LoginProfesor extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_profesores);

        Button loginProfesor = findViewById(R.id.buttonLogin);

        loginProfesor.setOnClickListener(v -> {
            // Redirigir al MainActivity como estudiante
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userType", "student");
            startActivity(intent);
            finish(); // Cierra esta actividad para no volver atrás
        });

    }
}
