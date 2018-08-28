package com.mesearment.com;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import com.mesearment.com.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

public class MainActivity extends Activity {
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    Bluetooth bluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = new Bluetooth(this);

        Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                }
            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException ex) {
                }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        } else {


            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("MattsBlueTooth")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            myLabel.setText("Bluetooth Device Found");
        }
    }

    void openBT() throws IOException {
     /*   UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
*/blueoothConnect2();

        myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            myLabel.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException {
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }

    void blueoothConnect2() {

        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
        }

        if (!bluetooth.isConnected() && bluetooth.isEnabled()) {
            bluetooth.startScanning();
        }
        bluetooth.setBluetoothCallback(new BluetoothCallback() {
            @Override
            public void onBluetoothTurningOn() {

                Log.e("Devices ", "onBluetoothTurningOn");

            }

            @Override
            public void onBluetoothOn() {
                Log.e("Devices ", "onBluetoothOn");

                bluetooth.startScanning();
            }

            @Override
            public void onBluetoothTurningOff() {
            }

            @Override
            public void onBluetoothOff() {
            }

            @Override
            public void onUserDeniedActivation() {

                // when using bluetooth.showEnableDialog()
                // you will also have to call bluetooth.onActivityResult()
            }
        });

        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {
                Log.e("Devices ", "onDiscoveryStarted");

            }

            @Override
            public void onDiscoveryFinished() {
                Log.e("Devices ", "onDiscoveryFinished");

                List<BluetoothDevice> devices = bluetooth.getPairedDevices();
                for (BluetoothDevice device : devices) {

                    Log.e("Device Found ", device.getName());
                    if (device.getName().contentEquals("HC-05"))
                        bluetooth.connectToDevice(device);
                }
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                List<BluetoothDevice> devices = bluetooth.getPairedDevices();
                Log.e("Devices ", device.getName());
                bluetooth.pair(device);

            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {

                Log.e("Devices ", "onDevicePaired");


            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {
                Log.e("Devices ", "onDeviceUnpaired");

            }

            @Override
            public void onError(String message) {

                Log.e("Devices ", "onError");

            }
        });
        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                Log.e("Devices ", "onDeviceConnected");
                beginListenForData();

            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device, String message) {
                Log.e("Devices ", "onDeviceDisconnected");

            }

            @Override
            public void onMessage(String message) {
                Log.e("Devices ", "onMessage");

            }

            @Override
            public void onError(String message) {
                Log.e("Devices ", "onError");

            }

            @Override
            public void onConnectError(BluetoothDevice device, String message) {
                Log.e("Devices ", "onConnectError");

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        bluetooth.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}