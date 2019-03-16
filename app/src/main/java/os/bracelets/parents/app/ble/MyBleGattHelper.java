package os.bracelets.parents.app.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.huichenghe.bleControl.Ble.BleGattHelperListener;
import com.huichenghe.bleControl.Ble.BleNotifyParse;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.IServiceCallback;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

import java.util.List;

/**
 * Created by zhouyezi on 2018/2/13.
 */

public class MyBleGattHelper implements IServiceCallback {
    private Context context;
    private BleGattHelperListener gattHelperListener;
    private static MyBleGattHelper MyBleGattHelper;

    public static MyBleGattHelper getInstance(Context context, BleGattHelperListener listener) {
        if(MyBleGattHelper == null) {
            Class var2 = MyBleGattHelper.class;
            synchronized(MyBleGattHelper.class) {
                if(MyBleGattHelper == null) {
                    MyBleGattHelper = new MyBleGattHelper(context);
                }
            }
        }

        MyBleGattHelper.setBleGattListener(listener);
        return MyBleGattHelper;
    }

    private MyBleGattHelper(Context context) {
        this.context = context;
    }

    private void setBleGattListener(BleGattHelperListener listener) {
        this.gattHelperListener = listener;
    }

    public void onBLEServiceFound(LocalDeviceEntity device, BluetoothGatt gatt, List<BluetoothGattService> list) {
    }

    public void onBLEDeviceConnected(LocalDeviceEntity device, BluetoothGatt gatt) {
    }

    public void onBLEDeviceDisConnected(LocalDeviceEntity device, BluetoothGatt gatt) {
    }

    public void onCharacteristicRead(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean success) {
    }

    public void onCharacteristicChanged(LocalDeviceEntity device, BluetoothGatt gatt, String uuid, byte[] value) {
        if(uuid.equals(DeviceConfig.HEARTRATE_FOR_TIRED_NOTIFY.toString())) {
            this.gattHelperListener.onDeviceStateChangeUI(device, gatt, uuid, value);
        } else if(uuid.equals(DeviceConfig.UUID_CHARACTERISTIC_NOTIFY.toString())) {
            BleNotifyParse.getInstance().doParse(this.context, value);
            if(value != null){
                try {
                    BleDataForSensor.getInstance().dealReceData(this.context, value, 88);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

    }

    public void onCharacteristicWrite(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean success) {
    }

    public void onDescriptorRead(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattDescriptor bd, boolean success) {
    }

    public void onDescriptorWrite(LocalDeviceEntity device, BluetoothGatt gatt, BluetoothGattDescriptor bd, boolean success) {
    }

    public void onNoBLEServiceFound() {
    }

    public void onBLEDeviceConnecError(LocalDeviceEntity device, boolean showToast, boolean fromServer) {
        this.gattHelperListener.onDeviceConnectedChangeUI(device, showToast, fromServer);
    }

    public void onReadRemoteRssi(LocalDeviceEntity device, BluetoothGatt gatt, int rssi, boolean success) {
    }

    public void onReliableWriteCompleted(LocalDeviceEntity device, BluetoothGatt gatt, boolean success) {
    }

    public void onMTUChange(BluetoothGatt gatt, int mtu, int status) {
    }
}
