package org.up.ple.dcim4owncloud;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.fileshare.FileShareCmisServiceFactory;
import org.apache.chemistry.opencmis.fileshare.FileShareUserManager;

public class OwnCloudServiceFactory  extends FileShareCmisServiceFactory {

	@Override
	public CmisService getService(CallContext context) {
		authenticateUser(context);
		OwnCloudCmisServiceImpl cmisServiceImpl = new OwnCloudCmisServiceImpl(context);
		return cmisServiceImpl;
	}

	/**
	 * I hope this solves the authentication by
	 * simply passing the credentials to the UserManager
	 * (fingers crossed)
	 * @param context
	 */
	private void authenticateUser(CallContext context) {
		super.getUserManager().addLogin(context.getUsername(), context.getPassword());
	}
	

}
