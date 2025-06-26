package com.example.unitylab_expoconfig.ui.visitante;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase de utilidades para funcionalidades de visitante
 */
public class VisitanteHelper {
    private static final String TAG = "VisitanteHelper";
    private static final String PREFS_NAME = "VisitantePrefs";
    private static final String PREF_VISITANTE_ID = "visitante_id";
    private static final String PREF_PRIMERA_VEZ = "primera_vez";
    private static final String PREF_ULTIMA_ACTIVIDAD = "ultima_actividad";

    /**
     * Obtiene o crea un ID de visitante
     * @param context Contexto de la aplicación
     * @param dbHelper Helper de base de datos
     * @return ID del visitante
     */
    public static int obtenerOCrearIdVisitante(Context context, DbmsSQLiteHelper dbHelper) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int idVisitante = prefs.getInt(PREF_VISITANTE_ID, -1);

        // Si no existe ID o el visitante ya no existe en BD, crear uno nuevo
        if (idVisitante == -1 || !dbHelper.existeVisitante(idVisitante)) {
            idVisitante = crearNuevoVisitante(context, dbHelper);
        }

        // Actualizar última actividad
        actualizarUltimaActividad(context);

        return idVisitante;
    }

    /**
     * Crea un nuevo visitante en la base de datos
     * @param context Contexto de la aplicación
     * @param dbHelper Helper de base de datos
     * @return ID del nuevo visitante
     */
    private static int crearNuevoVisitante(Context context, DbmsSQLiteHelper dbHelper) {
        try {
            long resultado = dbHelper.insertarVisitante("Visitante", "Anónimo", "", 1);
            if (resultado != -1) {
                int idVisitante = (int) resultado;

                // Guardar en SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit()
                        .putInt(PREF_VISITANTE_ID, idVisitante)
                        .putBoolean(PREF_PRIMERA_VEZ, true)
                        .apply();

                Log.d(TAG, "Nuevo visitante creado con ID: " + idVisitante);
                return idVisitante;
            } else {
                Log.e(TAG, "Error al crear visitante");
                Toast.makeText(context, "Error al inicializar sesión de visitante", Toast.LENGTH_SHORT).show();
                return -1;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al generar ID de visitante", e);
            Toast.makeText(context, "Error al inicializar sesión", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    /**
     * Verifica si es la primera vez que el visitante usa la app
     * @param context Contexto de la aplicación
     * @return true si es la primera vez
     */
    public static boolean esPrimeraVez(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean primeraVez = prefs.getBoolean(PREF_PRIMERA_VEZ, true);

        if (primeraVez) {
            // Marcar como ya visitado
            prefs.edit().putBoolean(PREF_PRIMERA_VEZ, false).apply();
        }

        return primeraVez;
    }

    /**
     * Actualiza la timestamp de última actividad
     * @param context Contexto de la aplicación
     */
    public static void actualizarUltimaActividad(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(PREF_ULTIMA_ACTIVIDAD, System.currentTimeMillis()).apply();
    }

    /**
     * Obtiene la fecha de última actividad
     * @param context Contexto de la aplicación
     * @return Fecha formateada de última actividad
     */
    public static String obtenerUltimaActividad(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long timestamp = prefs.getLong(PREF_ULTIMA_ACTIVIDAD, System.currentTimeMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Limpia los datos del visitante (logout)
     * @param context Contexto de la aplicación
     */
    public static void limpiarDatosVisitante(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Log.d(TAG, "Datos de visitante limpiados");
    }

    /**
     * Valida una calificación (1-5 estrellas)
     * @param calificacion Calificación a validar
     * @return true si es válida
     */
    public static boolean esCalificacionValida(float calificacion) {
        return calificacion >= 1.0f && calificacion <= 5.0f;
    }

    /**
     * Formatea una calificación para mostrar
     * @param calificacion Calificación numérica
     * @param cantEvaluaciones Cantidad de evaluaciones
     * @return String formateado
     */
    public static String formatearCalificacion(double calificacion, int cantEvaluaciones) {
        if (cantEvaluaciones == 0) {
            return "Sin evaluar";
        }

        String estrellas = "⭐".repeat((int) Math.round(calificacion));
        return String.format(Locale.getDefault(), "%.1f %s (%d eval.)",
                calificacion, estrellas, cantEvaluaciones);
    }

    /**
     * Trunca un texto a una longitud específica
     * @param texto Texto original
     * @param maxLength Longitud máxima
     * @return Texto truncado
     */
    public static String truncarTexto(String texto, int maxLength) {
        if (texto == null || texto.length() <= maxLength) {
            return texto != null ? texto : "";
        }
        return texto.substring(0, maxLength - 3) + "...";
    }

    /**
     * Convierte milisegundos a formato de tiempo legible
     * @param millis Milisegundos
     * @return Tiempo formateado (ej: "hace 5 minutos")
     */
    public static String formatearTiempoRelativo(long millis) {
        long ahora = System.currentTimeMillis();
        long diferencia = ahora - millis;

        long segundos = diferencia / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        long dias = horas / 24;

        if (dias > 0) {
            return "hace " + dias + " día" + (dias != 1 ? "s" : "");
        } else if (horas > 0) {
            return "hace " + horas + " hora" + (horas != 1 ? "s" : "");
        } else if (minutos > 0) {
            return "hace " + minutos + " minuto" + (minutos != 1 ? "s" : "");
        } else {
            return "hace un momento";
        }
    }

    /**
     * Genera un mensaje de bienvenida personalizado
     * @param context Contexto de la aplicación
     * @return Mensaje de bienvenida
     */
    public static String generarMensajeBienvenida(Context context) {
        if (esPrimeraVez(context)) {
            return "¡Bienvenido por primera vez!\nExplora y evalúa los proyectos más innovadores.";
        } else {
            String ultimaActividad = obtenerUltimaActividad(context);
            return "¡Bienvenido de nuevo!\nÚltima visita: " + ultimaActividad;
        }
    }

    /**
     * Valida comentarios de evaluación
     * @param comentarios Comentarios a validar
     * @return true si son válidos
     */
    public static boolean sonComentariosValidos(String comentarios) {
        if (comentarios == null) return true; // Los comentarios son opcionales

        // Limpiar espacios
        comentarios = comentarios.trim();

        // Verificar longitud máxima
        if (comentarios.length() > 500) return false;

        // Verificar que no contenga solo espacios o caracteres especiales
        if (comentarios.matches("^[\\s\\p{Punct}]*$")) return false;

        return true;
    }

    /**
     * Obtiene estadísticas rápidas del visitante
     * @param context Contexto de la aplicación
     * @param dbHelper Helper de base de datos
     * @return Array con [proyectos, equipos, evaluaciones]
     */
    public static int[] obtenerEstadisticasRapidas(Context context, DbmsSQLiteHelper dbHelper) {
        try {
            int idVisitante = obtenerOCrearIdVisitante(context, dbHelper);

            // Obtener estadísticas
            int totalProyectos = 0;
            int totalEquipos = 0;
            int misEvaluaciones = 0;

            // Total de proyectos y equipos
            android.database.Cursor cursorProyectos = dbHelper.obtenerTodosProyectos();
            if (cursorProyectos != null) {
                totalProyectos = cursorProyectos.getCount();
                cursorProyectos.close();
            }

            android.database.Cursor cursorEquipos = dbHelper.obtenerTodosEquipos();
            if (cursorEquipos != null) {
                totalEquipos = cursorEquipos.getCount();
                cursorEquipos.close();
            }

            // Mis evaluaciones
            misEvaluaciones = dbHelper.contarEvaluacionesVisitante(idVisitante);

            return new int[]{totalProyectos, totalEquipos, misEvaluaciones};

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estadísticas", e);
            return new int[]{0, 0, 0};
        }
    }

    /**
     * Clase para manejar constantes de feedback
     */
    public static class FeedbackConstants {
        public static final int CALIFICACION_MINIMA = 1;
        public static final int CALIFICACION_MAXIMA = 5;
        public static final int MAX_COMENTARIOS = 500;
        public static final int MIN_CALIFICACIONES_REQUERIDAS = 3; // Para feedback

        public static final String[] ETIQUETAS_CALIFICACION = {
                "", // 0 - no usado
                "Muy malo",
                "Malo",
                "Regular",
                "Bueno",
                "Excelente"
        };
    }

    /**
     * Clase para manejar constantes de UI
     */
    public static class UIConstants {
        public static final int REFRESH_DELAY = 1500;
        public static final int AUTO_CLOSE_DELAY = 3000;
        public static final int ANIMATION_DURATION = 300;
        public static final int MAX_PROYECTOS_DESTACADOS = 5;
        public static final int MAX_DESCRIPCION_LENGTH = 150;
    }
}