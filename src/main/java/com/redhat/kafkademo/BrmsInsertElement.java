package com.redhat.kafkademo;

import com.google.gson.annotations.SerializedName;

public class BrmsInsertElement {
    @SerializedName("out-identifier")
    private String outidentifier="result";
    //private PsiData object=new PsiData();
    private ResultWrapper object=new ResultWrapper();
    @SerializedName("return-object")
    private boolean returnObject=true;
    @SerializedName("entry-point")
    private String entryPoint="DEFAULT";
    public String getOutidentifier() {
        return outidentifier;
    }
    public void setOutidentifier(String outidentifier) {
        this.outidentifier = outidentifier;
    }
    public ResultWrapper getObject() {
        return object;
    }
    public void setObject(ResultWrapper object) {
        this.object = object;
    }
    public String getEntryPoint() {
        return entryPoint;
    }
    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }
}
