package os.bracelets.parents.app.ble;


import android.bluetooth.BluetoothGatt;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import aio.health2world.utils.Logger;

/**
 * Created by lishiyou on 2018/11/22 0022.
 */

public class BleGattCallbackWrap extends BleGattCallback {

    @Override
    public void onStartConnect() {
        Logger.i("BleGattCallbackWrap", "onStartConnect");
    }

    @Override
    public void onConnectFail(BleDevice bleDevice, BleException e) {
        Logger.i("BleGattCallbackWrap", "onConnectFail:" + bleDevice.getName() + " " + e.getDescription());
    }

    @Override
    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {

    }

    @Override
    public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
        Logger.i("BleGattCallbackWrap", "onDisConnected:" + bleDevice.getName());
    }
}
