package org.up.liferay.webdav;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryServiceContext;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Fileable;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Folder;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.DocumentImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.FolderImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.ObjectStoreImpl;
import org.apache.commons.io.IOUtils;
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
							davResource, endpoint);
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
	
	
	public StoredObject getObjectById(String objectNameDecoded, String parentNameDecoded) {
		return getObjectById(WebdavIdDecoderAndEncoder.encode(parentNameDecoded) + WebdavIdDecoderAndEncoder.encode(objectNameDecoded));
	}

	/**
	 * we assume that objectId is the URLEncoded path after the
	 * owncloud-server-path or 100 for root
	 */
	@Override
	public StoredObject getObjectById(String objectId) {
		getOrRefreshSardineEndpoint();
		if (objectId == null || objectId.equals(WebdavIdDecoderAndEncoder.LIFERAYROOTID)) {
			// objectId = "/" ??
			FolderImpl result = new FolderImpl("RootFolder", null);
			result.setName("RootFolder");
			result.setRepositoryId("A1");
			result.setTypeId("cmis:folder");
			result.setId(WebdavIdDecoderAndEncoder.LIFERAYROOTID);
			return result;
		} else {
			try {
				String decodedPath = WebdavIdDecoderAndEncoder
						.decode(objectId);
//				if (!decodedPath.startsWith("/")) {
//					return null;
//				}
				// entweder ist es ein folder oder ein document
				if (decodedPath.endsWith("/")) {			
					WebdavFolderImpl result = new WebdavFolderImpl(objectId);
					return result;
				} else {					
					WebdavDocumentImpl result = new WebdavDocumentImpl(objectId, endpoint);
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
		long before = System.currentTimeMillis();
		log.debug("showing resources for: " + listedPath);
		List<DavResource> resources = endpoint.getSardine().list(listedPath);
		// the first element is always the directory itself
		if (resources.get(0).isDirectory() && !getDirectory) {
			resources.remove(0);
		}
		long now = System.currentTimeMillis();
		log.warn("getting resource listing took: " + (now-before));
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


	
	public String createFolder(String folderName, String parentIdEncoded) {
		getOrRefreshSardineEndpoint();
		
		String parentIdDecoded = WebdavIdDecoderAndEncoder.decode(parentIdEncoded);		
		String path = parentIdDecoded + folderName;
		try {
			endpoint.getSardine().createDirectory(endpoint.getEndpoint()+path);
		} catch (IOException e) {			
			e.printStackTrace();
		}				
		return WebdavIdDecoderAndEncoder.encode(path);
	}


	public Boolean exists(String parentNameDecoded, String folderName) {
		getOrRefreshSardineEndpoint(); //should be in constructor but is not called!!
		
		String path = parentNameDecoded + folderName;
		try {
			return endpoint.getSardine().exists(endpoint.getEndpoint() + path);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return false;					
	}


	public String createFile(String documentNameDecoded, String parentIdEncoded, ContentStream contentStream) {
		getOrRefreshSardineEndpoint(); //should be in constructor but is not called!!
		
		ByteArrayInputStream buffer = null;
		try {
			InputStream inputStream = contentStream.getStream();
			buffer = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
		} catch (IOException e1) {		
			e1.printStackTrace();
		}
		
		String parentIdDecoded = WebdavIdDecoderAndEncoder.decode(parentIdEncoded);		
		String path = (parentIdDecoded+documentNameDecoded);
		String completePath= endpoint.getEndpoint()+path;
		try {
			if (endpoint.getSardine().exists(completePath)) {
				endpoint.getSardine().delete(completePath);
			} else { 
				endpoint.getSardine().put(completePath, (InputStream) buffer, contentStream.getMimeType(), false, contentStream.getLength());
			}
		} catch (IOException e) {		
			e.printStackTrace();
		}
			
		return WebdavIdDecoderAndEncoder.encode(path);
	}
	
}
