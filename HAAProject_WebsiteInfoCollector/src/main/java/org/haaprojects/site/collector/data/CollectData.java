/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 6:00:03 PM
 */
package org.haaprojects.site.collector.data;

import java.util.HashMap;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class CollectData extends HashMap<String, SiteInfo> {

	private static final long serialVersionUID = 1L;

	public SiteInfo getSiteInfo(String host) {
		if (host != null) {
			SiteInfo info = get(host);
			if (info == null) {
				info = new SiteInfo();
				info.setHostUrl(host);
				put(host, info);
			}
			return info;
		}
		return null;
	}

}
