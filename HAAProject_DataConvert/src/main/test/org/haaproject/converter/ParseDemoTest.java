package org.haaproject.converter;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ognl.OgnlException;

import org.haaproject.converter.dom.Component;
import org.haaproject.converter.dom.Converter;
import org.haaproject.converter.exception.BuildException;
import org.haaproject.converter.exception.CfgException;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.factory.BatchReaderFactory;
import org.haaproject.converter.factory.ConvertCfgFactory;
import org.haaproject.converter.reader.BatchReader;
import org.haaproject.converter.util.BuildUtil;
import org.haaproject.converter.util.OgnlUtil;
import org.haaproject.converter.util.XMLUtil;

public class ParseDemoTest extends TestCase {

	public void testParse() throws IOException, CfgException, ParseException, OgnlException {
		Converter converter = ConvertCfgFactory.load("/config/test.xml");
		InputStream stream = TestConverter.class.getResourceAsStream("/data/test.dat");
		BatchReader reader = BatchReaderFactory.streamReader(stream, converter);
		Object object = ParseDemoUtil.batchParse(reader);
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

		Component component = converter.getComponent();
		try {
			String obj2xml = BuildUtil.obj2xml(object, component);
			System.out.println(obj2xml);
		} catch (BuildException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		StringBuffer schema = XMLUtil.component2schema(component);
		System.out.println(schema);
	}
}
