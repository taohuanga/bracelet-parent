package os.bracelets.parents.app.contact;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;
import aio.health2world.glide_transformations.CropCircleTransformation;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.ContactBean;

/**
 * Created by lishiyou on 2019/2/24.
 */

public class ContactAdapter extends BaseQuickAdapter<ContactBean, BaseViewHolder> {


    public ContactAdapter(@Nullable List<ContactBean> data) {
        super(R.layout.item_contact_person, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, ContactBean item) {
        ImageView personImage = helper.getView(R.id.personImage);
        helper.setText(R.id.personName, item.getNickName());
        helper.setText(R.id.personPhone, item.getPhone());
        Glide.with(mContext)
                .load(item.getProfile())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(personImage);

        helper.addOnClickListener(R.id.ivContact);
    }
}
