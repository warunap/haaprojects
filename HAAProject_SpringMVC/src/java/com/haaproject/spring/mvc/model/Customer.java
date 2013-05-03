/**
 *      Project:HAAProject_SpringMVC       
 *    File Name:Customer.java	
 * Created Time:2013-5-2下午10:48:46
 */
package com.haaproject.spring.mvc.model;

/**
 * @author Geln Yang
 * 
 */
public class Customer {

	// textbox
	String userName;

	// textarea
	String address;

	// password
	String password;

	String confirmPassword;

	// checkbox
	boolean receiveNewsletter;

	String[] favFramework;

	// radio button
	String favNumber;

	String sex;

	// dropdown box
	String country;

	String javaSkills;

	// hidden value
	String secretValue;
	
	private boolean redirect;

	
	/**
	 * @return the redirect
	 */
	public boolean isRedirect() {
		return redirect;
	}

	
	/**
	 * @param redirect the redirect to set
	 */
	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * @param confirmPassword
	 *            the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * @return the receiveNewsletter
	 */
	public boolean isReceiveNewsletter() {
		return receiveNewsletter;
	}

	/**
	 * @param receiveNewsletter
	 *            the receiveNewsletter to set
	 */
	public void setReceiveNewsletter(boolean receiveNewsletter) {
		this.receiveNewsletter = receiveNewsletter;
	}

	/**
	 * @return the favFramework
	 */
	public String[] getFavFramework() {
		return favFramework;
	}

	/**
	 * @param favFramework
	 *            the favFramework to set
	 */
	public void setFavFramework(String[] favFramework) {
		this.favFramework = favFramework;
	}

	/**
	 * @return the favNumber
	 */
	public String getFavNumber() {
		return favNumber;
	}

	/**
	 * @param favNumber
	 *            the favNumber to set
	 */
	public void setFavNumber(String favNumber) {
		this.favNumber = favNumber;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the javaSkills
	 */
	public String getJavaSkills() {
		return javaSkills;
	}

	/**
	 * @param javaSkills
	 *            the javaSkills to set
	 */
	public void setJavaSkills(String javaSkills) {
		this.javaSkills = javaSkills;
	}

	/**
	 * @return the secretValue
	 */
	public String getSecretValue() {
		return secretValue;
	}

	/**
	 * @param secretValue
	 *            the secretValue to set
	 */
	public void setSecretValue(String secretValue) {
		this.secretValue = secretValue;
	}

}
