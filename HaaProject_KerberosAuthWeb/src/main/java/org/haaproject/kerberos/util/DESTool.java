/**
 */
package org.haaproject.kerberos.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 */
public class DESTool {

	private static final String DES_DEFAULT_KEY = "ComwaveSSO密钥";

	public static void main(String[] args) throws Exception {
		DESTool des = new DESTool();
		System.out.println(des.encrypt("password".getBytes()));
		System.out.println(new String(des.decrypt("8325fdef5c9c9e088259379c92be8e6a")));
	}

	private SecretKey key;

	public DESTool() {
		this(DES_DEFAULT_KEY);
	}

	public DESTool(String key) {
		this.key = buildDESKey(key);
	}

	public String encrypt(byte[] word) throws Exception {
		Cipher encrypt = Cipher.getInstance("DES");
		encrypt.init(Cipher.ENCRYPT_MODE, this.key);

		return bytesToHexStr(encrypt.doFinal(word));
	}

	public byte[] decrypt(String word) throws Exception {
		Cipher decrypt = Cipher.getInstance("DES");
		decrypt.init(Cipher.DECRYPT_MODE, this.key);

		return decrypt.doFinal(hexStrToBytes(word));
	}

	public SecretKey buildDESKey(String value) {
		byte[] byteValue = value.getBytes();
		byte[] eight_bit = new byte[8];
		for (int i = 0; i < eight_bit.length && i < byteValue.length; i++) {
			eight_bit[i] = byteValue[i];
		}
		SecretKey key = new SecretKeySpec(eight_bit, "DES");
		return key;
	}

	public static final String bytesToHexStr(byte[] value) {
		StringBuffer s = new StringBuffer(value.length * 2);

		for (int i = 0; i < value.length; i++) {
			s.append(bcdLookup[(value[i] >>> 4) & 0x0f]);
			s.append(bcdLookup[value[i] & 0x0f]);
		}

		return s.toString();
	}

	public static final byte[] hexStrToBytes(String value) {
		byte[] bytes;
		bytes = new byte[value.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(value.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

	private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}
