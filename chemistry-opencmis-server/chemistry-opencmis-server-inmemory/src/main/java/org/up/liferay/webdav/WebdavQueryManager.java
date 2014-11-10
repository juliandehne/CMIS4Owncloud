package org.up.liferay.webdav;

import java.util.Map;

import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.inmemory.TypeManagerImpl;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.chemistry.opencmis.server.support.query.PredicateWalker;
import org.apache.chemistry.opencmis.server.support.query.QueryUtilStrict;

public class WebdavQueryManager {

	private CallContext context;
	private String statement;

	public WebdavQueryManager(CallContext context, String statement) {
		this.context = context;
		this.statement = statement;
	}

	public ObjectListImpl computeObjectList() {

		// CmisQueryWalker walker = QueryUtil.getWalker(statement);
		WebdavQueryWalker pw = new WebdavQueryWalker(null, null, statement);				
		
		TypeManager tm = new TypeManagerImpl();
		QueryUtilStrict strict = new QueryUtilStrict(statement, tm, pw);		
//		String[] walker = strict.getWalker().getTokenNames();
		Map<String, String> types = strict.getQueryObject().getTypes();
		// great
		return null;
	}

}
