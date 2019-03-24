package os.bracelets.parents.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;

import com.huichenghe.bleControl.Ble.BleDataForDayData;
import com.huichenghe.bleControl.Ble.DataSendCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.app.ble.BleDataForSensor;
import os.bracelets.parents.common.MsgEvent;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.FileUtils;
import os.bracelets.parents.utils.StringUtils;

/**
 * Created by lishiyou on 2019/1/27.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class AppService extends Service implements DataSendCallback {

    private NotificationManager notificationManager;

    private NotificationCompat.Builder builder;

    private int notifyId = 11;

    private int countFile = 0;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    private FileUtils fileUtils = new FileUtils("Bracelet");

    private StringBuilder sb = new StringBuilder();

    private long lastTime = System.currentTimeMillis();

    private long startTime = System.currentTimeMillis();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);//创建通知消息实例
        builder.setContentTitle("衣带保父母端");
        builder.setContentText("正在上传蓝牙设备数据");
        builder.setWhen(System.currentTimeMillis());//通知栏显示时间
        builder.setSmallIcon(R.mipmap.ic_launcher);//通知栏小图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));//通知栏下拉是图标
        builder.setPriority(NotificationCompat.PRIORITY_MAX);//设置通知消息优先级
        builder.setAutoCancel(true);//设置点击通知栏消息后，通知消息自动消失
        builder.setVibrate(new long[]{0, 1000, 1000, 1000});//通知栏消息震动
        builder.setLights(Color.GREEN, 1000, 2000);//通知栏消息闪灯(亮一秒间隔两秒再亮)
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "name_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager
                    .IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        initData();
    }

    //计时器 十分钟执行一次数据上传操作
    private CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            timer.start();

        }
    };


    private void initData() {
        BleDataForSensor.getInstance().setSensorListener(this);
    }

    @Override
    public void sendSuccess(byte[] bytes) {
        handleData(bytes);
    }

    @Override
    public void sendFailed() {

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
            //清空sb
            sb.delete(0, sb.length());
            sb.append(data + "\n");
            startTime = currentTime;
            EventBus.getDefault().post(new MsgEvent<>(data));
        } else if (data.contains("68a80c00015453aa")) {//结束写入
            sb.append(data + "\n");
            String content = sb.toString();
            fileUtils.writeTxtToFile("开始时间：" + formatter.format(startTime) + "\n" + content + "\n" +
                    "结束时间：" + formatter.format(currentTime), "test6Sensor" + formatter.format(currentTime) + ".csv");
            uploadFile();
        } else if (data.substring(10, 14).equals("5453")) {//若第11位至第14位是5453，则原始数据上传
            sb.append(data + "\n");
            EventBus.getDefault().post(new MsgEvent<>(data));
        } else if (data.substring(10, 14).equals("5454")) {//跌倒报警，并上传报警信息
            sb.append(data + "\n");
            fall();
        } else if (currentTime - lastTime > 5000 && lastTime != 0L) {
            Date currentDate = new Date(currentTime);
            Date lastDate = new Date(lastTime);
            fileUtils.writeTxtToFile("开始时间：" + formatter.format(startTime) + "\n" + sb.toString() + "\n" +
                    "结束时间：" + formatter.format(currentDate), "test6Sensor" + formatter.format(currentDate) + ".csv");
        } else {
            //拼接数据
            sb.append(accXD + "," + accYD + "," + accZD + "," + gyrXD + "," + gyrYD + "," + gyrZD + "\n");
            EventBus.getDefault().post(new MsgEvent<>("X轴角速度：" + accXD + "\n" + "Y轴角速度：" + accYD + "\n" + "Z轴角速度：" + accZD + "\n" + "X轴加速度：" + gyrXD + "\n" + "Y轴加速度：" + gyrYD + "\n" + "Z轴加速度：" + gyrZD));
        }
        lastTime = currentTime;
    }


    private void uploadFile() {
        final List<File> fileList = fileUtils.getFile();
        if (fileList.size() == 0)
            return;
        boolean isLogin = (boolean) SPUtils.get(MyApplication.getInstance(), AppConfig.IS_LOGIN, false);
        if (!isLogin)
            return;

        notificationManager.notify(notifyId, builder.build());
        for (final File file : fileList) {

            ApiRequest.uploadFile(file, new HttpSubscriber() {

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    countFile++;
                    if (countFile == fileList.size()) {
                        notificationManager.cancel(notifyId);
                        countFile = 0;
                    }
                }

                @Override
                public void onNext(HttpResult result) {
                    super.onNext(result);
                    countFile++;
                    if (countFile == fileList.size()) {
                        notificationManager.cancel(notifyId);
                        countFile = 0;
                    }
                    if (result.code.equals(AppConfig.SUCCESS)) {
                        fileUtils.deleteFile(file.getName());
                    }
                }
            });
        }

    }

    private void fall() {
        ApiRequest.fall(new HttpSubscriber() {
            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
