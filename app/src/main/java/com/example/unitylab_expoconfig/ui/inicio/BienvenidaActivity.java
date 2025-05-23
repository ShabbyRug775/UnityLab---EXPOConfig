package com.example.unitylab_expoconfig.ui.inicio;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;

public class BienvenidaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        TextView textViewBienvenida = findViewById(R.id.textViewBienvenida);
        TextView textViewTipoUsuario = findViewById(R.id.textViewTipoUsuario);

        String tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
        String tipoMensaje = "Tipo de usuario: " +
                (tipoUsuario.equals("estudiante") ? "Estudiante" : "Profesor");

        textViewBienvenida.setText(mensajeBienvenida);
        textViewTipoUsuario.setText(tipoMensaje);
    }
}