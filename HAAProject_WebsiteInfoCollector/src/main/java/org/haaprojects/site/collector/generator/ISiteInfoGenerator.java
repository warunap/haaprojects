/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 5:58:27 PM
 */
package org.haaprojects.site.collector.generator;

import java.io.IOException;

import org.haaprojects.site.collector.data.CollectData;

/**
 * @author Geln Yang
 * @version 1.0
 */
public interface ISiteInfoGenerator {

	public String generate(CollectData data) throws IOException;

}
