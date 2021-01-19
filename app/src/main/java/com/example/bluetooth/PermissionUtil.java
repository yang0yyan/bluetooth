package com.example.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
    public static String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;     //内存读取
    public static String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;   //内存写入
    public static String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;                       //录音
    public static String ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;

    public static String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;   //获取粗略的位置信息
    public static String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;       //获取较精确的位置信息 *Android 10

    public static boolean getPermission(Activity activity, String permission) {
        //此处做动态权限申请
        //判断系统是否大于等于Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int request = ContextCompat.checkSelfPermission(activity,
                    permission);
            if (request != PackageManager.PERMISSION_GRANTED) {//缺少权限，进行权限申请
                /*Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent,100);*/
                //当前上下文;一个权限数组;一个唯一的请求码(0~65535的16位数)
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        0XFA);
                return false;
            } else {
                //权限同意 已授权
            }
        } else {
            //低于23 不需要特殊处理
        }
        return true;
    }

    public static boolean getPermissions(Activity activity, String... permission) {
        //此处做动态权限申请
        //判断系统是否大于等于Android 6.0
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : permission) {
                int request = ContextCompat.checkSelfPermission(activity, s);
                if (request != PackageManager.PERMISSION_GRANTED)
                    permissions.add(s);
            }
            if (permissions.size() > 0) {//缺少权限，进行权限申请
                //当前上下文;一个权限数组;一个唯一的请求码(0~65535的16位数)
                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), 0XFF);
                return false;
            } else {
                //权限同意 已授权
            }
        } else {
            //低于23 不需要特殊处理
        }
        return true;
    }

}
