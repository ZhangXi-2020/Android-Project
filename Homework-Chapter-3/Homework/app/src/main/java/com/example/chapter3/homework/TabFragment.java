package com.example.chapter3.homework;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

// PlaceholderFragment class has already been created. This TabFragment has the same function.
public class TabFragment<CommonAdapter> extends Fragment {

    private LottieAnimationView animationView;
    private TextView hellotext;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hello, container, false);
        animationView = root.findViewById(R.id.loading);
        hellotext = root.findViewById(R.id.hellotext);
        animationView.setVisibility(LottieAnimationView.VISIBLE);
        hellotext.setVisibility(View.INVISIBLE);


        ValueAnimator animator = ValueAnimator.ofInt(0,5000).setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int)animation.getAnimatedValue();
                if(curValue < 1000) {
                    animationView.setAlpha(1f*curValue/1000);
                }
                if(curValue > 4000) {
                    animationView.setAlpha(5f-1f*curValue/1000);
                }
                if(curValue==5000) {
                    animationView.pauseAnimation();
                    animationView.cancelAnimation();
                    animationView.setVisibility(LottieAnimationView.INVISIBLE);
                    hellotext.setVisibility(View.VISIBLE);
                }
            }
        });
        animator.start();

        return root;


    }


}