package com.zx.tinytiktok.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zx.tinytiktok.Network.VideoService;
import com.zx.tinytiktok.R;
import com.zx.tinytiktok.RecyclerView.VideoAdapter;
import com.zx.tinytiktok.DisplayItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
//import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DisplayFragment extends Fragment {

    // RecyclerView
    private RecyclerView mRecycleView;
    private VideoAdapter mVideoAdapter = new VideoAdapter();

    // Retrofit
    final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api-sjtu-camp.bytedance.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final VideoService videoService = retrofit.create(VideoService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_display, container, false);

        mRecycleView = root.findViewById(R.id.recyclerview);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mVideoAdapter);

        //mVideoAdapter.notifyItems(list);

        videoService.get().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("TAG", "fail!");
                    return;
                }
                final ResponseBody body = response.body();
                if (body == null) {
                    Log.e("TAG", "body is null");
                } else {
                    try {
                        String string = body.string();
                        Log.e("TAG", string);

                        // 将get到的数据添加到recyclerview的adapter中
                        // ((TextView) root.findViewById(R.id.tmp_net_text)).setText(string);
                        List<List> jsonInfoList = authorParser(string);
                        List<String> authorList = jsonInfoList.get(0);
                        List<String> dateList = jsonInfoList.get(1);
                        List<String> imgUrlList = jsonInfoList.get(2);
                        List<String> vedioUrlList = jsonInfoList.get(3);
                        List<DisplayItem> displayItemList = new ArrayList<>();
                        for(int i =0; i+1 < authorList.size(); i+=2)
                        {
                            DisplayItem displayItem = new DisplayItem();
                            displayItem.setLeft_author(authorList.get(i));
                            displayItem.setLeft_date(dateList.get(i));
                            displayItem.setLeft_ImageUrl(imgUrlList.get(i));
                            displayItem.setLeft_VedioUrl(vedioUrlList.get(i));
                            displayItem.setRight_author(authorList.get(i+1));
                            displayItem.setRight_date(dateList.get(i+1));
                            displayItem.setRight_ImageUrl(imgUrlList.get(i+1));
                            displayItem.setRight_VedioUrl(vedioUrlList.get(i+1));
                            displayItemList.add(displayItem);
                        }
                        mVideoAdapter.notifyItems(displayItemList);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

        return root;
    }

    public List<List> authorParser(String json){

        List<String> authorList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        List<String> imgUrlList = new ArrayList<>();
        List<String> vedioUrlList = new ArrayList<>();
        List<List> jsonInfo = new ArrayList<>();
        String string = json.substring(8);
        String[] strings = string.split("\\}");
        for (int i=0; i<strings.length; i++){
             int beginAuthorPos = strings[i].toString().indexOf("user_name") + 12;
             int endAuthorPos = strings[i].toString().indexOf("extra_value") - 3;
             int beginDatePos = strings[i].toString().indexOf("createdAt") + 12;
             int endDatePos = strings[i].toString().indexOf("updatedAt") - 3;
             int beginImagePos = strings[i].toString().indexOf("image_url") + 12;
             int endImagePos = strings[i].toString().indexOf("image_w") - 3;
            int beginVedioPos = strings[i].toString().indexOf("video_url") + 12;
            int endVedioPos = strings[i].toString().indexOf("image_url") - 3;
            if(beginAuthorPos<endAuthorPos && beginDatePos<endDatePos && beginVedioPos<endVedioPos && beginImagePos<endImagePos)
                 {String authorName = strings[i].toString().substring(beginAuthorPos,endAuthorPos);
                 String createTime = strings[i].toString().substring(beginDatePos,endDatePos);
                 String imageURL = strings[i].toString().substring(beginImagePos,endImagePos);
                 String vedioURL = strings[i].toString().substring(beginVedioPos,endVedioPos);
                 authorList.add(authorName);
                 dateList.add(createTime);
                 imgUrlList.add(imageURL);
                 vedioUrlList.add(vedioURL);
            }
        }
        jsonInfo.add(authorList);
        jsonInfo.add(dateList);
        jsonInfo.add(imgUrlList);
        jsonInfo.add(vedioUrlList);
        return jsonInfo;
    }

}

