
/**
 * @author Geln Yang
 * @version 1.0
 */
package org.haaproject.converter.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DBUtil {
	private static final String COMMA = ",";

	private static final String BATCH_ID_NAME = "batchIndentityId";

	private static final String SQL_KEY = "sql";

	private static final String TABLENAME_KEY = "tablename";

	private static final String ATTR_TABLE_NAME = "tableName";

	public static String[] getTableNames(Map<String, String[]> result) {
		return result.get(TABLENAME_KEY);
	}

	public static String[] getSqlCmds(Map<String, String[]> result) {
		return result.get(SQL_KEY);
	}

	public static Map<String, String[]> processTmx(String tmxContent, String batchId) throws Exception {
		StringReader strRead = new StringReader(tmxContent);
		BufferedReader buffer = new BufferedReader(strRead);

		SAXReader sax = new SAXReader();
		Document doc = sax.read(buffer);

		Element root = doc.getRootElement();
		ArrayList<String> sqlCmds = new ArrayList<String>();

		Set<String> tableNameSet = new HashSet<String>();
		// 遍歷root,產生SQL命令
		listElement(root, sqlCmds, tableNameSet, batchId);
		Map<String, String[]> result = new HashMap<String, String[]>();
		result.put(SQL_KEY, sqlCmds.toArray(new String[0]));
		result.put(TABLENAME_KEY, tableNameSet.toArray(new String[0]));
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void listElement(Element element, List<String> sqlCmds, Set<String> tableNames, String batchID) {
		List list = element.elements();
		Element ele = null;
		String tableName = null;
		for (int i = 0; i < list.size(); i++) {
			ele = (Element) list.get(i);
			tableName = ele.attributeValue(ATTR_TABLE_NAME);
			// 如果node有配置tableName的attribute且有child集合,則開始產生SQL命令
			if (ele.elements().size() > 0) {
				if (!isBlankOrNull(tableName)) {
					tableNames.add(tableName);
					genericSqlCmd(ele, sqlCmds, tableName, batchID);
				} else {
					listElement(ele, sqlCmds, tableNames, batchID);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void genericSqlCmd(Element element, List<String> sqlCmds, String tableName, String batchID) {
		StringBuffer insert = new StringBuffer("insert into ");
		StringBuffer values = new StringBuffer(") values (");
		insert.append(tableName).append("_TEMP (" + BATCH_ID_NAME + ",");
		values.append("'").append(batchID).append("',");

		Element ele = null;
		List list = element.elements();
		for (int i = 0; i < list.size(); i++) {
			ele = (Element) list.get(i);
			insert.append(ele.getName());
			if (isBlankOrNull(ele.getText())) {
				values.append("null");
			} else {
				values.append("'").append(ele.getText().trim()).append("'");
			}

			if (i < list.size() - 1) {
				insert.append(COMMA);
				values.append(COMMA);
			}
		}
		sqlCmds.add(insert.append(values.append(")")).toString());
	}

	private static boolean isBlankOrNull(String value) {
		return value == null || value.length() == 0;
	}

}
