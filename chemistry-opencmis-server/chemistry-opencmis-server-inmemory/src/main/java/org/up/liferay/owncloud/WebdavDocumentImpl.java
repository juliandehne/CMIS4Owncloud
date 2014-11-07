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
		String id = WebdavIdDecoderAndEncoder.webdavToIdEncoded(davResource);
		String name = WebdavIdDecoderAndEncoder.encodedIdToName(id);
		setNameDefaults(id, name);
	}

	public WebdavDocumentImpl(String encodedId) {
		// GregorianCalendar cal= new GregorianCalendar();
		// cal.setTime(davResource.getCreation());
		// TODO find out why null!!
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);
		this.setModifiedAt(cal);
		String name = WebdavIdDecoderAndEncoder.encodedIdToName(encodedId);		
		setNameDefaults(encodedId, name);
	}

	private void setNameDefaults(String encodedId, String name) {		
		this.setName(name);
		this.setId(encodedId);
		this.setTypeId("cmis:document");
		String parentID =	WebdavIdDecoderAndEncoder.decodedIdToParent(WebdavIdDecoderAndEncoder.decode(encodedId));
		this.addParentId(parentID);
	}
}
