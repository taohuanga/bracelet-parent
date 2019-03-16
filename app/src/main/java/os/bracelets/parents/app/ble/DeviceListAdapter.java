package os.bracelets.parents.app.ble;

import android.support.annotation.Nullable;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import os.bracelets.parents.R;

/**
 * Created by lishiyou on 2019/3/15.
 */

public class DeviceListAdapter extends BaseQuickAdapter<LocalDeviceEntity, BaseViewHolder> {

    public DeviceListAdapter(@Nullable List<LocalDeviceEntity> data) {
        super(R.layout.item_bluethooth_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalDeviceEntity item) {
        helper.setText(R.id.tvBlueName, item.getName());
        helper.setText(R.id.tvBlueRssi, "rssi  " + item.getRssi());
        helper.setText(R.id.tvBlueMac, item.getAddress());

        if(BluetoothLeService.getInstance().isDeviceConnected(item)){
            helper.setTextColor(R.id.tvBlueName, mContext.getResources().getColor(R.color.appThemeColor));
            helper.setTextColor(R.id.tvBlueMac, mContext.getResources().getColor(R.color.appThemeColor));
            helper.setTextColor(R.id.tvBlueRssi, mContext.getResources().getColor(R.color.appThemeColor));
            helper.setImageResource(R.id.ivBlueTooth,R.mipmap.icon_bluetooth_connect);
            helper.setImageResource(R.id.imgConnect,R.mipmap.switch_on);
        }else {
            helper.setTextColor(R.id.tvBlueName, mContext.getResources().getColor(R.color.black6));
            helper.setTextColor(R.id.tvBlueMac, mContext.getResources().getColor(R.color.black6));
            helper.setTextColor(R.id.tvBlueRssi, mContext.getResources().getColor(R.color.black6));
            helper.setImageResource(R.id.ivBlueTooth, R.mipmap.icon_bluetooth_disconnect);
            helper.setImageResource(R.id.ivBlueTooth,R.mipmap.icon_bluetooth_disconnect);
            helper.setImageResource(R.id.imgConnect,R.mipmap.switch_off);
        }

//        helper.addOnClickListener(R.id.imgConnect);
    }
}
