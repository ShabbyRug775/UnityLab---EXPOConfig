package com.example.unitylab_expoconfig.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.example.unitylab_expoconfig.SQLite.DbmsSQLiteHelper;
import com.example.unitylab_expoconfig.SQLite.EquipoBD;
import com.example.unitylab_expoconfig.SQLite.ProyectoBD;
import com.example.unitylab_expoconfig.SQLite.ProfesorBD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CartelGenerator {

    private static final String TAG = "CartelGenerator";
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int QR_SIZE = 120;

    private Context context;
    private DbmsSQLiteHelper dbHelper;

    public CartelGenerator(Context context) {
        this.context = context;
        this.dbHelper = new DbmsSQLiteHelper(context);
    }

    /**
     * Genera el cartel completo para un equipo específico
     */
    public CartelResult generarCartel(int idEquipo) {
        try {
            // Obtener información del equipo y proyecto relacionado
            EquipoInfo equipoInfo = obtenerInfoEquipo(idEquipo);
            if (equipoInfo == null) {
                return new CartelResult(false, "No se encontró información del equipo");
            }

            // Generar QR code
            String qrData = generarDataQR(idEquipo);
            Bitmap qrBitmap = generarQRCode(qrData);

            // Crear el XML del cartel
            String xmlCartel = generarXMLCartel(equipoInfo, qrData);

            // Convertir a PDF
            String pdfPath = convertirToPDF(equipoInfo, qrBitmap);

            // Actualizar estado en base de datos
            actualizarEstadoCartel(idEquipo, pdfPath);

            return new CartelResult(true, "Cartel generado exitosamente", pdfPath, xmlCartel);

        } catch (Exception e) {
            Log.e(TAG, "Error generando cartel: " + e.getMessage(), e);
            return new CartelResult(false, "Error al generar cartel: " + e.getMessage());
        }
    }

    /**
     * Obtiene la información del equipo y proyecto desde la base de datos
     */
    private EquipoInfo obtenerInfoEquipo(int idEquipo) {
        EquipoInfo info = null;
        Cursor cursorEquipo = null;
        Cursor cursorProyecto = null;
        Cursor cursorProfesor = null;

        try {
            // Obtener información del equipo
            cursorEquipo = dbHelper.obtenerEquipoPorId(idEquipo);

            if (cursorEquipo != null && cursorEquipo.moveToFirst()) {
                info = new EquipoInfo();
                info.idEquipo = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_ID_EQUIPO));
                info.nombreEquipo = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                info.nombreProyecto = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));
                info.descripcionCorta = cursorEquipo.getString(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_DESCRIPCION));
                info.lugar = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_LUGAR));
                info.numeroAlumnos = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_NUMERO_ALUMNOS));
                info.cantEvaluaciones = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_CANT_EVAL));
                info.promedio = cursorEquipo.getFloat(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_PROMEDIO));
                info.cantVisitas = cursorEquipo.getInt(cursorEquipo.getColumnIndexOrThrow(EquipoBD.COL_CANT_VISITAS));

                // Buscar proyecto relacionado por nombre (ya que no hay FK directa)
                cursorProyecto = dbHelper.buscarProyectosPorNombre(info.nombreProyecto);
                if (cursorProyecto != null && cursorProyecto.moveToFirst()) {
                    info.descripcionProyecto = cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_DESCRIPCION));
                    //info.fechaCreacion = cursorProyecto.getString(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_FECHA_CREACION));

                    int idProfesor = cursorProyecto.getInt(cursorProyecto.getColumnIndexOrThrow(ProyectoBD.COL_NUMERO_EMPLEADO_PROFESOR));

                    // Obtener información del profesor
                    cursorProfesor = dbHelper.obtenerProfesorPorNumeroEmpleado(idProfesor);
                    if (cursorProfesor != null && cursorProfesor.moveToFirst()) {
                        String nombreProfesor = cursorProfesor.getString(cursorProfesor.getColumnIndexOrThrow("Nombre"));
                        String apellidosProfesor = cursorProfesor.getString(cursorProfesor.getColumnIndexOrThrow("Apellido1"));
                        info.nombreProfesor = nombreProfesor + " " + (apellidosProfesor != null ? apellidosProfesor : "");
                    }
                }

                // Establecer valores por defecto para el evento (como no hay tabla de eventos)
                info.nombreEvento = "Exposición de Proyectos " + info.carrera;
                info.ubicacionEvento = "Campus ESCOM-IPN";
                info.fechaEvento = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener información del equipo: " + e.getMessage(), e);
        } finally {
            if (cursorEquipo != null) cursorEquipo.close();
            if (cursorProyecto != null) cursorProyecto.close();
            if (cursorProfesor != null) cursorProfesor.close();
        }

        return info;
    }

    /**
     * Genera los datos para el código QR
     */
    private String generarDataQR(int idEquipo) {
        // El QR contendrá información para identificar al equipo e incrementar visitas
        return "EXPOCONFIG_EQUIPO:" + idEquipo + ":VISIT:" + System.currentTimeMillis();
    }

    /**
     * Genera el código QR como Bitmap
     */
    private Bitmap generarQRCode(String data) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bitmap;
    }

    /**
     * Genera el XML del cartel con la plantilla predeterminada
     */
    private String generarXMLCartel(EquipoInfo info, String qrData) {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<cartel xmlns:android=\"http://schemas.android.com/apk/res/android\">\n");
        xml.append("  <!-- Plantilla Cartel ExpoConfig -->\n");
        xml.append("  <template version=\"1.0\" type=\"expo_cartel\">\n");

        // Header del evento
        xml.append("    <header>\n");
        xml.append("      <evento>\n");
        xml.append("        <nombre>").append(escapeXml(info.nombreEvento)).append("</nombre>\n");
        xml.append("        <ubicacion>").append(escapeXml(info.ubicacionEvento)).append("</ubicacion>\n");
        xml.append("        <fecha>").append(info.fechaEvento).append("</fecha>\n");
        xml.append("        <carrera>").append(escapeXml(info.carrera)).append("</carrera>\n");
        xml.append("      </evento>\n");
        xml.append("    </header>\n");

        // Contenido principal
        xml.append("    <contenido>\n");
        xml.append("      <proyecto>\n");
        xml.append("        <nombre>").append(escapeXml(info.nombreProyecto)).append("</nombre>\n");
        xml.append("        <descripcion>").append(escapeXml(info.descripcionProyecto)).append("</descripcion>\n");
        xml.append("        <materia>").append(escapeXml(info.materia)).append("</materia>\n");
        xml.append("        <grupo>").append(escapeXml(info.grupo)).append("</grupo>\n");
        xml.append("        <semestre>").append(escapeXml(info.semestre)).append("</semestre>\n");
        xml.append("        <herramientas>").append(escapeXml(info.herramientas)).append("</herramientas>\n");
        xml.append("        <arquitectura>").append(escapeXml(info.arquitectura)).append("</arquitectura>\n");
        xml.append("        <funciones>").append(escapeXml(info.funciones)).append("</funciones>\n");
        xml.append("      </proyecto>\n");
        xml.append("      <equipo>\n");
        xml.append("        <nombre>").append(escapeXml(info.nombreEquipo)).append("</nombre>\n");
        xml.append("        <numero_alumnos>").append(info.numeroAlumnos).append("</numero_alumnos>\n");
        xml.append("        <lugar>").append(info.lugar).append("</lugar>\n");
        xml.append("        <profesor>").append(escapeXml(info.nombreProfesor)).append("</profesor>\n");
        xml.append("      </equipo>\n");
        xml.append("    </contenido>\n");

        // Footer con QR
        xml.append("    <footer>\n");
        xml.append("      <qr_code>\n");
        xml.append("        <data>").append(escapeXml(qrData)).append("</data>\n");
        xml.append("        <position>bottom_right</position>\n");
        xml.append("        <size>120x120</size>\n");
        xml.append("      </qr_code>\n");
        xml.append("      <estadisticas>\n");
        xml.append("        <visitas>").append(info.cantVisitas).append("</visitas>\n");
        xml.append("        <evaluaciones>").append(info.cantEvaluaciones).append("</evaluaciones>\n");
        xml.append("        <promedio>").append(info.promedio).append("</promedio>\n");
        xml.append("      </estadisticas>\n");
        xml.append("      <generado>").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("</generado>\n");
        xml.append("    </footer>\n");

        xml.append("  </template>\n");
        xml.append("</cartel>");

        return xml.toString();
    }

    /**
     * Convierte el cartel a PDF
     */
    private String convertirToPDF(EquipoInfo info, Bitmap qrBitmap) throws IOException {
        // Crear documento PDF
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Configurar estilos de texto
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(26);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(0xFF1976D2); // Azul

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(18);
        headerPaint.setFakeBoldText(true);
        headerPaint.setColor(0xFF424242); // Gris oscuro

        Paint bodyPaint = new Paint();
        bodyPaint.setTextSize(14);
        bodyPaint.setColor(0xFF616161); // Gris

        Paint smallPaint = new Paint();
        smallPaint.setTextSize(12);
        smallPaint.setColor(0xFF757575); // Gris claro

        // Dibujar contenido del cartel
        float y = 50;

        // Header - Información del evento
        canvas.drawText(info.nombreEvento != null ? info.nombreEvento : "Exposición de Proyectos", 50, y, titlePaint);
        y += 30;
        canvas.drawText(info.ubicacionEvento != null ? info.ubicacionEvento : "Campus ESCOM", 50, y, headerPaint);
        y += 25;
        canvas.drawText("Fecha: " + (info.fechaEvento != null ? info.fechaEvento : "Por definir"), 50, y, bodyPaint);
        y += 20;
        canvas.drawText("Carrera: " + (info.carrera != null ? info.carrera : ""), 50, y, bodyPaint);
        y += 35;

        // Línea separadora
        canvas.drawLine(50, y, PAGE_WIDTH - 50, y, paint);
        y += 30;

        // Información del proyecto
        canvas.drawText("PROYECTO:", 50, y, headerPaint);
        y += 25;
        canvas.drawText(info.nombreProyecto != null ? info.nombreProyecto : "Sin nombre", 50, y, bodyPaint);
        y += 20;

        if (info.descripcionProyecto != null && !info.descripcionProyecto.isEmpty()) {
            canvas.drawText("Descripción:", 50, y, headerPaint);
            y += 20;
            dibujarTextoMultilinea(canvas, info.descripcionProyecto, 50, y, bodyPaint, PAGE_WIDTH - 100);
            y += calcularAlturaTexto(info.descripcionProyecto, PAGE_WIDTH - 100) + 20;
        }

        // Información del equipo en dos columnas
        float columna1X = 50;
        float columna2X = PAGE_WIDTH / 2;
        float yColumnas = y;

        // Columna 1 - Información del equipo
        canvas.drawText("EQUIPO:", columna1X, yColumnas, headerPaint);
        yColumnas += 20;
        canvas.drawText("Nombre: " + (info.nombreEquipo != null ? info.nombreEquipo : "Sin nombre"), columna1X, yColumnas, bodyPaint);
        yColumnas += 18;
        canvas.drawText("Integrantes: " + info.numeroAlumnos, columna1X, yColumnas, bodyPaint);
        yColumnas += 18;
        canvas.drawText("Ubicación: " + (info.lugar > 0 ? "Mesa " + info.lugar : "Por asignar"), columna1X, yColumnas, bodyPaint);
        yColumnas += 18;
        canvas.drawText("Grupo: " + (info.grupo != null ? info.grupo : ""), columna1X, yColumnas, bodyPaint);
        yColumnas += 18;
        canvas.drawText("Semestre: " + (info.semestre != null ? info.semestre : ""), columna1X, yColumnas, bodyPaint);

        // Columna 2 - Información técnica
        yColumnas = y;
        canvas.drawText("DETALLES TÉCNICOS:", columna2X, yColumnas, headerPaint);
        yColumnas += 20;
        canvas.drawText("Materia: " + (info.materia != null ? info.materia : ""), columna2X, yColumnas, smallPaint);
        yColumnas += 16;
        if (info.herramientas != null && !info.herramientas.isEmpty()) {
            canvas.drawText("Herramientas:", columna2X, yColumnas, smallPaint);
            yColumnas += 14;
            dibujarTextoMultilinea(canvas, info.herramientas, columna2X, yColumnas, smallPaint, PAGE_WIDTH / 2 - 75);
            yColumnas += calcularAlturaTexto(info.herramientas, PAGE_WIDTH / 2 - 75) + 10;
        }
        if (info.arquitectura != null && !info.arquitectura.isEmpty()) {
            canvas.drawText("Arquitectura:", columna2X, yColumnas, smallPaint);
            yColumnas += 14;
            dibujarTextoMultilinea(canvas, info.arquitectura, columna2X, yColumnas, smallPaint, PAGE_WIDTH / 2 - 75);
        }

        // Profesor responsable
        y = Math.max(yColumnas + 40, y + 150);
        canvas.drawText("Profesor: " + (info.nombreProfesor != null ? info.nombreProfesor : "No asignado"), 50, y, bodyPaint);

        // Dibujar QR en esquina inferior derecha
        if (qrBitmap != null) {
            float qrX = PAGE_WIDTH - QR_SIZE - 50;
            float qrY = PAGE_HEIGHT - QR_SIZE - 80;
            canvas.drawBitmap(qrBitmap, qrX, qrY, paint);

            // Texto explicativo del QR
            canvas.drawText("Escanea para evaluar", qrX - 20, qrY - 10, smallPaint);

            // Estadísticas junto al QR
            float statsY = qrY;
            canvas.drawText("Estadísticas:", 50, statsY, smallPaint);
            statsY += 16;
            canvas.drawText("Visitas: " + info.cantVisitas, 50, statsY, smallPaint);
            statsY += 14;
            canvas.drawText("Evaluaciones: " + info.cantEvaluaciones, 50, statsY, smallPaint);
            if (info.cantEvaluaciones > 0) {
                statsY += 14;
                canvas.drawText("Promedio: " + String.format("%.1f", info.promedio), 50, statsY, smallPaint);
            }
        }

        document.finishPage(page);

        // ✅ NUEVO: Guardar PDF en almacenamiento privado de la aplicación (SIN PERMISOS)
        String fileName = "cartel_" +
                (info.nombreEquipo != null ? info.nombreEquipo.replaceAll("[^a-zA-Z0-9]", "_") : "equipo") +
                "_" + System.currentTimeMillis() + ".pdf";

        // Usar directorio privado de la aplicación para documentos
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Carteles");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File pdfFile = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            document.writeTo(fos);
            document.close();

            Log.d(TAG, "Cartel guardado en: " + pdfFile.getAbsolutePath());
            return pdfFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error al guardar cartel: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Dibuja texto que puede ocupar múltiples líneas
     */
    private void dibujarTextoMultilinea(Canvas canvas, String texto, float x, float y, Paint paint, float maxWidth) {
        if (texto == null || texto.isEmpty()) return;

        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();
        float lineHeight = paint.getTextSize() + 4;

        for (String palabra : palabras) {
            String lineaConPalabra = lineaActual.length() > 0 ? lineaActual + " " + palabra : palabra;
            if (paint.measureText(lineaConPalabra) <= maxWidth) {
                lineaActual = new StringBuilder(lineaConPalabra);
            } else {
                if (lineaActual.length() > 0) {
                    canvas.drawText(lineaActual.toString(), x, y, paint);
                    y += lineHeight;
                    lineaActual = new StringBuilder(palabra);
                } else {
                    canvas.drawText(palabra, x, y, paint);
                    y += lineHeight;
                }
            }
        }
        if (lineaActual.length() > 0) {
            canvas.drawText(lineaActual.toString(), x, y, paint);
        }
    }

    /**
     * Calcula la altura necesaria para dibujar un texto multilinea
     */
    private float calcularAlturaTexto(String texto, float maxWidth) {
        if (texto == null || texto.isEmpty()) return 0;

        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();
        int lineas = 0;
        Paint tempPaint = new Paint();
        tempPaint.setTextSize(14);

        for (String palabra : palabras) {
            String lineaConPalabra = lineaActual.length() > 0 ? lineaActual + " " + palabra : palabra;
            if (tempPaint.measureText(lineaConPalabra) <= maxWidth) {
                lineaActual = new StringBuilder(lineaConPalabra);
            } else {
                lineas++;
                lineaActual = new StringBuilder(palabra);
            }
        }
        if (lineaActual.length() > 0) {
            lineas++;
        }

        return lineas * (tempPaint.getTextSize() + 4);
    }

    /**
     * Actualiza el estado del cartel en la base de datos
     */
    private void actualizarEstadoCartel(int idEquipo, String rutaPdf) {
        try {
            // Obtener datos actuales del equipo
            Cursor cursor = dbHelper.obtenerEquipoPorId(idEquipo);
            if (cursor != null && cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE));
                String nombreProyecto = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_NOMBRE_PROYECTO));
                int numAlumnos = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_NUMERO_ALUMNOS));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_DESCRIPCION));
                String lugar = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_LUGAR));
                int cantEval = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_CANT_EVAL));
                float promedio = cursor.getFloat(cursor.getColumnIndexOrThrow(EquipoBD.COL_PROMEDIO));
                int cantVisitas = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_CANT_VISITAS));
                String turno = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_TURNO));
                int idproyecto = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_ID_PROYECTO));
                int boletalider = cursor.getInt(cursor.getColumnIndexOrThrow(EquipoBD.COL_BOLETA_LIDER));
                String estadoquipo = cursor.getString(cursor.getColumnIndexOrThrow(EquipoBD.COL_ESTADO_EQUIPO));


                // Actualizar con la nueva ruta del cartel
                dbHelper.actualizarEquipo(idEquipo, nombre, nombreProyecto, numAlumnos,
                        descripcion, turno,lugar, rutaPdf, cantEval, promedio, cantVisitas,idproyecto,boletalider,estadoquipo);
            }
            if (cursor != null) cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar estado del cartel: " + e.getMessage(), e);
        }
    }

    /**
     * Incrementa el contador de visitas cuando se escanea el QR
     */
    public static void incrementarVisitas(Context context, int idEquipo) {
        try {
            DbmsSQLiteHelper dbHelper = new DbmsSQLiteHelper(context);
            dbHelper.incrementarVisitasEquipo(idEquipo);

            Log.i(TAG, "Visitas incrementadas para equipo: " + idEquipo);
        } catch (Exception e) {
            Log.e(TAG, "Error al incrementar visitas: " + e.getMessage(), e);
        }
    }

    /**
     * Procesa el escaneo de QR y extrae el ID del equipo
     */
    public static int procesarQRScaneo(String qrData) {
        try {
            if (qrData.startsWith("EXPOCONFIG_EQUIPO:")) {
                String[] partes = qrData.split(":");
                if (partes.length >= 2) {
                    return Integer.parseInt(partes[1]);
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parseando QR data: " + qrData, e);
        }
        return -1;
    }

    /**
     * Escapa caracteres especiales para XML
     */
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // Clases de datos actualizadas
    public static class EquipoInfo {
        public int idEquipo;
        public String nombreEquipo;
        public String nombreProyecto;
        public String descripcionCorta;
        public String descripcionProyecto;
        public int lugar;
        public int numeroAlumnos;
        public int cantEvaluaciones;
        public float promedio;
        public int cantVisitas;

        // Datos del proyecto relacionado
        public String materia;
        public String grupo;
        public String semestre;
        public String carrera;
        public String herramientas;
        public String arquitectura;
        public String funciones;
        public String fechaCreacion;
        public String nombreProfesor;

        // Datos del evento (valores por defecto)
        public String nombreEvento;
        public String ubicacionEvento;
        public String fechaEvento;
    }

    public static class CartelResult {
        public boolean exito;
        public String mensaje;
        public String rutaPdf;
        public String xmlCartel;

        public CartelResult(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public CartelResult(boolean exito, String mensaje, String rutaPdf, String xmlCartel) {
            this.exito = exito;
            this.mensaje = mensaje;
            this.rutaPdf = rutaPdf;
            this.xmlCartel = xmlCartel;
        }
    }
}