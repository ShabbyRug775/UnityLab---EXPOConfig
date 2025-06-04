package com.example.unitylab_expoconfig.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;

public class BienvenidaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        TextView textViewBienvenida = findViewById(R.id.textViewBienvenida);
        TextView textViewTipoUsuario = findViewById(R.id.textViewTipoUsuario);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
        String tipoMensaje = "Tipo de usuario: " +
                (tipoUsuario.equals("estudiante") ? "Estudiante" : "Profesor");

        textViewBienvenida.setText(mensajeBienvenida);
        textViewTipoUsuario.setText(tipoMensaje);

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        // Redirigir a MainActivity y limpiar el historial de navegación
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish(); // Finalizar la actividad actual
    }

    @Override
    public void onBackPressed() {
        // Opcional: Deshabilitar el botón de retroceso o manejarlo de manera específica
        super.onBackPressed();
    }
}