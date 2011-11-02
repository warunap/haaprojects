/**
 * $Revision: 1.7 $
 * $Author: geln_yang $
 * $Date: 2011/09/06 12:51:17 $
 *
 * Author: Eric Yang
 * Date  : May 21, 2010 11:02:32 AM
 *
 */
package org.haaproject.converter.dom;

import java.io.Serializable;

/**
 * @Author Eric Yang
 * @version 1.0
 */
public abstract class Container implements Serializable {

	public static final String OCCURS_ONCE = "1";

	public static final String OCCURS_NONE_ONCE = "?";

	public static final String OCCURS_MANY = "+";

	public static final String OCCURS_NONE_MANY = "*";

	private static final int FLAG_READ = 1;

	private static final int FLAG_UNREAD = 0;

	protected String name;

	/** The class name which the data of the line belong to */
	protected String className;

	/**
	 * how many times this type line appear.
	 * <li>1:appear one time</li>
	 * <li>?(&#63;):not or appear one time</li>
	 * <li>+(&#43;):appear one or many times</li>
	 * <li>*(&#42;):not or appear many times</li>
	 * default 1;
	 */
	protected String occurs = "1";

	protected Container parent;
	/**
	 * The flag of read.<br>
	 * <li>0:unread</li>
	 * <li>1:read</li>
	 * This field is used when batch reading.
	 */
	private int readFlag = FLAG_UNREAD;

	private Converter converter;

	public Converter getConverter() {
		if (converter == null && parent != null) {
			return parent.getConverter();
		}
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	/*--------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	public Object newDataInstance() {
		try {
			Class objClass = Class.forName(className);
			Object obj = objClass.newInstance();
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public abstract boolean isBelongToMe(String firstLineContent);

	public String getNamespace() {
		if (parent != null)
			return parent.getNamespace();
		return Converter.SCHEMA_BASE + name + ".xsd";
	}

	public boolean isShowOnce() {
		return OCCURS_ONCE.equals(occurs);
	}

	public boolean isShowNoneOnce() {
		return OCCURS_NONE_ONCE.equals(occurs);
	}

	public boolean isShowMany() {
		return OCCURS_MANY.equals(occurs);
	}

	public boolean isShowNoneMany() {
		return OCCURS_NONE_MANY.equals(occurs);
	}

	/** set read flag */
	public void setReadFlag() {
		readFlag = FLAG_READ;
	}

	/** reset read flag */
	public void resetReadFlag() {
		readFlag = FLAG_UNREAD;
	}

	/** whether read */
	public boolean isRead() {
		return readFlag == FLAG_READ;
	}

	/* ========================================== */

	public String getOccurs() {
		return occurs;
	}

	public void setOccurs(String occurs) {
		this.occurs = occurs;
	}

	public Container getParent() {
		return parent;
	}

	public void setParent(Container parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
