/**
 * <p>
 * <ul>
 * <li>File: DesktopAppSample.java</li>
 * <li>Create:Jun 12, 2011</li>
 * <li>Author:Geln Yang</li>
 * </ul>
 * </p>
 * <hr>
 * <p>
 * <!-- comment -->
 * </p>
 */
package org.haaprojects.oauth2.desktop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * <pre><b><font color="blue">DesktopAppSample</font></b></pre>
 * 
 * <pre><b>&nbsp;-- description--</b></pre>
 * <pre></pre>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */

public class FireEagleMain {

	//private static String consumerKey = "2qnk0OzpuzzU";
	//private static String consumerSecret = "Ctp1QtFbtSaFhOJbOLMCUPio9c75zIaG";
	private static String consumerKey = "dj0yJmk9cVpXbXBsMEo3TFFtJmQ9WVdrOVZFRmpVMmszTkRnbWNHbzlPREU0TURJd05UWXkmcz1jb25zdW1lcnNlY3JldCZ4PWU5";

	private static String consumerSecret = "90fd04d418f86d969a7edc79749817db";

	public static void main(String[] args) throws Exception {

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);

		OAuthProvider provider = new CommonsHttpOAuthProvider(
				"https://fireeagle.yahooapis.com/oauth/request_token",
				"https://fireeagle.yahooapis.com/oauth/access_token",
				"https://fireeagle.yahoo.net/oauth/authorize");

		System.out.println("Fetching request token from Fire Eagle...");

		// we do not support callbacks, thus pass OOB
		String authUrl = provider.retrieveRequestToken(consumer, "http://sisopipo.com");

		System.out.println("Request token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());

		System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
		System.out.println("Enter the verification code and hit ENTER when you're done");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();

		System.out.println("Fetching access token from Fire Eagle...");

		provider.retrieveAccessToken(consumer, code);

		System.out.println("Access token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());

		HttpPost request = new HttpPost("https://fireeagle.yahooapis.com/api/0.1/update");
		StringEntity body = new StringEntity("city=hamburg&label="
				+ URLEncoder.encode("Send via Signpost!", "UTF-8"));
		body.setContentType("application/x-www-form-urlencoded");
		request.setEntity(body);

		consumer.sign(request);

		System.out.println("Sending update request to Fire Eagle...");

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);

		System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
				+ response.getStatusLine().getReasonPhrase());
	}
}
