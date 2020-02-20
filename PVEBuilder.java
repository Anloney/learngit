package com.ushareit.logindialog.utils.stats;

public class PVEBuilder {

    private StringBuilder mStringBuilder;

    private PVEBuilder(){
        mStringBuilder = new StringBuilder();
    }
    private PVEBuilder(String page){
        mStringBuilder = new StringBuilder(page);
    }

    public PVEBuilder append(String s){
        mStringBuilder.append(s);
        return this;
    }
    public String build(){
        return mStringBuilder.toString();
    }

    public static PVEBuilder create(){
        return new PVEBuilder();
    }
    public static PVEBuilder create(String page){
        return new PVEBuilder(page);
    }

    public PVEBuilder clone() {
        return new PVEBuilder(mStringBuilder.toString());
    }
    //element or action
    public static final String SEPARATOR = "/";
    public static final String ELEMENT_NONE = "/0";
    public static final String ELEMENT_OK = "/ok";
    public static final String ELEMENT_CANCEL = "/cancel";
    public static final String ELEMENT_CLICK = "/click";
    public static final String ELEMENT_BACK_KEY = "/back_key";

    @Override
    public String toString() {
        return build();
    }
}
