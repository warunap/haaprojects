package org.haaproject.kerberos.test;
/**
 * Created By: Geln Yang
 * Created Date: 2013-4-3 下午4:35:38
 */

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestDecodeKey {

	/**
	 * WWW-Authenticate = Negotiate<br>
	 * WWW-Authenticate = NTLM<br>
	 * authorization = Negotiate TlRMTVNTUAABAAAAB4IIogAAAAAAAAAAAAAAAAAAAAAFAs4OAAAADw==<br>
	 * WWW-Authenticate = Negotiate TlRMTVNTUAACAAAAAAAAAAAAAAABAgAAB+KgcyrfF9k=<br>
	 * authorization = Negotiate
	 * TlRMTVNTUAADAAAAGAAYAHwAAAAYABgAlAAAAA4ADgBIAAAAEgASAFYAAAAUABQAaAAAAAAAAACsAAAABQIAAgUCzg4AAAAPQwBPAE0AVwBBAFYARQBjAHcAdQBzAGUAcgAyADIAMgBDAFcAMQAxADIAQwBXADIAMgAyAOZ6jcofHJajvJronJOL45FhZqp9EE11SeZ6jcofHJajvJronJOL45FhZqp9EE11SQ
	 * ==<br>
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws Exception {
		String encode = "UTF-8";
		String message1 = "TlRMTVNTUAABAAAAB4IIogAAAAAAAAAAAAAAAAAAAAAFAs4OAAAADw==";
		byte[] bytes = Base64.decodeBase64(message1);
		System.out.println(new String(bytes, encode));
		for (int i = 0; i < bytes.length; i++) {
			// bytes = Arrays.copyOfRange(bytes, i, bytes.length + 1);
			// System.out.println(new String(bytes));
			System.out.print((char) bytes[i]);
		}
		System.out.println();
		String message2 = "TlRMTVNTUAACAAAAAAAAAAAAAAABAgAAB+KgcyrfF9k=";
		bytes = Base64.decodeBase64(message2);
		System.out.println(new String(bytes, encode));
		String message3 = "TlRMTVNTUAADAAAAGAAYAHwAAAAYABgAlAAAAA4ADgBIAAAAEgASAFYAAAAUABQAaAAAAAAAAACsAAAABQIAAgUCzg4AAAAPQwBPAE0AVwBBAFYARQBjAHcAdQBzAGUAcgAyADIAMgBDAFcAMQAxADIAQwBXADIAMgAyAOZ6jcofHJajvJronJOL45FhZqp9EE11SeZ6jcofHJajvJronJOL45FhZqp9EE11SQ==";
		bytes = Base64.decodeBase64(message3);
		System.out.println(new String(bytes, encode));
	}
}
