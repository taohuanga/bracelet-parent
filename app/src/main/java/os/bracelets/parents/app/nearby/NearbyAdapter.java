package os.bracelets.parents.app.nearby;

import android.support.annotation.Nullable;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.NearbyPerson;

/**
 * Created by lishiyou on 2019/2/24.
 */

public class NearbyAdapter extends BaseQuickAdapter<NearbyPerson,BaseViewHolder> {

    public NearbyAdapter(@Nullable List<NearbyPerson> data) {
        super(R.layout.item_nearby_person,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NearbyPerson item) {

    }
}
