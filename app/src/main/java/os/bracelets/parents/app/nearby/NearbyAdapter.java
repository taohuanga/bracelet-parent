package os.bracelets.parents.app.nearby;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import aio.health2world.glide_transformations.CropCircleTransformation;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.NearbyPerson;
import os.bracelets.parents.utils.AppUtils;

/**
 * Created by lishiyou on 2019/2/24.
 */

public class NearbyAdapter extends BaseQuickAdapter<NearbyPerson, BaseViewHolder> {

    public NearbyAdapter(@Nullable List<NearbyPerson> data) {
        super(R.layout.item_nearby_person, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NearbyPerson item) {
        helper.setText(R.id.personName, item.getNickName());
        helper.setText(R.id.personSex, AppUtils.getSex(item.getSex()));
        helper.setText(R.id.personAge, String.valueOf(item.getAge()) + "岁");
        helper.setText(R.id.personDistance, item.getDistance());

        if (item.getUserType() == 0)
            helper.setText(R.id.tvType, "[子女端]");
        else
            helper.setText(R.id.tvType, "[父母端]");

        Glide.with(mContext)
                .load(item.getProfile())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into((ImageView) helper.getView(R.id.personImage));
    }
}
