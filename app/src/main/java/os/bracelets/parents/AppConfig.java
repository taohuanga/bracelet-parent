package os.bracelets.parents;

/**
 * 医服程序常量类
 * Created by lishiyou on 2017/7/20 0020.
 */

public class AppConfig {

    //是否为调试模式
    public static final boolean isDebug = BuildConfig.BUILD_TYPE.equals("debug");

    public static final String SERVER_URL = "http://47.101.221.44/api/";

    /**
     * 系统程序相关
     */
    //请求成功的标识
    public static final String SUCCESS = "000";
    //每页十条数据
    public static final int PAGE_SIZE = 10;
    //token
    public static final String TOKEN_ID = "token_id";
    public static final String USER_ID = "user_id";
    public static final String USER_IMG = "user_img";
    public static final String USER_NICK = "user_nick";
    public static final String USER_PHONE = "user_nick";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String CITY_CODE = "city_code";
    public static final String CURRENT_DEVICE = "current_device";
    //标记当前程序是否登录
    public static final String IS_LOGIN = "is_login";

    //发送测量数据
    public static final int MSG_BLE_DATA = 65;
    //数据通知
    public static final int MSG_BLE_NOTIFY = 66;


    //蓝牙连接成功
    public static final int MSG_DEVICE_CONNECT = 10;
    //蓝牙失去连接
    public static final int MSG_DEVICE_DISCONNECT = 11;
    //蓝牙连接状态发生变化
    public static final int MSG_DEVICE_CHANGED = 12;

    //手环UUID
    public static final String BLUETOOTH_NAME = "DFZ";
    public static final String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static final String UUID_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static final String UUID_READ_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String UUID_READ_NOTIFY = "00002a00-0000-1000-8000-00805f9b34fb";


}
