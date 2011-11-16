/**
 * Created Date: Nov 16, 2011 5:20:02 PM
 */
package test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.haaproject.converter.TestConverter;
import org.haaproject.converter.dom.Converter;
import org.haaproject.converter.exception.CfgException;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.factory.BatchReaderFactory;
import org.haaproject.converter.factory.ConvertCfgFactory;
import org.haaproject.converter.reader.BatchReader;
import org.haaproject.converter.util.ParseUtil;

import junit.framework.TestCase;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestStockData extends TestCase {

	@SuppressWarnings("unchecked")
	public void testParse() throws CfgException, ParseException, IOException {
		Converter converter = ConvertCfgFactory.load("/config/stockData.xml");
		InputStream stream = TestConverter.class.getResourceAsStream("/data/601899.csv");
		BatchReader reader = BatchReaderFactory.streamReader(stream, converter);
		HashMap<String, Object> data = (HashMap<String, Object>) ParseUtil.batchParse(reader);
		Object header = data.get("header");
		System.out.println(header);

		List<HashMap<String, Object>> items = (List<HashMap<String, Object>>) data.get("details");
		for (HashMap<String, Object> hashMap : items) {
			System.out.println(hashMap);
		}
	}
}
