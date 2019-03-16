package os.bracelets.parents.common;

/**
 * Created by Administrator on 2018/7/4 0004.
 */

public interface BaseView<T extends BasePresenter> {

    void showLoading();

    void showLoading(String tips);

    void hideLoading();
}

