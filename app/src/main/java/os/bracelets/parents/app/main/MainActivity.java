package os.bracelets.parents.app.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapRouteActivity;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.huichenghe.bleControl.Ble.BleDataForBattery;
import com.huichenghe.bleControl.Ble.BleDataforSyn;
import com.huichenghe.bleControl.Ble.BleGattHelperListener;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.bleControl.Utils.FormatUtils;
import com.j256.ormlite.stmt.query.In;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aio.health2world.http.HttpResult;
import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.DateUtil;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import aio.health2world.utils.ToastUtil;
import cn.jpush.android.api.JPushInterface;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.app.about.HelpActivityIn;
import os.bracelets.parents.app.ble.DeviceListActivity;
import os.bracelets.parents.app.ble.MyBleGattHelper;
import os.bracelets.parents.app.contact.ContactActivity;
import os.bracelets.parents.app.nearby.NearbyActivity;
import os.bracelets.parents.app.news.HealthInfoActivity;
import os.bracelets.parents.app.personal.IntegralDetailActivity;
import os.bracelets.parents.app.personal.PersonalMsgActivity;
import os.bracelets.parents.app.setting.SettingActivity;
import os.bracelets.parents.app.setting.SystemMsgActivity;
import os.bracelets.parents.bean.RemindBean;
import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.bean.WeatherInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.db.DBManager;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.jpush.JPushUtil;
import os.bracelets.parents.jpush.TagAliasOperatorHelper;
import os.bracelets.parents.utils.DataString;
import os.bracelets.parents.view.BatteryView;
import rx.functions.Action1;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends MVPBaseActivity<MainContract.Presenter> implements MainContract.View,
        INaviInfoCallback {

    private View layoutDialing, layoutNews, layoutSetting, layoutNavigation, layoutNearby, msgLayout;

    private TextView tvWeek, tvWeather, tvConnect, tvBattery, tvCity, tvStep, tvIntegral;

    private ImageView ivSports;

    private RecyclerView recyclerView;

    private RemindAdapter remindAdapter;

    private List<RemindBean> remindList;

    private View bleLayout;

    private BatteryView batteryView;

    private UserInfo info;

    @Override
    protected MainContract.Presenter getPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
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
        ivSports = findView(R.id.ivSports);
        msgLayout = findView(R.id.msgLayout);

        tvWeek = findView(R.id.tvWeek);
        tvIntegral = findView(R.id.tvIntegral);
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
        String userId = (String) SPUtils.get(this, AppConfig.USER_ID, "");
        JPushInterface.init(this);
        JPushUtil.setJPushAlias(TagAliasOperatorHelper.ACTION_SET, userId);
//        Set<String> set = new HashSet<>();
//        set.add("android");
//        JPushUtil.setJPushTags(TagAliasOperatorHelper.ACTION_SET, set);

        tvConnect.setText(MyApplication.getInstance().isBleConnect() ? getString(R.string.connected) : getString(R.string.disconnected));
        //星期
        tvWeek.setText(DataString.getWeek(this) + "\r\n" + DateUtil.getDate(new Date(System.currentTimeMillis())));

        remindList = new ArrayList<>();
        remindAdapter = new RemindAdapter(remindList);
        recyclerView.setAdapter(remindAdapter);
        remindAdapter.bindToRecyclerView(recyclerView);
        remindAdapter.setEmptyView(R.layout.layout_empty_text);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                MyApplication.getInstance().setBlueEnable(true);
                                MyApplication.getInstance().startScan();
                            } else {
                                ToastUtil.showShort(getString(R.string.permission_denied));
                            }
                        }
                    });
        } else {
            BluetoothAdapter.getDefaultAdapter().enable();
            MyApplication.getInstance().setBlueEnable(true);
            MyApplication.getInstance().startScan();
        }

        mPresenter.getWeather();
        mPresenter.uploadLocation();

        boolean firstIn = (boolean) SPUtils.get(this, AppConfig.FIRST_IN, true);

        //程序第一次安装则弹出帮助页面
        if (firstIn) {
            startActivity(new Intent(this, HelpActivityIn.class));
            SPUtils.put(this, AppConfig.FIRST_IN, false);
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
        msgLayout.setOnClickListener(this);
        tvIntegral.setOnClickListener(this);
    }

    @Override
    public void loadMsgSuccess(int stepNum, List<RemindBean> list) {
        tvStep.setText(String.valueOf(stepNum));
        if (info != null) {
            //性别描述0 未知 1 男 2 女
            if (info.getSex() == 2) {
                ivSports.setImageResource(R.mipmap.icon_sports_woman);
            } else {
                ivSports.setImageResource(R.mipmap.icon_sports_man);
            }
        }
        remindList.clear();
        remindList.addAll(list);
        remindAdapter.notifyDataSetChanged();

        DBManager.getInstance().saveRemindList(list);

        if (remindList.size() > 0)
            MyApplication.getInstance().alarmClock(this, remindList.get(0).getRemindTime());
    }

    @Override
    public void loginWeatherSuccess(WeatherInfo info) {
        tvCity.setText(info.getCity());
        tvWeather.setText(info.getWeather());
    }

    @Override
    public void loadUserInfo(UserInfo userInfo) {
        this.info = userInfo;
        mPresenter.homeMsg();
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
                if (info == null) {
                    ToastUtil.showShort(getString(R.string.user_info_load_failed));
                    mPresenter.userInfo();
                    return;
                }
                if (TextUtils.isEmpty(info.getLocation())) {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.set_home_address_first))
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton(getString(R.string.go_setting), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, PersonalMsgActivity.class);
                                    startActivityForResult(intent, 0x11);
                                }
                            })
                            .show();
                    return;
                }
                double latitude = Double.parseDouble(info.getLatitude());
                double longitude = Double.parseDouble(info.getLongitude());
                LatLng latLng = new LatLng(latitude, longitude);
                AmapNaviParams params = new AmapNaviParams(new Poi("", null, ""),
                        null, new Poi(info.getLocation(), latLng, ""), AmapNaviType.DRIVER);
                params.setUseInnerVoice(true);
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params,
                        MainActivity.this, AmapRouteActivity.class);
//                startActivity(new Intent(this, NavigateActivity.class));
                break;
            case R.id.layoutNearby:
                //附近的人
                startActivity(new Intent(this, NearbyActivity.class));
                break;
            case R.id.bleLayout:
                startActivity(new Intent(this, DeviceListActivity.class));
                break;
            case R.id.msgLayout:
                startActivity(new Intent(this, SystemMsgActivity.class));
                break;
            case R.id.tvIntegral:
                startActivity(new Intent(this, IntegralDetailActivity.class));
                break;

        }
    }


    //蓝牙设备扫描完成之后回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent event) {
        //设备已连接
        if (event.getAction() == AppConfig.MSG_DEVICE_CONNECT) {
            tvConnect.setText(getString(R.string.connected));
            onResume();
            LocalDeviceEntity entity = MyApplication.getInstance().getDeviceEntity();
//            uploadLog("设备" + (entity == null ? "--" :
//                    entity.getAddress().replace(":", "").toUpperCase()) + "连接成功！");
        }
        //设备失去连接
        if (event.getAction() == AppConfig.MSG_DEVICE_DISCONNECT) {
            tvConnect.setText(getString(R.string.disconnected));
            batteryView.setPower(10);
            tvBattery.setText("---");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.userInfo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BluetoothLeService.getInstance() != null) {
                    if (MyApplication.getInstance().isBleConnect()) {
                        BluetoothLeService.getInstance().addCallback(
                                MyBleGattHelper.getInstance(MainActivity.this, new GattHelperListener()));
                        getBraceletData();
                    }
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

    /**
     * 电量数据修改：
     * 0~100：当前正在使用电池供电，数值为电池剩余电量的百分比
     * 128~228：当前正在充电，数值减去128后为已充电电量的百分比
     * 240：充电完成
     */
    private void getBattery() {
        //获取电量
        BleDataForBattery.getInstance().setBatteryListener(new DataSendCallback() {
            @Override
            public void sendSuccess(final byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = FormatUtils.bytesToHexString(bytes);
                        Logger.i("lsy", getString(R.string.electric_quantity) + data);
                        Long batteryLong = Long.parseLong(data.substring(0, 2), 16);
                        int batteryInt = batteryLong.intValue();
                        if (batteryInt <= 100) {
                            tvBattery.setText(batteryInt + "%");
                            batteryView.setPower(batteryInt);
                        } else if (batteryInt >= 128 && batteryInt <= 228) {
                            batteryInt = batteryInt - 128;
                            tvBattery.setText(getString(R.string.charging) + batteryInt + "%");
                            batteryView.setPower(batteryInt);
                        } else if (batteryInt == 240) {
                            tvBattery.setText(getString(R.string.charge_complete));
                            batteryView.setPower(100);
                        }
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
                .setMessage(getString(R.string.confirm_exit_application))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    private void uploadLog(String log) {
        ApiRequest.log(log, new HttpSubscriber() {
            @Override
            public void onNext(HttpResult result) {
            }
        });
    }
}
