/**
 * <p>
 * <ul>
 * <li>File: JCEInstallTest.java</li>
 * <li>Create:Jul 3, 2011</li>
 * <li>Author:Geln Yang</li>
 * </ul>
 * </p>
 * <hr>
 * <p>
 * <!-- comment -->
 * </p>
 */
package org.haaproject.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * <pre><b><font color="blue">JCEInstallTest</font></b></pre>
 * 
 * <pre><b>&nbsp;-- description--</b></pre>
 * <pre></pre>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class JCEInstallTest {
	public static final String stringToEncrypt = "This is a test.";

	public static void main(String[] args) throws Exception {

		System.out.print("Attempting to get a Blowfish key...");
		KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		System.out.println("OK");
		System.out.println("Attempting to get a Cipher and encrypt...");
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(stringToEncrypt.getBytes("UTF8"));
		System.out.println(cipherText);
		System.out.println("OK");
		System.out.println("Test completed successfully.");
	}
}
