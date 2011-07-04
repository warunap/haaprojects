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
 * <pre><b><font color="blue">SignatureTest</font></b></pre>
 * 
 * <pre><b>&nbsp;-- description--</b></pre>
 * <pre></pre>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class SignatureTest {

	public static void main(String[] args) throws Exception {
		String text = "security test";
		System.out.println("text:" + text);

		KeyPair keyPair = SignatureTool.generateKeyPair();
		String privateKey = SignatureTool.getEncodedPrivateKey(keyPair);
		String publicKey = SignatureTool.getEncodedPublicKey(keyPair);
		System.out.println("privateKey:" + privateKey);
		System.out.println("publicKey:" + publicKey);

		String sign = SignatureTool.sign(privateKey, text);
		System.out.println("sign:" + sign);

		boolean verifyResult = SignatureTool.verify(publicKey, sign, text);
		System.out.println("verifyResult:" + verifyResult);

		String encrypt = SignatureTool.publicEncrypt(publicKey, text);
		System.out.println("encrypt:" + encrypt);
		String decrypt = SignatureTool.privateDecrypt(privateKey, encrypt);
		System.out.println("decrypt:" + decrypt);
	}
}
