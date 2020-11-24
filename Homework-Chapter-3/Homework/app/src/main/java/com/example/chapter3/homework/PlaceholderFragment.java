package com.example.chapter3.homework;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderFragment extends Fragment {

    private LottieAnimationView animationView;
    private AnimatorSet animatorSet;

    private List<Map<String, Object>> lists;
    private SimpleAdapter adapter;
    private ListView listView;
    private String[] name = {"张三","李四","王五","赵六","孙七","周八","吴九","郑十"};
    private int[] imageViews = {R.mipmap.tx0,R.mipmap.tx1,R.mipmap.tx2,R.mipmap.tx3,
            R.mipmap.tx4,R.mipmap.tx5,R.mipmap.tx6,R.mipmap.tx7};  //用到的图片是mipmap中的ic_launcher

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        View root = inflater.inflate(R.layout.fragment_placeholder, container, false);
        animationView = root.findViewById(R.id.loading);
        listView = (ListView) root.findViewById(R.id.listview);
        animationView.setAlpha(1f);
        listView.setAlpha(0f);

        lists = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", imageViews[i]);
            map.put("name", name[i]);
            lists.add(map);
        }

        adapter = new SimpleAdapter(getActivity(), lists, R.layout.friendlist,
                new String[]{"image", "name"}, new int[]{R.id.image1, R.id.name});

        listView.setAdapter(adapter);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(animationView, "alpha",
                        1f, 0f);
                animator1.setDuration(1000);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(listView, "alpha",
                        0f, 0f, 1f);
                animator2.setDuration(2000);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator1);
                animatorSet.playTogether(animator2);
                animatorSet.start();
            }
        }, 5000);
    }
}
