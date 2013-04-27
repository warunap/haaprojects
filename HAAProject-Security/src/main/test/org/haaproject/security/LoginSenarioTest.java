/**
 *      Project:HAAProject-Security       
 *    File Name:LoginSenarioTest.java	
 * Created Time:2012-12-13下午11:46:03
 */
package org.haaproject.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 * @author Geln Yang
 * 
 */
public class LoginSenarioTest {

	private static final String DIGEST_ALGORITHM = "MD5";

	static Map<String, String> userDBMap = new HashMap<String, String>();

	static Map<String, String> authenticationServerDBMap = new HashMap<String, String>();

	static Map<String, String> gameServerDBMap = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		// System.out.println(SecurityTool.symmetricEncrypt("e84ad660c4721ae0",
		// "test"));
		// System.out.println(generateSecret("salt", "user", "pass"));
		String userName = "eric";
		String password = "pass";
		String server = "gameServer1";
		userDBMap.put(userName, password);
		authenticationServerDBMap.put(server, "xxxxxx");

		loginTest(userName, password, server);
	}

	/**
	 * 登陆流程是这样的：<br>
	 * 
	 * 1. G 发现有 C 企图登陆时，生成一个一次性的 salt （如果直接用时间的话，可选对称加密），然后发送给 C<br>
	 * 2. C 收到 salt ，利用自己的用户密码，对 salt 加密并签名。加密可以使用标准的 DES 算法，签名可以用标准的 MD5
	 * 算法。具体方法是：讲用户密码先 md5 hash 一次，得到一个串。取串的前一半做 DES 加密算法的 key 加密 salt
	 * ；然后把结果连接上密码 hash 串的后半部分，一起做一次 MD5 。密文和签名连接在一起，最终会得到一个加过签名的密文 secret 。<br>
	 * 3. C 将第 2 步生成的 secret 连同自己的登陆名，以及需要登陆的游戏名发送给 E 。<br>
	 * 4.E 收到 secret 后，从登陆名查询到用户信息，用保存在 E 上的密码做反向操作，验证签名是否正确。若正确则解出 salt
	 * 。失败则发送错误信息给 C ，登陆流程结束。<br>
	 * 5.E 利用解开的 salt ，附加上用户 id 。利用游戏名查询到事先和 G 约定的游戏服务器密码，做同样流程的加密签名处理，发送回 C 。<br>
	 * 6.C 转发认证密文回给 G 。G 用约定要的服务器密码，校验签名并解密，核对 salt 是否是在步骤 1 里发送出去的 salt
	 * ，确认用户的合法性。<br>
	 * 
	 */
	private static void loginTest(String username, String password, String gameServer) throws Exception {

		System.out.println(username + " try to login to the server " + gameServer);
		String sessionid = "session_" + System.currentTimeMillis();
		String salt = requestSalt(sessionid);
		System.out.println("Game Server(G) return salt:[" + salt + "]");

		String secret = generateSecret(salt, username, password);
		System.out.println("Client(C) generate a secret:[" + secret + "]");

		System.out.println("Client(C) send secret and Game Server(G) name to Authentication Server(AS)...");
		String validSecret = validLoginByAuthenticationServer(username, secret, gameServer);
		System.out.println("Authentication server(AS) return valid secret to Client(C):[" + validSecret + "]");

		System.out.println("Client(C) send valid secret to Game Server(G)...");
		boolean loginResult = validLoginByGameServer(sessionid, validSecret);
		System.out.println(loginResult ? "login success" : "login failed");
	}

	/**
	 */
	private static String requestSalt(String sessionid) {
		String salt = "SALT" + System.currentTimeMillis();
		gameServerDBMap.put(sessionid, salt);
		System.out.println("record salt:" + sessionid + "," + salt);
		return salt;
	}

	/**
	 */
	private static String generateSecret(String salt, String username, String password) throws Exception {
		String digest = SecurityTool.digest(DIGEST_ALGORITHM, password);
		String digestFirst = digest.substring(0, digest.length() / 2);
		String digestSecond = digest.substring(digestFirst.length());

		String encyptSalt = SecurityTool.symmetricEncrypt(digestFirst, salt);
		String signature = SecurityTool.digest(DIGEST_ALGORITHM, encyptSalt + digestSecond);

		return SecurityTool.symmetricEncrypt(digestSecond, encyptSalt + "," + signature);
	}

	/**
	 */
	private static String validLoginByAuthenticationServer(String username, String secret, String server) throws Exception {
		String password = userDBMap.get(username);

		String digest = SecurityTool.digest(DIGEST_ALGORITHM, password);
		String digestFirst = digest.substring(0, digest.length() / 2);
		String digestSecond = digest.substring(digestFirst.length());

		String symmetricDecrypt = SecurityTool.symmetricDecrypt(digestSecond, secret);
		String encyptSalt = StringUtils.substringBefore(symmetricDecrypt, ",");

		String signature = StringUtils.substringAfter(symmetricDecrypt, ",");

		String signatureValid = SecurityTool.digest(DIGEST_ALGORITHM, encyptSalt + digestSecond);
		if (!signature.equals(signatureValid)) {
			throw new Exception("signature not valid!");
		}

		String salt = SecurityTool.symmetricDecrypt(digestFirst, encyptSalt);
		System.out.println("Authentication Server(AS) decrypt to get Game Server(G) salt:[" + salt + "]");
		String newsalt = salt + "," + username;
		System.out.println("Authentication Server(AS) salt:[" + newsalt + "], it will be encrypt by Game Server(G) key.");
		String gameServerKey = authenticationServerDBMap.get(server);
		return generateSecret(newsalt, username, gameServerKey);
	}

	/**
	 */
	private static boolean validLoginByGameServer(String sessionid, String validSecret) throws Exception {
		String dbsalt = gameServerDBMap.get(sessionid);

		String server = "gameServer1";//
		String gameServerKey = authenticationServerDBMap.get(server);

		String digest = SecurityTool.digest(DIGEST_ALGORITHM, gameServerKey);
		String digestFirst = digest.substring(0, digest.length() / 2);
		String digestSecond = digest.substring(digestFirst.length());

		String symmetricDecrypt = SecurityTool.symmetricDecrypt(digestSecond, validSecret);
		String encyptSalt = StringUtils.substringBefore(symmetricDecrypt, ",");

		String signature = StringUtils.substringAfter(symmetricDecrypt, ",");

		String signatureValid = SecurityTool.digest(DIGEST_ALGORITHM, encyptSalt + digestSecond);
		if (!signature.equals(signatureValid)) {
			throw new Exception("signature not valid!");
		}

		String newsalt = SecurityTool.symmetricDecrypt(digestFirst, encyptSalt);
		System.out.println("Game Server(G) decrypts to AS salt:[" + newsalt + "]");
		String salt = StringUtils.substringBefore(newsalt, ",");
		String username = StringUtils.substringAfter(newsalt, ",");
		if (dbsalt.equals(salt)) {
			System.out.println("add to server login record:[" + username + "]");
			return true;
		}
		return false;
	}

}
