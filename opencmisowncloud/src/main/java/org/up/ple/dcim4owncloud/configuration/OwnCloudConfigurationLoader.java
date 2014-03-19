package org.up.ple.dcim4owncloud.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OwnCloudConfigurationLoader {

	private static final Logger LOG = LoggerFactory
			.getLogger(OwnCloudConfigurationLoader.class);

	public String getOwnCloudAddress() {
		Properties prop = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("owncloud.properties");
		try {
			prop.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return (String) prop.getProperty("owncloudaddress");
	}
}
