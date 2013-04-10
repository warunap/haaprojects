/**
 * Created By: Geln
 * Created Date: 2012-9-6 下午3:48:28
 */
package org.haaproject.ldap.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author Eric Yang
 * @version 1.1
 */
public class OpenLDAPUtil {

	private static final Log logger = LogFactory.getLog(OpenLDAPUtil.class);

	private static final String OBJECT_CLASS_ORG_UNIT = "organizationalUnit";

	private static final String OBJECT_CLASS_USER = "person";

	/**
	 * search orgUnits
	 */
	public static List<Map<String, String>> searchOrgUnitList(String principle, String credentials, String domain, String provider_url, String searchBase, String[] returnedAtts, String parentOrgUnit) {
		if (parentOrgUnit != null) {
			searchBase = getOrgUnitDistinguishedName(principle, credentials, domain, provider_url, searchBase, parentOrgUnit);
			if (searchBase == null)
				return new ArrayList<Map<String, String>>();
		}
		String searchFilter = "(&(objectClass=" + OBJECT_CLASS_ORG_UNIT + "))";
		return searchMultiple(principle, credentials, domain, provider_url, searchBase, searchFilter, returnedAtts);
	}

	/**
	 */
	public static String getOrgUnitDistinguishedName(String principle, String credentials, String domain, String provider_url, String searchBase, String orgUnit) {
		Map<String, String> unit = searchOrgUnit(principle, credentials, domain, provider_url, searchBase, orgUnit, new String[] { "distinguishedName" });
		if (unit != null && unit.get("distinguishedName") != null)
			return unit.get("distinguishedName");
		return null;
	}

	/**
	 * search orgUnit
	 */
	public static Map<String, String> searchOrgUnit(String principle, String credentials, String domain, String provider_url, String searchBase, String orgUnitName, String[] returnedAtts) {
		String searchFilter = "(&(objectClass=" + OBJECT_CLASS_ORG_UNIT + ")(ou=" + orgUnitName + "))";
		return searchFirst(domain, provider_url, principle, credentials, searchBase, searchFilter, returnedAtts);
	}

	/**
	 * 用戶LDAP登錄
	 */
	public static boolean userLoginValidate(String provider_url, String searchBase, String ldapAdmin, String ldapAdminPassword, String loginUserName, String loginUserPassword) throws Exception {
		String searchFilter = "(&(objectClass=" + OBJECT_CLASS_USER + ")(sn=" + loginUserName + "))";
		String returnedAtts[] = { "sn", "cn", "dn", "userPassword" };

		String userDN;
		if (loginUserName != null && loginUserName.length() > 3 && loginUserName.toUpperCase().startsWith("CN=")) {
			userDN = loginUserName;
			String name = loginUserName.substring(3, loginUserName.indexOf(","));
			searchFilter = "(&(objectClass=" + OBJECT_CLASS_USER + ")(sn=" + name + "))";
		} else {
			userDN = getUserDN(provider_url, searchBase, ldapAdmin, ldapAdminPassword, loginUserName);
			if (userDN == null || userDN.trim().length() == 0) {
				throw new Exception("account_not_exists");
			}
		}
		if (ldapAdmin.equalsIgnoreCase(loginUserName) && userDN != null) {
			return true;
		}
		if (userDN != null && userDN.trim().length() > 0) {
			Map<String, String> result = searchFirst(null, provider_url, userDN, loginUserPassword, searchBase, searchFilter, returnedAtts);
			if (result == null)
				throw new Exception("ldap_error_passwordwrong");
		}

		return true;

	}

	/**
	 * 用戶LDAP登錄. <BR>
	 * NOTICE:如果LDAP有限制userPassword不能夠被查，則不能使用此方法。
	 */
	public static boolean md5UserLoginValidate(String provider_url, String searchBase, String ldapAdmin, String ldapAdminPassword, String loginUserName, String loginUserPassword) throws Exception {
		String returnedAtts[] = { "sn", "cn", "dn", "userPassword" };
		if (loginUserPassword != null) {
			Map<String, String> result = searchUser(null, provider_url, ldapAdmin, ldapAdminPassword, searchBase, returnedAtts, loginUserName);
			if (result == null) {
				logger.info("can't find user:" + loginUserName);
			} else {
				String userPassword = result.get("userPassword");
				return ("{MD5}" + loginUserPassword).equals(userPassword);
			}
		} else {
			logger.info("validate password is null");
		}
		return false;
	}

	public static String getUserDN(String provider_url, String searchBase, String ldapAdmin, String ldapAdminPassword, String userId) {
		String searchFilter = "(&(objectClass=" + OBJECT_CLASS_USER + ")(sn=" + userId + "))";
		String returnedAtts[] = { "sn", "cn", "dn" };
		try {
			Map<String, String> result = searchFirst(null, provider_url, ldapAdmin, ldapAdminPassword, searchBase, searchFilter, returnedAtts);
			if (result == null) {
				logger.info("can't find user:" + userId);
			} else {
				return result.get("DN");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String digest(String algorithm, String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(str.getBytes());
		return Base64.encodeBase64String(md.digest());
	}

	/**
	 * search the first user by user name
	 */
	public static Map<String, String> searchUser(String domain, String provider_url, String principle, String credentials, String searchBase, String[] returnedAtts, String searchUserName) {
		String searchFilter = "(&(objectClass=" + OBJECT_CLASS_USER + ")(sn=" + searchUserName + "))";
		return searchFirst(domain, provider_url, principle, credentials, searchBase, searchFilter, returnedAtts);
	}

	/**
	 * search users by orgUnit number
	 */
	public static List<Map<String, String>> searchUserList(String principle, String credentials, String domain, String provider_url, String searchBase, String parentOrgUnit, String[] returnedAtts) {
		if (parentOrgUnit != null) {
			searchBase = getOrgUnitDistinguishedName(principle, credentials, domain, provider_url, searchBase, parentOrgUnit);
			if (searchBase == null)
				return new ArrayList<Map<String, String>>();
		}
		String searchFilter = "(&(objectClass=" + OBJECT_CLASS_USER + "))";
		return searchMultiple(principle, credentials, domain, provider_url, searchBase, searchFilter, returnedAtts);
	}

	/**
	 * search first single result
	 */
	public static Map<String, String> searchFirst(String domain, String provider_url, String principle, String credentials, String searchBase, String searchFilter, String[] returnedAtts) {
		return firstResult(ldapsearch(domain, provider_url, principle, credentials, searchBase, searchFilter, returnedAtts, true));
	}

	/**
	 * Search multiple result
	 */
	public static List<Map<String, String>> searchMultiple(String principle, String credentials, String domain, String provider_url, String searchBase, String searchFilter, String[] returnedAtts) {
		return multipleResult(ldapsearch(domain, provider_url, principle, credentials, searchBase, searchFilter, returnedAtts, false));
	}

	/**
	 * The SECURITY_PRINCIPLE is the property that gives the most trouble. Two options: <br>
	 * (1) set it to the user's distinguished name, which often has the form: <br>
	 * CN=First Last,CN=Users,CN=domain,CN=com <br>
	 * Note that the user's CN attribute in AD is often set to the full name of the person. <br>
	 * <br>
	 * (2) you can use the userPrincipleName, which is not in DN format, and often looks like (and in fact may be, but does not have to be) the user's email address: user@domain.com<br>
	 * <br>
	 * (3) If the user has not userPrincipleName property, then use name property.
	 * 
	 * @param domain
	 * @param provider_url
	 * @param principle
	 * @param credentials
	 * @param searchBase
	 * @param searchFilter
	 *            specify the LDAP search filter
	 * @param returnedAtts
	 * @param searchFirst
	 *            whether to search the first or search the all result
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static NamingEnumeration ldapsearch(String domain, String provider_url, String principle, String credentials, String searchBase, String searchFilter, String[] returnedAtts,
			boolean searchFirst) {
		Properties env = new Properties();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		if (domain == null) {
			// domain = getDomainFromDest(searchBase);
		}

		String principal = principle;
		if (principal.indexOf("@") == -1 && !principle.toUpperCase().startsWith("CN=")) {
			if (domain != null) {
				principal += ("@" + domain);
			} else {
				principal = "CN=" + principle + "," + searchBase;
			}
		} else {
			principal = principle;
		}

		// set security credentials, note using simple clear text authentication
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, credentials);

		// connect to my domain controller
		env.put(Context.PROVIDER_URL, provider_url);

		try {
			// Create the initial directory context
			LdapContext ctx = new InitialLdapContext(env, null);

			// Create the search controls
			SearchControls searchCtls = new SearchControls();

			// Specify the search scope
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// limit search count
			if (searchFirst)
				searchCtls.setCountLimit(1);

			// set return attributes
			searchCtls.setReturningAttributes(returnedAtts);

			// Search for objects using the filter
			NamingEnumeration ne = ctx.search(searchBase, searchFilter, searchCtls);

			ctx.close();
			return ne;
		} catch (Exception e) {
			logger.warn(e.getMessage());
			logger.debug(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param attrs
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> parseAttributes(Attributes attrs) throws NamingException {
		HashMap<String, String> map = new HashMap<String, String>();
		for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
			Attribute attr = (Attribute) ae.next();
			String id = attr.getID();

			String value = "";
			logger.debug("prase:" + id);
			for (NamingEnumeration e = attr.getAll(); e.hasMore();) {
				Object obj = e.next();
				if (value != null && value.trim().length() != 0) {
					value += ",";
				}
				if ("userPassword".equalsIgnoreCase(id) && (obj instanceof byte[])) {
					byte[] arr = (byte[]) obj;
					for (byte c : arr) {
						value += (char) c;
					}
				} else {
					value += obj;
				}
			}
			logger.debug("value:" + value);
			map.put(id, value);
		}
		return map;
	}

	private static String getDomainFromDest(String searchBase) {
		String domain = "";
		String[] arr = searchBase.split(",");
		for (int i = 0; i < arr.length; i++) {
			String item = arr[i];
			String[] keyval = item.split("=");
			if (keyval.length == 2) {
				if (keyval[0].toUpperCase().equals("DC"))
					domain += (keyval[1] + ".");
			}
		}
		if (domain.length() > 0)
			domain = domain.substring(0, domain.length() - 1);
		else
			domain = null;
		return domain;
	}

	/**
	 * @param searchByUserName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> firstResult(NamingEnumeration enumeration) {
		if (enumeration == null)
			return null;

		try {
			while (enumeration.hasMoreElements()) {
				SearchResult sr = (SearchResult) enumeration.next();
				Attributes attrs = sr.getAttributes();
				if (attrs != null) {
					Map<String, String> parseAttributes = parseAttributes(attrs);
					if (parseAttributes != null) {
						parseAttributes.put("DN", sr.getNameInNamespace());
					}
					return parseAttributes;
				}
			}
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return null;
	}

	/**
	 * @param searchByUserName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Map<String, String>> multipleResult(NamingEnumeration enumeration) {
		if (enumeration == null)
			return null;
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {
			while (enumeration.hasMoreElements()) {
				SearchResult sr = (SearchResult) enumeration.next();
				Attributes attrs = sr.getAttributes();
				if (attrs != null) {
					result.add(parseAttributes(attrs));
				}
			}
			/* return */
			return result;
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}
