package os.bracelets.parents.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;

import com.google.gson.Gson;
import com.huichenghe.bleControl.Ble.DataSendCallback;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.app.ble.BleDataForSensor;
import os.bracelets.parents.app.contact.ContactActivity;
import os.bracelets.parents.app.main.MainActivity;
import os.bracelets.parents.app.setting.DeviceBindActivity;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.FileUtils;
import os.bracelets.parents.utils.StringUtils;

/**
 * Created by lishiyou on 2019/1/27.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class AppService extends Service implements DataSendCallback, SensorEventListener {

    public static final String TAG = "AppService";

    private int notifyId = 11;

    private int countFile = 0;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");


    private FileUtils fileUtils = new FileUtils("Bracelet");

    private StringBuilder sb = new StringBuilder();

    private long lastTime = System.currentTimeMillis();

    private long startTime = System.currentTimeMillis();


    private SensorManager sensorManager;
    /**
     * 当前所走的步数
     */
    private int CURRENT_STEP;
    /**
     * 计步传感器类型  Sensor.TYPE_STEP_COUNTER或者Sensor.TYPE_STEP_DETECTOR
     */
    private static int stepSensorType = -1;
    /**
     * 每次第一次启动记步服务时是否从系统中获取了已有的步数记录
     */
    private boolean hasRecord = false;
    /**
     * 系统中获取到的已有的步数
     */
    private int hasStepCount = 0;
    /**
     * 上一次的步数
     */
    private int previousStepCount = 0;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //蓝牙数据回调监听
        BleDataForSensor.getInstance().setSensorListener(this);
        //计步器
        initSensor();

        timer.start();

        return super.onStartCommand(intent, flags, startId);
    }

    //计时器 十分钟执行一次数据上传操作
    private CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            timer.start();
            //计时结束 分发数据
            EventBus.getDefault().post(new MsgEvent<>(AppConfig.MSG_STEP_COUNT, CURRENT_STEP));
            uploadStepNum();
            getBindDeviceInfo();
            if (!MyApplication.getInstance().isBleConnect()) {
                MyApplication.getInstance().startScan();
            }
        }
    };

    private void initSensor() {
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_COUNTER;
            Logger.v(TAG, "Sensor.TYPE_STEP_COUNTER");
            sensorManager.registerListener(AppService.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (detectorSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_DETECTOR;
            Logger.v(TAG, "Sensor.TYPE_STEP_DETECTOR");
            sensorManager.registerListener(AppService.this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stepSensorType == Sensor.TYPE_STEP_COUNTER) {
            //获取当前传感器返回的临时步数
            int tempStep = (int) event.values[0];
            //首次如果没有获取手机系统中已有的步数则获取一次系统中APP还未开始记步的步数
            if (!hasRecord) {
                hasRecord = true;
                hasStepCount = tempStep;
            } else {
                //获取APP打开到现在的总步数=本次系统回调的总步数-APP打开之前已有的步数
                int thisStepCount = tempStep - hasStepCount;
                //本次有效步数=（APP打开后所记录的总步数-上一次APP打开后所记录的总步数）
                int thisStep = thisStepCount - previousStepCount;
                //总步数=现有的步数+本次有效步数
                CURRENT_STEP += (thisStep);
                //记录最后一次APP打开到现在的总步数
                previousStepCount = thisStepCount;
            }
        } else if (stepSensorType == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0) {
                CURRENT_STEP++;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void sendSuccess(byte[] bytes) {
        handleData(bytes);
    }

    @Override
    public void sendFailed() {
        CrashReport.postCatchedException(new Throwable("数据接收失败"));

    }

    @Override
    public void sendFinished() {

    }

    private void handleData(byte[] bytes) {
        String data = StringUtils.bytesToHexString(bytes);
        Logger.i("lsy", data);
        if (bytes.length < 10)
            return;
        if (!data.substring(0, 4).equals("68a8"))
            return;
        int accXInt = (bytes[6] << 8) | (bytes[5] & 0xff);
        int accYInt = (bytes[8] << 8) | (bytes[7] & 0xff);
        int accZInt = (bytes[10] << 8) | (bytes[9] & 0xff);
        int gyrXInt = (bytes[12] << 8) | (bytes[11] & 0xff);
        int gyrYInt = (bytes[14] << 8) | (bytes[13] & 0xff);
        int gyrZInt = (bytes[16] << 8) | (bytes[15] & 0xff);

        double accXD = (double) accXInt * 250 / 0x8000;
        double accYD = (double) accYInt * 250 / 0x8000;
        double accZD = (double) accZInt * 250 / 0x8000;
        double gyrXD = (double) gyrXInt * 9.8 / 0x8000 * 16;
        double gyrYD = (double) gyrYInt * 9.8 / 0x8000 * 16;
        double gyrZD = (double) gyrZInt * 9.8 / 0x8000 * 16;
        long currentTime = System.currentTimeMillis();

        if (data.contains("68a80c0001545355")) {//开始
//            //清空sb
//            sb.delete(0, sb.length());
//            sb.append(data + "\n");
//            startTime = currentTime;
            EventBus.getDefault().post(new MsgEvent<>(data));
        } else if (data.substring(10, 14).equals("5453")) {//若第11位至第14位是5453，则原始数据上传
//            sb.append(data + "\n");
            EventBus.getDefault().post(new MsgEvent<>(data));
        } else if (data.substring(10, 14).equals("5454")) {
//            sb.append(data + "\n");
        } else if (data.contains("68a80c00015453aa")) {//结束写入
//            sb.append(data + "\n");
//            String content = sb.toString();
//            fileUtils.writeTxtToFile("开始时间：" + formatter.format(startTime) +
//                            "\n" + content + "\n" + "结束时间：" + formatter.format(currentTime),
//                    "test6Sensor_" + formatter.format(currentTime) + ".csv");
//            uploadFile();
        } else {
//            sb.append(accXD + "," + accYD + "," + accZD + "," + gyrXD + "," + gyrYD + "," + gyrZD + "\n");
            EventBus.getDefault().post(new MsgEvent<>("X轴角速度：" + accXD + "\n" + "Y轴角速度：" + accYD + "\n" + "Z轴角速度：" + accZD + "\n" + "X轴加速度：" + gyrXD + "\n" + "Y轴加速度：" + gyrYD + "\n" + "Z轴加速度：" + gyrZD));
        }
        lastTime = currentTime;

        if (data.toUpperCase().contains("68A80C0001545301") || data.toUpperCase().contains("68A80C0001545303")) {
            fallMsg(0);
            sb.delete(0, sb.length());
            sb.append(data + "\n");
            String content = sb.toString();
            fileUtils.writeTxtToFile("\n" + content, "test6Sensor" + formatter.format(currentTime) + ".csv");
            uploadFile();
        }

        if (data.toUpperCase().contains("68A80C0001545302")) {
            fallMsg(1);
            sb.delete(0, sb.length());
            sb.append(data + "\n");
            String content = sb.toString();
            fileUtils.writeTxtToFile("\n" + content, "test6Sensor" + formatter.format(currentTime) + ".csv");
            uploadFile();
        }
    }


    private void uploadFile() {
        final List<File> fileList = fileUtils.getFile();
        if (fileList.size() == 0)
            return;
        boolean isLogin = (boolean) SPUtils.get(MyApplication.getInstance(), AppConfig.IS_LOGIN, false);
        if (!isLogin)
            return;
        for (final File file : fileList) {
            ApiRequest.uploadFile(file, new HttpSubscriber() {

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    countFile++;
                    if (countFile == fileList.size()) {
                        countFile = 0;
                    }
                    ToastUtil.showShort("文件上传失败");
                }

                @Override
                public void onNext(HttpResult result) {
                    super.onNext(result);
                    countFile++;
                    if (countFile == fileList.size()) {
                        countFile = 0;
                    }
                    if (result.code.equals(AppConfig.SUCCESS)) {
                        fileUtils.deleteFile(file.getName());
                    }
                }
            });
        }
    }

    private void fallMsg(int fallType) {
        //跳转到拨号界面
        Intent dialIntent = new Intent(this, ContactActivity.class);
        dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialIntent);

        ApiRequest.fall(fallType, new HttpSubscriber() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
            }
        });
    }

    private void uploadStepNum() {
        if (CURRENT_STEP == 0)
            return;
        ApiRequest.dailySports(CURRENT_STEP, new HttpSubscriber() {
            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
            }
        });
    }

    private void getBindDeviceInfo() {
        ApiRequest.deviceBindQuery(new HttpSubscriber() {
            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(HttpResult result) {
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        String macAddress = object.optString("macAddress");
                        macAddress = macAddress.replace(":", "").toUpperCase();
                        SPUtils.put(MyApplication.INSTANCE, AppConfig.MAC_ADDRESS, macAddress);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
