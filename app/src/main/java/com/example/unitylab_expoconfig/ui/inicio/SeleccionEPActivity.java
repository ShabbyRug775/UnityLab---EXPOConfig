package com.example.unitylab_expoconfig.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;

public class SeleccionEPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccion_e_p);

        Button btnEstudiante = findViewById(R.id.button2);
        Button btnProfesor = findViewById(R.id.button3);

        btnEstudiante.setOnClickListener(v -> {
            try {
                // Usa el paquete completo para el Intent
                Intent intent = new Intent(SeleccionEPActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.estudiantes.LoginEstudiante"));
                startActivity(intent);

                // Opcional: añadir animación
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                // Manejo de error si la clase no se encuentra
                Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            }
        });

        btnProfesor.setOnClickListener(v -> {
            try {
                // Usa el paquete completo para el Intent
                Intent intent = new Intent(SeleccionEPActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.profesores.LoginProfesor"));
                startActivity(intent);

                // Opcional: añadir animación
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                // Manejo de error si la clase no se encuentra
                Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            }
        });
    }
}