package org.up.liferay.webdav;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.inmemory.query.InMemoryQueryProcessor;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.Fileable;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.ObjectStore;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.ObjectStore.ChildrenResult;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.FolderImpl;
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

		WebdavObjectStore webdavObjectStore = ((WebdavObjectStore) objectStore);
		String selectorName = super.queryObj.getSelectReferences().iterator()
				.next().getName();
		if (selectorName.equals("cmis:objectId")) {
			String parent = super.whereTree.getChild(0).toString().replaceAll("'", "");
			ChildrenResult childrenResult = webdavObjectStore.getChildrenForName(50, 0, parent);
			for (Fileable storedObject : childrenResult.getChildren()) {
				if (storedObject instanceof WebdavFolderImpl) {
					matches.add(storedObject);
				}
			}			
//			ObjectListImpl result = new ObjectListImpl();
//			List<ObjectData> data = new ArrayList<ObjectData>();
//			result.setObjects(data);
//			return result;
		} else {
			String typeOfQuery = super.queryObj.getMainTypeAlias();
			Boolean isFolder = !typeOfQuery.equals("cmis:document");

			String folderName = super.whereTree.getChild(0).getChild(1)
					.toString().replaceAll("'", "");
			String parentNameDecoded = getParentNameInWhereTree();
			if (parentNameDecoded
					.contains(WebdavIdDecoderAndEncoder.LIFERAYROOTID)) {
				parentNameDecoded = "/";
			}
			if (isFolder) {
				folderName = "/" + folderName + "/";
				LOG.warn("checking match for folder for: " + folderName
						+ " and parent " + parentNameDecoded);
				
				if (webdavObjectStore.exists(parentNameDecoded, folderName)) {
					matches.add(webdavObjectStore.getObjectById(folderName,
							parentNameDecoded));
				}
			} else {
				folderName = "/" + folderName;
				LOG.warn("checking match for document for: " + folderName
						+ " and parent " + parentNameDecoded);				
				if (webdavObjectStore.exists(parentNameDecoded, folderName)) {
					matches.add(webdavObjectStore.getObjectById(folderName,
							parentNameDecoded));
				}
			}
		}

		ObjectList objList = buildResultList(tm, user, includeAllowableActions,
				includeRelationships, renditionFilter, maxItems, skipCount);
		LOG.debug("Query result, number of matching objects: "
				+ objList.getNumItems());
		return objList;
	}

	private String getParentNameInWhereTree() {
		String parentNameEncoded = super.whereTree.getChild(1).getChild(0)
				.toString();
		String parentNameDecoded = WebdavIdDecoderAndEncoder.decode(
				parentNameEncoded).replaceAll("'", "");
		return parentNameDecoded;
	}

}
