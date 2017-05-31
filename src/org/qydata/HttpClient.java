package org.qydata;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClient {

	@SuppressWarnings("deprecation")
	public static String doPostSSL(String baseUrl, String param) throws Exception {
		X509TrustManager trustManager = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(null, new TrustManager[]{trustManager}, null);
		SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sf, 443));
		HttpPost httpPost = new HttpPost(baseUrl);
		System.out.println("executing request " + httpPost.getURI());
		String result = "";
		httpPost.setHeader("Content-type", "application/json");

		StringEntity reqEntity;
		// 将请求参数封装成HttpEntity
		reqEntity = new StringEntity(param, Charset.forName("UTF-8").toString());
		httpPost.setEntity(reqEntity);
		BufferedHttpEntity bhe = new BufferedHttpEntity(reqEntity);
		httpPost.setEntity(bhe);
		HttpResponse response = httpclient.execute(httpPost);
		HttpEntity resEntity = response.getEntity();
		InputStreamReader reader = new InputStreamReader(resEntity.getContent());
		char[] buff = new char[1024];
		int length = 0;
		while ((length = reader.read(buff)) != -1) {
			result += new String(buff, 0, length);
		}
		httpclient.getConnectionManager().shutdown();
		return result;
	}
}
