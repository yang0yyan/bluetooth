package com.cetc.myapplication.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.cetc.myapplication.MainActivity;
import com.cetc.myapplication.PermissionUtil;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ClassicBluetooth {

    private static UUID BASE_UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
    private static UUID X3DD_UUID = UUID.fromString("00001137-0000-1000-8000-00805F9B34FB");

    Context context;
    private BluetoothAdapter bluetoothAdapter;
    MainActivity activity;
    private BluetoothSocket bluetoothSocket;

    public ClassicBluetooth(Activity context) {
        this.context = context;
        activity = (MainActivity) context;
        PermissionUtil.getPermissions(context, PermissionUtil.ACCESS_FINE_LOCATION);
        init();
    }

    private void init() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bluetoothDevices) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);
    }

    public void startDiscovery() {
        bluetoothAdapter.getState();
        bluetoothAdapter.cancelDiscovery();
        boolean isSuccess = bluetoothAdapter.startDiscovery();
        Toast.makeText(context, isSuccess ? "成功" : "失败", Toast.LENGTH_SHORT).show();
    }

    public void connectDevice(BluetoothDevice bluetoothDevice) {
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(X3DD_UUID);
            bluetoothSocket.connect();
            bluetoothAdapter.cancelDiscovery();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
                activity.setDevice(device);
            }
        }
    };

    public void close() {
        context.unregisterReceiver(receiver);
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
