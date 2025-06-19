package com.example.unitylab_expoconfig.ui.cartel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.services.QRScannerService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        // Configurar escáner QR
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanea el código QR del cartel");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String qrContent = result.getContents();
                procesarResultadoQR(qrContent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void procesarResultadoQR(String qrContent) {
        // Enviar al servicio para procesar
        Intent serviceIntent = new Intent(this, QRScannerService.class);
        serviceIntent.setAction(QRScannerService.ACTION_PROCESS_QR);
        serviceIntent.putExtra(QRScannerService.EXTRA_QR_DATA, qrContent);
        startService(serviceIntent);

        Toast.makeText(this, "Procesando código QR...", Toast.LENGTH_SHORT).show();

        // Regresar a la actividad anterior
        finish();
    }
}