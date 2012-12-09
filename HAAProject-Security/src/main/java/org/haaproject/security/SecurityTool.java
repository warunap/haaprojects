/**
 * <p>
 * <ul>
 * <li>File: SecurityTool.java</li>
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
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre>
 * <b><font color="blue">SecurityTool 签名认证工具</font></b>
 * </pre>
 * 
 * <p>
 * 加密介绍：<br>
 * 1. 加密密钥：分“单独密钥”（secret key）和密钥对，密钥对分为“公开密钥”（public key）和私有密钥(private key).<br>
 * 所谓对称密钥算法是指如果一个加密算法的加密密钥和解密密钥相同，或者虽然不相同，但是可由其中的任意一个很容易的推导出另一个，即密钥是双方共享的。 <br>
 * 非对称密钥算法是指一个加密算法的加密密钥和解密密钥是不一样的，或者说不能由其中一个密钥推导出另一个密钥。这两个密钥其中一个称为公钥，用于加密，是公开的，另一个称为私钥，用于解密，是保密的。其中由公钥计算私钥是计算上不可行的。<br>
 * 这两种密码算法的不同之处主要有如下几个方面：<br>
 * &nbsp;1)加解密时采用的密钥的差异：从上述对对称密钥算法和非对称密钥算法的描述中可看出，对称密钥加解密使用的同一个密钥，或者能从加密密钥很容易推出解密密钥；而非对称密钥算法加解密使用的不同密钥，其中一个很难推出另一个密钥。<br>
 * &nbsp;2)算法上区别：①对称密钥算法采用的分组加密技术，即将待处理的明文按照固定长度分组，并对分组利用密钥进行数次的迭代编码，最终得到密文。解密的处理同样，在固定长度密钥控制下，以一个分组为单位进行数次迭代解码，得到明文。而非对称密钥算法采用一种特殊的数学函数，单向陷门函数（one way trapdoor
 * function），即从一个方向求值是容易的，而其逆向计算却很困难，或者说是计算不可行的。加密时对明文利用公钥进行加密变换，得到密文。解密时对密文利用私钥进行解密变换，得到明文。②对称密钥算法具有加密处理简单，加解密速度快，密钥较短，发展历史悠久等特点，非对称密钥算法具有加解密速度慢的特点，密钥尺寸大，发展历史较短等特点。<br>
 * &nbsp;3)密钥管理安全性的区别：对称密钥算法由于其算法是公开的，其保密性取决于对密钥的保密。由于加解密双方采用的密钥是相同的，因此密钥的分发、更换困难。而非对称密钥算法由于密钥已事先分配，无需在通信过程中传输密钥，安全性大大提高，也解决了密钥管理问题。<br>
 * &nbsp;4)安全性：对称密钥算法由于其算法是公开的，其安全性依赖于分组的长度和密钥的长度，常的攻击方法包括：穷举密钥搜索法，字典攻击、查表攻击，差分密码分析，线性密码分析，其中最有效的当属差分密码分析，它通过分析明文对密文对的差值的影响来恢复某些密钥比特。非对称密钥算法安全性建立在所采用单向函数的难解性上，如椭圆曲线密码算法，许多密码专家认为它是指数级的难度，从已知求解算法看，160
 * bit的椭圆曲线密码算法安全性相当于1024bit RSA算法。 <br>
 * <br>
 * 2. 密钥对的使用：<br>
 * &nbsp;1）数据加密传输：发送者使用public key加密传送，接收者private key解密；<br>
 * &nbsp;2）签名：发送者使用private key加密，接收者使用public key解密；<br>
 * <br>
 * 3. 消息摘要：也称为hash散列，计算给定数据产生一个小的字节序列，好的消息摘要算法必须保证两个不同的消息得到同一摘要在计算上是不可行的。<br>
 * <br>
 * 4. 数字签名：主要用于验证消息在传输过程中没有被修改。<br>
 * &nbsp;1) 计算得到消息摘要；<br>
 * &nbsp;2）使用private key对消息摘要加密得到数字签名；<br>
 * </p>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 * @version 1.0
 */
public class SecurityTool {

	/** 编码方式 */
	private static final String DEFAULT_ENCODE = "UTF-8";

	/**
	 * 非对称密钥算法 <br>
	 * http://en.wikipedia.org/wiki/Asymmetric-key_algorithm
	 */
	private static final String DEFAULT_ASYMMETRIC_CIPHER = "RSA";

	/**
	 * 对称密钥算法 <br>
	 * http://en.wikipedia.org/wiki/Symmetric-key_algorithm
	 */
	private static final String DEFAULT_SYMMETRIC_CIPHER = "DESede";

	/** 签名算法 */
	private static final String SIGNATURE_SIPHER = "MD5withRSA";

	/**
	 * 密钥长度 About bouncy castle(BC) JCE provider:<br>
	 * The value of key size parameter to initial KeyPairGenerator must be one of the following: 192,239,256,224,384,521<br>
	 * 
	 * @see org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator.EC
	 */
	private static final int RSA_KEY_SIZE = 512;

	private static final int DESede_KEY_SIZE = 168;

	/**
	 * 产生对称密钥
	 */
	public static Key generateKey() throws NoSuchAlgorithmException {
		return generateKey(DEFAULT_SYMMETRIC_CIPHER, DESede_KEY_SIZE);
	}

	/**
	 * 产生对称密钥
	 * 
	 * @param cipher
	 *            密钥算法
	 * @param keySize
	 *            密钥长度
	 */
	public static Key generateKey(String cipher, int keySize) throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance(cipher);
		kg.init(keySize);
		return kg.generateKey();
	}

	/**
	 * 产生非对称密钥对
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		return generateKeyPair(DEFAULT_ASYMMETRIC_CIPHER, RSA_KEY_SIZE);
	}

	/**
	 * 产生非对称密钥对
	 * 
	 * @param cipher
	 *            密钥算法
	 * @param keySize
	 *            密钥长度
	 */
	public static KeyPair generateKeyPair(String cipher, int keySize) throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(cipher);
		SecureRandom secrand = new SecureRandom();
		// 设置当前时间为随机种子
		secrand.setSeed(System.currentTimeMillis());
		keygen.initialize(keySize, secrand);
		return keygen.genKeyPair();
	}

	/**
	 * 使用私钥对文本串进行签名，返回编码后的结果
	 * 
	 * @param privateKey
	 *            私钥
	 * @param text
	 *            需签名文本
	 */
	public static String sign(String privateKey, String text) throws Exception {
		Signature signet = Signature.getInstance(SIGNATURE_SIPHER);
		signet.initSign((PrivateKey) convert2AsymmetricKey(privateKey, false));
		signet.update(s2b(text));
		return b2s(Base64.encodeBase64(signet.sign()));
	}

	/**
	 * 使用公钥验证签名是否正确
	 * 
	 * @param publicKey
	 *            公钥
	 * @param signatureText
	 *            签名
	 * @param text
	 *            原始文本内容
	 */
	public static boolean verify(String publicKey, String signatureText, String text) throws Exception {
		Signature signature = Signature.getInstance(SIGNATURE_SIPHER);
		signature.initVerify((PublicKey) convert2AsymmetricKey(publicKey, true));
		signature.update(s2b(text));
		return signature.verify(decode64(s2b(signatureText)));
	}

	/**
	 * 使用公钥加密
	 * 
	 * @param publicKey
	 *            公钥
	 * @param text
	 *            文本内容
	 */
	public static String publicEncrypt(String publicKey, String text) throws Exception {
		return symmetricEncrypt(publicKey, true, text);
	}

	/**
	 * 使用私钥加密
	 * 
	 * @param privateKey
	 *            私钥
	 * @param text
	 *            文本内容
	 */
	public static String privateEncrypt(String privateKey, String text) throws Exception {
		return symmetricEncrypt(privateKey, false, text);
	}

	/**
	 * 使用公钥解密
	 * 
	 * @param publicKey
	 *            公钥
	 * @param text
	 *            文本内容
	 */
	public static String publicDecrypt(String publicKey, String text) throws Exception {
		return symmetricDecrypt(publicKey, true, text);
	}

	/**
	 * 使用私钥解密
	 * 
	 * @param privateKey
	 *            私钥
	 * 
	 * @param text
	 *            文本内容
	 */
	public static String privateDecrypt(String privateKey, String text) throws Exception {
		return symmetricDecrypt(privateKey, false, text);
	}

	/**
	 * 返回编码后的私钥字符串
	 * 
	 * @param keyPair
	 *            密钥对
	 */
	public static String getEncodedPrivateKey(KeyPair keyPair) throws UnsupportedEncodingException {
		return b2s(encode64(keyPair.getPrivate().getEncoded()));
	}

	/**
	 * 返回编码后的公钥字符串
	 * 
	 * @param keyPair
	 *            密钥对
	 */
	public static String getEncodedPublicKey(KeyPair keyPair) throws UnsupportedEncodingException {
		return b2s(encode64(keyPair.getPublic().getEncoded()));
	}

	/**
	 * 非对称密钥加密
	 * 
	 * @param encodedKey
	 *            编码后的密钥
	 * @param isPublicKey
	 *            是否公钥
	 * @param text
	 *            文本内容
	 */
	public static String symmetricEncrypt(String encodedKey, boolean isPublicKey, String text) throws Exception {
		return encrypt(convert2AsymmetricKey(encodedKey, isPublicKey), text);
	}

	/**
	 * 非对称密钥解密
	 * 
	 * @param encodedKey
	 *            编码后的密钥
	 * @param isPublicKey
	 *            是否公钥
	 * @param encryptString
	 *            加密文本
	 */
	public static String symmetricDecrypt(String encodedKey, boolean isPublicKey, String encryptString) throws Exception {
		return decrypt(convert2AsymmetricKey(encodedKey, isPublicKey), encryptString);
	}

	/**
	 * 加密
	 * 
	 * @param encodedKey
	 *            编码后的密钥
	 * @param isPublicKey
	 *            是否公钥
	 * @param text
	 *            文本内容
	 */
	public static String encrypt(Key key, String text) throws Exception {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return b2s(encode64(cipher.doFinal(s2b(text))));
	}

	/**
	 * 解密
	 * 
	 * @param encodedKey
	 *            编码后的密钥
	 * @param isPublicKey
	 *            是否公钥
	 * @param encryptString
	 *            加密文本
	 */
	public static String decrypt(Key key, String encryptString) throws Exception {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);
		return b2s(cipher.doFinal(decode64(s2b(encryptString))));
	}

	/**
	 * 将编码key字符串转换为{@link Key}对象
	 * 
	 * @param encodedKey
	 *            编码key字符串
	 * @param isPublicKey
	 *            是否公钥
	 */
	private static Key convert2AsymmetricKey(String encodedKey, boolean isPublicKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_ASYMMETRIC_CIPHER);
		if (isPublicKey) {
			return keyFactory.generatePublic(new X509EncodedKeySpec(decode64(s2b(encodedKey))));
		} else {
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decode64(s2b(encodedKey))));

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
