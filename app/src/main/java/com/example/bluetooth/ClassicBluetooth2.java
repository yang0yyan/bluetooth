package com.cetc.myapplication.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import com.cetc.myapplication.MainActivity;
import com.cetc.myapplication.PermissionUtil;

import java.io.IOException;
import java.util.UUID;

public class ClassicBluetooth2 {
    private static UUID BASE_UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
    private static UUID X3DD_UUID = UUID.fromString("00001137-0000-1000-8000-00805F9B34FB");
    private static UUID X3DG_UUID = UUID.fromString("00001138-0000-1000-8000-00805F9B34FB");

    Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    MainActivity activity;

    public ClassicBluetooth2(Activity context) {
        this.context = context;
        activity = (MainActivity) context;
        PermissionUtil.getPermissions(context, PermissionUtil.ACCESS_FINE_LOCATION);
        init();
    }

    private void init() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void startBroad() {
        //bluetoothAdapter.setName("3DD");
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
        thread.start();
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                BluetoothServerSocket bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("3DD", ClassicBluetooth2.X3DD_UUID);
                bluetoothSocket = bluetoothServerSocket.accept();
                bluetoothServerSocket.close();
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    });

    public void close() {
        if (null != bluetoothSocket) {
            try {
                bluetoothSocket.close();
                bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
