package os.bracelets.parents.app.setting;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleDataForBattery;
import com.huichenghe.bleControl.Ble.BleForLostRemind;
import com.huichenghe.bleControl.Ble.BleForQQWeiChartFacebook;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Utils.FormatUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import aio.health2world.utils.SPUtils;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/3/16.
 */

public class SensorMsgActivity extends BaseActivity {

    private TitleBar titleBar;

    private Button btnAlert, btnTestBle;

    private TextView tvTestSensor, tvAlertSensor;

    private Switch switchSleep, switchUpload;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor_msg;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        switchSleep = findView(R.id.switchSleep);
        switchUpload = findView(R.id.switchUpload);
        tvTestSensor = findView(R.id.tvTestSensor);
        tvAlertSensor = findView(R.id.tvAlertSensor);
        btnAlert = findView(R.id.btnAlert);
        btnTestBle = findView(R.id.btnTestBle);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "G-SENSOR检测", titleBar);
    }

    @Override
    protected void initListener() {
        EventBus.getDefault().register(this);
        btnAlert.setOnClickListener(this);
        btnTestBle.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switchSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SensorMsgActivity.this, "sw_lost", isChecked);
                getLost(isChecked);
            }
        });

        switchUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SensorMsgActivity.this, "sw_upload", isChecked);
                getFeedBack(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnAlert:
                alertMethod();
                break;
            case R.id.btnTestBle:
                getBattery();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent event) {
        String message = (String) event.getT();
        if (message.contains("688508")) {
            String str = message.substring(18, 20);
            if (str.equals("01")) {
                ToastUtil.showShort("测试成功");
            }
        } else {
            tvTestSensor.setText(message);
        }

    }

    private void alertMethod() {
        BleForQQWeiChartFacebook.getInstance().setOnDeviceDataBack(new DataSendCallback() {
            @Override
            public void sendSuccess(final byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = FormatUtils.bytesToHexString(bytes);
                        tvAlertSensor.setText(str);
                    }
                });
            }

            @Override
            public void sendFailed() {

            }

            @Override
            public void sendFinished() {

            }
        });
        BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0a, (byte) 0x01);
    }

    private void getBattery() {
        BleDataForBattery.getInstance().setBatteryListener(new DataSendCallback() {
            @Override
            public void sendSuccess(final byte[] receveData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("测试成功");
                    }
                });
            }

            @Override
            public void sendFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("测试失败");
                    }
                });
            }

            @Override
            public void sendFinished() {

            }
        });
        BleDataForBattery.getInstance().getBatteryPx();
    }

    private void getLost(final boolean isOpen) {
        BleForLostRemind.getInstance().setLostListener(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("操作成功");
                    }
                });
            }

            @Override
            public void sendFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("操作失败");
                    }
                });

            }

            @Override
            public void sendFinished() {
            }
        });
        BleForLostRemind.getInstance().openAndHandler(isOpen);
    }

    private void getFeedBack(boolean isChecked) {
        if (isChecked) {
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x01);
        } else {
            BleForQQWeiChartFacebook.getInstance().openRemind((byte) 0x0b, (byte) 0x00);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
