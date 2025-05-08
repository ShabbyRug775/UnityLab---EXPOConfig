package com.example.unitylab_expoconfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Primero mostramos la pantalla de inicio
        setContentView(R.layout.inicio_main);

        // 2. Configuramos los botones del inicio
        Button btnEstudiante = findViewById(R.id.button2);
        Button btnInvitado = findViewById(R.id.button3);

        btnEstudiante.setOnClickListener(v -> {
            try {
                // Usa el paquete completo para el Intent
                Intent intent = new Intent(MainActivity.this,
                        Class.forName("com.example.unitylab_expoconfig.ui.inicio.SeleccionEPActivity"));
                startActivity(intent);

                // Opcional: añadir animación
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                // Manejo de error si la clase no se encuentra
                Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            }
        });

        btnInvitado.setOnClickListener(v -> {
            // Modo invitado (puedes personalizar esto)
            //loadMainAppLayout();
            //setupNavigation();
            // Opcional: Deshabilitar algunas funciones para invitados
        });
    }




}