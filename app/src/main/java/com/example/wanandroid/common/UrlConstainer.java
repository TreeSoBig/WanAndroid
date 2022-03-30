package com.example.wanandroid.common;

/**
 * Api接口地址
 */
public class UrlConstainer {
    public static final String baseUrl = "https://www.wanandroid.com/";
    /**
     * 登录
     */
    public static final String LOGIN = "user/login";

    /**
     * 注册
     */
    public static final String REGISTER = "user/register";

    /**
     * 首页文章列表
     */
    public static final String HOME_LIST = "article/list/{page}/json";

    /**
     * 首页广告
     */
    public static final String MAIN_BANNER = "banner/json";

    /**
     * 常用网站
     */
    public static final String FRIEND = "friend/json";


    /**
     * 公众号
     */
    public static final String CHAPTERS = "wxarticle/chapters/json";

    /**
     * 公众号文章列表
     */
    public static final String CHAPTER_LIST = "wxarticle/list/{id}/{page}/json";

}
