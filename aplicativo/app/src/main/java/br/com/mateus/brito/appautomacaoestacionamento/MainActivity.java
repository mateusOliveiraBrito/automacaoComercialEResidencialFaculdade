package br.com.mateus.brito.appautomacaoestacionamento;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ADDRESS = "98:D3:21:F8:38:19";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Handler handler;
    private byte[] buffer = new byte[1024];
    private int bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnQrCode = (Button) findViewById(R.id.scanQrButton);
        btnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Escaneie o QR Code para entrar no estacionamento");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = new Handler();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            LoadingAlert loadingAlert = new LoadingAlert(this);
            loadingAlert.startAlertDialog();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingAlert.closeAlertDialog();

                    if (result.getContents() == null) {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                        connectToDevice(result.getContents());
                    }
                }
            }, 2500);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToDevice(String address) {
        BluetoothDevice device = this.bluetoothAdapter.getRemoteDevice(address);
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();
                return;
            }
            this.bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            this.bluetoothSocket.connect();
            this.outputStream = bluetoothSocket.getOutputStream();
            this.inputStream = bluetoothSocket.getInputStream();
            sendData('A');
            listenForData();
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Bluetooth connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData(char data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForData() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        bytes = inputStream.read(buffer);
                        handler.post(new Runnable() {
                            public void run() {
                                char data = (char) buffer[0];
                                Toast.makeText(MainActivity.this, "Arduino: " + data, Toast.LENGTH_SHORT).show();
                                //USAR O 'data' para decidir o que fazer com o que receber do arduino
                                //exibir se tem vagas
                                //desligar a conexão com o bluetooth
                            }
                        });
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        }).start();
    }
}