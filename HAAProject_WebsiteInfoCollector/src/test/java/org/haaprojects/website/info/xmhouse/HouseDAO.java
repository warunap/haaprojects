/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:BuildingDetailDAO.java	
 * Created Time:2013-2-27下午10:45:07
 */
package org.haaprojects.website.info.xmhouse;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.haaprojects.website.info.xmhouse.domain.HouseDetail;
import org.haaprojects.website.info.xmhouse.domain.SecondaryHouse;

/**
 * @author Geln Yang
 * 
 */
public class HouseDAO {

	private static final String SPLITOR = "\\|\\|";

	private static String encoding = "UTF-8";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		saveDataFromTextFile();
		System.out.println("over");

	}

	/**
	 * @throws Exception 
	 * 
	 */
	public static void saveDataFromTextFile() throws Exception {
		File file = new File("e:/housedata/xmhouse_house_detail_list.txt");
		List<String> lines = FileUtils.readLines(file, encoding);
		Connection connection = getJDBCConnection();
		for (String line : lines) {
			saveHouseTextInfoLine(connection,line);

		}
		connection.close();		
	}

	/**
	 * @param connection
	 * @param line
	 */
	public static void saveHouseTextInfoLine(Connection connection, String line) {
		HouseDetail houseDetail = new HouseDetail();
		String[] arr = line.split(SPLITOR);
		String id = arr[0];
		String address = arr[1];

		houseDetail.setId(id);
		houseDetail.setAddress(address);

		for (int i = 2; i < arr.length; i++) {
			houseDetail.setFieldValue(arr[i]);
		}

		saveHouseDetail(connection, houseDetail);
	}

	/**
	 * @param connection
	 * @param houseDetail
	 */
	public static void saveHouseDetail(Connection connection, HouseDetail houseDetail) {
		String id = houseDetail.getId();
		try {
			Statement statement = connection.createStatement();
			ResultSet query = statement.executeQuery("select * from house_detail where house_id='" + id + "'");
			if (query.next()) {
				String updateSql = "update house_detail set price=" + houseDetail.getPrice() + " , house_limit='" + houseDetail.getLimit() + "' where house_id='" + id + "'";
				System.out.println(updateSql);
				statement.executeUpdate(updateSql);
			} else {
				StringBuffer insertSql = new StringBuffer("insert into house_detail(house_id , house_address ,house_type ,house_usage ,house_area ,house_price , house_limit) values(");
				insertSql.append("'" + houseDetail.getId() + "'");
				insertSql.append(",'" + houseDetail.getAddress() + "'");
				insertSql.append(",'" + houseDetail.getType() + "'");
				insertSql.append(",'" + houseDetail.getUsage() + "'");
				insertSql.append(",'" + houseDetail.getArea() + "'");
				insertSql.append(",'" + houseDetail.getPrice() + "'");
				insertSql.append(",'" + houseDetail.getLimit() + "'");
				insertSql.append(")");
				System.out.println(insertSql);

				statement.execute(insertSql.toString());
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Set<String> getHouseIdSet(Connection connection) {
		Set<String> set =new HashSet<String>();
		try {
			Statement statement = connection.createStatement();
			ResultSet query = statement.executeQuery("select house_id from house_detail");
			while (query.next()) {
				set.add(query.getString(1));
			} 
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return set ;
	}

	
	
	/**
	 * @return
	 */
	public static Connection getJDBCConnection() {
		try {
			return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "house", "password");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		;
		return null;
	}

	/**
	 * @param connection
	 * @return
	 */
	public static Set<String> getSecondaryHouseIdSet(Connection connection) {
		Set<String> set =new HashSet<String>();
		try {
			Statement statement = connection.createStatement();
			ResultSet query = statement.executeQuery("select house_id from secondary_house");
			while (query.next()) {
				set.add(query.getString(1));
			} 
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return set ;
	}

	/**
	 * @param connection
	 * @param houseDetail
	 */
	public static void saveSecondaryHouse(Connection connection, SecondaryHouse houseDetail) {
		String id = houseDetail.getId();
		try {
			Statement statement = connection.createStatement();
			ResultSet query = statement.executeQuery("select * from secondary_house where house_id='" + id + "'");
			if (query.next()) {
				String updateSql = "update secondary_house set house_price=" + houseDetail.getPrice() + " , PUBLISH_TIME='" + houseDetail.getPublishTime() + "' where house_id='" + id + "'";
				System.out.println(updateSql);
				statement.executeUpdate(updateSql);
			} else {
				StringBuffer insertSql = new StringBuffer("insert into secondary_house(house_id , house_address ,house_type ,house_usage ,house_area ,house_price , PUBLISH_TIME,COMPANY_NAME,CONTACT,PHONE_NO,QQ,EMAIL) values(");
				insertSql.append("'" + houseDetail.getId() + "'");
				insertSql.append(",'" + houseDetail.getAddress() + "'");
				insertSql.append(",'" + houseDetail.getType() + "'");
				insertSql.append(",'" + houseDetail.getUsage() + "'");
				insertSql.append(",'" + houseDetail.getArea() + "'");
				insertSql.append(",'" + houseDetail.getPrice() + "'");
				insertSql.append(",'" + houseDetail.getPublishTime() + "'");
				insertSql.append(",'" + houseDetail.getCompanyName() + "'");
				insertSql.append(",'" + houseDetail.getContact() + "'");
				insertSql.append(",'" + houseDetail.getPhoneNo() + "'");
				insertSql.append(",'" + houseDetail.getQq() + "'");
				insertSql.append(",'" + houseDetail.getEmail() + "'");
				
				
				insertSql.append(")");
				System.out.println(insertSql);

				statement.execute(insertSql.toString());
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
