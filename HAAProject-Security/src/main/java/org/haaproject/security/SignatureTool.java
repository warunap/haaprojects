/**
 * <p>
 * <ul>
 * <li>File: Signaturer.java</li>
 * <li>Create:Jul 3, 2011</li>
 * <li>Author:Geln Yang</li>
 * </ul>
 * </p>
 * <hr>
 * <p>
 * 签名认证工具
 * </p>
 */
package org.haaproject.security;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre><b><font color="blue">Signaturer</font></b></pre>
 * 
 * <pre><b>&nbsp;签名认证工具</b></pre>
 * <pre></pre>
 * 
 * <p>
 * About bouncy castle(BC) JCE provider:<br>
 * The value of key size parameter to initial KeyPairGenerator must be one of
 * the following: 192,239,256,224,384,521
 * 
 * @see org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator.EC
 *      </p>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class SignatureTool {

	private static final String DEFAULT_ENCODE = "UTF-8";

	private static final String ASYMMETRIC_CIPHER = "RSA";

	private static final String SYMMETRIC_CIPHER = "RSA";

	private static final String SIGNATURE_SIPHER = "MD5withRSA";

	private static final int KEY_SIZE = 512;

	/**
	 * 产生密钥对
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		return generateKeyPair(null);
	}

	/**
	 * 产生密钥对
	 */
	public static KeyPair generateKeyPair(String cipher) throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(cipher == null ? ASYMMETRIC_CIPHER : cipher);
		SecureRandom secrand = new SecureRandom();
		secrand.setSeed(System.currentTimeMillis());
		keygen.initialize(KEY_SIZE, secrand);
		return keygen.genKeyPair();
	}

	/**
	 * 使用私钥对文本串进行签名，返回编码后的结果
	 */
	public static String sign(String privateKey, String text) throws Exception {
		Signature signet = java.security.Signature.getInstance(SIGNATURE_SIPHER);
		signet.initSign((PrivateKey) convert2Key(privateKey, false, false));
		signet.update(s2b(text));
		return b2s(Base64.encodeBase64(signet.sign()));
	}

	/**
	 * 使用公钥验证签名是否正确
	 */
	public static boolean verify(String publicKey, String signatureText, String text) throws Exception {
		Signature signature = Signature.getInstance(SIGNATURE_SIPHER);
		signature.initVerify((PublicKey) convert2Key(publicKey, true, false));
		signature.update(s2b(text));
		byte[] signed = decode64(signatureText.getBytes(DEFAULT_ENCODE));
		if (signature.verify(signed))
			return true;
		else
			return false;
	}

	/**
	 * 使用公钥加密
	 */
	public static String publicEncrypt(String publicKey, String text) throws Exception {
		return encrypt(publicKey, true, text);
	}

	/**
	 * 使用私钥加密
	 */
	public static String privateEncrypt(String privateKey, String text) throws Exception {
		return encrypt(privateKey, false, text);
	}

	/**
	 * 使用公钥解密
	 */
	public static String publicDecrypt(String publicKey, String text) throws Exception {
		return decrypt(publicKey, true, text);
	}

	/**
	 * 使用私钥解密
	 */
	public static String privateDecrypt(String privateKey, String text) throws Exception {
		return decrypt(privateKey, false, text);
	}

	/**
	 * 返回编码后的私钥字符串
	 */
	public static String getEncodedPrivateKey(KeyPair keyPair) throws UnsupportedEncodingException {
		return b2s(encode64(keyPair.getPrivate().getEncoded()));
	}

	/**
	 * 返回编码后的公钥字符串
	 */
	public static String getEncodedPublicKey(KeyPair keyPair) throws UnsupportedEncodingException {
		return b2s(encode64(keyPair.getPublic().getEncoded()));
	}

	/**
	 * 加密
	 */
	private static String encrypt(String encodedKey, boolean isPublicKey, String text) throws Exception {
		Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER);
		cipher.init(Cipher.ENCRYPT_MODE, convert2Key(encodedKey, isPublicKey, true));
		return b2s(encode64(cipher.doFinal(s2b(text))));
	}

	/**
	 * 解密
	 */
	private static String decrypt(String encodedKey, boolean isPublicKey, String encryptString) throws Exception {
		Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, convert2Key(encodedKey, isPublicKey, true));
		byte[] decodeVal = decode64(s2b(encryptString));
		return b2s(cipher.doFinal(decodeVal));
	}

	/**
	 * 将编码key字符串转换为{@link Key}对象
	 */
	private static Key convert2Key(String encodedKey, boolean isPublicKey, boolean symmetric) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(symmetric ? SYMMETRIC_CIPHER : ASYMMETRIC_CIPHER);
		if (isPublicKey) {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode64(s2b(encodedKey)));
			return keyFactory.generatePublic(keySpec);
		} else {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode64(s2b(encodedKey)));
			return keyFactory.generatePrivate(keySpec);

		}
	}

	private static byte[] encode64(byte[] s) {
		return Base64.encodeBase64(s);
	}

	private static byte[] decode64(byte[] s) {
		return Base64.decodeBase64(s);
	}

	private static String b2s(byte[] bytes) throws UnsupportedEncodingException {
		return new String(bytes, DEFAULT_ENCODE);
	}

	private static byte[] s2b(String s) throws UnsupportedEncodingException {
		return s.getBytes(DEFAULT_ENCODE);
	}

}
