package org.up.liferay.webdav;

import java.util.List;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryService;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoreManager;
import org.apache.chemistry.opencmis.inmemory.types.DocumentTypeCreationHelper;
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
	public FailedToDeleteData deleteTree(String repositoryId, String folderId,
			Boolean allVersions, UnfileObject unfileObjects,
			Boolean continueOnFailure, ExtensionsData extension) {
		// TODO Auto-generated method stub
		
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

}
