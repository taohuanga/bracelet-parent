package os.bracelets.parents.app.setting;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.SystemMsg;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

public class SystemMsgActivity extends BaseActivity {

    private TitleBar titleBar;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private int pageNo = 1;

    private List<SystemMsg> msgList;

    private MsgListAdapter msgListAdapter;

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
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", getString(R.string.message_list), titleBar);
        refreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.appThemeColor));

        msgList = new ArrayList<>();

        msgListAdapter = new MsgListAdapter(msgList);

        recyclerView.setAdapter(msgListAdapter);

        getMsgList();
    }

    @Override
    protected void initListener() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                getMsgList();
            }
        });
    }

    private void getMsgList() {
        if (pageNo == 1)
            refreshLayout.setRefreshing(true);
        ApiRequest.systemMsg(4, pageNo, new HttpSubscriber() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                refreshLayout.setRefreshing(false);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        JSONArray array = object.optJSONArray("list");
                        List<SystemMsg> list = new ArrayList<>();
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.optJSONObject(i);
                                SystemMsg msg = SystemMsg.parseBean(obj);
                                list.add(msg);
                            }
                        }

                        if (pageNo == 1) {
                            msgList.clear();
                        }
                        msgList.addAll(list);
                        msgListAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
