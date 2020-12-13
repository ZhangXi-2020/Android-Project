package com.zx.tinytiktok.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.zx.tinytiktok.DisplayItem;
import com.zx.tinytiktok.MainActivity;
import com.zx.tinytiktok.R;
import com.zx.tinytiktok.VideoDisplayFull;

import java.util.ArrayList;
import java.util.List;

public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView mAuthorViewL;
    private TextView mDateViewL;
    private ImageView mImageViewL;
    private TextView mAuthorViewR;
    private TextView mDateViewR;
    private ImageView mImageViewR;
    List<View> pagesL = new ArrayList<View>();
    List<View> pagesR = new ArrayList<View>();


    public VideoViewHolder(@NonNull View itemView) {
        super(itemView);
        // TODO: 这里应该传入视频的信息，并传递给下全屏播放页面
        mAuthorViewL = (TextView) itemView.findViewById(R.id.author_left);
        mDateViewL = (TextView) itemView.findViewById(R.id.date_left);
        mImageViewL = (ImageView) itemView.findViewById(R.id.image_left);
        mAuthorViewR = (TextView) itemView.findViewById(R.id.author_right);
        mDateViewR = (TextView) itemView.findViewById(R.id.date_right);
        mImageViewR = (ImageView) itemView.findViewById(R.id.image_right);
        itemView.setOnClickListener(this);
    }

    public void bind(DisplayItem item){
        mAuthorViewL.setText("Author: "+item.getLeft_author());
        mDateViewL.setText("Date: "+item.getLeft_date().substring(0,10)+" "+item.getLeft_date().substring(11,16));
        //Uri imgUriL = Uri.parse(item.getLeft_ImageUrl());
        //mImageViewL.setImageURI(imgUriL);
        addImage(item.getLeft_ImageUrl(),mImageViewL);

        mAuthorViewR.setText("Author: "+item.getRight_author());
        mDateViewR.setText("Date: "+item.getRight_date().substring(0,10)+" "+item.getRight_date().substring(11,16));
        //Uri imgUriR = Uri.parse(item.getRight_ImageUrl());
        //mImageViewL.setImageURI(imgUriR);
        addImage(item.getRight_ImageUrl(),mImageViewR);

        mImageViewL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VideoDisplayFull.class);
                intent.putExtra("extra", item.getLeft_VedioUrl());
                view.getContext().startActivity(intent);
            }
        });

        mImageViewR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VideoDisplayFull.class);
                intent.putExtra("extra", item.getRight_VedioUrl());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), VideoDisplayFull.class);
        //intent.putExtra("extra", mTextView.getText().toString());
        v.getContext().startActivity(intent);
    }

    private void addImage(String path, ImageView img) {
        Glide.with(itemView)
                .load(path)
                //.apply(new RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                //.error(R.drawable.error)
                //.transition(withCrossFade(4000))
                .override(180, 220)
                .into(img);
        //pagesL.add(imageViewL);
    }
}
