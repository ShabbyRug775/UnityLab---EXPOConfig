package com.example.unitylab_expoconfig.ui.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.ui.proyectos.ListaProyectosActivity;
import com.example.unitylab_expoconfig.ui.proyectos.CrearProyectoActivity;

public class BienvenidaActivity extends AppCompatActivity {

    private String tipoUsuario;
    private String nombreUsuario;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        TextView textViewBienvenida = findViewById(R.id.textViewBienvenida);
        TextView textViewTipoUsuario = findViewById(R.id.textViewTipoUsuario);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Nuevos elementos para proyectos
        CardView cardProyectos = findViewById(R.id.cardProyectos);
        CardView cardCrearProyecto = findViewById(R.id.cardCrearProyecto);
        CardView cardMisProyectos = findViewById(R.id.cardMisProyectos);

        // Obtener datos del usuario
        tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        String mensajeBienvenida = "¡Bienvenido, " + nombreUsuario + "!";
        String tipoMensaje = "Tipo de usuario: " +
                (tipoUsuario.equals("estudiante") ? "Estudiante" : "Profesor");

        textViewBienvenida.setText(mensajeBienvenida);
        textViewTipoUsuario.setText(tipoMensaje);

        // Configurar acciones para proyectos
        configurarAccionesProyectos(cardProyectos, cardCrearProyecto, cardMisProyectos);

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });


    }

    private void configurarAccionesProyectos(CardView cardProyectos, CardView cardCrearProyecto, CardView cardMisProyectos) {
        // Ver todos los proyectos
        cardProyectos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaProyectosActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("tipoUsuario", tipoUsuario);
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
        });

        // Crear nuevo proyecto
        cardCrearProyecto.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearProyectoActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("tipoUsuario", tipoUsuario);
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
        });

        // Ver mis proyectos
        cardMisProyectos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaProyectosActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("tipoUsuario", tipoUsuario);
            intent.putExtra("nombreUsuario", nombreUsuario);
            intent.putExtra("filtroInicial", "mis_proyectos"); // Filtro especial
            startActivity(intent);
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