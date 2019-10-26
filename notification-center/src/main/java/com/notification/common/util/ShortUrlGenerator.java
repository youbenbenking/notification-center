package com.notification.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ShortUrlGenerator {

    public static String getShortUrl(String url) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        java.io.InputStream is = null;

        String url1 = "http://api.t.sina.com.cn/short_url/shorten.json";
        //封装请求参数
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("source", "3271760578"));
        params.add(new BasicNameValuePair("url_long", url));
        String str = "";
        try {
            //转换为键值对
            str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
            //创建Get请求
            HttpGet httpGet = new HttpGet(url1+"?"+str);
            //执行Get请求，
            response = httpClient.execute(httpGet);
            //得到响应体
            org.apache.http.HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity, "UTF-8");
            JSONArray jo = JSON.parseArray(data);
            JSONObject obj = (JSONObject) jo.get(0);
            String str1 = (String) obj.get("url_short");
            return str1;
        } catch (ParseException e) {
        		e.printStackTrace();
        } catch (IOException e) {
        		e.printStackTrace();
        } finally{
            //关闭输入流，释放资源
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
            //消耗实体内容
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {

                }
            }
            //关闭相应 丢弃http连接
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }
}
