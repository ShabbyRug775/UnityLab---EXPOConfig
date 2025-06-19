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
import com.example.unitylab_expoconfig.ui.cartel.ImpresionActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_administrador); // Asegúrate de que el XML se llame activity_admin.xml

        // Obtener referencias a las vistas
        TextView tvBienvenidaAdmin = findViewById(R.id.tvBienvenidaAdmin);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        // CardViews principales
        CardView cardAsignarHorarios = findViewById(R.id.cardAsignarHorarios);
        //CardView cardGestionarProfesores = findViewById(R.id.cardGestionarProfesores);
        //CardView cardFechasExposicion = findViewById(R.id.cardFechasExposicion);
        CardView cardColaImpresion = findViewById(R.id.cardColaImpresion);

        // Opciones adicionales
        View linearEditarProfesores = findViewById(R.id.linearEditarProfesores);
        View linearEliminarProfesores = findViewById(R.id.linearEliminarProfesores);
        //View linearConfigurarEspacios = findViewById(R.id.linearConfigurarEspacios);

        // Configurar listeners para los CardViews principales
        cardAsignarHorarios.setOnClickListener(v -> {
            // Aquí iría la lógica para asignar horarios
            // Intent intent = new Intent(this, AsignarHorariosActivity.class);
            // startActivity(intent);
        });

        /*cardGestionarProfesores.setOnClickListener(v -> {
            // Aquí iría la lógica para gestionar profesores
            // Intent intent = new Intent(this, GestionarProfesoresActivity.class);
            // startActivity(intent);
        });*/

        /*cardFechasExposicion.setOnClickListener(v -> {
            // Aquí iría la lógica para fechas de exposición
            // Intent intent = new Intent(this, FechasExposicionActivity.class);
            // startActivity(intent);
        });*/

        cardColaImpresion.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ImpresionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Configurar listeners para las opciones adicionales
        linearEditarProfesores.setOnClickListener(v -> {
            // Aquí iría la lógica para editar profesores
            // Intent intent = new Intent(this, EditarProfesoresActivity.class);
            // startActivity(intent);
        });

        linearEliminarProfesores.setOnClickListener(v -> {
            // Aquí iría la lógica para eliminar profesores
            // Intent intent = new Intent(this, EliminarProfesoresActivity.class);
            // startActivity(intent);
        });

        /*linearConfigurarEspacios.setOnClickListener(v -> {
            // Aquí iría la lógica para configurar espacios
            // Intent intent = new Intent(this, ConfigurarEspaciosActivity.class);
            // startActivity(intent);
        });*/

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
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