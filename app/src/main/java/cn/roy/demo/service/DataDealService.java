package cn.roy.demo.service;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.roy.demo.cache.DataCache;
import cn.roy.demo.model.ChatMoment;

/**
 * @Description
 * @Author kk20
 * @Date 2020/11/1
 * @Version V1.0.0
 */
public class DataDealService extends JobIntentService {
    public static final String ACTION_DATA_DEAL_NOTIFY = "action_data_deal_notify";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("roy","处理任务");
        boolean deal = deal();
        Intent broadcastIntent = new Intent(ACTION_DATA_DEAL_NOTIFY);
        broadcastIntent.putExtra("success", deal);
        sendBroadcast(broadcastIntent);
    }

    private boolean deal() {
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = getAssets().open("moments.json");
            bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[8 * 1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            final String json = new String(bos.toByteArray());
            List<ChatMoment> chatMoments = JSON.parseArray(json, ChatMoment.class);
            DataCache.getInstance().setChatMoments(chatMoments);
            Log.i("roy","解析完成");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null)
                    is.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
