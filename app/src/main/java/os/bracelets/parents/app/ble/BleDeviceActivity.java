//package os.bracelets.parents.app.ble;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.os.Build;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.data.BleDevice;
//import com.huichenghe.bleControl.Ble.BleScanUtils;
//import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.List;
//
//import aio.health2world.brvah.BaseQuickAdapter;
//import aio.health2world.rx.rxpermissions.Permission;
//import aio.health2world.rx.rxpermissions.RxPermissions;
//import aio.health2world.utils.Logger;
//import aio.health2world.utils.ToastUtil;
//import os.bracelets.parents.AppConfig;
//import os.bracelets.parents.MyApplication;
//import os.bracelets.parents.R;
//import os.bracelets.parents.common.BaseActivity;
//import os.bracelets.parents.common.MsgEvent;
//import rx.functions.Action1;
//
///**
// * Created by lishiyou on 2019/1/28.
// */
//
//public class BleDeviceActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
//
//    private TextView tvTitle, tvScan;
//
//    private ImageView ivBack;
//
//    private ProgressBar progressBar;
//
//    private RecyclerView recyclerView;
//
//    private BleDeviceAdapter deviceAdapter;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_ble_device;
//    }
//
//    @Override
//    protected void initView() {
//        tvScan = findView(R.id.tvScan);
//        tvTitle = findView(R.id.tvTitle);
//        ivBack = findView(R.id.ivBack);
//        progressBar = findView(R.id.progressBar);
//        recyclerView = findView(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    @Override
//    protected void initData() {
//        deviceAdapter = new BleDeviceAdapter(MyApplication.getInstance().getDeviceList());
//        recyclerView.setAdapter(deviceAdapter);
//
//        //如果手机系统大于大于等于6.0 动态申请权限
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            RxPermissions rxPermissions = new RxPermissions(this);
//            rxPermissions
//                    .request(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION
//                            , Manifest.permission.ACCESS_FINE_LOCATION)
//                    .subscribe(new Action1<Boolean>() {
//                        @Override
//                        public void call(Boolean aBoolean) {
//                            if (aBoolean) {
//                                if (!MyApplication.getInstance().isBleConnect())
//                                    BleUtil.scanDevice();
//                            } else {
//                                ToastUtil.showShort("相关权限被拒绝");
//                            }
//                        }
//                    });
//        } else {
//            if (BleManager.getInstance().isSupportBle()) {
//                if (!BleManager.getInstance().isBlueEnable())
//                    BleManager.getInstance().enableBluetooth();
//                if (!MyApplication.getInstance().isBleConnect()) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    BleUtil.scanDevice();
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void initListener() {
//        EventBus.getDefault().register(this);
//        deviceAdapter.setOnItemChildClickListener(this);
//        ivBack.setOnClickListener(this);
//        tvTitle.setOnClickListener(this);
//        tvScan.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.ivBack:
//            case R.id.tvTitle:
//                finish();
//                break;
//            case R.id.tvScan:
//                startScan();
//                break;
//        }
//    }
//
//    private void startScan() {
//        if (BleManager.getInstance().isSupportBle()) {
//            if (!BleManager.getInstance().isBlueEnable())
//                BleManager.getInstance().enableBluetooth();
//            progressBar.setVisibility(View.VISIBLE);
//            BleUtil.scanDevice();
//            ToastUtil.showShort("正在扫描蓝牙设备");
//        } else {
//            ToastUtil.showShort("该设备不支持蓝牙");
//        }
//    }
//
//    //蓝牙设备扫描完成之后回调
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMsgEvent(MsgEvent event) {
//        //设备扫描完毕
//        if (event.getAction() == AppConfig.MSG_SCAN_FINISH) {
//            ToastUtil.showShort("设备扫描完毕");
//            progressBar.setVisibility(View.INVISIBLE);
////            deviceAdapter.notifyDataSetChanged();
//        }
//        //设备连接状态改变
//        if (event.getAction() == AppConfig.MSG_CONNECTION_CHANGED) {
//            deviceAdapter.notifyDataSetChanged();
//        }
//        //设备连接失败
//        if (event.getAction() == AppConfig.MSG_CONNECTION_FAIL) {
//            BleDevice bleDevice = (BleDevice) event.getT();
//            ToastUtil.showShort("设备" + bleDevice.getName() + "连接失败");
//        }
//        if (event.getAction() == AppConfig.MSG_DEVICE_DISCOVERY) {
//            deviceAdapter.notifyDataSetChanged();
//        }
//    }
//
//
//    @Override
//    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        BleDevice bleDevice = (BleDevice) adapter.getItem(position);
//        switch (view.getId()) {
//            case R.id.btnConnect:
//                if (getConnectCount() > 0) {
//                    ToastUtil.showShort("请先断开已经连接的设备");
//                    return;
//                }
//                if (!BleManager.getInstance().isConnected(bleDevice)) {
//                    ToastUtil.showShort("正在连接 " + bleDevice.getName());
//                    BleUtil.connectDevice(bleDevice);
//                } else {
//                    BleManager.getInstance().disconnect(bleDevice);
//                }
//                break;
//            case R.id.btnDisConnect:
//                if (BleManager.getInstance().isConnected(bleDevice)) {
//                    BleManager.getInstance().disconnect(bleDevice);
//                }
//                break;
//            case R.id.imgConnect:
//                if (BleManager.getInstance().isConnected(bleDevice)) {
//                    BleManager.getInstance().disconnect(bleDevice);
//                } else {
//                    if (getConnectCount() > 0) {
//                        ToastUtil.showShort("请先断开已经连接的设备");
//                    } else {
//                        ToastUtil.showShort("正在连接 " + bleDevice.getName());
//                        BleUtil.connectDevice(bleDevice);
//                    }
//                }
//                break;
//        }
//    }
//
//    private int getConnectCount() {
//        int count = 0;
//        List<BleDevice> list = MyApplication.getInstance().getDeviceList();
//        for (BleDevice device : list) {
//            if (BleManager.getInstance().isConnected(device))
//                count++;
//        }
//        return count;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//}
