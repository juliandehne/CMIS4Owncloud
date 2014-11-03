package org.up.liferay.owncloud;

import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.inmemory.storedobj.impl.DocumentImpl;

import com.github.sardine.DavResource;

public class WebdavDocumentImpl extends DocumentImpl {
	public WebdavDocumentImpl(DavResource davResource) {
		// GregorianCalendar cal= new GregorianCalendar();
		// cal.setTime(davResource.getCreation());
		// TODO find out why null!!
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);
		cal.setTime(davResource.getModified());
		this.setModifiedAt(cal);
		String id = StringConverter.webdavToIdEncoded(davResource);
		String name = StringConverter.encodedIdToName(id);
		// String parentID =
		// StringConverter.decodedIdToParent(StringConverter.decode(id));
		this.setName(name);
		this.setId(id);
		this.setTypeId("cmis:document");
	}

	public WebdavDocumentImpl(String encodedId) {
		// GregorianCalendar cal= new GregorianCalendar();
		// cal.setTime(davResource.getCreation());
		// TODO find out why null!!
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);
		this.setModifiedAt(cal);
		String name = StringConverter.encodedIdToName(encodedId);
		// String parentID =
		// StringConverter.decodedIdToParent(StringConverter.decode(id));
		this.setName(name);
		this.setId(encodedId);
		this.setTypeId("cmis:document");
	}
}
