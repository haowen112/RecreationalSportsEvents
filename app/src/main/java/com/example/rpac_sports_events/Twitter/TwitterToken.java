package com.example.rpac_sports_events.Twitter;

import com.google.gson.annotations.SerializedName;

public class TwitterToken {
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String token;

    public void setTokenType(String tokenType){
        this.tokenType = tokenType;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getTokenType(){
        return tokenType;
    }

    public String getToken(){
        return token;
    }
}
