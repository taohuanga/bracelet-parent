package os.bracelets.parents.app.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BleScanUtils;
import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.Logger;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.common.MsgEvent;
import rx.functions.Action1;

/**
 * Created by lishiyou on 2019/3/15.
 */

public class DeviceListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private TextView tvTitle, tvScan;

    private ImageView ivBack;

    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private DeviceListAdapter listAdapter;

    private LocalDeviceEntity entity;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_ble_device;
    }

    @Override
    protected void initView() {
        tvScan = findView(R.id.tvScan);
        tvTitle = findView(R.id.tvTitle);
        ivBack = findView(R.id.ivBack);
        progressBar = findView(R.id.progressBar);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        listAdapter = new DeviceListAdapter(MyApplication.getInstance().getDeviceList());
        recyclerView.setAdapter(listAdapter);

        //如果手机系统大于大于等于6.0 动态申请权限
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                if (!MyApplication.getInstance().isBleConnect())
                                    startScan();
                            } else {
                                ToastUtil.showShort("相关权限被拒绝");
                            }
                        }
                    });
        } else {
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                //蓝牙开启
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    if (!MyApplication.getInstance().isBleConnect())
                        startScan();
                } else {
                    //未开启蓝牙
                    ToastUtil.showShort("蓝牙未开启");
                }
            }
        }
    }

    @Override
    protected void initListener() {
        listAdapter.setOnItemClickListener(this);
        ivBack.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        tvScan.setOnClickListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BleScanUtils.getBleScanUtilsInstance(getApplicationContext()).stopScan();
        entity = (LocalDeviceEntity) adapter.getItem(position);
        if (BluetoothLeService.getInstance() != null) {
            if (BluetoothLeService.getInstance().isDeviceConnected(entity)) {
                BluetoothLeService.getInstance().disconnect();
            } else {
                ToastUtil.showShort("开始连接设备");
                BluetoothLeService.getInstance().connect(entity);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent event) {
        //设备连接成功
        if (event.getAction() == AppConfig.MSG_DEVICE_CONNECT) {
            MyApplication.getInstance().setBleConnect(true);
            MyApplication.getInstance().setDeviceEntity(entity);
            listAdapter.notifyDataSetChanged();
            this.finish();
        }
        //设备状态发生变化
        if (event.getAction() == AppConfig.MSG_DEVICE_CHANGED) {
            listAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvTitle:
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvScan:
                startScan();
                break;
        }
    }

    private void startScan() {
        ToastUtil.showShort("开始扫描蓝牙设备");
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
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScanStateChange(boolean isChange) {
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
