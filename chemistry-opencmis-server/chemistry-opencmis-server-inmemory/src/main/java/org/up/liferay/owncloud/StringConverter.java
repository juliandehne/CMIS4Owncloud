package org.up.liferay.owncloud;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.github.sardine.DavResource;

public class StringConverter {
	public static String createNotEncodedRootId() {
		return "/";
	}

	public static String createEncodedRootId() {
		return encode(createEncodedRootId());
	}

	public static String encodedIdToWebdav(String id) {
		return OwnCloudConfigurationLoader.getOwnCloudAddress() + decode(id);
	}

	/**
	 * Root Directory is also listed (first)!!
	 * 
	 * @param resource
	 * @return
	 */
	public static String webdavToIdNotEncoded(DavResource resource) {
		if (resource.isDirectory()) {
			String path = resource.toString().replace(OwnCloudConfigurationLoader.getEndpointPath(), "");			
			return path;
		} else {
			String path = resource.getPath().replace(
					OwnCloudConfigurationLoader.getEndpointPath(), "");
			return path;
		}
	}
	
	public static String webdavToIdEncoded(DavResource resource) {
		return encode(webdavToIdNotEncoded(resource));		
	}
	

	public static String encodedIdToName(String id) {
		String unencodedId = decode(id);
		if (unencodedId.endsWith("/")) {
			String idWithoutSlash =  unencodedId.substring(0, unencodedId.lastIndexOf("/"));
			return idWithoutSlash.substring(idWithoutSlash.lastIndexOf("/"));
		} else {
			return unencodedId.substring(unencodedId.lastIndexOf("/"));
		}
	}
	
	public static String decodedIdToParent(String id)  {
		File file = new File(id);
		String result = file.getParentFile().getName();		
		return result;
	}

	public static String encode(String s) {
		return URLEncoder.encode(s);
	}

	public static String decode(String s) {
		return URLDecoder.decode(s);
	}
}
