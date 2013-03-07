/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:SecondaryHouse.java	
 * Created Time:2013-3-1下午11:14:03
 */
package org.haaprojects.website.info.xmhouse.domain;


/**
 * @author Geln Yang
 *
 */
public class SecondaryHouse extends HouseDetail{

	private String companyName;
	private String contact;
	private String publishTime;
	private String qq;
	private String email;
	private String phoneNo;
	
	public void setFieldValue(String key , String value) {
		if(key==null) {
			return;
		}
		key =key.replace("&nbsp;", "").replace(":", "");
		if(value!=null) {
			value=value.replace("万元", "0000");
		}
		try {
			if("显示坐落".equals(key)) {
				setAddress(value);
			}
			else	if ("发布类型（租/售）".equals(key)) {
					setType(value);
				} else if ("用途".equals(key)) {
					setUsage(value);
				} else if ("面积".equals(key)) {
					setArea(value);
				} else if ("价格".equals(key)) {
					setPrice(Float.valueOf(value));
				} else if ("建筑面积（平方米）".equals(key)) {
					setArea(value);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	
	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	
	/**
	 * @param contact the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	
	/**
	 * @return the publishTime
	 */
	public String getPublishTime() {
		return publishTime;
	}

	
	/**
	 * @param publishTime the publishTime to set
	 */
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	
	/**
	 * @return the qq
	 */
	public String getQq() {
		return qq;
	}

	
	/**
	 * @param qq the qq to set
	 */
	public void setQq(String qq) {
		this.qq = qq;
	}

	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	
	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}


	
	/**
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
}
