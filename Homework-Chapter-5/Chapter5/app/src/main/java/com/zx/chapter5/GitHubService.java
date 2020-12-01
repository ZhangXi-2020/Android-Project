package com.zx.chapter5;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubService {
    @GET("users/{username}/repos")
    Call<List<Repo>> getRepos(@Path("username") String userName);
}
