package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnScan;
    private Button btnBroad;
    private ClassicBluetooth classicBluetooth;
    private ClassicBluetooth2 classicBluetooth2;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private MyBaseAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    public void initView(){
        btnScan = findViewById(R.id.btn_scan);
        btnBroad = findViewById(R.id.btn_broad);
        listView = findViewById(R.id.lv);
        btnScan.setOnClickListener(this);
        btnBroad.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position+"", Toast.LENGTH_SHORT).show();
                BluetoothDevice bluetoothDevice = deviceList.get(position);
                classicBluetooth.connectDevice(bluetoothDevice);
            }
        });
    }

    private void initData() {
        classicBluetooth = new ClassicBluetooth(this);
        classicBluetooth2 = new ClassicBluetooth2(this);
        adapter = new MyBaseAdapter(deviceList,this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                deviceList.clear();
                classicBluetooth.startDiscovery();
                break;
            case R.id.btn_broad:
                classicBluetooth2.startBroad();
                break;
        }
    }

    public void setDevice(final BluetoothDevice bluetoothDevice){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!deviceList.contains(bluetoothDevice)){
                    deviceList.add(bluetoothDevice);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private static class MyBaseAdapter extends BaseAdapter{
        private List<BluetoothDevice> deviceList;
        private Context context;
        public MyBaseAdapter(List<BluetoothDevice> deviceList, Context context) {
            this.deviceList = deviceList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaseHolder baseHolder = null;
            if(null==convertView){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_bluetooth,null);
                baseHolder = new BaseHolder();
                baseHolder.tvName = convertView.findViewById(R.id.name);
                baseHolder.tvAddress = convertView.findViewById(R.id.address);
                convertView.setTag(baseHolder);
            }else{
                baseHolder = (BaseHolder) convertView.getTag();
            }
            baseHolder.tvName.setText(deviceList.get(position).getName());
            baseHolder.tvAddress.setText(deviceList.get(position).getAddress());
            return convertView;
        }

        static class BaseHolder{
            TextView tvName;
            TextView tvAddress;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        classicBluetooth.close();
        classicBluetooth2.close();
    }
}