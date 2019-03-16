package os.bracelets.parents.app.about;

import android.support.annotation.Nullable;

import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.brvah.BaseViewHolder;

/**
 * Created by lishiyou on 2019/3/7.
 */

public class FeedBackAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public FeedBackAdapter(@Nullable List<String> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
