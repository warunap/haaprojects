package org.haa.dataconvert;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ognl.OgnlException;

import org.haa.dataconvert.dom.Component;
import org.haa.dataconvert.exception.BuildException;
import org.haa.dataconvert.exception.CfgException;
import org.haa.dataconvert.exception.ParseException;
import org.haa.dataconvert.factory.BatchReaderFactory;
import org.haa.dataconvert.factory.ConvertCfgFactory;
import org.haa.dataconvert.reader.BatchReader;
import org.haa.dataconvert.util.OgnlUtil;

/**
 * @Author Eric Yang
 * @version 1.0
 */
public class TestConverter extends TestCase {

	public void testParse() throws IOException, CfgException, ParseException, OgnlException {
		Component component = ConvertCfgFactory.load("/config/test.xml");
		InputStream stream = TestConverter.class.getResourceAsStream("/data/test.dat");
		BatchReader reader = BatchReaderFactory.streamReader(stream, "UTF-8", 100, component);
		Object object = Converter.batchParse(component, reader);
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
			String obj2xml = Converter.obj2xml(object, component);
			String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns=\"http://www.comwave.com.cn/schema/converter/root.xsd\"><header><title>the_title</title></header><details xmlns=\"http://www.comwave.com.cn/schema/converter/root.xsd\"><item><name>name1</name><data>data1</data></item><item><name>name2</name><data>data2</data></item></details><footer><comment>the_comment</comment></footer></root>";
			System.out.println(obj2xml);
			assertEquals(result, obj2xml);
		} catch (BuildException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		StringBuffer schema = Converter.component2schema(component);
		System.out.println(schema);
		String expectedSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.comwave.com.cn/schema/converter/root.xsd\" targetNamespace=\"http://www.comwave.com.cn/schema/converter/root.xsd\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\"><xs:element name=\"root\"><xs:complexType><xs:sequence><xs:element ref=\"header\"  /><xs:element ref=\"details\"  /><xs:element ref=\"footer\"  /></xs:sequence></xs:complexType></xs:element><xs:element name=\"header\"><xs:complexType><xs:sequence><xs:element name=\"title\" type=\"xs:string\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"details\"><xs:complexType><xs:sequence><xs:element ref=\"item\"  maxOccurs=\"unbounded\"  /></xs:sequence></xs:complexType></xs:element><xs:element name=\"item\"><xs:complexType><xs:sequence><xs:element name=\"name\" type=\"xs:string\"/><xs:element name=\"data\" type=\"xs:string\" minOccurs=\"0\" /></xs:sequence></xs:complexType></xs:element><xs:element name=\"footer\"><xs:complexType><xs:sequence><xs:element name=\"comment\" type=\"xs:string\"/></xs:sequence></xs:complexType></xs:element></xs:schema>";
		assertNotNull(schema);
		assertEquals(expectedSchema, schema.toString());
	}
	
	public static void main(String[] args) {
		System.out.println("aa");
	}
}
