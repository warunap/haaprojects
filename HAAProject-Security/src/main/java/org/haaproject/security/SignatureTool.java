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
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre><b><font color="blue">Signaturer</font></b></pre>
 * 
 * <pre><b>&nbsp;签名认证工具</b></pre>
 * <pre></pre>
 * 
 * <p>
 * This tool use bouncy castle(BC) as JCE provider.
 * </p>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class SignatureTool {

	private static final String DEFAULT_ENCODE = "UTF-8";

	/**
	 * BC provide a asymmetric cipher EC.
	 */
	private static String getAsymmetricCipher() {
		return "EC";
		//return "RSA";
	}

	private static String getSignatureCipher() {
		return "ECDSA";
		//return "MD5withRSA";
	}

	/**
	 * The value of key size parameter to initial KeyPairGenerator must be one
	 * of the following: 192,239,256,224,384,521
	 * 
	 * @see org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator.EC
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(getAsymmetricCipher());
		SecureRandom secrand = new SecureRandom();
		secrand.setSeed(System.currentTimeMillis());
		keygen.initialize(521, secrand);
		return keygen.genKeyPair();
	}

	public static String sign(String privateKey, String text) throws Exception {
		Signature signet = java.security.Signature.getInstance(getSignatureCipher());
		signet.initSign((PrivateKey) convert2Key(privateKey, false));
		signet.update(text.getBytes(DEFAULT_ENCODE));
		return new String(Base64.encodeBase64(signet.sign()), DEFAULT_ENCODE);
	}

	public static boolean verify(String publicKey, String signatureText, String text) throws Exception {
		Signature signature = Signature.getInstance(getSignatureCipher());
		signature.initVerify((PublicKey) convert2Key(publicKey, true));
		signature.update(text.getBytes(DEFAULT_ENCODE));
		byte[] signed = decode64(signatureText.getBytes(DEFAULT_ENCODE));
		if (signature.verify(signed))
			return true;
		else
			return false;
	}

	/**
	 * TODO
	 * 
	 */
	public static String encrypt(String encodedKey, boolean isPublicKey, String text) throws Exception {
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");// Create an 8-byte initialization vector
		byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
				0x0d, 0x0e, 0x0f };

		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, convert2Key(encodedKey, isPublicKey), paramSpec);
		return new String(encode64(cipher.doFinal(text.getBytes(DEFAULT_ENCODE))), DEFAULT_ENCODE);
	}

	/**
	 * TODO
	 * 
	 */
	public static String decrypt(String encodedKey, boolean isPublicKey, String encryptString) throws Exception {
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, convert2Key(encodedKey, isPublicKey));
		byte[] decodeVal = decode64(encryptString.getBytes(DEFAULT_ENCODE));
		return new String(cipher.doFinal(decodeVal), DEFAULT_ENCODE);
	}

	/**
	 * @see org.bouncycastle.jce.provider.asymmetric.ec.KeyFactory#engineGeneratePublic(KeySpec)
	 */
	private static Key convert2Key(String encodedKey, boolean isPublicKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(getAsymmetricCipher());
		if (isPublicKey) {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode64(encodedKey.getBytes(DEFAULT_ENCODE)));
			return keyFactory.generatePublic(keySpec);
		} else {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode64(encodedKey.getBytes(DEFAULT_ENCODE)));
			return keyFactory.generatePrivate(keySpec);

		}
	}

	private static byte[] encode64(byte[] s) {
		return Base64.encodeBase64(s);
	}

	private static byte[] decode64(byte[] s) {
		return Base64.decodeBase64(s);
	}

	public static String getEncodedPrivateKey(KeyPair keyPair) throws UnsupportedEncodingException {
		return new String(encode64(keyPair.getPrivate().getEncoded()), DEFAULT_ENCODE);
	}

	public static String getEncodedPublicKey(KeyPair keyPair) throws UnsupportedEncodingException {
		return new String(encode64(keyPair.getPublic().getEncoded()), DEFAULT_ENCODE);
	}
}
