package org.up.ple.dcim4owncloud;

import java.math.BigInteger;
import java.util.List;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.spi.Holder;

public class CmisServiceImpl implements CmisService {

	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public RepositoryInfo getRepositoryInfo(String repositoryId,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeDefinitionList getTypeChildren(String repositoryId,
			String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TypeDefinitionContainer> getTypeDescendants(
			String repositoryId, String typeId, BigInteger depth,
			Boolean includePropertyDefinitions, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeDefinition getTypeDefinition(String repositoryId, String typeId,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeDefinition createType(String repositoryId, TypeDefinition type,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeDefinition updateType(String repositoryId, TypeDefinition type,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteType(String repositoryId, String typeId,
			ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public ObjectInFolderList getChildren(String repositoryId, String folderId,
			String filter, String orderBy, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems,
			BigInteger skipCount, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ObjectInFolderContainer> getDescendants(String repositoryId,
			String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ObjectInFolderContainer> getFolderTree(String repositoryId,
			String folderId, BigInteger depth, String filter,
			Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ObjectParentData> getObjectParents(String repositoryId,
			String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectData getFolderParent(String repositoryId, String folderId,
			String filter, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectList getCheckedOutDocs(String repositoryId, String folderId,
			String filter, String orderBy, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createDocument(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createDocumentFromSource(String repositoryId,
			String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createFolder(String repositoryId, Properties properties,
			String folderId, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createRelationship(String repositoryId,
			Properties properties, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createPolicy(String repositoryId, Properties properties,
			String folderId, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createItem(String repositoryId, Properties properties,
			String folderId, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public AllowableActions getAllowableActions(String repositoryId,
			String objectId, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectData getObject(String repositoryId, String objectId,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getProperties(String repositoryId, String objectId,
			String filter, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RenditionData> getRenditions(String repositoryId,
			String objectId, String renditionFilter, BigInteger maxItems,
			BigInteger skipCount, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectData getObjectByPath(String repositoryId, String path,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentStream getContentStream(String repositoryId, String objectId,
			String streamId, BigInteger offset, BigInteger length,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateProperties(String repositoryId, Holder<String> objectId,
			Holder<String> changeToken, Properties properties,
			ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(
			String repositoryId,
			List<BulkUpdateObjectIdAndChangeToken> objectIdsAndChangeTokens,
			Properties properties, List<String> addSecondaryTypeIds,
			List<String> removeSecondaryTypeIds, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void moveObject(String repositoryId, Holder<String> objectId,
			String targetFolderId, String sourceFolderId,
			ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void deleteObject(String repositoryId, String objectId,
			Boolean allVersions, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public FailedToDeleteData deleteTree(String repositoryId, String folderId,
			Boolean allVersions, UnfileObject unfileObjects,
			Boolean continueOnFailure, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContentStream(String repositoryId, Holder<String> objectId,
			Boolean overwriteFlag, Holder<String> changeToken,
			ContentStream contentStream, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void deleteContentStream(String repositoryId,
			Holder<String> objectId, Holder<String> changeToken,
			ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void appendContentStream(String repositoryId,
			Holder<String> objectId, Holder<String> changeToken,
			ContentStream contentStream, boolean isLastChunk,
			ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void checkOut(String repositoryId, Holder<String> objectId,
			ExtensionsData extension, Holder<Boolean> contentCopied) {
		// TODO Auto-generated method stub

	}

	public void cancelCheckOut(String repositoryId, String objectId,
			ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void checkIn(String repositoryId, Holder<String> objectId,
			Boolean major, Properties properties, ContentStream contentStream,
			String checkinComment, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public ObjectData getObjectOfLatestVersion(String repositoryId,
			String objectId, String versionSeriesId, Boolean major,
			String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePolicyIds, Boolean includeAcl,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getPropertiesOfLatestVersion(String repositoryId,
			String objectId, String versionSeriesId, Boolean major,
			String filter, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ObjectData> getAllVersions(String repositoryId,
			String objectId, String versionSeriesId, String filter,
			Boolean includeAllowableActions, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectList query(String repositoryId, String statement,
			Boolean searchAllVersions, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectList getContentChanges(String repositoryId,
			Holder<String> changeLogToken, Boolean includeProperties,
			String filter, Boolean includePolicyIds, Boolean includeAcl,
			BigInteger maxItems, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addObjectToFolder(String repositoryId, String objectId,
			String folderId, Boolean allVersions, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void removeObjectFromFolder(String repositoryId, String objectId,
			String folderId, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public ObjectList getObjectRelationships(String repositoryId,
			String objectId, Boolean includeSubRelationshipTypes,
			RelationshipDirection relationshipDirection, String typeId,
			String filter, Boolean includeAllowableActions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public Acl getAcl(String repositoryId, String objectId,
			Boolean onlyBasicPermissions, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public Acl applyAcl(String repositoryId, String objectId, Acl addAces,
			Acl removeAces, AclPropagation aclPropagation,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void applyPolicy(String repositoryId, String policyId,
			String objectId, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public void removePolicy(String repositoryId, String policyId,
			String objectId, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public List<ObjectData> getAppliedPolicies(String repositoryId,
			String objectId, String filter, ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public String create(String repositoryId, Properties properties,
			String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteObjectOrCancelCheckOut(String repositoryId,
			String objectId, Boolean allVersions, ExtensionsData extension) {
		// TODO Auto-generated method stub

	}

	public Acl applyAcl(String repositoryId, String objectId, Acl aces,
			AclPropagation aclPropagation) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

}
