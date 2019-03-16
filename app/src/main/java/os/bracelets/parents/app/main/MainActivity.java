package os.bracelets.parents.app.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.app.ble.DeviceListActivity;
import os.bracelets.parents.app.contact.ContactActivity;
import os.bracelets.parents.app.navigate.NavigateActivity;
import os.bracelets.parents.app.nearby.NearbyActivity;
import os.bracelets.parents.app.news.HealthInfoActivity;
import os.bracelets.parents.app.setting.SettingActivity;
import os.bracelets.parents.bean.RemindBean;
import os.bracelets.parents.bean.WeatherInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.utils.DataString;
import rx.functions.Action1;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends MVPBaseActivity<MainContract.Presenter> implements MainContract.View {

    private View layoutDialing, layoutNews, layoutSetting, layoutNavigation, layoutNearby;

    private TextView tvWeek, tvWeather, tvConnect, tvBattery, tvCity, tvStep;

    private RecyclerView recyclerView;

    private RemindAdapter remindAdapter;

    private List<RemindBean> remindList;

    private View bleLayout;

    private String date = "";
    private String week = "";
    private String weather = "";
    private String day = "";

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

        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void initData() {

        tvConnect.setText(MyApplication.getInstance().isBleConnect() ? "已连接" : "未连接");
        //星期
        tvWeek.setText(DataString.getWeek());

        remindList = new ArrayList<>();
        remindAdapter = new RemindAdapter(remindList);
        recyclerView.setAdapter(remindAdapter);
        remindAdapter.bindToRecyclerView(recyclerView);
        remindAdapter.setEmptyView(R.layout.layout_empty_text);

        mPresenter.homeMsg();
//        mPresenter.remindList();
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
    }


    //蓝牙设备扫描完成之后回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent event) {
        //设备连接状态改变
        if (event.getAction() == AppConfig.MSG_CONNECTION_CHANGED
                || event.getAction() == AppConfig.MSG_CONNECTION_FAIL) {
            if (MyApplication.getInstance().isBleConnect()) {
                tvConnect.setText("已连接");
            } else {
                tvConnect.setText("未连接");
            }
        }
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
//                startActivity(new Intent(this, BleDeviceActivity.class));
                startActivity(new Intent(this, DeviceListActivity.class));
                break;

        }
    }


//    /**
//     * 注册广播
//     */
//    private void registerBleReceiver() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        filter.addAction(DeviceConfig.DEVICE_CONNECTING_AUTO);
//        registerReceiver(bleReceiver, filter);
//    }

//    /**
//     * 广播接收器
//     */
//    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, final Intent intent) {
//            switch (intent.getAction()) {
//                //设备已连接的广播
//                case DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL:
//                    //获取当前已连接的设备currentDevice
//                    LocalDeviceEntity device = BluetoothLeService.getInstance().getCurrentDevice();
//                    //把设备信息转换成json存储在sp中
//                    SPUtils.put(MainActivity.this, AppConfig.CURRENT_DEVICE, new Gson().toJson(device));
//                    //连接成功后，对设备进行一系列检测请求，如电池电量等
//                    //设置已连接设备名称
//                    tvConnect.setText("已连接");
//                    Logger.i("lsy", "设备已连接==" + device.getName());
//                    //获取手环数据
//                    getBraceletData();
//                    break;
//                case BluetoothAdapter.ACTION_STATE_CHANGED:
//                    if (BluetoothAdapter.getDefaultAdapter() != null) {
//                        //蓝牙开启
//                        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//                            startScan();
//                            Logger.i("lsy", "开始扫描蓝牙");
//                        } else {
//                            //未开启蓝牙
//                            Logger.i("lsy", "蓝牙未开启");
//                        }
//                    }
//                    break;
//            }
//        }
//    };

    /**
     * 获取手环数据
     */
    private void getBraceletData() {

    }

//    private void startScan() {
//        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
//        //扫描设备前，如果没有连接设备，开始监听蓝牙设备连接
//        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).setmOnDeviceScanFoundListener(deviceFoundListener);
//        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).scanDevice(null);
//    }
//
//
//    /**
//     * 实例化设备监听器，并对扫描到的设备进行监听
//     */
//    private BleScanUtils.OnDeviceScanFoundListener deviceFoundListener = new BleScanUtils.OnDeviceScanFoundListener() {
//        @Override
//        public void OnDeviceFound(LocalDeviceEntity mLocalDeviceEntity) {
//            String deviceName = mLocalDeviceEntity.getName();
//            if (deviceName != null && deviceName.startsWith("DFZ")) {
//                Logger.i("lsy", "扫描到设备" + deviceName);
//            }
//        }
//
//        @Override
//        public void onScanStateChange(boolean isChange) {
//        }
//    };

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
