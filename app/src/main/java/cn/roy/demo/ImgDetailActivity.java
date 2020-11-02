package cn.roy.demo;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @Description: 图片详情页
 * @Author: kk20
 * @Date: 2020/11/2 3:55 PM
 * @Version: v1.0
 */
public class ImgDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_img_detail);

        String url = getIntent().getStringExtra("url");
        PhotoView photoView = findViewById(R.id.photoView);
        photoView.setMinimumScale(0.5f);
        photoView.setMaximumScale(3f);
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                ActivityCompat.finishAfterTransition(ImgDetailActivity.this);
            }
        });
        Glide.with(this).load(url).into(photoView);
    }
}
