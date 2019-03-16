package os.bracelets.parents.app.ble;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.exception.BleException;

import aio.health2world.utils.Logger;

/**
 * Created by lishiyou on 2018/11/22 0022.
 */

public class BleNotifyCallbackWrap extends BleNotifyCallback {

    @Override
    public void onNotifySuccess() {
        Logger.i("lsy", "notify success");
    }

    @Override
    public void onNotifyFailure(BleException e) {
        Logger.i("lsy", "notify exception");
    }

    @Override
    public void onCharacteristicChanged(byte[] bytes) {

    }
}
