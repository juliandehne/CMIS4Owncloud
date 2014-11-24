package org.up.liferay.owncloud;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.up.liferay.webdav.WebdavConfigurationLoader;


public class OwncloudShareCreator {
	public void createShare(Set<String> users, String authorName, String authorpasswd, String sharepath) {
		users.remove(authorName);
		for (String user : users) {
			createShare(user, authorName, authorpasswd, sharepath);
		}
	}
	
	private void createShare (String user, String authorName, String authorpasswd, String sharepath) {
		HttpClient client = new HttpClient();
		String auth = authorName+":"+authorpasswd;
		String encoding = Base64.encodeBase64String(auth.getBytes());
		BufferedReader br = null;
		
		PostMethod method = new PostMethod(WebdavConfigurationLoader.getOwncloudShareAddress());
		
		method.addParameter("path", sharepath);
		method.addParameter("shareType", "0"); // share to a user
		method.addParameter("shareWith",  user);
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
