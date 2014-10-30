package org.up.liferay.owncloud;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

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
	
	private static final Logger log = LoggerFactory.getLogger(WebdavObjectStore.class.getName());

	public WebdavObjectStore(String repositoryId) {
		super(repositoryId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ChildrenResult getFolderChildren(Folder folder, int maxItems,
			int skipCount, String user) {
		return getChildren(folder, maxItems, skipCount, user, false);
	}

	@Override
	public ChildrenResult getChildren(Folder folder, int maxItems,
			int skipCount, String user, boolean usePwc) {
		String path = folder.getName();
		path = hackRootId(path);

		// resultSet contains folders and files
		List<Fileable> folderChildren = new ArrayList<Fileable>();
		// get Endpoint
		WebdavEndpoint endpoint = getSardineEndpoint();		
		// converts webdav result to CMIS type of files
		try {
			List<DavResource> resources = getResourcesForUrl(path, endpoint);
			String endpointPath= endpoint.getEndpointPath();			
			Iterator<DavResource> it = resources.iterator();
			
			while (it.hasNext()) {
				DavResource davResource = it.next();
				String encodedPath = encodePath(endpointPath, davResource);
				
				if (davResource.isDirectory()) {
					//TODO: folder.getId() ??
					FolderImpl folderResult = new FolderImpl(encodedPath, folder.getId());
					folderResult.setTypeId("cmis:folder");
					folderChildren.add(folderResult);
				}
				else {
					DocumentImpl doc= new DocumentImpl();					
					fillDocumentWithMetadata(davResource, encodedPath, doc);
					folderChildren.add(doc);					
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block	
			if (endpoint.isValidCredentialinDebug()) {				
				log.error("problems with webdav authentication at owncloud", e);
			}
			else {
				log.debug("the user credentials are not valid");
			}
			
			return new ChildrenResult(folderChildren, 0);
		}

		ChildrenResult result = sortChildrenResult(maxItems, skipCount,
				folderChildren);
		
		return result;
	}

	private String encodePath(String endpointPath, DavResource davResource) {
		String itemPath= davResource.getPath();
		itemPath= itemPath.replace(endpointPath, "");
		String encodedPath = URLEncoder.encode(itemPath);
		return encodedPath;
	}

	private void fillDocumentWithMetadata(DavResource davResource,
			String itemPath, DocumentImpl doc) {
//		GregorianCalendar cal= new GregorianCalendar();
//		cal.setTime(davResource.getCreation());
		// TODO find out why null!!
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		doc.setCreatedAt(cal);
		cal.setTime(davResource.getModified());
		doc.setModifiedAt(cal);
		doc.setName(davResource.getName());
		doc.setId(itemPath);
		doc.setTypeId("cmis:baseTypeId");
		
		//doc.set
	}

	private String hackRootId(String path) {
		if (path.equals("RootFolder")) {
			path = "/";
		}
		return path;
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

	private List<DavResource> getResourcesForUrl(String path,
			WebdavEndpoint endpoint) throws IOException {
		String listedPath = endpoint.getEndpoint() + path;		
		log.debug("showing directory for: " + listedPath);
		List<DavResource> resources = endpoint.getSardine().list(listedPath);
		return resources;
	}

	private WebdavEndpoint getSardineEndpoint() {
		// creates Sardine Endpoint
		CallContext callContext = InMemoryServiceContext.getCallContext();
		WebdavEndpoint endpoint = new WebdavEndpoint(callContext);
		return endpoint;
	}
	
	@Override
	public StoredObject getObjectById(String objectId) {
		// TODO implement this with document metadata ( or real data)
		return super.getObjectById(objectId);
	}

}
