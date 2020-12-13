package com.zx.tinytiktok;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

/**
 * 开屏页
 *
 */
public class BeginActivity extends Activity {

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle arg0) {
        final View view = View.inflate(this, R.layout.activity_begin, null);
        setContentView(view);
        super.onCreate(arg0);
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy()) //设置动画效果
                .create();

//        openingStartAnimation.show(this);
        TextView skipBtn = findViewById(R.id.skip);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override public void run() {
                //跳转到首页
                jumpToMainActivity();
            }
        };
        handler.postDelayed(runnable, 3000);
        skipBtn.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                handler.removeCallbacks(runnable);
                jumpToMainActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        new Thread(new Runnable() {
//            public void run() {
//                long start = System.currentTimeMillis();
//                long costTime = System.currentTimeMillis() - start;
//                //等待sleeptime时长
//                if (sleepTime - costTime > 0) {
//                    try {
//                        Thread.sleep(sleepTime - costTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //进入主页面
//                startActivity(new Intent(BeginActivity.this, MainActivity.class));
//                finish();

//            }
//        }).start();
    }

    private void jumpToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}

