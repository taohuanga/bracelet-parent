package os.bracelets.parents.common;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import aio.health2world.utils.AppManager;


/**
 * Created by Administrator on 2018/7/3 0003.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "BaseActivity";

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    protected Activity mContext;

    private SparseArray<View> mViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 隐藏标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //保持屏幕不熄灭
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(getLayoutId());
        initPublic();
        initView();
        initData();
        initListener();
    }

    public void initPublic() {

        mViews = new SparseArray<>();

        AppManager.getInstance().addActivity(this);

    }

    /**
     * 通过id找到view
     */
    public <E extends View> E findView(int viewId) {
        E view = (E) mViews.get(viewId);
        if (view == null) {
            view = (E) findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * view设置OnClick事件
     */
    public <E extends View> void setOnClickListener(E view) {
        view.setOnClickListener(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
