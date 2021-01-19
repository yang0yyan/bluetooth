package com.cetc.myapplication.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.cetc.myapplication.MainActivity2;
import com.cetc.myapplication.PermissionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothLE {
    private static String TAG = "BluetoothLE";

    private static UUID X3DD_UUID = UUID.fromString("00001137-0000-1000-8000-00805F9B34FB");
    private static UUID X3DG_UUID = UUID.fromString("00001138-0000-1000-8000-00805F9B34FB");

    private static UUID SERVICE_UUID = UUID.fromString("00000001-0003-1000-8000-00805F9B34FB");
    private static UUID CHARA_UUID1 = UUID.fromString("00000001-0004-1000-8000-00805F9B34FB");
    private static UUID CHARA_UUID2 = UUID.fromString("00000001-0005-1000-8000-00805F9B34FB");

    Context context;
    MainActivity2 activity;
    private BluetoothAdapter bluetoothAdapter;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopLeScan();
        }
    };
    private BluetoothGatt bluetoothGatt;
    private BluetoothLeScanner bluetoothLeScanner;

    public BluetoothLE(Activity context) {
        this.context = context;
        activity = (MainActivity2) context;
        PermissionUtil.getPermissions(context, PermissionUtil.ACCESS_FINE_LOCATION);
        init();
    }

    private void init() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void startLeScan() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,10000);
        List<ScanFilter> list = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(X3DD_UUID))
                .build();
        list.add(scanFilter);
        ScanSettings scanSettings = new ScanSettings.Builder().build();
        bluetoothLeScanner.startScan(list,scanSettings,scanCallback);
    }

    public void stopLeScan() {
        bluetoothLeScanner.stopScan(scanCallback);
    }

    public void connectDevice(BluetoothDevice bluetoothDevice){
        bluetoothGatt = bluetoothDevice.connectGatt(context,false,bluetoothGattCallback);
    }

    public void writeData(){
        if(null==bluetoothGatt)return;
        BluetoothGattService service = bluetoothGatt.getService(SERVICE_UUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARA_UUID2);
        bluetoothGatt.setCharacteristicNotification(characteristic,true);
        characteristic.setValue("你好");
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            activity.setDevice(bluetoothDevice);
            if(bluetoothDevice.getName().equals("3DD")){
                if(result.getDataStatus()==ScanResult.DATA_COMPLETE){
                    Log.d(TAG, "onScanResult: 数据完整");
                    ScanRecord scanRecord = result.getScanRecord();//广播和扫描响应的组合
                    if(null==scanRecord)return;
                    byte[] bytes1 = scanRecord.getManufacturerSpecificData(1);
                    byte[] bytes2 = scanRecord.getManufacturerSpecificData(2);
                    Log.d(TAG, "onScanResult: 1 "+ Arrays.toString(bytes1));
                    Log.d(TAG, "onScanResult: 2 "+ Arrays.toString(bytes2));
                }else if(result.getDataStatus()==ScanResult.DATA_TRUNCATED){
                    Log.d(TAG, "onScanResult: 数据不完整");
                }

            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "onConnectionStateChange: 连接");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "onConnectionStateChange: 断开");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status==BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "onServicesDiscovered: 服务发现");
                for(BluetoothGattService bluetoothGattService : gatt.getServices()){
//                    for(BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()){
//                        for(BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()){
//
//                        }
//                    }
                }
            }
            Log.d(TAG, "onServicesDiscovered: ");
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            Log.d(TAG, "onPhyUpdate: ");
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            Log.d(TAG, "onPhyRead: ");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead: ");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite: ");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onCharacteristicChanged: 接收到服务端写入的数据");
            String data = new String(characteristic.getValue());
            activity.setTvShow(data);

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead: ");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite: ");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(TAG, "onReliableWriteCompleted: ");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(TAG, "onReadRemoteRssi: ");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.d(TAG, "onMtuChanged: ");
        }
    };

    public void close(){
        handler.removeCallbacks(runnable);
        stopLeScan();
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }
}
