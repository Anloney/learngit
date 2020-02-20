package com.ushareit.logindialog.utils.stats;

import android.text.TextUtils;

import com.ushareit.analytics.Stats;
import com.ushareit.common.lang.ObjectStore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PVEStats {
    private static final String PARAM_PVE_CUR = "pve_cur";
    private static final String PARAM_CONTEXT_CUR = "context_cur";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_EXTRAS = "extras";

    private static final String POPUP_SHOW= "Popup_Show";
    private static final String POPUP_CLICK= "Popup_Click";
    private static final String VE_SHOW= "VE_Show";
    private static final String VE_CLICK= "VE_Click";
    private static final String VE_CLICK_RESULT= "VE_Click_Result";
    private static final String LISTITEM_SHOW= "ListItem_Show";
    private static final String LISTITEM_CLICK= "ListItem_Click";

    public static void popupShow(String pveCur, String contextCur, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur + PVEBuilder.ELEMENT_NONE);
        params.put(PARAM_CONTEXT_CUR, contextCur);
        try {
            if(extras != null && !extras.isEmpty()) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), POPUP_SHOW, params);
        } catch (Exception e) {
        }
    }
    public static void popupClick(String pveCur, String action){
        popupClick(pveCur, null,action, null);
    }
    public static void popupClick(String pveCur, String contextCur,String action, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur + action);
        params.put(PARAM_CONTEXT_CUR, contextCur);
        params.put(PARAM_ACTION, action);
        try {
            if(extras != null && extras.size() > 0) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), POPUP_CLICK, params);
        } catch (Exception e) {
        }
    }
    public static void veShow(String pveCur){
        veShow(pveCur, null, null);
    }

    public static void veShow(String pveCur, String contextCur, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur);
        params.put(PARAM_CONTEXT_CUR, contextCur);
        try {
            if(extras != null && !extras.isEmpty()) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), VE_SHOW, params);
        } catch (Exception e) {
        }
    }

    public static void veClick(String pveCur){
        veClick(pveCur, null, null);
    }

    public static void veClick(String pveCur, String contextCur, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur);
        params.put(PARAM_CONTEXT_CUR, contextCur);
        try {
            if(extras != null) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), VE_CLICK, params);
        } catch (Exception e) {
        }
    }

    public static void veClickResult(String pveCur, String result, String failedMsg, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur);
        params.put("result", result);
        params.put("failed_msg", failedMsg);
        try {
            if(extras != null) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), VE_CLICK_RESULT, params);
        } catch (Exception e) {
        }
    }

    public static void listItemShow(PVEBuilder pveCur, String itemId, String position, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur.append(PVEBuilder.ELEMENT_NONE).build());
        params.put("item_id", itemId);
        params.put("position", position);
        try {
            if(extras != null && !extras.isEmpty()) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), LISTITEM_SHOW, params);
        } catch (Exception e) {
        }
    }

    public static void listItemClick(PVEBuilder pveCur, String itemId, String position, String clickArea, LinkedHashMap<String, String> extras){
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_PVE_CUR, pveCur.append(PVEBuilder.SEPARATOR).append(clickArea).build());
        params.put("item_id", itemId);
        params.put("position", position);
        params.put("click_area", clickArea);
        try {
            if(extras != null) {
                JSONObject json = new JSONObject(extras);
                params.put(PARAM_EXTRAS, json.toString());
            }
//            Stats.onEvent(ObjectStore.getContext(), LISTITEM_CLICK, params);
        } catch (Exception e) {
        }
    }

    public static String getProviderValue(String provider, String itemType) {
        if (TextUtils.isEmpty(provider))
            return null;

        if (TextUtils.isEmpty(itemType))
            return provider;

        return provider + "_" + itemType;
    }


    public static String arrayToString(String[] strings) {
        if (strings == null || strings.length < 1)
            return null;
        StringBuilder result = new StringBuilder();
        for (String s : strings) {
            result.append(s).append('_');
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

}