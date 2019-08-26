package os.bracelets.parents;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.huichenghe.bleControl.Ble.BleScanUtils;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import aio.health2world.SApplication;
import aio.health2world.http.HttpResult;
import aio.health2world.utils.AppManager;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import aio.health2world.utils.ToastUtil;
import cn.jpush.android.api.JPushInterface;
import os.bracelets.parents.app.account.AgreementActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.receiver.AlarmReceiver;
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
        SApplication.init(this, AppConfig.IS_DEBUG);

        initApp();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, AppService.class));
//            startForegroundService(new Intent(this, BluetoothLeService.class));
//        } else {
//            startService(new Intent(this, AppService.class));
//            startService(new Intent(this, BluetoothLeService.class));
//        }

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
    public void logout(boolean flag) {
        SPUtils.put(this, AppConfig.IS_LOGIN, false);
        AppManager.getInstance().finishAllActivity();
        Intent intent = new Intent("os.bracelets.parents.login");
        if (flag)
            intent.putExtra("flag", true);
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
        filter.addAction(BleReceiver.NOTIFICATION_SHOW);
        registerReceiver(new BleReceiver(), filter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(AppConfig.ALARM_CLOCK);
        registerReceiver(new AlarmReceiver(), filter1);

        //极光
        JPushInterface.init(this);
        JPushInterface.setDebugMode(AppConfig.IS_DEBUG);
        //环信 目前使用的是简单版的
//        EaseUI.getInstance().init(this, null);
        //高德
        initLocation();
        // Bugly SDK初始化
        CrashReport.initCrashReport(getApplicationContext(), AppConfig.BUGLY_ID, AppConfig.IS_DEBUG);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5c9d7cb1");
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

    public void startScan() {
        if (BluetoothAdapter.getDefaultAdapter() == null)
            return;
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
            return;
        BleScanUtils.getBleScanUtilsInstance(MyApplication.getInstance()).stopScan();
        //扫描设备前，如果没有连接设备，开始监听蓝牙设备连接
        BleScanUtils.getBleScanUtilsInstance(MyApplication.getInstance()).setmOnDeviceScanFoundListener(deviceFoundListener);
        BleScanUtils.getBleScanUtilsInstance(MyApplication.getInstance()).scanDevice(null);
    }

    /**
     * 实例化设备监听器，并对扫描到的设备进行监听
     */
    private BleScanUtils.OnDeviceScanFoundListener deviceFoundListener = new BleScanUtils.OnDeviceScanFoundListener() {
        @Override
        public void OnDeviceFound(LocalDeviceEntity mLocalDeviceEntity) {
            String deviceName = mLocalDeviceEntity.getName();
            if (deviceName != null && deviceName.startsWith("DFZ")) {
                Logger.i("lsy", "扫描到设备" + deviceName);
                MyApplication.getInstance().addDevice(mLocalDeviceEntity);
            }
            //根据绑定的设备自带链接
            String macAddress = (String) SPUtils.get(INSTANCE, AppConfig.MAC_ADDRESS, "");
            if (!TextUtils.isEmpty(macAddress)) {
                String mAddress = mLocalDeviceEntity.getAddress().replace(":", "").toUpperCase();
                if (macAddress.equals(mAddress)) {
                    BleScanUtils.getBleScanUtilsInstance(MyApplication.getInstance()).stopScan();
                    BluetoothLeService.getInstance().connect(mLocalDeviceEntity);
                }
            }
        }

        @Override
        public void onScanStateChange(boolean isChange) {
        }

    };

    /**
     * 添加设备到集合
     *
     * @param bleDevice
     */
    public synchronized void addDevice(LocalDeviceEntity bleDevice) {
        removeDevice(bleDevice);
        deviceList.add(bleDevice);
    }

    /**
     * 移除设备
     *
     * @param bleDevice
     */
    public synchronized void removeDevice(LocalDeviceEntity bleDevice) {
        for (int i = 0; i < deviceList.size(); i++) {
            LocalDeviceEntity device = deviceList.get(i);
            if (bleDevice.getAddress().equals(device.getAddress())) {
                deviceList.remove(i);
            }
        }
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
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(location.getTime());
                String time = df.format(date);//定位时间

                Logger.i("lsy", "定位时间：" + time + ",纬度："
                        + location.getLatitude() + ",经度："
                        + location.getLongitude()
                        + ",城市：" + location.getCity()
                        + ",城市代码：" + location.getAdCode());
                ApiRequest.uploadLocation(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()),
                        new HttpSubscriber() {
                            @Override
                            public void onError(Throwable e) {
//                                super.onError(e);
                            }

                            @Override
                            public void onNext(HttpResult result) {
//                                super.onNext(result);
                            }
                        });

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Logger.e("AmapError", "location Error, ErrCode:"
                        + location.getErrorCode() + ", errInfo:"
                        + location.getErrorInfo());
            }
        }
    }

    public void alarmClock(Context context, String time) {
//        time="20:22";
        Intent intent = new Intent(AppConfig.ALARM_CLOCK);
        PendingIntent sender = PendingIntent.getBroadcast(context, AppConfig.CLOCK_ID, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        Date date = null;
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH) + 1;
        int day = calendar.get(calendar.DAY_OF_MONTH);
        String mTime = year + "-" + month + "-" + day + " " + time;
        try {
            date = format.parse(mTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);

        if (System.currentTimeMillis() - calendar.getTimeInMillis() > 0)
            return;

        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 100, sender);
    }

    private SpeechSynthesizer mTts;

    public void speakVoice() {
        if (mTts == null)
            mTts = SpeechSynthesizer.createSynthesizer(this, null);
        setTts();
        mTts.startSpeaking("您有新的待办任务，请及时处理", mTtsListener);

    }

    private void setTts() {
        // 设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

        // 设置语速
        mTts.setParameter(SpeechConstant.SPEED, "20");

        // 设置音调
        mTts.setParameter(SpeechConstant.PITCH, "50");

        // 设置音量0-100
        mTts.setParameter(SpeechConstant.VOLUME, "100");

        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
    }


    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        // 缓冲进度回调，arg0为缓冲进度，arg1为缓冲音频在文本中开始的位置，arg2为缓冲音频在文本中结束的位置，arg3为附加信息
        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
            // TODO Auto-generated method stub

        }

        // 会话结束回调接口，没有错误时error为空
        @Override
        public void onCompleted(SpeechError error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        // 开始播放
        @Override
        public void onSpeakBegin() {
            // TODO Auto-generated method stub

        }

        // 停止播放
        @Override
        public void onSpeakPaused() {
            // TODO Auto-generated method stub

        }

        // 播放进度回调,arg0为播放进度0-100；arg1为播放音频在文本中开始的位置，arg2为播放音频在文本中结束的位置。
        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        // 恢复播放回调接口
        @Override
        public void onSpeakResumed() {
            // TODO Auto-generated method stub

        }

    };
}
