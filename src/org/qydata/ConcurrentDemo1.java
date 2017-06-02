package org.qydata;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

public class ConcurrentDemo1 {

	public static JSONObject postData(String username,String certNo) throws Exception {
		RequestData requestData = new RequestData();
		// 待验证的持有人姓名
		requestData.setRealName(username);
		// 待验证的身份证号
		requestData.setIdNo(certNo);
		// 账号
		requestData.setAuthId("jmljkj");
		requestData.setTs(System.currentTimeMillis());
		requestData.setReqId(HashHelper.reqId());
		// 密码
		requestData.setSign(HashHelper.md5(requestData.getAuthId() + "12a0240e5a5e474aa6863b0d84387a1e"
				+ requestData.getReqId() + requestData.getTs()));
		String result = ConcurrentDemo1.getData(requestData);
		//解析返回结果
		JSONObject resultJo = JSONObject.fromObject(result);
		return resultJo;
	}

	public static String getData(RequestData requestData) {
		final String uri = "https://api.qydata.org:9000/id/query/photo";
		String json = JSON.toJSONString(requestData);
		try {
			return HttpClient.doPostSSL(uri, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	private static byte[] generateKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		keyGenerator.init(56); // des 必须是56, 此初始方法不必须调用
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();
	}

	/**
	 * 还原密钥
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		DESKeySpec des = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(KEY_ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(des);
		return secretKey;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            原文

	 * @return 密文
	 * @throws Exception
	 */
	public static String encrypt(String data, String keyStr) throws Exception {
		byte[] key = keyStr.getBytes();
		Thread.sleep(1000);
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		byte[] doFinal = cipher.doFinal(data.getBytes());
		byte[] r = cipher.doFinal(data.getBytes());
		return Base64.encodeBase64String(r);
	}

	/**
	 * 解密
	 * 密文
	 * @return 明文、原文
	 * @throws Exception
	 */
	public static String decrypt(String encrypt, String keyStr)
			throws Exception {
		byte[] decodeBase64 = Base64.decodeBase64(encrypt.toString());
		Key k = toKey(keyStr.getBytes());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return new String(cipher.doFinal(decodeBase64));
	}

	// 算法名称
	public static final String KEY_ALGORITHM = "DES";
	// 算法名称/加密模式/填充方式
	public static final String CIPHER_ALGORITHM_ECB = "DES/ECB/PKCS5Padding";
}