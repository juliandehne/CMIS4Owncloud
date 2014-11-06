package org.up.liferay.owncloud;

import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.inmemory.storedobj.impl.FolderImpl;

import com.github.sardine.DavResource;

public class WebdavFolderImpl extends FolderImpl {
	private String id;

	public WebdavFolderImpl(DavResource davResource) {
		this.id = WebdavIdDecoderAndEncoder.webdavToIdEncoded(davResource);
		setDefault();
		setNameProperties();
	}

	public WebdavFolderImpl(String encodedId) {
		this.id = encodedId;
		setDefault();
		setNameProperties();
	}

	private String setNameProperties() {
		String name = WebdavIdDecoderAndEncoder.encodedIdToName(id);
		String parentID = WebdavIdDecoderAndEncoder
				.decodedIdToParent(WebdavIdDecoderAndEncoder.decode(id));
		this.setName(name);
		setParentId(parentID);
		this.setId(id);		
		return parentID;
	}

	private void setDefault() {
		this.setTypeId("cmis:folder");
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);
		this.setModifiedAt(cal);
	}

}
