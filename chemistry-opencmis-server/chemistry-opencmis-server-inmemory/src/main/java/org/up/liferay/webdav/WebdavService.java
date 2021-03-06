package org.up.liferay.webdav;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.jaxb.DeleteContentStream;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryService;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoreManager;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;
import org.apache.chemistry.opencmis.inmemory.types.DocumentTypeCreationHelper;
import org.apache.chemistry.opencmis.inmemory.types.PropertyCreationHelper;
import org.apache.chemistry.opencmis.server.impl.webservices.AbstractUsernameTokenAuthHandler.ObjectFactory;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdavService extends InMemoryService {

	private static final Logger LOG = LoggerFactory
			.getLogger(WebdavService.class.getName());

	public WebdavService(StoreManager sm) {
		super(sm);
	}
	
	@Override
	public void deleteObject(String repositoryId, String objectId,
			Boolean allVersions, ExtensionsData extension) {
		// TODO Auto-generated method stub
		super.deleteObject(repositoryId, objectId, allVersions, extension);
	}
	
	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId,
			String objectId, Boolean allVersions, ExtensionsData extension) {				
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		objectStore.deleteObject(objectId, allVersions, null);
		
	}
	
	@Override
	public void moveObject(String repositoryId, Holder<String> objectId,
			String targetFolderId, String sourceFolderId,
			ExtensionsData extension) {
		String objectIdEncoded = objectId.getValue();		
		String sourceFolderIdEncoded = WebdavIdDecoderAndEncoder.encode(sourceFolderId);
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		String newObjectIdEncoded = targetFolderId + objectIdEncoded.replace(sourceFolderIdEncoded, "");
		objectStore.rename(objectIdEncoded, newObjectIdEncoded);		
//		super.moveObject(repositoryId, objectId, targetFolderId, sourceFolderId,
//				extension);
	}
		
	
	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId,
			Boolean allVersions, UnfileObject unfileObjects,
			Boolean continueOnFailure, ExtensionsData extension) {		
		
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		objectStore.deleteDirectory(folderId);
		
		FailedToDeleteData result = new FailedToDeleteData() {
			
			@Override
			public void setExtensions(List<CmisExtensionElement> extensions) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public List<CmisExtensionElement> getExtensions() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> getIds() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return result;
	}
	
	@Override
	public void deleteContentStream(String repositoryId,
			Holder<String> objectId, Holder<String> changeToken,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		super.deleteContentStream(repositoryId, objectId, changeToken, extension);
	}

	@Override
	public String create(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			ExtensionsData extension) {

		@SuppressWarnings("unchecked")
		PropertyData<String> pd = (PropertyData<String>) properties
				.getProperties().get(PropertyIds.OBJECT_TYPE_ID);
		String typeId = pd == null ? null : pd.getFirstValue();
		if (null == typeId) {
			throw new CmisInvalidArgumentException(
					"Cannot create object, without a type (no property with id CMIS_OBJECT_TYPE_ID).");
		}

		TypeDefinitionContainer typeDefC = super.getStoreManager().getTypeById(
				repositoryId, typeId);
		if (typeDefC == null) {
			throw new CmisInvalidArgumentException(
					"Cannot create object, a type with id " + typeId
							+ " is unknown");
		}

		BaseTypeId typeBaseId = typeDefC.getTypeDefinition().getBaseTypeId();

		if (typeBaseId.equals(DocumentTypeCreationHelper.getCmisDocumentType()
				.getBaseTypeId())) {
			return createDocument(repositoryId, properties, folderId,
					contentStream, versioningState, policies, null, null,
					extension);
		} else if (typeBaseId.equals(DocumentTypeCreationHelper
				.getCmisFolderType().getBaseTypeId())) {
			return createFolder(repositoryId, properties, folderId, policies,
					null, null, extension);
		} else if (typeBaseId.equals(DocumentTypeCreationHelper
				.getCmisPolicyType().getBaseTypeId())) {
			throw new NotImplementedException(this.getClass());
		} else if (typeBaseId.equals(DocumentTypeCreationHelper
				.getCmisRelationshipType().getBaseTypeId())) {
			throw new NotImplementedException(this.getClass());
		} else if (typeBaseId.equals(DocumentTypeCreationHelper
				.getCmisItemType().getBaseTypeId())) {
			throw new NotImplementedException(this.getClass());
		} else {
			LOG.error("The type contains an unknown base object id, object can't be created");
		}

		throw new NotImplementedException(this.getClass());
	}

	@Override
	public String createFolder(String repositoryId, Properties properties,
			String folderId, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		String folderName = extractName(properties);
		String parentIdEncoded = folderId;
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		String folderNameDecoded = "/" + folderName + "/";
		return objectStore.createFolder(folderNameDecoded, parentIdEncoded);
	}

	private String extractName(Properties properties) {
		PropertyData<?> pd = properties.getProperties().get(PropertyIds.NAME);
		String folderName = (String) pd.getFirstValue();
		return folderName;
	}

	@Override
	public String createDocument(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		String documentName = extractName(properties);
		String parentIdEncoded = folderId;
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		//String documentNameDecoded = "/" + documentName;
		return objectStore.createFile(documentName, parentIdEncoded, contentStream);
	}
	
	@Override
	public ContentStream getContentStream(String repositoryId, String objectId,
			String streamId, BigInteger offset, BigInteger length,
			ExtensionsData extension) {		
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		WebdavDocumentImpl resultObject = (WebdavDocumentImpl) objectStore.getObjectById(objectId);
		return resultObject.getContent();
	}
	
	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId,
			Holder<String> changeToken, Properties properties,
			ExtensionsData extension) {
		String newName = extractName(properties);
		String oldName = objectId.getValue();
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		objectStore.rename(oldName, newName);
		
	}
	
	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl aces,
			AclPropagation aclPropagation) {
		
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		String siteFoldeName = WebdavIdDecoderAndEncoder.encodedIdToName(WebdavIdDecoderAndEncoder.encode(objectId));
		String liferayRootName = WebdavIdDecoderAndEncoder.decodedIdToParent(objectId);
		objectStore.createRootFolder(liferayRootName);
		objectStore.createFolder(siteFoldeName, liferayRootName);		
		
		return aces;
	}
	
//	@Override
//	public Acl applyAcl(String repositoryId, String objectId, Acl addAces,
//			Acl removeAces, AclPropagation aclPropagation,
//			ExtensionsData extension) {
//		// TODO Auto-generated method stub
//		return super.applyAcl(repositoryId, objectId, addAces, removeAces,
//				aclPropagation, extension);
//	}
//	
//	@Override
//	public void applyPolicy(String repositoryId, String policyId,
//			String objectId, ExtensionsData extension) {
//		// TODO Auto-generated method stub
//		super.applyPolicy(repositoryId, policyId, objectId, extension);
//	}
	
	@Override
	public ObjectData getObjectByPath(String repositoryId, String path,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		
		WebdavObjectStore objectStore = new WebdavObjectStore(repositoryId);
		
		
		StoredObject resultObject =  objectStore.getObjectById(WebdavIdDecoderAndEncoder.encode(path));
		
		TypeManager tm = super.getStoreManager().getTypeManager(repositoryId);
		List<String> requestedIds = Collections.singletonList(resultObject.getId());
		Properties props = PropertyCreationHelper.getPropertiesFromObject(resultObject, objectStore, tm, requestedIds, false);
		
		ObjectDataImpl dataImpl = new ObjectDataImpl();
		dataImpl.setProperties(props);
		
		return dataImpl;
//		return super.getObjectByPath(repositoryId, path, filter,
//				includeAllowableActions, includeRelationships, renditionFilter,
//				includePolicyIds, includeAcl, extension);
	}

}
