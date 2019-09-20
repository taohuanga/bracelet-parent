//package os.bracelets.parents.service;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//
//import com.huichenghe.bleControl.Ble.BluetoothLeService;
//
//import os.bracelets.parents.R;
//
//public class BleService extends BluetoothLeService {
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            createNotificationChannel();
//        }
//        return super.onStartCommand(intent, flags, startId);
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void createNotificationChannel() {
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        // 通知渠道的id
//        String id = "my_channel_02";
//        // 用户可以看到的通知渠道的名字.
//        CharSequence name = getString(R.string.channel_name);
////         用户可以看到的通知渠道的描述
//        String description = getString(R.string.channel_description);
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
////         配置通知渠道的属性
//        mChannel.setDescription(description);
////         设置通知出现时的闪灯（如果 android 设备支持的话）
//        mChannel.enableLights(true);
//        mChannel.setLightColor(Color.RED);
////         设置通知出现时的震动（如果 android 设备支持的话）
//        mChannel.enableVibration(false);
//        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
////         最后在notificationmanager中创建该通知渠道 //
//        mNotificationManager.createNotificationChannel(mChannel);
//
//        // 为该通知设置一个id
//        int notifyID = 2;
//        // 通知渠道的id
//        String CHANNEL_ID = "my_channel_02";
//        // Create a notification and set the notification channel.
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("衣带保父母端") .setContentText("蓝牙扫描服务运行中...")
//                .setSmallIcon(R.mipmap.ic_app_logo)
//                .setChannelId(CHANNEL_ID)
//                .build();
//        startForeground(notifyID,notification);
//    }
//
//}
