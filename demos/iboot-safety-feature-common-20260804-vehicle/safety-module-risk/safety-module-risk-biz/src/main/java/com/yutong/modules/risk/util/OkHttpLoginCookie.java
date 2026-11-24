package com.yutong.modules.risk.util;

import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class OkHttpLoginCookie {
    public static void main(String[] args) throws IOException {
        // OkHttpClient自带CookieJar自动管理cookie
        OkHttpClient client = new OkHttpClient();

        // 表单参数构建
        RequestBody formBody = new FormBody.Builder()
                .add("username", "wangwjh")
                .add("password", "L9D5xVDe5FNgYlWAZOvcgg==")
                .add("service", "")
                .build();

        Request request = new Request.Builder()
                .url("http://eiptest.yutong.com/portalapi/self/login/doLogin")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // 1. 获取所有Set-Cookie
            List<Cookie> cookieList = Cookie.parseAll(request.url(), response.headers());

            System.out.println("=== 全部Cookie ===");
            StringBuilder cookieTotal = new StringBuilder();
            for (Cookie cookie : cookieList) {
                System.out.println(cookie.toString());
                cookieTotal.append(cookie.name()).append("=").append(cookie.value()).append("; ");
            }

            System.out.println("\n合并Cookie字符串：");
            System.out.println(cookieTotal.toString().trim());

            // 打印返回内容
            System.out.println("\n响应数据：" + response.body().string());
        }
    }
}