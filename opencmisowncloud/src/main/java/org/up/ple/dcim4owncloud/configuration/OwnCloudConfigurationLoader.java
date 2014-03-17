package org.up.ple.dcim4owncloud.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OwnCloudConfigurationLoader {
	public String getOwnCloudAddress() throws IOException {
		Properties prop = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("owncloud.properties");
		prop.load(in);
		in.close();
		return (String) prop.getProperty("owncloudaddress");
	}
}
