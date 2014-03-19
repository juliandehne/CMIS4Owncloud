package org.up.ple.dcim4owncloud.webdav;

import java.io.File;
import java.io.IOException;

import org.up.ple.dcim4owncloud.configuration.OwnCloudConfigurationLoader;
import org.up.ple.dcim4owncloud.util.StreamUtil;

import com.github.sardine.Sardine;

public class WebDavClient {
	public static File getFileForPath(Sardine sardine, String relativPath)
			throws IOException {
		OwnCloudConfigurationLoader loader = new OwnCloudConfigurationLoader();
		return StreamUtil.stream2file(sardine.get(loader + relativPath));
	}
}
