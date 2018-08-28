package com.mesearment.com;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.security.auth.login.LoginException;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;
import io.realm.Realm;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

public class HomeActivity extends AppCompatActivity {
    String Unit = "";
    String dataBT = "";
    Button cent;
    StringBuilder stringBuilder;
    Button meterr;
    Button feett;
    SharedPreferences sharedpreferences;
    boolean isPlaying = false;
    boolean isFirst = false;
    Timer timer;
    MediaPlayer mp;
    MediaPlayer mpp;

    int unit = 0;
    Timer t;
    TextView dataMonitor;
    Bluetooth bluetooth;
    private SmoothBluetooth mSmoothBluetooth;
    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            //device does not support bluetooth

            Log.e("Bluetooth", "onBluetoothNotSupported");
        }

        @Override
        public void onBluetoothNotEnabled() {
            //bluetooth is disabled, probably call Intent request to enable bluetooth
            Log.e("Bluetooth", "onBluetoothNotEnabled");

        }

        @Override
        public void onConnecting(Device device) {
            //called when connecting to particular device

            Log.e("Bluetooth", "onConnecting");

        }

        @Override
        public void onConnected(Device device) {
            //called when connected to particular device
            Log.e("Bluetooth", "onConnected");

        }

        @Override
        public void onDisconnected() {

            Log.e("Bluetooth", "onDisconnected");

            //called when disconnected from device
        }

        @Override
        public void onConnectionFailed(Device device) {

            Log.e("Bluetooth", "onConnectionFailed");

            //called when connection failed to particular device
        }

        @Override
        public void onDiscoveryStarted() {
            Log.e("Bluetooth", "onDiscoveryStarted");

            //called when discovery is started
        }

        @Override
        public void onDiscoveryFinished() {

            Log.e("Bluetooth", "onDiscoveryFinished");

            //called when discovery is finished
        }

        @Override
        public void onNoDevicesFound() {
            Log.e("Bluetooth", "onNoDevicesFound");


            //called when no devices found
        }

        @Override
        public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
            Log.e("Bluetooth", "onDevicesFound" + deviceList.toString());

            for (Device device : deviceList) {
                if (device.getName().equalsIgnoreCase("HC-05")) {
                    connectionCallback.connectTo(device);

                    Log.e("Conected ", device.getName());
                }
            }

        }

        @Override
        public void onDataReceived(final int data) {
            //receives all bytes

            stringBuilder.append((char) data);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    try {
                        // Log.e("onDataReceived ", " " + stringBuilder.toString());
                        if (!stringBuilder.toString().trim().isEmpty()) {
                            showData(Integer.parseInt(stringBuilder.toString().trim()));
                        }


                        if (sharedpreferences.getBoolean("isSmart", false)) {
                            if (!sharedpreferences.getString("sdData", "").isEmpty()) {

                                Log.e(" IS SMart ", sharedpreferences.getBoolean("isSmart", false) + "");

                                String dataSm = sharedpreferences.getString("sdData", "");
                                int compSM = Integer.parseInt(dataSm.trim());
                                if (!stringBuilder.toString().trim().isEmpty()) {
                                    int valueSM = Integer.parseInt(stringBuilder.toString().trim());
                                    Log.e(" IS SMart ", (valueSM == compSM) + "   " + compSM + " " + " " + valueSM);

                                    if (valueSM > compSM) {

                                        if (!mp.isPlaying()) {
                                            mp.start();
                                        }
                                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                mediaPlayer.seekTo(0);

                                            }
                                        });
                                    } else if (valueSM == compSM) {

                                        if (!mp.isPlaying()) {
                                            mpp.start();
                                        }
                                        mpp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                mediaPlayer.seekTo(0);

                                            }
                                        });
                                    }

                                }

                            }
                        }

                        stringBuilder.setLength(0);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
           /* if (timer == null) {
                timer = new Timer();
            }
            if (!isFirst) {
                isFirst = true;


                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showData(Double.parseDouble(dataBT));
                                isFirst = false;

                            }
                        });
                    }
                }, 0, 700);
            }*/


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        stringBuilder = new StringBuilder();
        dataMonitor = findViewById(R.id.textDataReading);
        bluetooth = new Bluetooth(this);

        if (mSmoothBluetooth == null) {
            mSmoothBluetooth = new SmoothBluetooth(this, mListener);
        }
        mSmoothBluetooth.setListener(mListener);
        sharedpreferences = getSharedPreferences("smartData", Context.MODE_PRIVATE);
        mp = MediaPlayer.create(HomeActivity.this, R.raw.beep1);
        mpp = MediaPlayer.create(HomeActivity.this, R.raw.bep);
        mSmoothBluetooth.tryConnection();
        mSmoothBluetooth.doDiscovery();


     /*   t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                double start = 100;
                double end = 300;
                double random = new Random().nextDouble();
                double result = start + (random * (end - start));

            }
        }, 0, 1000);
*/

        final Realm realm = Realm.getDefaultInstance();

/*        realm.beginTransaction();
        realm.where(RecordModel.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();*/


        findViewById(R.id.connectToDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.cancel();
            }
        });
        findViewById(R.id.unitChooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.choose_unit);
                final Button meter = dialog.findViewById(R.id.meter);
                final Button cm = dialog.findViewById(R.id.centemeter);
                final Button feet = dialog.findViewById(R.id.feet);

                dialog.show();
                feet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        feet.setBackgroundColor(Color.GREEN);
                        cm.setBackgroundColor(Color.WHITE);
                        meter.setBackgroundColor(Color.WHITE);

                        unit = 1;
                        dialog.dismiss();

                    }
                });
                cm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        feet.setBackgroundColor(Color.WHITE);
                        cm.setBackgroundColor(Color.GREEN);
                        meter.setBackgroundColor(Color.WHITE);

                        unit = 0;

                        dialog.dismiss();


                    }
                });
                meter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        feet.setBackgroundColor(Color.WHITE);
                        cm.setBackgroundColor(Color.WHITE);
                        meter.setBackgroundColor(Color.GREEN);
                        unit = 2;

                        dialog.dismiss();


                    }
                });
            }
        });


        findViewById(R.id.historyData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
            }
        });

        findViewById(R.id.saveData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.record_save);
                dialog.show();

                cent = dialog.findViewById(R.id.cem);
                meterr = dialog.findViewById(R.id.meter);
                feett = dialog.findViewById(R.id.feet);


                final EditText value = dialog.findViewById(R.id.value);
                final EditText notes = dialog.findViewById(R.id.notes);

                cent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Unit = "Centimeter";


                        feett.setBackgroundColor(Color.WHITE);
                        cent.setBackgroundColor(Color.GREEN);
                        meterr.setBackgroundColor(Color.WHITE);


                    }
                });
                meterr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Unit = "Meter";


                        feett.setBackgroundColor(Color.WHITE);
                        cent.setBackgroundColor(Color.WHITE);
                        meterr.setBackgroundColor(Color.GREEN);
                    }
                });
                feett.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Unit = "Feet";

                        feett.setBackgroundColor(Color.GREEN);
                        cent.setBackgroundColor(Color.WHITE);
                        meterr.setBackgroundColor(Color.WHITE);
                    }
                });

                dialog.findViewById(R.id.noteSave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (value.getText().toString().isEmpty() || notes.getText().toString().isEmpty() || Unit.isEmpty()) {
                            Toast.makeText(HomeActivity.this, "Something is missing!!", Toast.LENGTH_SHORT).show();
                        } else {
                            RecordModel recordModel = new RecordModel(notes.getText().toString(), value.getText().toString(), Unit);
                            realm.beginTransaction();
                            realm.copyToRealm(recordModel);
                            realm.commitTransaction();
                            Unit = "";
                            Toast.makeText(HomeActivity.this, "Saved Successfully !!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

            }
        });


        findViewById(R.id.smartMeasure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.smart_distance);

                dialog.show();

                final EditText editText = dialog.findViewById(R.id.smartDistanceET);


                dialog.findViewById(R.id.setSmartDistance).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String data = editText.getText().toString();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("sdData", data);
                        editor.putBoolean("isSmart", true);
                        editor.commit();

                        dialog.dismiss();

                    }
                });


            }
        });
        findViewById(R.id.connectToDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mSmoothBluetooth.isConnected()) {
                    mSmoothBluetooth.tryConnection();
                }
            }
        });
    }

    private void showData(final int item) {
        dataMonitor.setText(item + "");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (unit == 0) {
                    dataMonitor.setText(new DecimalFormat("##.##").format(item).toString() + "  CM");
                } else if (unit == 1) {
                    dataMonitor.setText(new DecimalFormat("##.##").format(cmToFeet(item)).toString() + "  Ft");

                } else if (unit == 2) {
                    dataMonitor.setText(new DecimalFormat("##.##").format(cmToMeter(item)).toString() + "  M");
                } else {
                    dataMonitor.setText(new DecimalFormat("##.##").format(item).toString() + "  CM");

                }

            }
        });


    }

    public double cmToFeet(int item) {

        return (item / 30.48);
    }

    public double cmToMeter(int item) {

        return (item / 100.00);
    }


    void blueoothConnect() {
        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class; // BluetoothClassicService.class or BluetoothLeService.class
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "HC-05";
        config.callListenersInMainThread = true;

// Bluetooth Classic
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Set null to find all devices on scan.

/*// Bluetooth LE
        config.uuidService = UUID.fromString("e7810a71-73ae-499d-8c15-faa9aef0c3f2");
        config.uuidCharacteristic = UUID.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f");*/
        // config.transport = BluetoothDevice.TRANSPORT_LE; // Only for dual-mode devices

        BluetoothService.init(config);

        final BluetoothService service = BluetoothService.getDefaultInstance();


        service.setOnScanCallback(new BluetoothService.OnBluetoothScanCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
                if (device.getName().contentEquals("HC-05")) {
                    service.connect(device); // See also service.disconnect();
                }
                Log.e(" Device List ", device.getUuids().toString() + " " + device.getName());

            }

            @Override
            public void onStartScan() {
            }

            @Override
            public void onStopScan() {
            }
        });

        service.startScan();


        service.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {
            @Override
            public void onDataRead(byte[] buffer, int length) {


                Log.e(" Read Data ", buffer.toString());
            }

            @Override
            public void onStatusChange(BluetoothStatus status) {
            }

            @Override
            public void onDeviceName(String deviceName) {


                Log.e(" On Device Name ", deviceName);
            }

            @Override
            public void onToast(String message) {

                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataWrite(byte[] buffer) {

                Log.e(" Write ", buffer.toString());
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
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("sdData", "");
        editor.putBoolean("isSmart", false);
        editor.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("sdData", "");
        editor.putBoolean("isSmart", false);
        editor.commit();
        System.exit(0);
    }
}
