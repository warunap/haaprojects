/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:HouseDetail.java	
 * Created Time:2013-2-27下午10:57:50
 */
package org.haaprojects.website.info.xmhouse.domain;

/**
 * @author Geln Yang
 * 
 */
public class HouseDetail {

	String id;

	String address;

	String type;

	String usage;

	String area;

	float price;

	String limit;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the usage
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * @param usage
	 *            the usage to set
	 */
	public void setUsage(String usage) {
		this.usage = usage;
	}

	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * @return the limit
	 */
	public String getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(String limit) {
		this.limit = limit;
	}

	/**
	 * @param string
	 */
	public void setFieldValue(String value) {
		try {
			String[] arr = value.split(":");
			if (arr.length > 1) {
				if ("性质".equals(arr[0])) {
					setType(arr[1]);
				} else if ("用途".equals(arr[0])) {
					setUsage(arr[1]);
				} else if ("面积".equals(arr[0])) {
					setArea(arr[1]);
				} else if ("价格".equals(arr[0])) {
					setPrice(Float.valueOf(arr[1]));
				} else if ("权属限制".equals(arr[0])) {
					setLimit(arr[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
