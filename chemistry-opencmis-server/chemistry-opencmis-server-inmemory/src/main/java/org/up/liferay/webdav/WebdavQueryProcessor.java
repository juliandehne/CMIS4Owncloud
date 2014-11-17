package org.up.liferay.webdav;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.inmemory.query.InMemoryQueryProcessor;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.ObjectStore;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.ObjectStoreImpl;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.chemistry.opencmis.server.support.query.CmisSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdavQueryProcessor extends InMemoryQueryProcessor {

	private static final Logger LOG = LoggerFactory
			.getLogger(WebdavQueryProcessor.class);

	public WebdavQueryProcessor(ObjectStoreImpl objStore) {
		super(objStore);
	}

	@Override
	public ObjectList query(TypeManager tm, ObjectStore objectStore,
			String user, String repositoryId, String statement,
			Boolean searchAllVersions, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount) {
		processQueryAndCatchExc(statement, tm); // calls query processor

		
		String selectorName = super.queryObj.getSelectReferences().iterator().next().getName();
		if (selectorName.equals("cmis:objectId")){
			ObjectListImpl result = new ObjectListImpl();
			List<ObjectData> data = new ArrayList<ObjectData>();			
			result.setObjects(data);
			return result;
		}
		
		
		String typeOfQuery = super.queryObj.getMainTypeAlias();
		Boolean isFolder = !typeOfQuery.equals("cmis:document");
		
		String folderName = super.whereTree.getChild(0).getChild(1).toString().replaceAll("'", "");				
		String parentNameEncoded = super.whereTree.getChild(1).getChild(0)
				.toString();		
		String parentNameDecoded = WebdavIdDecoderAndEncoder
				.decode(parentNameEncoded).replaceAll("'", "");
		if (parentNameDecoded.contains(WebdavIdDecoderAndEncoder.LIFERAYROOTID)) {
			parentNameDecoded = "/";
		}
		if (isFolder) {
			folderName = "/" + folderName + "/";
			LOG.warn("checking match for folder for: " + folderName
					+ " and parent " + parentNameDecoded);
			WebdavObjectStore webdavObjectStore = ((WebdavObjectStore) objectStore);
			if (webdavObjectStore.exists(parentNameDecoded, folderName)) {
				matches.add(webdavObjectStore.getObjectById(folderName,
						parentNameDecoded));
			}
		} else {
			folderName = "/" + folderName;
			LOG.warn("checking match for document for: " + folderName
					+ " and parent " + parentNameDecoded);
			WebdavObjectStore webdavObjectStore = ((WebdavObjectStore) objectStore);
			if (webdavObjectStore.exists(parentNameDecoded, folderName)) {
				matches.add(webdavObjectStore.getObjectById(folderName,
						parentNameDecoded));
			}
		}

		ObjectList objList = buildResultList(tm, user, includeAllowableActions,
				includeRelationships, renditionFilter, maxItems, skipCount);
		LOG.debug("Query result, number of matching objects: "
				+ objList.getNumItems());
		return objList;
	}

}
