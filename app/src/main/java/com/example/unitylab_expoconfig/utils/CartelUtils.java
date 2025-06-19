package com.example.unitylab_expoconfig.utils;

import android.content.Context;
import android.database.Cursor;

import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoDB;

import java.util.ArrayList;
import java.util.List;

public class CartelUtils {

    /**
     * Obtiene estadísticas de visitas de todos los equipos
     */
    public static List<EstadisticaEquipo> obtenerEstadisticasVisitas(Context context) {
        List<EstadisticaEquipo> estadisticas = new ArrayList<>();
        DbmsSQLiteHelper dbHelper = new DbmsSQLiteHelper(context);
        Cursor cursor = null;

        try {
            cursor = dbHelper.obtenerEquiposPorPromedio(); // Ordenados por promedio DESC

            while (cursor != null && cursor.moveToNext()) {
                EstadisticaEquipo estadistica = new EstadisticaEquipo();
                estadistica.idEquipo = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_ID));
                estadistica.nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE));
                estadistica.nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE_PROYECTO));
                estadistica.cantidadVisitas = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_CANT_VISITAS));
                estadistica.cantidadEvaluaciones = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_CANT_EVAL));
                estadistica.promedio = cursor.getFloat(cursor.getColumnIndexOrThrow(EquipoDB.COL_PROMEDIO));
                estadistica.lugar = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_LUGAR));
                estadistica.numeroAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_NUM_ALUMNOS));

                estadisticas.add(estadistica);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            dbHelper.cerrarConexion();
        }

        return estadisticas;
    }

    /**
     * Verifica si un equipo tiene cartel generado
     */
    public static boolean equipoTieneCartel(Context context, int equipoId) {
        DbmsSQLiteHelper dbHelper = new DbmsSQLiteHelper(context);
        Cursor cursor = null;
        boolean tieneCartel = false;

        try {
            cursor = dbHelper.obtenerEquipoPorId(equipoId);

            if (cursor != null && cursor.moveToFirst()) {
                String rutaCartel = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_CARTEL));
                tieneCartel = (rutaCartel != null && !rutaCartel.isEmpty());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            dbHelper.cerrarConexion();
        }

        return tieneCartel;
    }

    /**
     * Obtiene información resumida de un equipo
     */
    public static ResumenEquipo obtenerResumenEquipo(Context context, int equipoId) {
        DbmsSQLiteHelper dbHelper = new DbmsSQLiteHelper(context);
        Cursor cursor = null;
        ResumenEquipo resumen = null;

        try {
            cursor = dbHelper.obtenerEquipoPorId(equipoId);

            if (cursor != null && cursor.moveToFirst()) {
                resumen = new ResumenEquipo();
                resumen.idEquipo = equipoId;
                resumen.nombreEquipo = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE));
                resumen.nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_NOMBRE_PROYECTO));
                resumen.descripcion = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_DESCRIPCION));
                resumen.lugar = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_LUGAR));
                resumen.numeroAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_NUM_ALUMNOS));
                resumen.cantidadVisitas = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_CANT_VISITAS));
                resumen.cantidadEvaluaciones = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoDB.COL_CANT_EVAL));
                resumen.promedio = cursor.getFloat(cursor.getColumnIndexOrThrow(EquipoDB.COL_PROMEDIO));
                resumen.rutaCartel = cursor.getString(cursor.getColumnIndexOrThrow(EquipoDB.COL_CARTEL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            dbHelper.cerrarConexion();
        }

        return resumen;
    }

    public static class EstadisticaEquipo {
        public int idEquipo;
        public String nombreEquipo;
        public String nombreProyecto;
        public int cantidadVisitas;
        public int cantidadEvaluaciones;
        public float promedio;
        public int lugar;
        public int numeroAlumnos;
    }

    public static class ResumenEquipo {
        public int idEquipo;
        public String nombreEquipo;
        public String nombreProyecto;
        public String descripcion;
        public int lugar;
        public int numeroAlumnos;
        public int cantidadVisitas;
        public int cantidadEvaluaciones;
        public float promedio;
        public String rutaCartel;
    }
}