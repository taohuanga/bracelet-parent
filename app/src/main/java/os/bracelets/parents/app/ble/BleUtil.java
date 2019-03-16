package os.bracelets.parents.app.ble;

import android.bluetooth.BluetoothGatt;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.huichenghe.bleControl.Ble.BleScanUtils;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import aio.health2world.utils.Logger;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.utils.StringUtils;

/**
 * Created by lishiyou on 2019/2/28.
 */

public class BleUtil {

    /**
     * 扫描设备
     */
    public static void scanDevice() {
        if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
            return;
        BleManager.getInstance().scan(new BleScanCallbackWrap() {
            @Override
            public void onScanFinished(List<BleDevice> list) {
                EventBus.getDefault().post(new MsgEvent(AppConfig.MSG_SCAN_FINISH, list));
            }

            @Override
            public void onScanning(@NonNull BleDevice bleDevice) {
                Logger.i("lsy", "name=" + bleDevice.getName() + ",mac=" + bleDevice.getMac());
                if (bleDevice.getName() != null) {
                    MyApplication.getInstance().addDevice(bleDevice);
                    EventBus.getDefault().post(new MsgEvent(AppConfig.MSG_DEVICE_DISCOVERY));
                }
            }
        });

    }

    /**
     * 连接蓝牙设备
     *
     * @param bleDevice
     */
    public static void connectDevice(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallbackWrap() {
            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                ToastUtil.showShort("设备连接成功");
                String deviceName = bleDevice.getName();
                if (!TextUtils.isEmpty(deviceName) && deviceName.startsWith(AppConfig.BLUETOOTH_NAME)) {
                    MyApplication.getInstance().setBleConnect(true);
                    EventBus.getDefault().post(new MsgEvent(AppConfig.MSG_BLE_NOTIFY));
                    EventBus.getDefault().post(new MsgEvent(AppConfig.MSG_CONNECTION_CHANGED));
                }
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException e) {
                super.onConnectFail(bleDevice, e);
                MyApplication.getInstance().setBleConnect(false);
                EventBus.getDefault().post(new MsgEvent(AppConfig.MSG_CONNECTION_FAIL, bleDevice));
            }

            @Override
            public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                MyApplication.getInstance().setBleConnect(false);
                EventBus.getDefault().post(new MsgEvent(AppConfig.MSG_CONNECTION_CHANGED));
            }
        });
    }

    /**
     * 设备数据接收通知
     *
     * @param bleDevice
     */
    public static void bleNotify(BleDevice bleDevice) {
        BleManager.getInstance().notify(bleDevice, AppConfig.UUID_SERVICE, AppConfig.UUID_NOTIFY,
                new BleNotifyCallbackWrap() {
                    @Override
                    public void onCharacteristicChanged(byte[] bytes) {
                        EventBus.getDefault().post(new MsgEvent<>(AppConfig.MSG_BLE_DATA, bytes));
                    }
                });
    }
}
