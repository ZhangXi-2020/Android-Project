package com.zx.chapter5;

import com.google.gson.annotations.SerializedName;

public class Repo {
    // repository name
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
