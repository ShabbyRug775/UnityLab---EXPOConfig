package com.example.unitylab_expoconfig.ui.profesores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;

public class LoginProfesor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_profesores);

        Button loginProfesor = findViewById(R.id.buttonLogin);
        TextView textNoProfesor = findViewById(R.id.textViewNoProfesor);

        // Configuración del botón de login para profesores
        loginProfesor.setOnClickListener(v -> {
            // Redirigir al MainActivity como profesor
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userType", "professor"); // Cambiado a "professor" en lugar de "student"
            startActivity(intent);
            finish();
        });

        // Configurar el clic para "No eres profesor"
        textNoProfesor.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(LoginProfesor.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.estudiantes.LoginEstudiante"));
                startActivity(intent);

                // Añadir animación
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la pantalla de selección", Toast.LENGTH_SHORT).show();

                // Opcional: Redirigir a MainActivity como fallback
                Intent fallbackIntent = new Intent(this, MainActivity.class);
                startActivity(fallbackIntent);
                finish();
            }
        });
    }
}