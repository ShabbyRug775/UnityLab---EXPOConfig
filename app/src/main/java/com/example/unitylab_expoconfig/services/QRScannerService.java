package com.example.unitylab_expoconfig.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.unitylab_expoconfig.utils.CartelGenerator;

public class QRScannerService extends Service {

    private static final String TAG = "QRScannerService";
    public static final String ACTION_PROCESS_QR = "com.example.unitylab_expoconfig.PROCESS_QR";
    public static final String EXTRA_QR_DATA = "qr_data";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_PROCESS_QR.equals(intent.getAction())) {
            String qrData = intent.getStringExtra(EXTRA_QR_DATA);
            procesarQRCode(qrData);
        }

        return START_NOT_STICKY;
    }

    private void procesarQRCode(String qrData) {
        Log.d(TAG, "Procesando QR: " + qrData);

        try {
            int equipoId = CartelGenerator.procesarQRScaneo(qrData);

            if (equipoId > 0) {
                // Incrementar contador de visitas usando el método actualizado
                CartelGenerator.incrementarVisitas(this, equipoId);

                // Enviar broadcast para notificar la actualización
                Intent broadcastIntent = new Intent("com.example.unitylab_expoconfig.VISITA_REGISTRADA");
                broadcastIntent.putExtra("equipo_id", equipoId);
                sendBroadcast(broadcastIntent);

                Log.i(TAG, "Visita registrada exitosamente para equipo: " + equipoId);
            } else {
                Log.w(TAG, "QR inválido o no reconocido: " + qrData);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error procesando QR: " + qrData, e);
        }

        stopSelf();
    }
}