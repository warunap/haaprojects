package org.haaproject.converter;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ognl.OgnlException;

import org.haaproject.converter.dom.Component;
import org.haaproject.converter.exception.BuildException;
import org.haaproject.converter.exception.CfgException;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.factory.BatchReaderFactory;
import org.haaproject.converter.factory.ConvertCfgFactory;
import org.haaproject.converter.reader.BatchReader;
import org.haaproject.converter.util.ConverterUtil;
import org.haaproject.converter.util.OgnlUtil;

/**
 * @Author Eric Yang
 * @version 1.0
 */
public class TestConverter extends TestCase {

	public void testParse() throws IOException, CfgException, ParseException, OgnlException {
		Component component = ConvertCfgFactory.load("/config/test.xml");
		InputStream stream = TestConverter.class.getResourceAsStream("/data/test.dat");
		BatchReader reader = BatchReaderFactory.streamReader(stream, "UTF-8", 100, component);
		Object object = ConverterUtil.batchParse(component, reader);
		assertNotNull(object);
		Object value = OgnlUtil.getValue("footer.comment", object);
		assertEquals("the_comment", value);
		value = OgnlUtil.getValue("header.title", object);
		assertEquals("the_title", value);
		value = OgnlUtil.getValue("details[0].name", object);
		assertEquals("name1", value);
		value = OgnlUtil.getValue("details[0].data", object);
		assertEquals("data1", value);
		value = OgnlUtil.getValue("details[1].name", object);
		assertEquals("name2", value);
		value = OgnlUtil.getValue("details[1].data", object);
		assertEquals("data2", value);

		try {
			String obj2xml = ConverterUtil.obj2xml(object, component);
			System.out.println(obj2xml);
		} catch (BuildException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		StringBuffer schema = ConverterUtil.component2schema(component);
		System.out.println(schema);
	}

}
