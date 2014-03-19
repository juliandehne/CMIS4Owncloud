package org.up.ple.dcim4owncloud.webdav;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.apache.chemistry.opencmis.fileshare.FileShareUserManager;
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

	@Deprecated
	public OwncloudWebDavFile(File parent, String child) {
		super(parent, child);
	}

	@Deprecated
	public OwncloudWebDavFile(String pathname) {
		super(pathname);
	}

	public OwncloudWebDavFile(String pathname, FileShareUserManager userManager) {
		super(pathname);
		sardine = getSardineEndpoint(userManager);
		loader = new OwnCloudConfigurationLoader();
		getResources(pathname, userManager);
	}

	@Deprecated
	public OwncloudWebDavFile(String parent, String child) {
		super(parent, child);
	}

	@Deprecated
	public OwncloudWebDavFile(URI uri) {
		super(uri);
	}

	@Override
	public boolean canExecute() {
		return false;
	}

	@Override
	public boolean canRead() {
		// TODO Auto-generated method stub
		return super.canRead();
	}

	@Override
	public boolean canWrite() {
		// TODO Auto-generated method stub
		return super.canWrite();
	}

	@Override
	public int compareTo(File pathname) {
		// TODO Auto-generated method stub
		return super.compareTo(pathname);
	}

	@Override
	public boolean createNewFile() throws IOException {
		// TODO Auto-generated method stub
		return super.createNewFile();
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return super.delete();
	}

	@Override
	public void deleteOnExit() {
		// TODO Auto-generated method stub
		super.deleteOnExit();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return super.exists();
	}

	@Override
	public File getAbsoluteFile() {
		// TODO Auto-generated method stub
		return super.getAbsoluteFile();
	}

	@Override
	public String getAbsolutePath() {
		// TODO Auto-generated method stub
		return super.getAbsolutePath();
	}

	@Override
	public File getCanonicalFile() throws IOException {
		// TODO Auto-generated method stub
		return super.getCanonicalFile();
	}

	@Override
	public String getCanonicalPath() throws IOException {
		// TODO Auto-generated method stub
		return super.getCanonicalPath();
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
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public String getParent() {
		// TODO Auto-generated method stub
		return super.getParent();
	}

	@Override
	public File getParentFile() {
		// TODO Auto-generated method stub
		return super.getParentFile();
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return super.getPath();
	}

	private void getResources(String pathname, FileShareUserManager userManager) {
		webdavPath = loader.getOwnCloudAddress() + pathname;
		initResources(pathname, userManager);

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

	private void initResources(String pathname, FileShareUserManager userManager) {
		try {
			resources = sardine.list(webdavPath);
		} catch (com.github.sardine.impl.SardineException e) {
			LOG.debug(e.getMessage());
			LOG.warn("could not authenticate for user"
					+ userManager.getDefaultUserName() + ":"
					+ userManager.getDefaultPassword());
		} catch (IOException e) {
			LOG.error("could not get resources for "
					+ loader.getOwnCloudAddress() + pathname + e.getMessage());
		}
	}

	@Override
	public boolean isAbsolute() {
		// TODO Auto-generated method stub
		return super.isAbsolute();
	}

	@Override
	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return super.isDirectory();
	}

	@Override
	public boolean isFile() {
		// TODO Auto-generated method stub
		return super.isFile();
	}

	@Override
	public boolean isHidden() {
		// TODO Auto-generated method stub
		return super.isHidden();
	}

	@Override
	public long lastModified() {
		// TODO Auto-generated method stub
		return super.lastModified();
	}

	@Override
	public long length() {
		// TODO Auto-generated method stub
		return super.length();
	}

	@Override
	public String[] list() {
		// TODO Auto-generated method stub
		return super.list();
	}

	@Override
	public String[] list(FilenameFilter filter) {
		// TODO Auto-generated method stub
		return super.list(filter);
	}

	@Override
	public File[] listFiles() {
		// TODO Auto-generated method stub
		return super.listFiles();
	}

	@Override
	public File[] listFiles(FileFilter filter) {
		// TODO Auto-generated method stub
		return super.listFiles(filter);
	}

	@Override
	public File[] listFiles(FilenameFilter filter) {
		// TODO Auto-generated method stub
		return super.listFiles(filter);
	}

	@Override
	public boolean mkdir() {
		// TODO Auto-generated method stub
		return super.mkdir();
	}

	@Override
	public boolean mkdirs() {
		// TODO Auto-generated method stub
		return super.mkdirs();
	}

	@Override
	public boolean renameTo(File dest) {
		// TODO Auto-generated method stub
		return super.renameTo(dest);
	}

	@Override
	public boolean setExecutable(boolean executable) {
		// TODO Auto-generated method stub
		return super.setExecutable(executable);
	}

	@Override
	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		// TODO Auto-generated method stub
		return super.setExecutable(executable, ownerOnly);
	}

	@Override
	public boolean setLastModified(long time) {
		// TODO Auto-generated method stub
		return super.setLastModified(time);
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

}
