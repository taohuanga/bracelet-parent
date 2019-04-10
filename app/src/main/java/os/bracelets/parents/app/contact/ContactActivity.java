package os.bracelets.parents.app.contact;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.ContactBean;
import os.bracelets.parents.bean.NearbyPerson;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.StringUtils;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;
import rx.functions.Action1;

/**
 * 一键拨号
 * Created by lishiyou on 2019/2/24.
 */

public class ContactActivity extends MVPBaseActivity<ContactContract.Presenter> implements ContactContract.View,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnItemChildClickListener {

    private TitleBar titleBar;

    private ContactAdapter contactAdapter;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private List<ContactBean> personList;

    private int pageNo = 1;

    private RxPermissions rxPermissions;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_list;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        refreshLayout = findView(R.id.refreshLayout);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "一键拨号", titleBar);
        rxPermissions = new RxPermissions(this);
        refreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.appThemeColor));
        personList = new ArrayList<>();
        contactAdapter = new ContactAdapter(personList);
        recyclerView.setAdapter(contactAdapter);

        contactAdapter.bindToRecyclerView(recyclerView);
        contactAdapter.setEmptyView(R.layout.layout_empty_view);

        onRefresh();
    }

    @Override
    protected ContactContract.Presenter getPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(this);
        contactAdapter.setOnLoadMoreListener(this, recyclerView);
        contactAdapter.setOnItemChildClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        refreshLayout.setRefreshing(true);
        mPresenter.contactList(pageNo);
    }

    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        mPresenter.contactList(pageNo);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        ContactBean contact = (ContactBean) adapter.getItem(position);
        String phone = contact.getPhone();
        if (TextUtils.isEmpty(phone))
            return;
        callPhone(phone);
    }

    @Override
    public void loadContactSuccess(List<ContactBean> contactList) {
        refreshLayout.setRefreshing(false);
        if (pageNo == 1)
            personList.clear();
        personList.addAll(contactList);
        contactAdapter.notifyDataSetChanged();
        if (contactList.size() >= AppConfig.PAGE_SIZE)
            contactAdapter.loadMoreComplete();
        else
            contactAdapter.loadMoreEnd();
    }

    @Override
    public void loadContactError() {
        if (pageNo > 1)
            pageNo--;
        refreshLayout.setRefreshing(false);
    }

    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
//        rxPermissions.request(Manifest.permission.CALL_PHONE)
//                .subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if (aBoolean) {
//
//                        } else {
//                            ToastUtil.showShort("相关权限被拒绝");
//                        }
//                    }
//                });
    }
}
