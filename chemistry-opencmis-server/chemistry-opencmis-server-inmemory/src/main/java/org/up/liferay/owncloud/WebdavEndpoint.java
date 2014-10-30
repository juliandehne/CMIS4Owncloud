package org.up.liferay.owncloud;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.chemistry.opencmis.commons.server.CallContext;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class WebdavEndpoint {
	private Sardine sardine;
	private String endpoint;
	private String user;
	private String password;

	public WebdavEndpoint(CallContext context) {
		this.sardine = SardineFactory.begin();
		user = context.getUsername();
		password =   context.getPassword();
		this.sardine.setCredentials(user,password);
		this.endpoint = OwnCloudConfigurationLoader.getOwnCloudAddress();
	}
	
	public Sardine getSardine() {
		return sardine;
	}

	public String getEndpoint() {
		return endpoint;
	}
	
	public String getEndpointPath() {
		try {
			return new URL(endpoint).getPath();
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
	}
	
	public Boolean isValidCredentialinDebug() {
		if (user == null || password == null) {
			return false;
		}
		return (user.equals("test") && password.equals("test"));
	}
	
}
