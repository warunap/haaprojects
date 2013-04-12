/**
 * Created By: Comwave Project Team
 * Created Date: 2013-4-10 上午9:09:53
 */
package org.haaproject.ldap.util;

import java.util.Map;
import java.util.Properties;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class LdapSearch {

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(OpenLDAPUtil.class.getResourceAsStream("/ldaptest.properties"));

		String returnedAtts[] = { "sn", "cn", "name", "title", "givenName", "objectClass" };

		String returnedAttsStr = properties.getProperty("ldap.test.returnedAtts");
		if (returnedAttsStr != null && returnedAttsStr.trim().length() > 0) {
			returnedAtts = returnedAttsStr.split(",");
		}

		String domain = properties.getProperty("ldap.test.domain");
		if (domain == null || domain.trim().length() == 0) {
			domain = null;
		}
		String provider_url = properties.getProperty("ldap.test.provider");
		String searchBase = properties.getProperty("ldap.test.searchBase");
		String userDN = properties.getProperty("ldap.test.admin.dn");
		String password = properties.getProperty("ldap.test.admin.password");
		String searchUserName = properties.getProperty("ldap.test.searchUser");
		String searchFilter = "(&(objectClass=person)(sn=" + searchUserName + "))";

		String searchFileterStr = properties.getProperty("ldap.test.searchFileter");
		if (searchFileterStr != null && searchFileterStr.trim().length() > 0) {
			searchFilter = searchFileterStr.replace("{search_user_name}", searchUserName);
		}
		System.out.println("domain" + "\t:\t" + domain);
		System.out.println("provider_url" + "\t:\t" + provider_url);
		System.out.println("searchBase" + "\t:\t" + searchBase);
		System.out.println("userDN" + "\t:\t" + userDN);
		System.out.println("password" + "\t:\t" + password);
		System.out.print("returnedAtts" + "\t:\t");
		for (int i = 0; i < returnedAtts.length; i++) {
			System.out.print(returnedAtts[i] + ", ");
		}
		System.out.println();
		System.out.println("searchFilter" + "\t:\t" + searchFilter);
		System.out.println("searchUserName" + "\t:\t" + searchUserName);
		System.out.println("----------------");
		Map<String, String> result = OpenLDAPUtil.searchFirst(domain, provider_url, userDN, password, searchBase, searchFilter, returnedAtts);

		System.out.println(result);
		System.out.println("----------------");
		System.out.println("over");
	}
}
