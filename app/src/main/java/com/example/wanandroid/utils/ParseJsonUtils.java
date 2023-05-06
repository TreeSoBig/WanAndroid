package com.example.wanandroid.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ParseJsonUtils {

    public static void getArticleData(String mResponseData,
                                      List<String> mArticleTitleList,
                                      List<String> mArticleAuthorList,
                                      List<String> mArticleLinkList,
                                      List<Long>mArticleTimeList)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(mResponseData);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("datas");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                mArticleTitleList.add(jsonObject.getString("title"));
                mArticleAuthorList.add(jsonObject.getString("author"));
                mArticleLinkList.add(jsonObject.getString("link"));
                mArticleTimeList.add(jsonObject.getLong("publishTime"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
