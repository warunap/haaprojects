/**
 * <p>
 * <ul>
 * <li>File: SignatureTest.java</li>
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

import java.security.KeyPair;

/**
 * <pre>
 * <b><font color="blue">SignatureTest</font></b>
 * </pre>
 * 
 * <pre>
 * <b>&nbsp;-- description--</b>
 * </pre>
 * 
 * <pre></pre>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class SignatureTest {

	public static void main(String[] args) throws Exception {
		// testEncrypt("security test");
		testEncrypt("你好");

	}

	private static void testEncrypt(String text) throws Exception {
		System.out.println("text:" + text);

		KeyPair keyPair = SecurityTool.generateKeyPair();
		String privateKey = SecurityTool.getEncodedPrivateKey(keyPair);
		String publicKey = SecurityTool.getEncodedPublicKey(keyPair);
		System.out.println("privateKey:" + privateKey);
		System.out.println("publicKey:" + publicKey);

		String sign = SecurityTool.sign(privateKey, text);
		System.out.println("sign:" + sign);

		boolean verifyResult = SecurityTool.verify(publicKey, sign, text);
		System.out.println("verifyResult:" + verifyResult);

		String encrypt = SecurityTool.publicEncrypt(publicKey, text);
		System.out.println("encrypt:" + encrypt);
		String decrypt = SecurityTool.privateDecrypt(privateKey, encrypt);
		System.out.println("decrypt:" + decrypt);
	}
}
