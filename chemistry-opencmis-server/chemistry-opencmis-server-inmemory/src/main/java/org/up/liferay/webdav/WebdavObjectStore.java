package org.up.liferay.webdav;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.xml.namespace.QName;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisException;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisFaultType;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Fileable;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Folder;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.DocumentImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.FolderImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.ObjectStoreImpl;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctc.wstx.compat.QNameCreator;
import com.github.sardine.DavResource;

public class WebdavObjectStore extends ObjectStoreImpl {

	private static final Logger log = LoggerFactory
			.getLogger(WebdavObjectStore.class.getName());
	private WebdavEndpoint endpoint;

	public WebdavObjectStore(String repositoryId) {
		super(repositoryId);
		// getOrRefreshSardineEndpoint();
	}

	public String createFile(String documentNameDecoded,
			String parentIdEncoded, ContentStream contentStream) {
		getOrRefreshSardineEndpoint(); // should be in constructor but is not
										// called!!

		ByteArrayInputStream buffer = null;
		try {
			InputStream inputStream = contentStream.getStream();
			buffer = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String parentIdDecoded = WebdavIdDecoderAndEncoder
				.decode(parentIdEncoded);
		String path = (parentIdDecoded + documentNameDecoded);
		String completePath = endpoint.getEndpoint() + path;
		try {
			if (endpoint.getSardine().exists(completePath)) {
				endpoint.getSardine().delete(completePath);
			} else {
				endpoint.getSardine().put(completePath, (InputStream) buffer,
						contentStream.getMimeType(), false,
						contentStream.getLength());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return WebdavIdDecoderAndEncoder.encode(path);
	}

	public String createFolder(String folderName, String parentIdEncoded) {
		getOrRefreshSardineEndpoint();

		String parentIdDecoded = WebdavIdDecoderAndEncoder
				.decode(parentIdEncoded);
		String path = parentIdDecoded + folderName;
		String webdavpath = endpoint.getEndpoint() + path;
		try {
			if (!endpoint.getSardine().exists(webdavpath)) {
				endpoint.getSardine().createDirectory(webdavpath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return WebdavIdDecoderAndEncoder.encode(path);
	}

	public String createRootFolder(String folderName) {
		getOrRefreshSardineEndpoint();
		String liferayRootPath = endpoint.getEndpoint() + folderName;
		try {
			if (!endpoint.getSardine().exists(liferayRootPath)) {
				endpoint.getSardine().createDirectory(liferayRootPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return WebdavIdDecoderAndEncoder.encode(folderName);
	}

	public Boolean exists(String parentNameDecoded, String folderName) {
		getOrRefreshSardineEndpoint(); // should be in constructor but is not
										// called!!

		String path = parentNameDecoded + folderName;
		try {
			return endpoint.getSardine().exists(endpoint.getEndpoint() + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ChildrenResult getChildren(Folder folder, int maxItems,
			int skipCount, String user, boolean usePwc) {
		// singletonpattern methods are called independent of constructor
		getOrRefreshSardineEndpoint();

		// hack root folder
		String name = folder.getName();
		// String path = folder.getPathSegment();
		String path = WebdavIdDecoderAndEncoder.decode(folder.getId());
		if (name.equals("RootFolder") || name.equals("Liferay%20Home")) {
			path = "/";
		}

		// resultSet contains folders and files
		List<Fileable> folderChildren = new ArrayList<Fileable>();

		// converts webdav result to CMIS type of files
		try {
			List<DavResource> resources = getResourcesForID(path, false);
			Iterator<DavResource> it = resources.iterator();

			while (it.hasNext()) {
				DavResource davResource = it.next();
				if (davResource.isDirectory()) {
					FolderImpl folderResult = new WebdavFolderImpl(davResource);
					folderChildren.add(folderResult);
				} else {
					DocumentImpl documentImpl = new WebdavDocumentImpl(
							davResource, endpoint);
					folderChildren.add(documentImpl);
				}
			}

		} catch (IOException e) {
			handleStartUpErrors(e);
			return new ChildrenResult(folderChildren, 0);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChildrenResult result = sortChildrenResult(maxItems, skipCount,
				folderChildren);
		return result;
	}

	/**
	 * we assume that objectId is the URLEncoded path after the
	 * owncloud-server-path or 100 for root
	 */
	@Override
	public StoredObject getObjectById(String objectId) {
		getOrRefreshSardineEndpoint();
		if (objectId == null
				|| objectId.equals(WebdavIdDecoderAndEncoder.LIFERAYROOTID)) {
			// objectId = "/" ??
			FolderImpl result = new FolderImpl("RootFolder", null);
			result.setName("RootFolder");
			result.setRepositoryId("A1");
			result.setTypeId("cmis:folder");
			result.setId(WebdavIdDecoderAndEncoder.LIFERAYROOTID);
			return result;
		} else {
			try {
				String decodedPath = WebdavIdDecoderAndEncoder.decode(objectId);
				// if (!decodedPath.startsWith("/")) {
				// return null;
				// }
				// entweder ist es ein folder oder ein document
				if (decodedPath.endsWith("/")) {
					WebdavFolderImpl result = new WebdavFolderImpl(objectId);
					return result;
				} else {
					WebdavDocumentImpl result = new WebdavDocumentImpl(
							objectId, endpoint);
					return result;
				}
			} catch (Exception e) {
				log.error("error occurred whilst getting the resource for: "
						+ objectId);
				e.printStackTrace();
			}

		}
		return null;
	}

	@Override
	public ChildrenResult getFolderChildren(Folder folder, int maxItems,
			int skipCount, String user) {
		return getChildren(folder, maxItems, skipCount, user, false);
	}

	@Override
	public String getFolderPath(String folderId) {
		return WebdavIdDecoderAndEncoder.decode(folderId);
	}

	public StoredObject getObjectById(String objectNameDecoded,
			String parentNameDecoded) {
		return getObjectById(WebdavIdDecoderAndEncoder
				.encode(parentNameDecoded)
				+ WebdavIdDecoderAndEncoder.encode(objectNameDecoded));
	}

	private WebdavEndpoint getOrRefreshSardineEndpoint() {
		// creates Sardine Endpoint
		if (endpoint != null && endpoint.isUserContextSet()) {
			return endpoint;
		}
		CallContext callContext = InMemoryServiceContext.getCallContext();
		endpoint = new WebdavEndpoint(callContext);
		return endpoint;
	}

	private List<DavResource> getResourcesForID(String path,
			boolean getDirectory) throws IOException, ExecutionException {
		WebdavResourceKey key = new WebdavResourceKey(path, getDirectory,
				this.endpoint.getUser());
		List<DavResource> result = InMemoryServiceContext.CACHE.get(key,
				new WebdavCacheLoader(this, key));
		if (!key.getGetDirectory()) {
			InMemoryServiceContext.CACHE.invalidate(key);
		}
		for (DavResource davResource : result) {
			final String encodedId = WebdavIdDecoderAndEncoder
					.webdavToIdEncoded(davResource);
			final WebdavResourceKey webdavResourceKey = new WebdavResourceKey(
					encodedId, davResource.isDirectory(),
					this.endpoint.getUser());
			final WebdavObjectStore webdavObjectStore = this;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InMemoryServiceContext.CACHE.get(webdavResourceKey,
								new WebdavCacheLoader(webdavObjectStore,
										webdavResourceKey));
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
		return result;
	}

	public List<DavResource> getResourcesForIDintern(String encodedId,
			Boolean getDirectory) throws IOException {
		String listedPath = WebdavIdDecoderAndEncoder
				.encodedIdToWebdav(encodedId);
		long before = System.currentTimeMillis();
		log.debug("showing resources for: " + listedPath);

		List<DavResource> resources = endpoint.getSardine().list(listedPath);
		// the first element is always the directory itself
		if (resources.get(0).isDirectory() && !getDirectory) {
			resources.remove(0);
		}
		long now = System.currentTimeMillis();
		log.warn("getting resource listing took: " + (now - before));
		return resources;
	}

	private void handleStartUpErrors(IOException e) {
		if (endpoint.isValidCredentialinDebug()) {
			log.error("problems with webdav authentication at owncloud", e);
		} else {
			log.debug("the user credentials are not valid");
		}
	}

	public void setEndpoint(WebdavEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	private ChildrenResult sortChildrenResult(int maxItems, int skipCount,
			List<Fileable> folderChildren) {
		sortFolderList(folderChildren);
		int from = Math.min(skipCount, folderChildren.size());
		int to = Math.min(maxItems + from, folderChildren.size());
		int noItems = folderChildren.size();
		folderChildren = folderChildren.subList(from, to);
		ChildrenResult result = new ChildrenResult(folderChildren, noItems);
		return result;
	}

	public void deleteDirectory(String objectIdEncoded) {
		getOrRefreshSardineEndpoint();

		String objectIdDecoded = WebdavIdDecoderAndEncoder
				.decode(objectIdEncoded);
		try {
			String finalPath = endpoint.getEndpoint() + objectIdDecoded;
			// endpoint.getSardine().exists(finalPath);
			endpoint.getSardine().delete(finalPath);
			InMemoryServiceContext.CACHE.invalidate(new WebdavResourceKey(
					objectIdEncoded, true, this.endpoint.getUser()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteObject(String encodedObjectId, Boolean allVersions,
			String user) {
		deleteDirectory(encodedObjectId);
	}

	public void rename(String oldName, String newName) {
		getOrRefreshSardineEndpoint();

		String oldNameUrl = endpoint.getEndpoint()
				+ WebdavIdDecoderAndEncoder.decode(oldName);
		String newNameUrl = endpoint.getEndpoint() + "/" + newName;
		try {
			if (endpoint.getSardine().exists(oldNameUrl)) {
				endpoint.getSardine().move(oldNameUrl, newNameUrl);
				InMemoryServiceContext.CACHE.invalidate(new WebdavResourceKey(
						oldName, true, this.endpoint.getUser()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
