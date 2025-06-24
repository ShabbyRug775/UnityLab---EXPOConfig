package com.example.unitylab_expoconfig.ui.inicio;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.unitylab_expoconfig.MainActivity;
import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.ui.cartel.ImpresionActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminActivity extends AppCompatActivity {

    private DbmsSQLiteHelper dbHelper;
    private TextView tvTotalProfesores, tvTotalProyectos, tvTotalEventos;
    private TextView tvBienvenidaAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_administrador);

        // Inicializar base de datos
        dbHelper = new DbmsSQLiteHelper(this);

        // Inicializar vistas
        inicializarVistas();

        // Configurar listeners
        configurarListeners();

        // Cargar estadísticas del sistema
        cargarEstadisticas();

        // Configurar mensaje de bienvenida
        configurarBienvenida();
    }

    private void inicializarVistas() {
        tvBienvenidaAdmin = findViewById(R.id.tvBienvenidaAdmin);
        tvTotalProfesores = findViewById(R.id.tvTotalProfesores);
        tvTotalProyectos = findViewById(R.id.tvTotalProyectos);
        tvTotalEventos = findViewById(R.id.tvTotalEventos);
    }

    private void configurarListeners() {
        // CardViews principales
        findViewById(R.id.cardAsignarHorarios).setOnClickListener(v -> mostrarAsignarHorarios());
        findViewById(R.id.cardRegistroProfesores).setOnClickListener(v -> mostrarRegistroProfesor());
        findViewById(R.id.cardCreacionEvento).setOnClickListener(v -> mostrarCreacionEvento());
        findViewById(R.id.cardColaImpresion).setOnClickListener(v -> abrirColaImpresion());

        // Opciones adicionales
        findViewById(R.id.linearEditarProfesores).setOnClickListener(v -> mostrarEditarProfesores());
        findViewById(R.id.linearEliminarProfesores).setOnClickListener(v -> mostrarEliminarProfesores());

        // Botón cerrar sesión
        findViewById(R.id.btnCerrarSesionAdmin).setOnClickListener(v -> cerrarSesion());
    }

    private void configurarBienvenida() {
        // Obtener datos del intent si están disponibles
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tvBienvenidaAdmin.setText("¡Bienvenido, " + nombreUsuario + "!");
        }
    }

    private void cargarEstadisticas() {
        try {
            // Contar profesores
            Cursor cursorProfesores = dbHelper.obtenerTodosProfesores();
            int totalProfesores = cursorProfesores != null ? cursorProfesores.getCount() : 0;
            tvTotalProfesores.setText(String.valueOf(totalProfesores));
            if (cursorProfesores != null) cursorProfesores.close();

            // Contar proyectos
            Cursor cursorProyectos = dbHelper.obtenerTodosProyectos();
            int totalProyectos = cursorProyectos != null ? cursorProyectos.getCount() : 0;
            tvTotalProyectos.setText(String.valueOf(totalProyectos));
            if (cursorProyectos != null) cursorProyectos.close();

            // Contar eventos
            Cursor cursorEventos = dbHelper.obtenerTodosEventos();
            int totalEventos = cursorEventos != null ? cursorEventos.getCount() : 0;
            tvTotalEventos.setText(String.valueOf(totalEventos));
            if (cursorEventos != null) cursorEventos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== FUNCIONALIDADES PRINCIPALES ====================

    private void mostrarAsignarHorarios() {
        // Primero mostrar lista de eventos para seleccionar
        Cursor cursor = dbHelper.obtenerTodosEventos();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No hay eventos registrados", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        ArrayList<String> eventos = new ArrayList<>();
        ArrayList<Integer> idsEventos = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int idEvento = cursor.getInt(cursor.getColumnIndexOrThrow("IdEvento"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("FechaInicio"));

                eventos.add(nombre + " (" + fechaInicio + ")");
                idsEventos.add(idEvento);
            } while (cursor.moveToNext());
        }
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Evento para Editar Horarios")
                .setItems(eventos.toArray(new String[0]), (dialog, which) -> {
                    int idEvento = idsEventos.get(which);
                    editarHorariosEvento(idEvento);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarHorariosEvento(int idEvento) {
        Cursor cursor = dbHelper.obtenerEventoPorId(idEvento);
        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "Evento no encontrado", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        // Obtener datos actuales del evento
        String nombreEvento = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
        String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("FechaInicio"));
        String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("FechaFin"));
        String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("HoraInicio"));
        String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("HoraFin"));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
        String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("Ubicacion"));
        String diasPresentacion = cursor.getString(cursor.getColumnIndexOrThrow("Diaspresentacion"));
        int asistencia = cursor.getInt(cursor.getColumnIndexOrThrow("Asistencia"));
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_asignar_horarios, null);

        EditText editFechaInicio = dialogView.findViewById(R.id.editFechaInicio);
        EditText editFechaFin = dialogView.findViewById(R.id.editFechaFin);
        EditText editHoraInicio = dialogView.findViewById(R.id.editHoraInicio);
        EditText editHoraFin = dialogView.findViewById(R.id.editHoraFin);

        // Llenar con datos actuales
        editFechaInicio.setText(fechaInicio != null ? fechaInicio : "");
        editFechaFin.setText(fechaFin != null ? fechaFin : "");
        editHoraInicio.setText(horaInicio != null ? horaInicio : "");
        editHoraFin.setText(horaFin != null ? horaFin : "");

        // Configurar selecciones de fecha y hora
        editFechaInicio.setOnClickListener(v -> mostrarDatePicker(editFechaInicio));
        editFechaFin.setOnClickListener(v -> mostrarDatePicker(editFechaFin));
        editHoraInicio.setOnClickListener(v -> mostrarTimePicker(editHoraInicio));
        editHoraFin.setOnClickListener(v -> mostrarTimePicker(editHoraFin));

        builder.setView(dialogView)
                .setTitle("Editar Horarios: " + nombreEvento)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String nuevaFechaInicio = editFechaInicio.getText().toString().trim();
                    String nuevaFechaFin = editFechaFin.getText().toString().trim();
                    String nuevaHoraInicio = editHoraInicio.getText().toString().trim();
                    String nuevaHoraFin = editHoraFin.getText().toString().trim();

                    if (nuevaFechaInicio.isEmpty() || nuevaFechaFin.isEmpty()) {
                        Toast.makeText(this, "Las fechas son obligatorias", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int resultado = dbHelper.actualizarEvento(idEvento, nombreEvento, descripcion, ubicacion,
                            nuevaFechaInicio, nuevaFechaFin, diasPresentacion, nuevaHoraInicio, nuevaHoraFin, asistencia);

                    if (resultado > 0) {
                        Toast.makeText(this, "Horarios actualizados correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al actualizar horarios", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarRegistroProfesor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_registro_profesor, null);

        EditText editNumEmpleado = dialogView.findViewById(R.id.editNumEmpleado);
        EditText editNombre = dialogView.findViewById(R.id.editNombre);
        EditText editApellido1 = dialogView.findViewById(R.id.editApellido1);
        EditText editApellido2 = dialogView.findViewById(R.id.editApellido2);
        EditText editCorreo = dialogView.findViewById(R.id.editCorreo);
        EditText editTelefono = dialogView.findViewById(R.id.editTelefono);
        EditText editContrasena = dialogView.findViewById(R.id.editContrasena);
        Spinner spinnerAcademias = dialogView.findViewById(R.id.spinnerAcademias);

        // Cargar academias en el spinner
        cargarAcademiasEnSpinner(spinnerAcademias);

        builder.setView(dialogView)
                .setTitle("Registro de Profesor")
                .setPositiveButton("Registrar", (dialog, which) -> {
                    try {
                        int numEmpleado = Integer.parseInt(editNumEmpleado.getText().toString().trim());
                        String nombre = editNombre.getText().toString().trim();
                        String apellido1 = editApellido1.getText().toString().trim();
                        String apellido2 = editApellido2.getText().toString().trim();
                        String correo = editCorreo.getText().toString().trim();
                        String telefono = editTelefono.getText().toString().trim();
                        String contrasena = editContrasena.getText().toString().trim();

                        if (nombre.isEmpty() || contrasena.isEmpty()) {
                            Toast.makeText(this, "Nombre y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Obtener ID de administrador actual
                        int idAdmin = getIntent().getIntExtra("idUsuario", 0);

                        long resultado = dbHelper.insertarProfesor(numEmpleado, nombre, apellido1, apellido2,
                                contrasena, correo, telefono, "Activo", 1, idAdmin);

                        if (resultado != -1) {
                            Toast.makeText(this, "Profesor registrado exitosamente", Toast.LENGTH_SHORT).show();
                            cargarEstadisticas(); // Actualizar estadísticas
                        } else {
                            Toast.makeText(this, "Error al registrar profesor", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Número de empleado inválido", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarCreacionEvento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_evento, null);

        EditText editNombreEvento = dialogView.findViewById(R.id.editNombreEvento);
        EditText editDescripcion = dialogView.findViewById(R.id.editDescripcion);
        EditText editUbicacion = dialogView.findViewById(R.id.editUbicacion);
        EditText editFechaInicio = dialogView.findViewById(R.id.editFechaInicio);
        EditText editFechaFin = dialogView.findViewById(R.id.editFechaFin);
        EditText editDiasPresentacion = dialogView.findViewById(R.id.editDiasPresentacion);
        EditText editHoraInicio = dialogView.findViewById(R.id.editHoraInicio);
        EditText editHoraFin = dialogView.findViewById(R.id.editHoraFin);

        // Configurar selecciones de fecha y hora
        editFechaInicio.setOnClickListener(v -> mostrarDatePicker(editFechaInicio));
        editFechaFin.setOnClickListener(v -> mostrarDatePicker(editFechaFin));
        editHoraInicio.setOnClickListener(v -> mostrarTimePicker(editHoraInicio));
        editHoraFin.setOnClickListener(v -> mostrarTimePicker(editHoraFin));

        builder.setView(dialogView)
                .setTitle("Crear Nuevo Evento")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editNombreEvento.getText().toString().trim();
                    String descripcion = editDescripcion.getText().toString().trim();
                    String ubicacion = editUbicacion.getText().toString().trim();
                    String fechaInicio = editFechaInicio.getText().toString().trim();
                    String fechaFin = editFechaFin.getText().toString().trim();
                    String diasPresentacion = editDiasPresentacion.getText().toString().trim();
                    String horaInicio = editHoraInicio.getText().toString().trim();
                    String horaFin = editHoraFin.getText().toString().trim();

                    if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                        Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long resultado = dbHelper.insertarEvento(nombre, descripcion, ubicacion,
                            fechaInicio, fechaFin, diasPresentacion, horaInicio, horaFin, 0);

                    if (resultado != -1) {
                        Toast.makeText(this, "Evento creado exitosamente", Toast.LENGTH_SHORT).show();
                        cargarEstadisticas(); // Actualizar estadísticas
                    } else {
                        Toast.makeText(this, "Error al crear evento", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirColaImpresion() {
        Intent intent = new Intent(AdminActivity.this, ImpresionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // ==================== FUNCIONALIDADES OPCIONALES ====================

    private void mostrarEditarProfesores() {
        // Mostrar lista de profesores para editar
        Cursor cursor = dbHelper.obtenerTodosProfesores();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No hay profesores registrados", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        ArrayList<String> profesores = new ArrayList<>();
        ArrayList<Integer> numerosEmpleado = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int numEmpleado = cursor.getInt(cursor.getColumnIndexOrThrow("NumeroEmpleado"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow("Apellido1"));

                profesores.add(numEmpleado + " - " + nombre + " " + (apellido != null ? apellido : ""));
                numerosEmpleado.add(numEmpleado);
            } while (cursor.moveToNext());
        }
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Profesor a Editar")
                .setItems(profesores.toArray(new String[0]), (dialog, which) -> {
                    int numEmpleado = numerosEmpleado.get(which);
                    editarProfesor(numEmpleado);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarProfesor(int numeroEmpleado) {
        Cursor cursor = dbHelper.obtenerProfesorPorNumeroEmpleado(numeroEmpleado);
        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "Profesor no encontrado", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        // Obtener datos actuales
        String nombreActual = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
        String apellido1Actual = cursor.getString(cursor.getColumnIndexOrThrow("Apellido1"));
        String apellido2Actual = cursor.getString(cursor.getColumnIndexOrThrow("Apellido2"));
        String correoActual = cursor.getString(cursor.getColumnIndexOrThrow("Correo"));
        String telefonoActual = cursor.getString(cursor.getColumnIndexOrThrow("Telefono"));
        cursor.close();

        // Mostrar diálogo de edición (similar al de registro)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_registro_profesor, null);

        EditText editNombre = dialogView.findViewById(R.id.editNombre);
        EditText editApellido1 = dialogView.findViewById(R.id.editApellido1);
        EditText editApellido2 = dialogView.findViewById(R.id.editApellido2);
        EditText editCorreo = dialogView.findViewById(R.id.editCorreo);
        EditText editTelefono = dialogView.findViewById(R.id.editTelefono);
        EditText editContrasena = dialogView.findViewById(R.id.editContrasena);

        // Llenar con datos actuales
        editNombre.setText(nombreActual);
        editApellido1.setText(apellido1Actual != null ? apellido1Actual : "");
        editApellido2.setText(apellido2Actual != null ? apellido2Actual : "");
        editCorreo.setText(correoActual != null ? correoActual : "");
        editTelefono.setText(telefonoActual != null ? telefonoActual : "");

        // Ocultar campo de número de empleado
        dialogView.findViewById(R.id.editNumEmpleado).setVisibility(View.GONE);

        builder.setView(dialogView)
                .setTitle("Editar Profesor: " + numeroEmpleado)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String nombre = editNombre.getText().toString().trim();
                    String apellido1 = editApellido1.getText().toString().trim();
                    String apellido2 = editApellido2.getText().toString().trim();
                    String correo = editCorreo.getText().toString().trim();
                    String telefono = editTelefono.getText().toString().trim();
                    String contrasena = editContrasena.getText().toString().trim();

                    if (nombre.isEmpty()) {
                        Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Si no se cambió la contraseña, mantener la actual
                    if (contrasena.isEmpty()) {
                        contrasena = "sin_cambio"; // Placeholder, necesitarías obtener la actual
                    }

                    int idAdmin = getIntent().getIntExtra("idUsuario", 0);
                    int resultado = dbHelper.actualizarProfesor(numeroEmpleado, nombre, apellido1, apellido2,
                            contrasena, correo, telefono, "Activo", 1, idAdmin);

                    if (resultado > 0) {
                        Toast.makeText(this, "Profesor actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al actualizar profesor", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarEliminarProfesores() {
        // Mostrar lista de profesores para eliminar
        Cursor cursor = dbHelper.obtenerTodosProfesores();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No hay profesores registrados", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        ArrayList<String> profesores = new ArrayList<>();
        ArrayList<Integer> numerosEmpleado = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int numEmpleado = cursor.getInt(cursor.getColumnIndexOrThrow("NumeroEmpleado"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow("Apellido1"));

                profesores.add(numEmpleado + " - " + nombre + " " + (apellido != null ? apellido : ""));
                numerosEmpleado.add(numEmpleado);
            } while (cursor.moveToNext());
        }
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Profesor a Eliminar")
                .setItems(profesores.toArray(new String[0]), (dialog, which) -> {
                    int numEmpleado = numerosEmpleado.get(which);
                    String nombreProfesor = profesores.get(which);
                    confirmarEliminacionProfesor(numEmpleado, nombreProfesor);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminacionProfesor(int numeroEmpleado, String nombreProfesor) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Está seguro de que desea eliminar al profesor?\n\n" + nombreProfesor +
                        "\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    int resultado = dbHelper.eliminarProfesor(numeroEmpleado);
                    if (resultado > 0) {
                        Toast.makeText(this, "Profesor eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        cargarEstadisticas(); // Actualizar estadísticas
                    } else {
                        Toast.makeText(this, "Error al eliminar profesor", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ==================== MÉTODOS AUXILIARES ====================



    private void cargarAcademiasEnSpinner(Spinner spinner) {
        Cursor cursor = dbHelper.obtenerTodasAcademias();
        ArrayList<String> academias = new ArrayList<>();
        academias.add("Seleccionar academia...");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                academias.add(nombre);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, academias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void mostrarDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    editText.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void mostrarTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String hora = String.format("%02d:%02d:00", hourOfDay, minute);
                    editText.setText(hora);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro de que desea cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estadísticas cuando se regrese a esta actividad
        cargarEstadisticas();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.cerrarConexion();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Mostrar confirmación antes de salir
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Desea salir del panel de administración?")
                .setPositiveButton("Sí", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("No", null)
                .show();
    }
}