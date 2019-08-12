package os.bracelets.parents.app.personal;

import android.support.annotation.Nullable;

import com.amap.api.services.core.PoiItem;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import os.bracelets.parents.R;

public class AmapSearchAdapter extends BaseQuickAdapter<PoiItem, BaseViewHolder> {

    public AmapSearchAdapter(@Nullable List<PoiItem> data) {
        super(R.layout.item_amap_search, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiItem item) {
        helper.setText(R.id.tvAddress, item.toString());
    }
}
