package com.redhat.kafkademo;

import com.google.gson.annotations.SerializedName;

public class ResultWrapper {

    @SerializedName("com.myspace.drl_fraud.Result")
    private Result result=new Result();

    public Result getPsiDate() {
        return result;
    }

    public void setPsiDate(Result result) {
        this.result = result;
    }

}
