/**
 * Created By: Geln
 * Created Date: 2012-11-16 下午2:16:18
 */
package test;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestEncode {

	public static void main(String[] args) throws Exception {
		String s = "\u4f60";
		testout(s, "UTF-8");
		testout(s, "BIG5");
		testout(s, "GBK");
		System.out.println("\u3333");
		System.out.println("\u20FFF");
	}

	private static void testout(String s, String encode) throws Exception {
		byte[] bytes = s.getBytes(encode);
		for (byte b : bytes) {
			String c = Integer.toHexString((int) (b));
			if (c.length() > 4) {
				c = c.substring(c.length() - 4);
			}
			System.out.print(c.toUpperCase() + ",");
		}
		System.out.println();
		System.out.println("======================");
	}
}
