package org.up.ple.dcim4owncloud;

import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;

public class ServiceFactory  extends AbstractServiceFactory {

	@Override
	public CmisService getService(CallContext context) {
		CmisServiceImpl cmisServiceImpl = new CmisServiceImpl();
		return cmisServiceImpl;
	}

}
