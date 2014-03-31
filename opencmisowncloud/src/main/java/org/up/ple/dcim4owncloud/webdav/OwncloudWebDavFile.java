package org.up.ple.dcim4owncloud.webdav;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.fileshare.FileShareUserManager;
import org.apache.commons.io.IOUtils;
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
	private OwncloudWebDavFile parent;
	// private DavResource resource = null;

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

	public OwncloudWebDavFile(String pathname, FileShareUserManager userManager) {
		super(pathname);
		this.userManager = userManager;
		sardine = getSardineEndpoint(userManager);
		loader = new OwnCloudConfigurationLoader();
		webdavPath = loader.getOwnCloudAddress() + pathname;

		// initParent();
		// getResource();

		isDir = webdavPath.endsWith("/");
		if (isDir) {
			initResources();
		}
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
		String parentString = webdavPath.substring(0,
				webdavPath.lastIndexOf('/'));
		return parentString;
	}

	private DavResource getResource() {
		try {
			/**
			 * we assume that every path to file is unique
			 */
			DavResource resource = sardine.list(webdavPath).iterator().next();
			return resource;
		} catch (com.github.sardine.impl.SardineException e) {
			LOG.debug(e.getMessage());
			LOG.warn("could not authenticate for user"
					+ userManager.getDefaultUserName() + ":"
					+ userManager.getDefaultPassword());
		} catch (IOException e) {
			LOG.error("could not get resources for "
					+ loader.getOwnCloudAddress() + webdavPath + e.getMessage());
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
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return true;
	}

	@Override
	public int compareTo(File pathname) {
		return getName().equals(pathname) ? 1 : 0;
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
		}

		return false;
	}

	@Override
	public boolean delete() {
		try {
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
		LOG.debug("checking if file exists" + webdavPath + "\n"
				+ "with usercredentials" + userManager.getDefaultUserName()
				+ ":" + userManager.getDefaultUserName());
		try {
			return (boolean) sardine.exists(webdavPath);
		} catch (IOException e) {
			LOG.error("error while checking if existst" + webdavPath);

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

			LOG.error(e.getMessage());
		}
		return this;
	}

	@Override
	public String getAbsolutePath() {
		return this.getName();
	}

	@Override
	public File getCanonicalFile() throws IOException {
		return this;
	}

	@Override
	public String getCanonicalPath() throws IOException {
		return this.webdavPath;
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
		return webdavPath;
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
		return getName();
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
		}
		return -1;
	}

	@Override
	public int hashCode() {
		@SuppressWarnings("unused")
		String test = webdavPath;
		if (test == null) {
			LOG.error("webdavPath is null");
		}
		return webdavPath.hashCode();
	}

	private void initResources() {
		try {
			resources = sardine.list(webdavPath);
		} catch (com.github.sardine.impl.SardineException e) {
			LOG.debug(e.getMessage());
			LOG.warn("could not authenticate for user"
					+ userManager.getDefaultUserName() + ":"
					+ userManager.getDefaultPassword());
		} catch (IOException e) {
			LOG.error("could not get resources for "
					+ loader.getOwnCloudAddress() + webdavPath + e.getMessage());
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
		return getResource().getModified().getTime();
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
		String[] result = new String[resources.size()];
		Iterator<DavResource> it = resources.iterator();
		int i = 0;
		while (it.hasNext()) {
			result[i] = it.next().toString();
			i++;
		}
		if (isDir) {

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
			files.add(new OwncloudWebDavFile(it.next().toString(), userManager));
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
			sardine.createDirectory(webdavPath);
		} catch (IOException e) {
			LOG.error(e.getMessage());

		}
		return super.mkdir();
	}

	@Deprecated
	@Override
	public boolean mkdirs() {
		try {
			sardine.createDirectory(webdavPath);
		} catch (IOException e) {
			LOG.error(e.getMessage());

		}
		return super.mkdirs();
	}

	@Override
	public boolean renameTo(File dest) {
		if (!isDir) {
			try {
				InputStream inputstream = sardine.get(webdavPath);
				webdavPath = loader.getOwnCloudAddress() + dest.getName();
				sardine.put(webdavPath, IOUtils.toByteArray(inputstream));
			} catch (IOException e) {
				LOG.error("error while renaming");

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
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public URI toURI() {
		// TODO Auto-generated method stub
		return super.toURI();
	}

	@Override
	public URL toURL() throws MalformedURLException {

		// TODO Auto-generated method stub
		return super.toURL();
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
