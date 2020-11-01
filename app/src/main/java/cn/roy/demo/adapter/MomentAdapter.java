package cn.roy.demo.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import cn.roy.demo.MainActivity;
import cn.roy.demo.R;
import cn.roy.demo.model.ChatMoment;

/**
 * @Description
 * @Author kk20
 * @Date 2020/11/1
 * @Version V1.0.0
 */
public class MomentAdapter extends CommonAdapter<ChatMoment> {

    public MomentAdapter(Context context, List<ChatMoment> datas) {
        super(context, R.layout.item_chat_moment, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, ChatMoment chatMoment, int i) {
        viewHolder.setText(R.id.tv_user_name, chatMoment.getSender().getNick());
        TextView tv_moment_content = viewHolder.getView(R.id.tv_moment_content);
        if (TextUtils.isEmpty(chatMoment.getContent())) {
            tv_moment_content.setVisibility(View.GONE);
        } else {
            tv_moment_content.setText(chatMoment.getContent());
            tv_moment_content.setVisibility(View.VISIBLE);
        }
        View vg_img_container = viewHolder.getView(R.id.vg_img_container);
        List<ChatMoment.ImagesBean> images = chatMoment.getImages();
        if (images == null || images.size() == 0) {
            vg_img_container.setVisibility(View.GONE);
        } else {
            vg_img_container.setVisibility(View.VISIBLE);
            displayImages(vg_img_container, images);
        }

        View vg_action = viewHolder.getView(R.id.vg_action);
        viewHolder.getView(R.id.iv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewGroup.LayoutParams layoutParams = vg_action.getLayoutParams();
                int start = layoutParams.width;
                int end = start == 0 ? MainActivity.dip2px(mContext, 160) : 0;
                ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int width = (int) animation.getAnimatedValue();
                        Log.i("roy", "width=" + width);
                        layoutParams.width = width;
                        vg_action.setLayoutParams(layoutParams);
                    }
                });
                valueAnimator.setDuration(500);
                valueAnimator.start();
            }
        });
        View vg_comment_container = viewHolder.getView(R.id.vg_comment_container);
    }

    private void displayImages(View containerView, List<ChatMoment.ImagesBean> images) {
        int size = images.size();
        if (size == 1) {
            containerView.findViewById(R.id.vg_img_mul).setVisibility(View.GONE);
            containerView.findViewById(R.id.iv_img_single).setVisibility(View.VISIBLE);
            ImageView iv_single = containerView.findViewById(R.id.iv_img_single);
            iv_single.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(images.get(0).getUrl())
                    .into(iv_single);
        } else {
            containerView.findViewById(R.id.vg_img_mul).setVisibility(View.VISIBLE);
            containerView.findViewById(R.id.iv_img_single).setVisibility(View.GONE);
            View vg_3 = containerView.findViewById(R.id.vg_3);
            View vg_6 = containerView.findViewById(R.id.vg_6);
            View vg_9 = containerView.findViewById(R.id.vg_9);
            if (size < 4) {
                vg_3.setVisibility(View.VISIBLE);
                vg_6.setVisibility(View.GONE);
                vg_9.setVisibility(View.GONE);
            } else if (size < 7) {
                vg_3.setVisibility(View.VISIBLE);
                vg_6.setVisibility(View.VISIBLE);
                vg_9.setVisibility(View.GONE);
            } else {
                vg_3.setVisibility(View.VISIBLE);
                vg_6.setVisibility(View.VISIBLE);
                vg_9.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < 9; i++) {
                ImageView iv = containerView.findViewById(getIdByIdentify((i + 1)));
                if (i < images.size()) {
                    iv.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(images.get(i).getUrl())
                            .into(iv);
                } else {
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private int getIdByIdentify(int index) {
        String str = "iv_" + index;
        Resources res = mContext.getResources();
        int viewId = res.getIdentifier(str,// 需要转换的资源名称
                "id",                 // 资源类型
                mContext.getPackageName());   // R类所在的包名
        return viewId;
    }

}
