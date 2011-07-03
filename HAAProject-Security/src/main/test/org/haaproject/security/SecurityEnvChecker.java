/**
 * <p>
 * <ul>
 * <li>File: SecuritySystemChecker.java</li>
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

import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.Set;

/**
 * <pre><b><font color="blue">SecuritySystemChecker</font></b></pre>
 * 
 * <pre><b>&nbsp;-- description--</b></pre>
 * <pre></pre>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class SecurityEnvChecker {

	public static void main(String[] args) {
		System.out.println("-------列出加密服务提供者-----");
		Provider[] pro = Security.getProviders();
		for (Provider p : pro) {
			System.out.println("Provider:" + p.getName() + " - version:" + p.getVersion());
			System.out.println(p.getInfo());
			//			if (p.getName().equals("BC")) {
			//				System.out.println("===================================");
			//				Set<Service> services = p.getServices();
			//				for (Service service : services) {
			//					if (service.getType().equalsIgnoreCase("Cipher")) {
			//						System.out.println(service.getAlgorithm());
			//					}
			//				}
			//			}
		}
		System.out.println("");
		System.out.println("-------列出系统支持的消息摘要算法：");
		for (String s : Security.getAlgorithms("MessageDigest")) {
			System.out.println(s);
		}
		System.out.println("-------列出系统支持的生成公钥和私钥对的算法：");
		for (String s : Security.getAlgorithms("KeyPairGenerator")) {
			System.out.println(s);
		}
		System.out.println("-------列出系统支持签名的算法：");
		for (String s : Security.getAlgorithms("Signature")) {
			System.out.println(s);
		}
		//		System.out.println("-------列出系统支持Cipher的算法：");
		//		for (String s : Security.getAlgorithms("Cipher")) {
		//			System.out.println(s);
		//		}
	}
}
