package os.bracelets.parents.app.setting;

import android.support.annotation.Nullable;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.SystemMsg;

public class MsgListAdapter extends BaseQuickAdapter<SystemMsg, BaseViewHolder> {


    public MsgListAdapter(@Nullable List<SystemMsg> data) {
        super(R.layout.item_integral_detail, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SystemMsg item) {

        helper.setVisible(R.id.tvIntegral, false);

        helper.setText(R.id.tvTime, item.getDate());

        helper.setText(R.id.tvRemark, item.getContent());

        helper.setText(R.id.tvTitle, item.getTitle());


    }
}
