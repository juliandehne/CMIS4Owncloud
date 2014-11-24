package org.up.liferay.owncloud;

import java.util.HashSet;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoreManager;
import org.up.liferay.webdav.InMemoryServiceContext;
import org.up.liferay.webdav.WebdavService;

public class OwncloudService extends WebdavService {

	public OwncloudService(StoreManager sm) {
		super(sm);		
	}
	
	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl aces,
			AclPropagation aclPropagation) {
		Acl result = super.applyAcl(repositoryId, objectId, aces, aclPropagation);
		// TODO create Share
		Set<String> users = new HashSet<String>();
		for (Ace ace : aces.getAces()) {
			users.add(ace.getPrincipalId());
		}
		OwncloudShareCreator creator = new OwncloudShareCreator();
		creator.createShare(users, InMemoryServiceContext.getCallContext().getUsername(), InMemoryServiceContext.getCallContext().getPassword(), objectId);
		return result;				
	}
	
	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl addAces,
			Acl removeAces, AclPropagation aclPropagation,
			ExtensionsData extension) {
		// TODO Auto-generated method stub
		return super.applyAcl(repositoryId, objectId, addAces, removeAces,
				aclPropagation, extension);
	}
	
	@Override
	public void applyPolicy(String repositoryId, String policyId,
			String objectId, ExtensionsData extension) {
		// TODO Auto-generated method stub
		super.applyPolicy(repositoryId, policyId, objectId, extension);
	}

}
