package com.zx.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api-sjtu-camp.bytedance.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final TestService testService = retrofit.create(TestService.class);

    String studentId = "123";
    String userName = "hhh";
    String coverImageName = "pic.png";
    String videoName = "video.mp4";


    private MultipartBody.Part getMultipartFromAsset(String name, String fileName) {
        final AssetManager assetManager = getAssets();
        try {
            RequestBody requestFile = RequestBody
                    .create(MediaType.parse("multipart/form-data"), toByteArray(assetManager.open(fileName)));
            return MultipartBody.Part.createFormData(name, fileName, requestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.get_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testService.post(studentId, userName, getMultipartFromAsset("image",coverImageName),getMultipartFromAsset("video", videoName))
                        .enqueue(new Callback<ResponseBody>() {
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
                                final String string = body.string();
                                Log.e("TAG", string);
                                ((TextView) findViewById(R.id.text)).setText(string);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }
        });
    }
}