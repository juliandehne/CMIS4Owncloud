package org.up.ple.dcim4owncloud.webdav;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.fileshare.FileShareUserManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.up.ple.dcim4owncloud.configuration.OwnCloudConfigurationLoader;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class OwncloudWebDavFile extends File {

	private static final Logger LOG = LoggerFactory
			.getLogger(OwncloudWebDavFile.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OwnCloudConfigurationLoader loader;

	private List<DavResource> resources;

	private Sardine sardine;

	private String webdavPath;

	private boolean isDir;

	private FileShareUserManager userManager;

	@Deprecated
	public OwncloudWebDavFile(File parent, String child) {
		super(parent, child);
		try {
			throw new Exception("constuctor not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	@Deprecated
	public OwncloudWebDavFile(String pathname) {
		super(pathname);
		try {
			throw new Exception("constuctor not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	public OwncloudWebDavFile(String path, FileShareUserManager userManager) {
		super(path);
		this.userManager = userManager;
		sardine = getSardineEndpoint(userManager);
		loader = new OwnCloudConfigurationLoader();

		initWebdavPath(path);

		isDir = webdavPath.endsWith("/");
		if (isDir) {
			initResources();
		}
	}

	private void initWebdavPath(String pathname) {
		Boolean isFullPath = pathname.startsWith(loader.getOwnCloudAddress());

		if (!pathname.startsWith("/") && !isFullPath) {
			throw new NotImplementedException(
					"local path should always start with / for " + pathname);
		}
		while (pathname.contains("webdav")) {
			pathname = pathname.replaceAll("^.*webdav", "");
		}
		webdavPath = loader.getOwnCloudAddress() + pathname;
		webdavPath = webdavPath.replace("//", "/");
		webdavPath = webdavPath.replace("http:/", "http://");
	}

	// private void initParent() {
	// if (getName() != "/") {
	// String parentString = getParentString();
	// this.parent = new OwncloudWebDavFile(parentString, userManager);
	// } else {
	// LOG.debug("root does not have parent dir and parent remains null");
	// }
	// }

	private String getParentString() {
		if (isFile()) {
			String parentString = webdavPath.substring(0,
					webdavPath.lastIndexOf('/'));
			return parentString;
		}
		String tmp1 = webdavPath.substring(0, webdavPath.length() - 1);
		String parentString = tmp1.substring(0, webdavPath.lastIndexOf('/'));
		parentString.replaceAll(loader.getOwnCloudAddress(), "");
		if (!parentString.endsWith("/")) {
			parentString += "/";
		}
		return parentString;

	}

	private DavResource getResource() {
		try {
			/**
			 * we assume that every path to file is unique
			 */
			sardine = getSardineEndpoint(userManager);
			DavResource resource = sardine.list(webdavPath).iterator().next();
			return resource;
		} catch (com.github.sardine.impl.SardineException e) {
			LOG.warn("could not get resource" + webdavPath + "for user"
					+ userManager.getDefaultUserName() + ":"
					+ userManager.getDefaultPassword());
		} catch (IOException e) {
			LOG.error("could not get resources for: " + webdavPath);
			e.printStackTrace();
		}
		return null;

	}

	@Deprecated
	public OwncloudWebDavFile(String parent, String child) {
		super(parent, child);
		try {
			throw new Exception("constuctor not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	@Deprecated
	public OwncloudWebDavFile(URI uri) {
		super(uri);
		try {
			throw new Exception("constuctor not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	@Override
	public boolean canExecute() {
		return false;
	}

	@Deprecated
	@Override
	public boolean canRead() {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return true;
	}

	@Deprecated
	@Override
	public boolean canWrite() {
		// try {
		// throw new Exception("Not implemented");
		// } catch (Exception e) {
		// e.printStackTrace();
		// LOG.error(e.getMessage());
		// }
		LOG.debug("can write is not implemented");
		return true;
	}

	@Override
	public int compareTo(File pathname) {
		return getName().equals(pathname.getName()) ? 1 : 0;
	}

	/**
	 * atomically creating files without content is not allowed!
	 */
	@Override
	public boolean createNewFile() throws IOException {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean delete() {
		try {
			sardine = getSardineEndpoint(userManager);
			sardine.delete(webdavPath);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * this function does not make sense in this context
	 */
	@Deprecated
	@Override
	public void deleteOnExit() {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {

			LOG.error(e.getMessage());
		}
	}

	@Override
	public boolean equals(Object obj) {
		return ((File) obj).getName().equals(this.getName());
	}

	@Override
	public boolean exists() {
		try {
			sardine = getSardineEndpoint(userManager);
			return (boolean) sardine.exists(webdavPath);
		} catch (IOException e) {
			LOG.error("error while checking if exists: " + webdavPath);
		}
		return false;
	}

	/**
	 * this function does not make sense in this context
	 */
	@Deprecated
	@Override
	public File getAbsoluteFile() {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return this;
	}

	@Override
	public String getAbsolutePath() {
		return this.getPath();
	}

	@Override
	public File getCanonicalFile() throws IOException {
		return this;
	}

	@Override
	public String getCanonicalPath() throws IOException {
		return this.getPath();
	}

	/**
	 * for simplicity we assume, that the webdav space is unlimited
	 */
	@Override
	public long getFreeSpace() {
		return 1000000000;
	}

	@Override
	public String getName() {
		if (isFile()) {
			int lastDirIndex = webdavPath.lastIndexOf("/");
			int lastIndex = webdavPath.length();
			return webdavPath.substring(lastDirIndex, lastIndex);
		} else {
			Boolean isRoot = isRoot();
			if (isRoot) {
				return "/";
			} else {
				String[] dirs = webdavPath.split("/");
				return "/" + dirs[dirs.length - 1] + "/";
			}
		}
	}

	private Boolean isRoot() {
		Boolean isRoot = webdavPath.equals(loader.getOwnCloudAddress() + "/")
				|| webdavPath.endsWith("/webdav/");
		return isRoot;
	}

	@Override
	public String getParent() {
		return getParentString();
	}

	@Override
	public File getParentFile() {
		return new OwncloudWebDavFile(getParentString(), userManager);
	}

	@Override
	public String getPath() {
		return this.webdavPath.replace(loader.getOwnCloudAddress(), "");
	}

	private Sardine getSardineEndpoint(final FileShareUserManager userManager) {
		Sardine sardine = SardineFactory.begin();
		sardine.setCredentials(userManager.getDefaultUserName(),
				userManager.getDefaultPassword());
		return sardine;
	}

	@Override
	public long getTotalSpace() {
		try {
			throw new Exception("not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return -1;
	}

	@Override
	public long getUsableSpace() {
		try {
			throw new Exception("not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int hashCode() {
		String test = webdavPath;
		if (test == null) {
			LOG.error("webdavPath is null");
		}
		return webdavPath.hashCode();
	}

	private void initResources() {
		try {
			sardine = getSardineEndpoint(userManager);
			resources = sardine.list(webdavPath);
		} catch (com.github.sardine.impl.SardineException e) {
			LOG.error("could not get resources for " + webdavPath
					+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("could not get resources for " + webdavPath
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAbsolute() {
		return true;
	}

	@Override
	public boolean isDirectory() {
		return isDir;
	}

	@Override
	public boolean isFile() {
		return !isDir;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	/**
	 * dies ist ein sehr teurer aufruf
	 */
	@Override
	public long lastModified() {
		Date date = getResource().getModified();
		Long lastModified = date.getTime();
		return lastModified;
	}

	@Override
	public long length() {
		if (!isDir) {
			return (long) getResource().getContentLength();
		} else {
			return -1;
		}
	}

	@Override
	public String[] list() {
		String[] result = new String[listFiles().length];
		for (int i = 0; i <= listFiles().length - 1; i++) {
			result[i] = listFiles()[i].getName();
		}
		return result;
	}

	@Deprecated
	@Override
	public String[] list(FilenameFilter filter) {

		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	@Override
	public File[] listFiles() {
		HashSet<File> files = new HashSet<File>();
		Iterator<DavResource> it = resources.iterator();
		while (it.hasNext()) {
			DavResource davResource = it.next();
			String childResource = davResource.toString();
			if (davResource.isDirectory() && !childResource.endsWith("/")) {
				childResource += "/";
			}
			files.add(new OwncloudWebDavFile(getPath() + childResource,
					userManager));
		}
		return files.toArray(new OwncloudWebDavFile[resources.size()]);
	}

	@Deprecated
	@Override
	public File[] listFiles(FileFilter filter) {
		return listFiles();
	}

	@Deprecated
	@Override
	public File[] listFiles(FilenameFilter filter) {
		return listFiles();
	}

	@Override
	public boolean mkdir() {
		try {
			sardine = getSardineEndpoint(userManager);
			sardine.createDirectory(webdavPath);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return super.mkdir();
	}

	@Deprecated
	@Override
	public boolean mkdirs() {
		try {
			sardine = getSardineEndpoint(userManager);
			sardine.createDirectory(webdavPath);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return super.mkdirs();
	}

	@Override
	public boolean renameTo(File dest) {
		if (!isDir) {
			try {
				sardine = getSardineEndpoint(userManager);
				InputStream inputstream = sardine.get(webdavPath);
				sardine.delete(webdavPath);
				webdavPath = loader.getOwnCloudAddress() + dest.getName();
				sardine.put(webdavPath, IOUtils.toByteArray(inputstream));
			} catch (IOException e) {
				LOG.error("error while renaming: " + getPath() + ":"
						+ dest.getPath());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * executable files are not supported
	 */
	@Override
	public boolean setExecutable(boolean executable) {
		return false;
	}

	/**
	 * executable files are not supported
	 */
	@Override
	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		return false;
	}

	@Deprecated
	@Override
	public boolean setLastModified(long time) {
		getResource().getModified().setTime(time);
		return isDir;
	}

	@Deprecated
	@Override
	public boolean setReadable(boolean readable) {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	@Deprecated
	@Override
	public boolean setReadable(boolean readable, boolean ownerOnly) {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	@Deprecated
	@Override
	public boolean setReadOnly() {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	@Deprecated
	@Override
	public boolean setWritable(boolean writable) {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	@Deprecated
	@Override
	public boolean setWritable(boolean writable, boolean ownerOnly) {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return false;
	}

	@Deprecated
	@Override
	public Path toPath() {
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return super.toPath();
	}

	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public URI toURI() {
		try {
			return new URI(getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}

	@Override
	public URL toURL() throws MalformedURLException {
		return new URL(getPath());
	}

	public void put(InputStream in) {
		try {
			sardine.put(webdavPath, in);
		} catch (IOException e) {
			LOG.error("could not create file on server" + webdavPath);
		}

	}

	/**
	 * byte[] data = FileUtils.readFileToByteArray(new
	 * File("/testdav/test_file.txt")); sardine.put(url+"/test/test.txt", data);
	 */

}
