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
	private DavResource resource = null;

	private Sardine sardine;

	private String webdavPath;

	private boolean isDir;

	private FileShareUserManager userManager;

	@Deprecated
	public OwncloudWebDavFile(File parent, String child) {
		super(parent, child);
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	@Deprecated
	public OwncloudWebDavFile(String pathname) {
		super(pathname);
		try {
			throw new Exception("Not implemented");
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

		initParent();
		getResource();

		// isDir = webdavPath.endsWith("/");
		isDir = resource.isDirectory();
		if (isDir) {
			initResources();
		}

	}

	private void initParent() {
		if (getName() != "/") {
			this.parent = new OwncloudWebDavFile(webdavPath.substring(0,
					webdavPath.lastIndexOf('/') - 1), userManager);
		} else {
			LOG.debug("root does not have parent dir and parent remains null");
		}
	}

	private void getResource() {
		try {
			/**
			 * we assume that every path to file is unique
			 */
			resource = sardine.getResources(webdavPath).iterator().next();
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

	@Deprecated
	public OwncloudWebDavFile(String parent, String child) {
		super(parent, child);
		try {
			throw new Exception("Not implemented");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	@Deprecated
	public OwncloudWebDavFile(URI uri) {
		super(uri);
		try {
			throw new Exception("Not implemented");
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
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	@Override
	public boolean equals(Object obj) {
		return ((File) obj).getName().equals(this.getName());
	}

	@Override
	public boolean exists() {
		return (boolean) (resource != null);
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
		return this.getName();
	}

	@Override
	public File getCanonicalFile() throws IOException {
		return this;
	}

	@Override
	public String getCanonicalPath() throws IOException {
		return this.getName();
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
		return super.getName();
	}

	@Override
	public String getParent() {
		return parent.getName();
	}

	@Override
	public File getParentFile() {
		return parent;
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
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return -1;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
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

	@Override
	public long lastModified() {
		return resource.getModified().getTime();
	}

	@Override
	public long length() {
		if (!isDir) {
			return (long) resource.getContentLength();
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
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public File[] listFiles() {
		HashSet<File> files = new HashSet<File>();
		Iterator<DavResource> it = resources.iterator();
		while (it.hasNext()) {
			files.add(new OwncloudWebDavFile(it.next().toString()));
		}
		return files.toArray(new File[resources.size()]);
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
			e.printStackTrace();
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
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	@Override
	public boolean setLastModified(long time) {
		resource.getModified().setTime(time);
		return isDir;
	}

	@Override
	public boolean setReadable(boolean readable) {
		// TODO Auto-generated method stub
		return super.setReadable(readable);
	}

	@Override
	public boolean setReadable(boolean readable, boolean ownerOnly) {
		// TODO Auto-generated method stub
		return super.setReadable(readable, ownerOnly);
	}

	@Override
	public boolean setReadOnly() {
		// TODO Auto-generated method stub
		return super.setReadOnly();
	}

	@Override
	public boolean setWritable(boolean writable) {
		// TODO Auto-generated method stub
		return super.setWritable(writable);
	}

	@Override
	public boolean setWritable(boolean writable, boolean ownerOnly) {
		// TODO Auto-generated method stub
		return super.setWritable(writable, ownerOnly);
	}

	@Override
	public Path toPath() {
		// TODO Auto-generated method stub
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

	/**
	 * byte[] data = FileUtils.readFileToByteArray(new
	 * File("/testdav/test_file.txt")); sardine.put(url+"/test/test.txt", data);
	 */

}
