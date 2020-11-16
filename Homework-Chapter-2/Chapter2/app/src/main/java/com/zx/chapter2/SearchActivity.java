package com.zx.chapter2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private RecyclerView mRecycleView;
    private SearchAdapter mSearchAdapter = new SearchAdapter();
    private SearchLayout mSearchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecycleView = findViewById(R.id.recyclerview);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mSearchAdapter);

        final List<String> list = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            list.add("    这是第 " + i + " 行    ");
        }
        mSearchAdapter.notifyItems(list);

        mSearchLayout = findViewById(R.id.search);
        mSearchLayout.setOnSearchTextChangedListener(new SearchLayout.OnSearchTextChangedListener() {
            @Override
            public void afterChanged(String text) {
                Log.i(TAG, "afterChanged: " + text);
                List<String> filters = new ArrayList<>();
                for (String item : list) {
                    if (item.contains(text)) {
                        filters.add(item);
                    }
                }
                mSearchAdapter.notifyItems(filters);
            }
        });
    }
}
