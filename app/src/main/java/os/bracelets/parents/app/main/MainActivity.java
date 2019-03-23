package os.bracelets.parents.app.main;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huichenghe.bleControl.Ble.BleDataForBattery;
import com.huichenghe.bleControl.Ble.BleDataforSyn;
import com.huichenghe.bleControl.Ble.BleGattHelperListener;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.IServiceCallback;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.bleControl.Utils.FormatUtils;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.Logger;
import aio.health2world.utils.ToastUtil;
import cn.jpush.android.api.JPushInterface;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.app.ble.DeviceListActivity;
import os.bracelets.parents.app.ble.MyBleGattHelper;
import os.bracelets.parents.app.contact.ContactActivity;
import os.bracelets.parents.app.navigate.NavigateActivity;
import os.bracelets.parents.app.nearby.NearbyActivity;
import os.bracelets.parents.app.news.HealthInfoActivity;
import os.bracelets.parents.app.setting.SettingActivity;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.RemindBean;
import os.bracelets.parents.bean.WeatherInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.utils.DataString;
import os.bracelets.parents.view.BatteryView;
import rx.functions.Action1;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends MVPBaseActivity<MainContract.Presenter> implements MainContract.View {

    private View layoutDialing, layoutNews, layoutSetting, layoutNavigation, layoutNearby;

    private TextView tvWeek, tvWeather, tvConnect, tvBattery, tvCity, tvStep;

    private RecyclerView recyclerView;

    private RemindAdapter remindAdapter;

    private List<RemindBean> remindList;

    private View bleLayout;

    private BatteryView batteryView;

    private Handler handler;

    private BaseInfo info;

    @Override
    protected MainContract.Presenter getPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        layoutDialing = findView(R.id.layoutDialing);
        layoutNews = findView(R.id.layoutNews);
        layoutSetting = findView(R.id.layoutSetting);
        layoutNearby = findView(R.id.layoutNearby);
        layoutNavigation = findView(R.id.layoutNavigation);

        tvWeek = findView(R.id.tvWeek);
        tvCity = findView(R.id.tvCity);
        tvWeather = findView(R.id.tvWeather);
        tvConnect = findView(R.id.tvConnect);
        tvBattery = findView(R.id.tvBattery);
        tvStep = findView(R.id.tvStep);
        bleLayout = findView(R.id.bleLayout);
        batteryView = findView(R.id.batteryView);

        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void initData() {
        handler = new Handler();
        tvConnect.setText(MyApplication.getInstance().isBleConnect() ? "已连接" : "未连接");
        //星期
        tvWeek.setText(DataString.getWeek());

        remindList = new ArrayList<>();
        remindAdapter = new RemindAdapter(remindList);
        recyclerView.setAdapter(remindAdapter);
        remindAdapter.bindToRecyclerView(recyclerView);
        remindAdapter.setEmptyView(R.layout.layout_empty_text);

        mPresenter.homeMsg();
        mPresenter.getWeather();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                            } else {
                                ToastUtil.showShort("读写权限被拒绝");
                            }
                        }
                    });
        }
        if (getIntent().hasExtra("info"))
            info = (BaseInfo) getIntent().getSerializableExtra("info");
        if (info != null)
            mPresenter.loginHx(info);
    }

    @Override
    protected void initListener() {
        EventBus.getDefault().register(this);
        layoutDialing.setOnClickListener(this);
        layoutNews.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutNavigation.setOnClickListener(this);
        bleLayout.setOnClickListener(this);
        layoutNearby.setOnClickListener(this);
    }

    @Override
    public void loadMsgSuccess(int stepNum, List<RemindBean> list) {
        tvStep.setText(String.valueOf(stepNum));
        remindList.clear();
        remindList.addAll(list);
        remindAdapter.notifyDataSetChanged();
    }

    @Override
    public void loginWeatherSuccess(WeatherInfo info) {
        tvCity.setText(info.getCity());
        tvWeather.setText(info.getWeather());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layoutDialing:
                startActivity(new Intent(this, ContactActivity.class));
                break;
            case R.id.layoutNews:
                startActivity(new Intent(this, HealthInfoActivity.class));
                break;
            case R.id.layoutSetting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.layoutNavigation:
                startActivity(new Intent(this, NavigateActivity.class));
                break;
            case R.id.layoutNearby:
                //附近的人
                startActivity(new Intent(this, NearbyActivity.class));
                break;
            case R.id.bleLayout:
                startActivity(new Intent(this, DeviceListActivity.class));
                break;

        }
    }

    //蓝牙设备扫描完成之后回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent event) {
        //设备已连接
        if (event.getAction() == AppConfig.MSG_DEVICE_CONNECT) {
            tvConnect.setText("已连接");
            onResume();
        }
        //设备失去连接
        if (event.getAction() == AppConfig.MSG_DEVICE_DISCONNECT) {
            tvConnect.setText("未连接");
            batteryView.setPower(10);
            tvBattery.setText("---");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyApplication.getInstance().isBleConnect()) {
                    BluetoothLeService.getInstance().addCallback(
                            MyBleGattHelper.getInstance(getApplicationContext(), new GattHelperListener()));
                    getBraceletData();
                }
            }
        }, 500);
    }


    /**
     * 获取手环数据
     */
    private void getBraceletData() {
        checkTime();
        getBattery();
    }

    private void getBattery() {
        //获取电量
        BleDataForBattery.getInstance().setBatteryListener(new DataSendCallback() {
            @Override
            public void sendSuccess(final byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = FormatUtils.bytesToHexString(bytes);
                        Long batteryLong = Long.parseLong(data.substring(0, 2), 16);
                        int batteryInt = batteryLong.intValue();
                        tvBattery.setText(String.valueOf(batteryInt) + "%");
                        batteryView.setPower(batteryInt);
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
        BleDataForBattery.getInstance().getBatteryPx();
    }

    //校验时间
    private void checkTime() {
        BleDataforSyn syn = BleDataforSyn.getSynInstance();
        syn.setDataSendCallback(new DataSendCallback() {
            @Override
            public void sendSuccess(byte[] bytes) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showShort("校时成功");
//                    }
//                });
            }

            @Override
            public void sendFailed() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showShort("校时失败");
//                    }
//                });
            }

            @Override
            public void sendFinished() {

            }
        });
        syn.syncCurrentTime();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("确认退出程序吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    private class GattHelperListener implements BleGattHelperListener {
        @Override
        public void onDeviceStateChangeUI(LocalDeviceEntity device,
                                          BluetoothGatt gatt,
                                          final String uuid, final byte[] value) {
        }

        @Override
        public void onDeviceConnectedChangeUI(final LocalDeviceEntity device,
                                              boolean showToast,
                                              final boolean fromServer) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
//        countDownTimer.cancel();
    }
}
