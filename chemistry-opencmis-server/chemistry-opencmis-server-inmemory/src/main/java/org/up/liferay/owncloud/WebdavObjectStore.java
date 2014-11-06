package org.up.liferay.owncloud;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryServiceContext;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Fileable;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Folder;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.DocumentImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.FolderImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.ObjectStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sardine.DavResource;

public class WebdavObjectStore extends ObjectStoreImpl {

	private static final Logger log = LoggerFactory
			.getLogger(WebdavObjectStore.class.getName());
	private WebdavEndpoint endpoint;

	public WebdavObjectStore(String repositoryId) {
		super(repositoryId);
		//getOrRefreshSardineEndpoint();
	}

	@Override
	public String getFolderPath(String folderId) {
		return WebdavIdDecoderAndEncoder.decode(folderId);
	}

	@Override
	public ChildrenResult getChildren(Folder folder, int maxItems,
			int skipCount, String user, boolean usePwc) {
		// singletonpattern methods are called independent of constructor
		getOrRefreshSardineEndpoint();

		// hack root folder
		String name = folder.getName();
		//String path = folder.getPathSegment();
		String path = WebdavIdDecoderAndEncoder.decode(folder.getId());
		if (name.equals("RootFolder")) {
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
							davResource);
					folderChildren.add(documentImpl);
				}
			}

		} catch (IOException e) {
			handleStartUpErrors(e);
			return new ChildrenResult(folderChildren, 0);
		}
		ChildrenResult result = sortChildrenResult(maxItems, skipCount,
				folderChildren);
		return result;
	}

	private void handleStartUpErrors(IOException e) {
		if (endpoint.isValidCredentialinDebug()) {			
			log.error("problems with webdav authentication at owncloud", e);
		} else {
			log.debug("the user credentials are not valid");
		}
	}

	@Override
	public ChildrenResult getFolderChildren(Folder folder, int maxItems,
			int skipCount, String user) {
		return getChildren(folder, maxItems, skipCount, user, false);
	}

	/**
	 * we assume that objectId is the URLEncoded path after the
	 * owncloud-server-path or 100 for root
	 */
	@Override
	public StoredObject getObjectById(String objectId) {
		getOrRefreshSardineEndpoint();
		if (objectId == null || objectId.equals("100")) {
			// objectId = "/" ??
			FolderImpl result = new FolderImpl("RootFolder", null);
			result.setName("RootFolder");
			result.setRepositoryId("A1");
			result.setTypeId("cmis:folder");
			result.setId("100");
			return result;
		} else {
			try {
				String decodedPath = WebdavIdDecoderAndEncoder
						.decode(objectId);
				if (decodedPath.endsWith("/")) {
					// DavResource davresource = getResourcesForID(objectId,
					// true).get(0); // we expect exactly one resource
					// WebdavFolderImpl result = new
					// WebdavFolderImpl(davresource);
					WebdavFolderImpl result = new WebdavFolderImpl(objectId);
					return result;
				} else {
					// DavResource davresource = getResourcesForID(objectId,
					// false).get(0); // we expect exactly one resource
					// WebdavDocumentImpl result = new
					// WebdavDocumentImpl(davresource);
					WebdavDocumentImpl result = new WebdavDocumentImpl(objectId);
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

	private List<DavResource> getResourcesForID(String encodedId,
			Boolean getDirectory) throws IOException {
		String listedPath = WebdavIdDecoderAndEncoder
				.encodedIdToWebdav(encodedId);
		log.debug("showing resources for: " + listedPath);
		List<DavResource> resources = endpoint.getSardine().list(listedPath);
		// the first element is always the directory itself
		if (resources.get(0).isDirectory() && !getDirectory) {
			resources.remove(0);
		}
		return resources;
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

}
