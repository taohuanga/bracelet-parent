package os.bracelets.parents.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.app.main.MainActivity;

/**
 * Created by lishiyou on 2019/3/2.
 */

public class BleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            //设备已连接的广播
            case DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL:
                //获取当前已连接的设备currentDevice
                LocalDeviceEntity device = BluetoothLeService.getInstance().getCurrentDevice();
                //把设备信息转换成json存储在sp中
                SPUtils.put(MyApplication.getInstance(), AppConfig.CURRENT_DEVICE, new Gson().toJson(device));
                //连接成功后，对设备进行一系列检测请求，如电池电量等
                //设置已连接设备名称
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                if (BluetoothAdapter.getDefaultAdapter() != null) {
                    //蓝牙开启
                    if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        Logger.i("lsy", "开始扫描蓝牙");
                    } else {
                        //未开启蓝牙
                        Logger.i("lsy", "蓝牙未开启");
                    }
                }
                break;
        }
    }
}
