package org.up.ple.dcim4owncloud;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.fileshare.FileShareCmisServiceFactory;

public class OwnCloudServiceFactory  extends FileShareCmisServiceFactory {

	@Override
	public CmisService getService(CallContext context) {
		CmisServiceImpl cmisServiceImpl = new CmisServiceImpl();
		return cmisServiceImpl;
	}

}
