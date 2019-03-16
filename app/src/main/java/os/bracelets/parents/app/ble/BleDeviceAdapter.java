//package os.bracelets.parents.app.ble;
//
//import android.support.annotation.Nullable;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.data.BleDevice;
//
//import java.util.List;
//
//import aio.health2world.brvah.BaseQuickAdapter;
//import aio.health2world.brvah.BaseViewHolder;
//import os.bracelets.parents.R;
//
///**
// * Created by lishiyou on 2018/11/22 0022.
// */
//
//public class BleDeviceAdapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {
//
//    public BleDeviceAdapter(@Nullable List<BleDevice> data) {
//        super(R.layout.item_bluethooth_list, data);
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, BleDevice device) {
//        if (device == null)
//            return;
//        Button btnConnect = helper.getView(R.id.btnConnect);
//        Button btnDisConnect = helper.getView(R.id.btnDisConnect);
//        ImageView imgConnect = helper.getView(R.id.imgConnect);
//        helper.setText(R.id.tvBlueName, device.getName() == null ? "" : device.getName());
////        helper.setText(R.id.tvBlueMac, "" + device.getMac());
//        helper.setText(R.id.tvBlueRssi, "rssi  " + device.getRssi());
////        helper.setVisible(R.id.tvBlueMac, false);
//        if (BleManager.getInstance().isConnected(device)) {
//            helper.setTextColor(R.id.tvBlueName, mContext.getResources().getColor(R.color.appThemeColor));
////            helper.setTextColor(R.id.tvBlueMac, mContext.getResources().getColor(R.color.appThemeColor));
//            helper.setTextColor(R.id.tvBlueRssi, mContext.getResources().getColor(R.color.appThemeColor));
//            helper.setImageResource(R.id.ivBlueTooth, R.mipmap.icon_bluetooth_connect);
//            btnConnect.setText("已连接");
//            btnConnect.setEnabled(false);
//            btnDisConnect.setEnabled(true);
//            imgConnect.setImageResource(R.mipmap.switch_on);
//        } else {
//            helper.setTextColor(R.id.tvBlueName, mContext.getResources().getColor(R.color.black6));
////            helper.setTextColor(R.id.tvBlueMac, mContext.getResources().getColor(R.color.black6));
//            helper.setTextColor(R.id.tvBlueRssi, mContext.getResources().getColor(R.color.black6));
//            helper.setImageResource(R.id.ivBlueTooth, R.mipmap.icon_bluetooth_disconnect);
//            btnConnect.setText("未连接");
//            btnConnect.setEnabled(true);
//            btnDisConnect.setEnabled(false);
//            imgConnect.setImageResource(R.mipmap.switch_off);
//        }
//        helper.addOnClickListener(R.id.imgConnect);
//        helper.addOnClickListener(R.id.btnConnect);
//        helper.addOnClickListener(R.id.btnDisConnect);
//    }
//}
