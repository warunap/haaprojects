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
import java.security.Provider.Service;
import java.security.Security;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * <pre>
 * <b><font color="blue">SecuritySystemChecker</font></b>
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
public class SecurityEnvChecker {

	public static void main(String[] args) {
		showProviders();
		showAlgorithmByType("MessageDigest");
		showAlgorithmByType("KeyPairGenerator");
		showAlgorithmByType("Signature");
		showAlgorithmByType("Cipher");
		showAlgorithmByType("KeyFactory");
	}

	public static void showProviders() {
		System.out.println("-------列出加密服务提供者-----");
		HashSet<String> algorithmTypes = new HashSet<String>();
		Provider[] pro = Security.getProviders();
		for (Provider p : pro) {
			System.out.println("Provider:" + p.getName() + " - version:" + p.getVersion());
			System.out.println(p.getInfo());
			for (Enumeration e = p.keys(); e.hasMoreElements();) {
				String currentKey = ((String) e.nextElement());
				if (currentKey.indexOf(" ") < 0) {
					int idx = currentKey.indexOf(".");
					algorithmTypes.add(currentKey.substring(0, idx));
				}
			}
			// if (p.getName().equals("BC")) {
			// System.out.println("===================================");
			// Set<Service> services = p.getServices();
			// for (Service service : services) {
			// if (service.getType().equalsIgnoreCase("Cipher")) {
			// System.out.println(service.getAlgorithm());
			// }
			// }
			// }
		}

		System.out.println("-------列出加密 类型-----");
		for (String type : algorithmTypes) {
			System.out.println(type);
		}
	}

	private static void showAlgorithmByType(String type) {
		System.out.println();
		System.out.println("-------列出系统支持 [" + type + "] 的算法：");
		for (String s : Security.getAlgorithms(type)) {
			System.out.println(s);
		}

	}
}
