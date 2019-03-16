package com.notification.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.vo.HttpResult;



/**
 * http工具类
 * 
 * @author qinqx
 * @Date:2016年7月26日上午11:13:38
 * @version
 */
public class HttpsClientUtil {

	 private static final Logger log = LoggerFactory.getLogger(HttpsClientUtil.class);


    public static final int cache = 10 * 1024;

    private static RequestConfig requestConfig;
    private static CloseableHttpClient httpClient;

    static {
        requestConfig = getRequestConfig();
		httpClient = getHttpClient(requestConfig);

    }

    /**
     * 设置RequestConfig配置参数
     * 
     * @author qinqx
     * @param charset
     *            参数编码集, 可空
     * @return DefaultHttpClient 对象
     */
    private static RequestConfig getRequestConfig() {
		RequestConfig customRequestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).setRedirectsEnabled(false).build();
        return customRequestConfig;
    }

    /**
     * 根据自定义参数获取httpClient实例
     * 
     * @author qinqx
     * @param customRequestConfig
     * @return
     */
    private static CloseableHttpClient getHttpClient(RequestConfig customRequestConfig) {

		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 默认信任所有证书
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();

			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				@Override
				public boolean verify(String string, SSLSession ssls) {
					return true;
				}
			};

			SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

			CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(customRequestConfig)
					.setMaxConnTotal(200).setMaxConnPerRoute(20).setRetryHandler(requestRetryHandler)
					.setSSLSocketFactory(sslcsf).build();

			return httpclient;
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}


		return HttpClients.createDefault();
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
	 * post 返回结果为实体
	 * 
	 * @author liuyongchao 2018年1月15日
	 * @param url
	 * @param reqMap
	 * @param encoding
	 * @return
	 */
	public static HttpResult postSSLUrlWithParamsDiffReturn(String url, Map<String, String> reqMap, String encoding) {
		HttpPost post = new HttpPost(url);
		// 添加参数
		List<NameValuePair> params = new ArrayList<>();
		if (reqMap != null && reqMap.keySet().size() > 0) {
			Iterator<Map.Entry<String, String>> iter = reqMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entity = iter.next();
				params.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
			}
		}

		CloseableHttpResponse response = null;

		post.setConfig(requestConfig);

		try {
			// 设置编码格式
			post.setEntity(new UrlEncodedFormEntity(params, encoding));
			response = httpClient.execute(post);
			return new HttpResult(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + encoding);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException("读取流文件异常", e);
		} catch (Exception e) {
			throw new RuntimeException("通讯未知系统异常", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * get 返回结果为实体
	 * 
	 * @author liuyongchao 2018年1月15日
	 * @param url
	 * @return
	 */
	public static HttpResult getSSLUrlWithParamsDiffReturn(String url) {
		HttpGet get = new HttpGet(url);

		CloseableHttpResponse response = null;

		get.setConfig(requestConfig);

		try {
			response = httpClient.execute(get);
			return new HttpResult(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity(), "UTF-8"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException("读取流文件异常", e);
		} catch (Exception e) {
			throw new RuntimeException("通讯未知系统异常", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 返回另外一个路径地址
	 * 
	 * @author liuyongchao 2018年1月15日
	 * @param url
	 * @return
	 */
	public static String gotoOtherUrl(String url) {

		HttpGet get = new HttpGet(url);
		
		CloseableHttpResponse response = null;

		get.setConfig(requestConfig);

		try {
			response = httpClient.execute(get);

			Header location = response.getFirstHeader("Location");

			return location.getValue();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException("读取流文件异常", e);
		} catch (Exception e) {
			throw new RuntimeException("通讯未知系统异常", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 无需本地证书keyStore的SSL https带参数请求
	 * 
	 * @param url
	 * @param paramsMap
	 * @param encoding
	 * @return
	 */
	public static String postSSLUrlWithParams(String url, Map<String, String> reqMap, String encoding) {
		HttpPost post = new HttpPost(url);
		// 添加参数
		List<NameValuePair> params = new ArrayList<>();
		if (reqMap != null && reqMap.keySet().size() > 0) {
			Iterator<Map.Entry<String, String>> iter = reqMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entity = iter.next();
				params.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
			}
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			post.setConfig(requestConfig);
			// 设置编码格式
			post.setEntity(new UrlEncodedFormEntity(params, encoding));
			HttpResponse response = httpClient.execute(post);
			HttpEntity httpEntity = response.getEntity();
			br = new BufferedReader(new InputStreamReader(httpEntity.getContent(), encoding));
			String s = null;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + encoding);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException("读取流文件异常", e);
		} catch (Exception e) {
			throw new RuntimeException("通讯未知系统异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 下载文件到指定路径
	 * 
	 * @author liuyongchao 2018年2月27日
	 * @param fileUrl
	 * @param fileLocal
	 * @throws Exception
	 */
	public static void downloadFile(String fileUrl, String fileLocal) throws Exception {

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			// 默认信任所有证书
			@Override
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();

		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String string, SSLSession ssls) {
				return true;
			}
		};

		URL url = new URL(fileUrl);

		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection urlCon = (HttpsURLConnection) url.openConnection();
		urlCon.setConnectTimeout(6000);
		urlCon.setReadTimeout(6000);
		int code = urlCon.getResponseCode();
		if (code != HttpURLConnection.HTTP_OK) {
			throw new Exception("文件读取失败");
		}
		// 读文件流
		DataInputStream in = new DataInputStream(urlCon.getInputStream());
		DataOutputStream out = new DataOutputStream(new FileOutputStream(fileLocal));
		byte[] buffer = new byte[2048];
		int count = 0;
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
}
