package com.zx.tinytiktok;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.io.IOException;

// TODO: 视频全屏播放页面，单击暂停继续，双击点赞
public class VideoDisplayFull extends AppCompatActivity implements MyClickListener.MyClickCallBack {

    private SurfaceView mSurfaceView;
    private MediaPlayer player;
    int progress = 0;
    private ImageView likeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vedio_layout);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        likeImage = (ImageView) findViewById(R.id.like);


        setListener();

        Intent intent = getIntent();
        String vedioString = intent.getStringExtra("extra");
        Uri uri = Uri.parse(vedioString);
        //Log.e("VedioDisplayFull", vedioString);

        player = new MediaPlayer();
        try{
            player.setDataSource(VideoDisplayFull.this,uri);
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.setFormat(PixelFormat.TRANSPARENT);
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                    player.setLooping(true);
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    System.out.println(i);
                }
            });
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setListener() {
        mSurfaceView.setOnTouchListener(new MyClickListener(this));
    }


    @Override
    public void oneClick() {
        if (player.isPlaying()){
            player.pause();
        }
        else {
            player.start();
        }
    }

    @Override
    public void doubleClick(){

        AnimatorSet animatorSet = new AnimatorSet();
        //缩放动画，X轴2倍缩小至0.9倍
        animatorSet.play(scale(likeImage, "scaleX", 2f, 0.9f, 100, 0))
                //缩放动画，Y轴2倍缩放至0.9倍
                .with(scale(likeImage, "scaleY", 2f, 0.9f, 100, 0))
                //渐变透明动画，透明度从0-1
                .with(alpha(likeImage, 0, 1, 100, 0))
                //缩放动画，X轴0.9倍缩小至
                //.with(scale(likeImage, "scaleX", 0.9f, 1, 50, 150))
                //缩放动画，Y轴0.9倍缩放至
                //.with(scale(likeImage, "scaleY", 0.9f, 1, 50, 150))
                //位移动画，Y轴从0上移至600
                .with(translationY(likeImage, 0, -800, 800, 100))
                //透明动画，从1-0
                .with(alpha(likeImage, 1, 0, 300, 800))
                //缩放动画，X轴1至3倍
                .with(scale(likeImage, "scaleX", 1, 3f, 700, 800))
                //缩放动画，Y轴1至3倍
                .with(scale(likeImage, "scaleY", 1, 3f, 700, 800));
        //开始动画
        animatorSet.start();
        Log.e("TAG","animation Play");
    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , propertyName
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationX"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationY"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "alpha"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        return rotation;
    }
}