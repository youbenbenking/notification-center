package com.notification.util;


/**
 * Copyright(c) 2013-2016 by Puhuifinance Inc.
 * All Rights Reserved
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.vo.HttpResult;



/**
 * http工具类
 *
 * @author qinqx
 * @Date:2016年7月26日上午11:13:38
 */
public class HttpClientUtil {
	private  static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static final int cache = 10 * 1024;

    public static final String CHARSET = "UTF-8";

    private static RequestConfig requestConfig;
    private static CloseableHttpClient httpClient;

    static {
        requestConfig = getRequestConfig();
        httpClient = getHttpClient(requestConfig);

    }

    /**
     * 设置RequestConfig配置参数
     *
     * @param charset 参数编码集, 可空
     * @return DefaultHttpClient 对象
     * @author qinqx
     */
    private static RequestConfig getRequestConfig() {
        RequestConfig customRequestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000).build();
        return customRequestConfig;
    }


    /**
     * 设置RequestConfig配置参数
     *
     * @param charset 参数编码集, 可空
     * @return DefaultHttpClient 对象
     * @author qinqx
     */

    private static RequestConfig getRequestConfigForMaoyan() {
        RequestConfig customRequestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000).build();
        return customRequestConfig;
    }


    /**
     * 设置RequestConfig配置参数
     *
     * @param time 单位毫秒
     * @return
     */
    public static RequestConfig setRequestConfig(int time) {
        RequestConfig customRequestConfig = RequestConfig.custom().setSocketTimeout(time).setConnectTimeout(time)
                .setConnectionRequestTimeout(time).build();
        return customRequestConfig;
    }

    /**
     * 根据自定义参数获取httpClient实例
     *
     * @param customRequestConfig
     * @return
     * @author qinqx
     */
    private static CloseableHttpClient getHttpClient(RequestConfig customRequestConfig) {
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(customRequestConfig)
                .setMaxConnTotal(200).setMaxConnPerRoute(20)
                .setRetryHandler(requestRetryHandler).build();
        return httpclient;
    }

    /**
     * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
     *
     * @author qinqx
     * @return
     */
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
        // 自定义的恢复策略
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            // 设置恢复策略，在发生异常时候将自动重试3次
            if (executionCount >= 3) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                return false;
            }
            HttpRequest request = (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
            boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
            if (!idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }
    };

    /**
     * httpGet请求
     *
     * @param url
     * @author qinqx
     */
    public static String httpGet(String url) {
        RequestConfig requestConfig = getRequestConfig();
        CloseableHttpClient httpclient = getHttpClient(requestConfig);
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer("");
        try {
            HttpGet httpget = new HttpGet(url);
            log.info("executing request " + httpget.getURI());
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            log.info("Response StatusLine：" + response.getStatusLine());
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                // 消耗掉response
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error("HTTP protocol exception:" + e.getMessage(), e);
        } catch (IOException e) {
            log.error("HTTP Get IOException:" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("HTTP Get Exception:" + e.getMessage(), e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("关闭连接异常！", e);
            }
        }
        return buffer.toString();
    }

    /**
     * httpPost请求
     *
     * @param url
     * @author qinqx
     */
    public static void httpPost(String url, List<NameValuePair> nvps) {
        RequestConfig requestConfig = getRequestConfig();
        CloseableHttpClient httpclient = getHttpClient(requestConfig);
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);
            httpPost.setEntity(uefEntity);
            log.info("executing request " + httpPost.getURI());
            // 请求url地址
            CloseableHttpResponse response = httpclient.execute(httpPost);
            log.info("Response StatusLine：" + response.getStatusLine());
            try {
                HttpEntity entity = response.getEntity();
                // 消耗掉response
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error("HTTP Post ClientProtocolException:" + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.error("HTTP Post UnsupportedEncodingException:" + e.getMessage(), e);
        } catch (IOException e) {
            log.error("HTTP Post IOException:" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("HTTP Post Exception:" + e.getMessage(), e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("关闭连接异常！", e);
            }
        }
    }

    /**
     * httpPost请求(普通)
     *
     * @param url
     * @author liufei
     */
    public static String httpPost(String url, String json) {
        return httpPost(url, json, null);
    }

    /**
     * httpPost请求(猫眼洗号)
     *
     * @param url
     * @author chenguangyao
     */
    public static String httpPostFormaoyan(String url, String json) {
        return httpPost(url, json, "maoyan");
    }

    /**
     * httpPost请求
     *
     * @param url
     * @author chenguangyao
     */
    private static String httpPost(String url, String json, String type) {

        RequestConfig requestConfig = null;
        if ("maoyan".equals(type)) {
            requestConfig = getRequestConfigForMaoyan();
        } else {
            requestConfig = getRequestConfig();
        }
        CloseableHttpClient httpclient = getHttpClient(requestConfig);
        StringBuffer buffer = new StringBuffer("");
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            StringEntity entity2 = new StringEntity(json, "utf-8");
            entity2.setContentEncoding("UTF-8");
            entity2.setContentType("application/json");
            httpPost.setEntity(entity2);
            log.info("executing request " + httpPost.getURI());
            // 请求url地址
            log.info(httpPost.toString());
            BufferedReader reader = null;
            CloseableHttpResponse response = httpclient.execute(httpPost);
            log.info("Response StatusLine：" + response.getStatusLine());
            try {
                HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                // 消耗掉response
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.info("HTTP Post ClientProtocolException:" + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.info("HTTP Post UnsupportedEncodingException:" + e.getMessage(), e);
        } catch (IOException e) {
            log.info("HTTP Post IOException:" + e.getMessage(), e);
        } catch (Exception e) {
            log.info("HTTP Post Exception:" + e.getMessage(), e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("关闭连接异常！", e);
            }
        }
        return buffer.toString();
    }

    /**
     * httpPost异步请求
     *
     * @param url
     * @param json
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
//    public static ErrorMessage httpPostAsync(String url, String json) throws InterruptedException, ExecutionException, IOException {
//        ErrorMessage errorMessage = new ErrorMessage();
//        errorMessage.setSuccess(false);
//        errorMessage.setMsg("调用失败!");
//        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
//        try {
//            RequestConfig requestConfig = getRequestConfig();
//            // Start the client
//            httpclient.start();
//            // Execute 100 request in async
//            HttpPost httpPost = new HttpPost(url);
//            httpPost.setConfig(requestConfig);
//            StringEntity entity2 = new StringEntity(json, "utf-8");
//            entity2.setContentEncoding("UTF-8");
//            entity2.setContentType("application/json");
//            httpPost.setEntity(entity2);
//            List<Future<HttpResponse>> respList = new LinkedList<Future<HttpResponse>>();
//            respList.add(httpclient.execute(httpPost, null));
//
//            // Print response code
//            for (Future<HttpResponse> response : respList) {
//                response.get().getStatusLine();
//                HttpEntity entity = response.get().getEntity();
//                if (entity != null) {
//                    InputStream is = entity.getContent();
//                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
//                    StringBuffer buffer = new StringBuffer();
//                    String line = "";
//                    while ((line = in.readLine()) != null) {
//                        buffer.append(line);
//                    }
//                    System.out.println(buffer.toString());
//                }
//            }
//            errorMessage.setSuccess(true);
//        } catch (Exception e) {
//            errorMessage.setSuccess(false);
//            errorMessage.setMsg(e.getMessage());
//            log.info("HTTP Post Exception:" + e.getMessage(), e);
//        } finally {
//            httpclient.close();
//        }
//
//        return errorMessage;
//    }

    public static String httpPostWithJson(String url, String json) {

        String result = null;

        RequestConfig requestConfig = getRequestConfig();
        CloseableHttpClient httpclient = getHttpClient(requestConfig);
        InputStream input = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            StringEntity entity2 = new StringEntity(json, "utf-8");
            entity2.setContentEncoding("UTF-8");
            entity2.setContentType("application/json");
            httpPost.setEntity(entity2);
            log.info("executing request " + httpPost.getURI());
            // 请求url地址
            log.info(httpPost.toString());

            CloseableHttpResponse response = httpclient.execute(httpPost);
            log.info("Response StatusLine：" + response.getStatusLine());
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    InputStream is = entity.getContent();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        buffer.append(line);
                    }

                    result = buffer.toString();
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.info("HTTP Post ClientProtocolException:" + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.info("HTTP Post UnsupportedEncodingException:" + e.getMessage(), e);
        } catch (IOException e) {
            log.info("HTTP Post IOException:" + e.getMessage(), e);
        } catch (Exception e) {
            log.info("HTTP Post Exception:" + e.getMessage(), e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("关闭连接异常！", e);
            }
        }

        return result;
    }

    @SuppressWarnings({"deprecation", "resource"})
    public static String download(String url, String filepath) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            if (filepath == null)
                filepath = getFilePath(response);
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer = new byte[cache];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取response要下载的文件的默认路径
     *
     * @param response
     * @return
     */
    public static String getFilePath(HttpResponse response) {
        /* String filepath = root + splash; */
        String filepath = "C:/Users/Dell/Desktop/";
        String filename = getFileName(response);

        if (filename != null) {
            filepath += filename;
        } else {
            filepath += getRandomFileName();
        }
        return filepath;
    }

    /**
     * 获取response header中Content-Disposition中的filename值
     *
     * @param response
     * @return
     */
    public static String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        // filename = new
                        // String(param.getValue().toString().getBytes(),
                        // "utf-8");
                        // filename=URLDecoder.decode(param.getValue(),"utf-8");
                        filename = param.getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filename;
    }

    /**
     * 获取随机文件名
     *
     * @return
     */
    public static String getRandomFileName() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 执行GET请求
     *
     * @param url
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String doGet(String url) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 带有参数的GET请求
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String doGet(String url, Map<String, String> params)
            throws ClientProtocolException, IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        for (String key : params.keySet()) {
            uriBuilder.addParameter(key, params.get(key));
        }
        return doGet(uriBuilder.build().toString());
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static HttpResult doPost(String url, Map<String, String> params) throws IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (params != null) {
            // 设置2个post参数，一个是scope、一个是q
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                parameters.add(new BasicNameValuePair(key, params.get(key)));
            }
            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 提交json数据
     *
     * @param url
     * @param json
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult doPostJson(String url, String json) throws ClientProtocolException, IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        if (json != null) {
            // 构造一个form表单式的实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    /**
     * @param url
     * @param json
     * post json请求格式  返回的数据格式指定为json
     * 适用于对接对方返回对象为ResponseEntity时 ResponseEntity默认返回为xml格式
     * */
    public static HttpResult doPostJsonForAcceptJson(String url, String json) throws ClientProtocolException, IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        if (json != null) {
            // 构造一个form表单式的实体
            StringEntity stringEntity = new StringEntity(json, Charset.forName("UTF-8"));
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static HttpResult doPostJsonTimeLong(String url, String json) throws ClientProtocolException, IOException {

        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfigForMaoyan());

        if (json != null) {
            // 构造一个form表单式的实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 提交json数据
     *
     * @param url
     * @param json
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult doPostJsonByConfig(String url, String json, RequestConfig config) throws ClientProtocolException, IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        HttpResult httpResult = new HttpResult();
        if (json != null) {
            // 构造一个form表单式的实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            httpResult = new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (Exception e) {
            log.error("调用doPostJsonByConfig异常", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return  httpResult;
    }

    public String postUrlWithParams(String url, Map params) {
        return postUrlWithParams(url, params, CHARSET);
    }

    /**
     * 发送post请求 ，带有参数
     *
     * @param url
     * @param params
     * @param encoding
     * @return
     */
    public String postUrlWithParams(String url, Map params, String encoding) {
        String encode = CHARSET;
        if (!StringUtils.isEmpty(encoding)) {
            encode = encoding;
        }
        log.info("HttpClient方式调用开始");
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        // 添加参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null && params.keySet().size() > 0) {
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, encode));
            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();
            br = new BufferedReader(new InputStreamReader(entity.getContent(), encode));
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (StringUtils.isNotBlank(sb)) {
                log.info("服务器响应数据：" + sb.toString());
            }
            return sb.toString();

        } catch (UnsupportedEncodingException e) {
            log.error("创建通信异常", e);
            throw new RuntimeException("创建通信异常", e);
        } catch (ConnectException e) {
            log.error("调用服务器异常", e);
            throw new RuntimeException("调用服务器异常", e);
        } catch (IOException e) {
            log.error("读取流文件异常", e);
            throw new RuntimeException("读取流文件异常", e);
        } catch (Exception e) {
            log.error("通讯未知系统异常", e);
            throw new RuntimeException("通讯未知系统异常", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    log.error("关闭br异常" + e);
                }
            }
        }

    }
    
    public static void main(String[] args) {
    	
		
	}
}
