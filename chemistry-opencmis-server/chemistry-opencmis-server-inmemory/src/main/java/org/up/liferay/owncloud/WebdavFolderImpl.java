package org.up.liferay.owncloud;

import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.inmemory.storedobj.impl.FolderImpl;

import com.github.sardine.DavResource;

public class WebdavFolderImpl extends FolderImpl {
	public WebdavFolderImpl(DavResource davResource) {
		this.setTypeId("cmis:folder");
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);
		cal.setTime(davResource.getModified());
		this.setModifiedAt(cal);
		String id = StringConverter.webdavToIdEncoded(davResource);
		String name = StringConverter.encodedIdToName(id);
		String parentID = StringConverter.decodedIdToParent(StringConverter.decode(id));
		this.setName(name);
		this.setId(id);
		setParentId(parentID);
	}
	
	public WebdavFolderImpl(String encodedId) {
		this.setTypeId("cmis:folder");
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);		
		this.setModifiedAt(cal);		
		String name = StringConverter.encodedIdToName(encodedId);
		String parentID = StringConverter.decodedIdToParent(StringConverter.decode(encodedId));
		this.setName(name);
		this.setId(encodedId);
		setParentId(parentID);
	}


}
