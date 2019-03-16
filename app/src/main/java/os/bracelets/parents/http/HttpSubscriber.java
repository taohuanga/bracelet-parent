package os.bracelets.parents.http;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.ExceptionHandle;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import rx.Subscriber;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public abstract class HttpSubscriber extends Subscriber<HttpResult> {

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            ExceptionHandle.ResponseThrowable e1 = ExceptionHandle.handleException(e);
            ToastUtil.showLong(e1.message);
        }
    }

    @Override
    public void onNext(HttpResult result) {
        //登录失效004  账号被移除009
        if (!result.code.equals(AppConfig.SUCCESS)) {
            ToastUtil.showShort(result.errorMessage);
        }
        if (result.code.equals("004") || result.code.equals("109")) {
            MyApplication.getInstance().logout();
        }
    }
}
