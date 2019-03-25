package os.bracelets.parents;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseUI;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aio.health2world.SApplication;
import aio.health2world.utils.AppManager;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import cn.jpush.android.api.JPushInterface;
import os.bracelets.parents.receiver.BleReceiver;
import os.bracelets.parents.service.AppService;

/**
 * Created by lishiyou on 2019/1/24.
 */

public class MyApplication extends Application implements AMapLocationListener {

    public static MyApplication INSTANCE;

    private boolean isBleConnect = false;
    //扫描到的蓝牙设备的集合
    private List<LocalDeviceEntity> deviceList = new ArrayList<>();
    //记录已连接的设备
    private LocalDeviceEntity deviceEntity;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        SApplication.init(this);

        initApp();

        startService(new Intent(this, BluetoothLeService.class));
        startService(new Intent(this, AppService.class));
    }

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    //登录者的tokenId
    public String getTokenId() {
        return (String) SPUtils.get(this, AppConfig.TOKEN_ID, "");
    }

    public String getServerUrl() {
        return AppConfig.SERVER_URL;
    }

    public List<LocalDeviceEntity> getDeviceList() {
        return deviceList;
    }

    public LocalDeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(LocalDeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    public boolean isBleConnect() {
        return isBleConnect;
    }

    public void setBleConnect(boolean bleConnect) {
        isBleConnect = bleConnect;
    }

    public void clearEntityList() {
        deviceList.clear();
    }

    //退出当前程序 回到登录界面
    public void logout() {
        SPUtils.put(this, AppConfig.IS_LOGIN, false);
        AppManager.getInstance().finishAllActivity();
        Intent intent = new Intent("os.bracelets.parents.login");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initApp() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceConfig.DEVICE_CONNECTING_AUTO);
        filter.addAction(DeviceConfig.DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(new BleReceiver(), filter);
        //极光
        JPushInterface.init(this);
        JPushInterface.setDebugMode(AppConfig.IS_DEBUG);
        //环信
        initHx();
        //高德
        initLocation();
        // Bugly SDK初始化
        CrashReport.initCrashReport(getApplicationContext(), AppConfig.BUGLY_ID, AppConfig.IS_DEBUG);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
    }

    /**
     * 添加设备到集合
     *
     * @param bleDevice
     */
    public void addDevice(LocalDeviceEntity bleDevice) {
        removeDevice(bleDevice);
        deviceList.add(bleDevice);
    }

    /**
     * 移除设备
     *
     * @param bleDevice
     */
    public void removeDevice(LocalDeviceEntity bleDevice) {
        for (int i = 0; i < deviceList.size(); i++) {
            LocalDeviceEntity device = deviceList.get(i);
            if (bleDevice.getAddress().equals(device.getAddress())) {
                deviceList.remove(i);
            }
        }
    }

    //目前使用的是简单版的
    private void initHx() {
        //init demo helper
        EaseUI.getInstance().init(this, null);
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(60 * 1000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (location.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                location.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //获取纬度
                SPUtils.put(this, AppConfig.LATITUDE, location.getLatitude() + "");
                //获取经度
                SPUtils.put(this, AppConfig.LONGITUDE, location.getLongitude() + "");
                //城市编码
                SPUtils.put(this, AppConfig.CITY_CODE, location.getAdCode());
                //详细地址
                SPUtils.put(this, AppConfig.ADDRESS, location.getAddress());
                //获取精度信息
//                float accuracy = location.getAccuracy();
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(location.getTime());
//                String time = df.format(date);//定位时间

//                Logger.i("lsy", "定位时间：" + time + ",纬度："
//                        + location.getLatitude() + ",经度："
//                        + location.getLongitude()
//                        + ",城市：" + location.getCity()
//                        + ",城市代码：" + location.getAdCode());
//
//                String address = location.getProvince()+location.getCity()+location.getAccuracy()+location.getAddress();
//                Logger.i("lsy",address);

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Logger.e("AmapError", "location Error, ErrCode:"
                        + location.getErrorCode() + ", errInfo:"
                        + location.getErrorInfo());
            }
        }
    }
}
