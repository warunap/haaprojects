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
public class TestLdapSearch {

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(OpenLDAPUtil.class.getResourceAsStream("/ldaptest.properties"));

		String returnedAtts[] = { "sn", "cn", "name", "title", "givenName", "objectClass" };
		String domain = properties.getProperty("ldap.test.domain");
		String provider_url = properties.getProperty("ldap.test.provider");
		String searchBase = properties.getProperty("ldap.test.searchBase");
		String userDN = properties.getProperty("ldap.test.admin.dn");
		String password = properties.getProperty("ldap.test.admin.password");
		String searchUserName = properties.getProperty("ldap.test.searchUser");

		System.out.println("search user:" + searchUserName);

		Map<String, String> result = OpenLDAPUtil.searchUser(domain, provider_url, userDN, password, searchBase, returnedAtts, searchUserName);
		System.out.println(result);
		System.out.println("over");
	}
}
