/**
 *      Project:HAAProject-Security       
 *    File Name:SymmetricCipherTest.java	
 * Created Time:2012-12-9下午4:40:00
 */
package org.haaproject.security;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Geln Yang
 * 
 */
public class SymmetricCipherTest {

	private static final String DEFAULT_ENCODE = "UTF-8";

	/**
	 * AES Blowfish DES Triple DES Serpent Twofish
	 * 
	 */
	public static void main(String[] args) throws Exception {
		Key key = SecurityTool.generateKey();
		String text = "hello world";
		System.out.println("src:" + text);
		String encryptString = SecurityTool.encrypt(key, text);
		System.out.println(encryptString);
		String decryptString = SecurityTool.decrypt(key, encryptString);
		System.out.println(decryptString);
	}

	public static void orignalTest() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance("DESede");
		kg.init(168);
		Key k = kg.generateKey();

		String encodedKey = b2s(encode64(k.getEncoded()));
		System.out.println(encodedKey);
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, k);

		String text = "hello";
		byte[] encryptBytes = cipher.doFinal(s2b(text));
		String encrypt = b2s(encode64(encryptBytes));
		System.out.println("encrypt:" + encrypt);

		cipher = Cipher.getInstance("DESede");

		cipher.init(Cipher.DECRYPT_MODE, k);
		String decrypt = b2s(cipher.doFinal(encryptBytes));
		System.out.println("decrypt:" + decrypt);
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
