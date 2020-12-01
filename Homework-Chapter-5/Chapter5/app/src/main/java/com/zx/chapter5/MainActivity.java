package com.zx.chapter5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSampleInfo();
            }
        });
    }

    private void requestSampleInfo() {
        GitHubService service = retrofit.create(GitHubService.class);
        Call<List<Repo>> call = service.getRepos("JakeWharton");
        //execute 同步的
        call.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                final List<Repo> body = response.body();
                if (body == null || body.isEmpty()) {
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < body.size(); i++) {
                    final Repo repo = body.get(i);
                    stringBuilder.append("仓库名： ").append(repo.getName()).append("\n");
                }
                ((TextView) findViewById(R.id.tv)).setText(stringBuilder.toString());
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}