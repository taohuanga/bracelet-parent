package os.bracelets.parents.app.ble;

import android.content.Context;

import com.huichenghe.bleControl.Ble.BleBaseDataManage;
import com.huichenghe.bleControl.Ble.DataSendCallback;

/**
 * Created by zhouyezi on 2018/2/13.
 */

public class BleDataForSensor extends BleBaseDataManage {
    private DataSendCallback sensorListener;
    private static BleDataForSensor bleBattery;


    public static BleDataForSensor getInstance() {
        if (bleBattery == null) {
            Class var0 = BleDataForSensor.class;
            synchronized (BleDataForSensor.class) {
                if (bleBattery == null) {
                    bleBattery = new BleDataForSensor();
                }
            }
        }

        return bleBattery;
    }


    private BleDataForSensor() {
    }


    public void dealReceData(Context mContext, byte[] data, int dataLength) {
        if (sensorListener == null) return;
        this.sensorListener.sendSuccess(data);
    }

    public void setSensorListener(DataSendCallback sensorCallback) {
        this.sensorListener = sensorCallback;
    }
}
