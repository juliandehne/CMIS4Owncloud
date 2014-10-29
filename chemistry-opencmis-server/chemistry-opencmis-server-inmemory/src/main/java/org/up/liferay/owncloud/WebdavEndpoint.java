package org.up.liferay.owncloud;

import org.apache.chemistry.opencmis.commons.server.CallContext;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class WebdavEndpoint {
	private Sardine sardine;
	private String endpoint;

	public WebdavEndpoint(CallContext context) {
		this.sardine = SardineFactory.begin();
		this.sardine.setCredentials(context.getUsername(), context.getPassword());
		this.endpoint = OwnCloudConfigurationLoader.getOwnCloudAddress();
	}
	
	public Sardine getSardine() {
		return sardine;
	}

	public String getEndpoint() {
		return endpoint;
	}
	
}
