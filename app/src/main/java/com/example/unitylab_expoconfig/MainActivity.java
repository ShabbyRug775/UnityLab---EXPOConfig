package com.example.unitylab_expoconfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.unitylab_expoconfig.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Primero mostramos la pantalla de inicio
        setContentView(R.layout.inicio_main);

        // 2. Configuramos los botones del inicio
        Button btnEstudiante = findViewById(R.id.button2);
        Button btnInvitado = findViewById(R.id.button3);

        btnEstudiante.setOnClickListener(v -> {
            // 3. Al hacer clic, cargamos la app completa
            loadMainAppLayout();
            setupNavigation();
        });

        btnInvitado.setOnClickListener(v -> {
            // Modo invitado (puedes personalizar esto)
            loadMainAppLayout();
            setupNavigation();
            // Opcional: Deshabilitar algunas funciones para invitados
        });
    }

    private void loadMainAppLayout() {
        // Cambiamos al layout principal de la aplicación
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración básica de la toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setAnchorView(R.id.fab)
                            .show());
        }
    }

    private void setupNavigation() {
        // Configuración del sistema de navegación
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Configuración del Navigation Drawer
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Configuración de la Bottom Navigation (si existe)
        if (binding.appBarMain.contentMain.bottomNavView != null) {
            NavigationUI.setupWithNavController(binding.appBarMain.contentMain.bottomNavView, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}