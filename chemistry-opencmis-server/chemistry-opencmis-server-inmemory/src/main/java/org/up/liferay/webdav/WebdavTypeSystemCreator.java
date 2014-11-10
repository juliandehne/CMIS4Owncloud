package org.up.liferay.webdav;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.inmemory.types.DefaultTypeSystemCreator;

public class WebdavTypeSystemCreator extends DefaultTypeSystemCreator {
	
	@Override
	public List<TypeDefinition> createTypesList() {
		return new ArrayList<TypeDefinition>();
	}

}
