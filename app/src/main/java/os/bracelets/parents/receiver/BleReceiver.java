package os.bracelets.parents.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.huichenghe.bleControl.Ble.BleGattHelperListener;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

import org.greenrobot.eventbus.EventBus;

import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.app.ble.MyBleGattHelper;
import os.bracelets.parents.app.main.MainActivity;
import os.bracelets.parents.common.MsgEvent;

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
                EventBus.getDefault().post(new MsgEvent<LocalDeviceEntity>(AppConfig.MSG_DEVICE_CONNECT));
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                if (BluetoothAdapter.getDefaultAdapter() != null) {
                    LocalDeviceEntity entity = MyApplication.getInstance().getDeviceEntity();
                    if (entity != null) {
                        if (BluetoothLeService.getInstance() != null) {
                            boolean isConnect = BluetoothLeService.getInstance().isDeviceConnected(entity);
                            MyApplication.getInstance().setBleConnect(isConnect);
                            if (!isConnect)
                                MyApplication.getInstance().setDeviceEntity(null);
                        }
                    } else {
                        MyApplication.getInstance().setBleConnect(false);
                    }
                    EventBus.getDefault().post(new MsgEvent<LocalDeviceEntity>(AppConfig.MSG_DEVICE_CHANGED));
                }
                break;
            case DeviceConfig.DEVICE_CONNECTING_AUTO:
                break;
        }
    }
}
