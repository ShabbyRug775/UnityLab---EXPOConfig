package com.example.unitylab_expoconfig.ui.estudiantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;

public class LoginEstudiante extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_estudiantes);

        Button loginEstudiante = findViewById(R.id.buttonLogin);
        TextView textNoEstudiante = findViewById(R.id.textViewNoEstudiante); // Asegúrate de agregar este ID al XML

        loginEstudiante.setOnClickListener(v -> {
            // Redirigir al MainActivity como estudiante
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userType", "student");
            startActivity(intent);
            finish();
        });

        // Configurar el clic para no estudiantes
        textNoEstudiante.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(LoginEstudiante.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.profesores.LoginProfesor"));
                startActivity(intent);

                // Opcional: añadir animación como en MainActivity
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la pantalla de invitado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}