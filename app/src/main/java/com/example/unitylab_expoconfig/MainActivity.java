package com.example.unitylab_expoconfig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_main);
        EjemploUsoCompleto Ejemplo = new EjemploUsoCompleto(this);
        Ejemplo.ejecutarEjemploCompleto();

        // Configurar click listeners para cada CardView
        CardView cardAdmin = findViewById(R.id.cardAdministrador);
        CardView cardProfesor = findViewById(R.id.cardProfesor);
        CardView cardAlumno = findViewById(R.id.cardAlumno);
        CardView cardVisitante = findViewById(R.id.cardVisitante);

        // Listener para Administrador
        cardAdmin.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.administrador.LoginAdministrador"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                handleActivityError(e);
            }
        });

        // Listener para Profesor
        cardProfesor.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.profesores.LoginProfesor"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                handleActivityError(e);
            }
        });

        // Listener para Alumno
        cardAlumno.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.estudiantes.LoginEstudiante"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                handleActivityError(e);
            }
        });

        // Listener para Visitante
        cardVisitante.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.visitante.VisitanteActivity"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                handleActivityError(e);
            }
        });
    }

    // Metodo para manejar errores de clase no encontrada
    private void handleActivityError(ClassNotFoundException e) {
        e.printStackTrace();
        Toast.makeText(this, "Error: Pantalla no disponible", Toast.LENGTH_SHORT).show();

        // Opcional: Log para depuración
        Log.e("MainActivity", "Error al cargar actividad", e);
    }

}