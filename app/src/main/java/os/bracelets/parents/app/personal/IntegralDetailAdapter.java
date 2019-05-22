package os.bracelets.parents.app.personal;

import android.support.annotation.Nullable;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import os.bracelets.parents.R;

public class IntegralDetailAdapter extends BaseQuickAdapter<IntegralInfo, BaseViewHolder> {


    public IntegralDetailAdapter(@Nullable List<IntegralInfo> data) {
        super(R.layout.item_integral_detail, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, IntegralInfo item) {
        helper.setText(R.id.tvTime, item.getCreateDate());

        helper.setText(R.id.tvRemark, item.getRemark());
        //+积分
        if (item.getChangeType() == 0) {
            helper.setText(R.id.tvIntegral, "+" + item.getIntegral());
            helper.setTextColor(R.id.tvIntegral,mContext.getResources().getColor(R.color.appThemeColor));
        }
        //-积分
        if (item.getChangeType() == 1) {
            helper.setText(R.id.tvIntegral, "-" + item.getIntegral());
            helper.setTextColor(R.id.tvIntegral,mContext.getResources().getColor(R.color.red));
        }

    }
}
