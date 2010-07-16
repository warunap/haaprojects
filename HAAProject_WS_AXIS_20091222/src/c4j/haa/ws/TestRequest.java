/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Jul 16, 2010 11:56:33 PM $
 *
 * Author: Geln Yang
 * Date  : Jul 16, 2010 11:56:33 PM
 *
 */
package c4j.haa.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

/**
 * @Author Geln Yang
 * @version 1.0
 */
public class TestRequest {

	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
		String strUrl = "http://localhost:8080/axis/services/Corticon?ZHSalary";
		String content = FileUtils.readFileToString(new File("I:/ZHSalary.xml"), "UTF-8");
		String postRequest = postRequest(strUrl, content, null);
		System.out.println(postRequest);
	}

	public static String postRequest(String strUrl, String content, String encode) throws IOException,
			MalformedURLException, ProtocolException {
		if (encode == null)
			encode = "UTF-8";
		byte[] buf = content.getBytes(encode);
		URL url = new URL(strUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Length", String.valueOf(buf.length));
		httpConn.setRequestProperty("Content-Type", "application/xml; charset=" + encode);
		httpConn.setRequestProperty("SOAPAction", "\"\"");
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(buf);
		out.close();
		BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1)
			outputStream.write(b);
		in.close();
		outputStream.close();
		String result = new String(outputStream.toByteArray(), encode);
		return result;
	}
}
