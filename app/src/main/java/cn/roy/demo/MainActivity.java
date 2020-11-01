package cn.roy.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

import cn.roy.demo.adapter.MomentAdapter;
import cn.roy.demo.cache.DataCache;
import cn.roy.demo.model.ChatMoment;
import cn.roy.demo.service.DataDealService;

public class MainActivity extends AppCompatActivity {

    private View vg_top_bar;
    private TextView tv_top_bar_title;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private HeaderAndFooterWrapper<ChatMoment> headerAndFooterWrapper;
    private BroadcastReceiver broadcastReceiver;
    private List<ChatMoment> chatMomentList = new ArrayList<>();
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vg_top_bar = findViewById(R.id.vg_top_bar);
        tv_top_bar_title = findViewById(R.id.tv_top_bar_title);

        smartRefreshLayout = findViewById(R.id.refreshLayout);
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currentPage = 1;
                refreshLayout.setEnableLoadMore(false);
                loadData();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currentPage = currentPage + 1;
                loadData();
            }
        });

        MomentAdapter momentAdapter =
                new MomentAdapter(this, chatMomentList);
        headerAndFooterWrapper = new HeaderAndFooterWrapper(momentAdapter);
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_chat_moment_header, null);
        headerAndFooterWrapper.addHeaderView(headerView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(headerAndFooterWrapper);
        int[] position = new int[2];
        int statusBarHeight = getStatusBarHeight(this);
        int dividerHeight = dip2px(this, 30);
        int topBarHeight = dip2px(this, 45);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                headerView.getLocationOnScreen(position);
                Log.i("roy", "宽高：" + headerView.getWidth() + "/" + headerView.getHeight()
                        + "，滑动：" + position[0] + "/" + position[1]);
                // 判断是否更改背景
                if (position[1] < 0) {
                    int offset = headerView.getHeight() + position[1];
                    if (offset < statusBarHeight + topBarHeight) {// TopBar灰色
                        vg_top_bar.setBackgroundColor(Color.parseColor("#cbcaca"));
                        tv_top_bar_title.setTextColor(Color.parseColor("#666666"));
                    } else if (offset > statusBarHeight + topBarHeight
                            && offset <= statusBarHeight + topBarHeight + dividerHeight) {// TopBar渐变色
                        int x = 255 - 255 * (offset - statusBarHeight - topBarHeight) / dividerHeight;
                        String hexStr = numToHex16(x);
                        vg_top_bar.setBackgroundColor(Color.parseColor("#" + hexStr + "cbcaca"));
                        tv_top_bar_title.setTextColor(Color.parseColor("#" + hexStr + "666666"));
                    } else {// TopBar透明
                        vg_top_bar.setBackgroundColor(Color.TRANSPARENT);
                        tv_top_bar_title.setTextColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        Log.i("roy", "状态栏高度：" + getStatusBarHeight(this));
        // 注册广播
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 模拟数据加载成功
                if (intent.getBooleanExtra("success", false)) {
                    currentPage = 1;
                    smartRefreshLayout.autoRefresh();
                    loadData();
                } else {
                    Toast.makeText(MainActivity.this, "解析数据出错", Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(DataDealService.ACTION_DATA_DEAL_NOTIFY));
        // 数据处理
        DataDealService.enqueueWork(this, DataDealService.class, 100, new Intent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);
    }

    private Handler handler = new Handler();

    private void loadData() {
        // 模拟数据加载
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPage == 1) {
                    smartRefreshLayout.finishRefresh();
                    chatMomentList.clear();
                } else {
                    smartRefreshLayout.finishLoadMore();
                }
                List<ChatMoment> chatMomentsByPage = DataCache.getInstance()
                        .getChatMomentsByPage(currentPage);
                chatMomentList.addAll(chatMomentsByPage);
                headerAndFooterWrapper.notifyDataSetChanged();
                smartRefreshLayout.setEnableLoadMore(DataCache.getInstance().hasMore(currentPage));
            }
        }, 2000);
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen",
                "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String numToHex16(int b) {
        return String.format("%02x", b);
    }
}