package org.up.liferay.test.owncloud;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class OwncloudTests {

	@Test
	public void simpleCreateShareTest() throws IOException {
		HttpClient client = new HttpClient();
		String auth = "root:voyager";
		String encoding = Base64.encodeBase64String(auth.getBytes());
		BufferedReader br = null;

		PostMethod method = new PostMethod(
				"http://localhost/owncloud/owncloud/ocs/v1.php/apps/files_sharing/api/v1/shares");
		
//		DeleteMethod method = new DeleteMethod("http://localhost/owncloud/owncloud/ocs/v1.php/apps/files_sharing/api/v1/shares");
//		method.setRequestHeader("Authorization", "Basic " + encoding);
//
//		method.add
		method.addParameter("path", "/liferay_documents/");
		method.addParameter("shareType", "0"); // share to a user
		method.addParameter("shareWith", "Test");
		method.addParameter("permissions", "31"); // all permissions

		try {
			int returnCode = client.executeMethod(method);

			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err
						.println("The Post method is not implemented by this URI");
				// still consume the response body
				method.getResponseBodyAsString();
			} else {
				br = new BufferedReader(new InputStreamReader(
						method.getResponseBodyAsStream()));
				String readLine;
				while (((readLine = br.readLine()) != null)) {
					System.err.println(readLine);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			method.releaseConnection();
			if (br != null)
				try {
					br.close();
				} catch (Exception fe) {
				}
		}
	}
}