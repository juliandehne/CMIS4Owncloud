package org.up.liferay.webdav;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.inmemory.storedobj.impl.DocumentImpl;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sardine.DavResource;

public class WebdavDocumentImpl extends DocumentImpl {
	private String decodedId;
	private WebdavObjectStore objectStore;
	

	private static final Logger log = LoggerFactory
			.getLogger(WebdavDocumentImpl.class.getName());	

	public WebdavDocumentImpl(DavResource davResource, WebdavEndpoint endpoint, WebdavObjectStore objectStore) {			
		this.objectStore = objectStore;
		String id = WebdavIdDecoderAndEncoder.webdavToIdEncoded(davResource);			
		setDefaults(id);
		setWebdavContentDefaults();		
		setDebugProperties();			
		setWebdavContentProperties(davResource);		
		
	}

	public WebdavDocumentImpl(String encodedId,  WebdavEndpoint endpoint, WebdavObjectStore objectStore) {			
		this.objectStore = objectStore;
		setDefaults(encodedId);
		setWebdavContentDefaults();		
		setDebugProperties();			
	}

	private void setDebugProperties() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		this.setCreatedAt(cal);
		this.setModifiedAt(cal);
	}

	private void setDefaults(String encodedId) {
		this.setContent(new ContentStreamImpl());
		this.setId(encodedId);
		this.setTypeId("cmis:document");
		this.decodedId = WebdavIdDecoderAndEncoder.decode(encodedId);
		String name = WebdavIdDecoderAndEncoder.encodedIdToName(encodedId);
		this.setName(name);
		String parentID =	WebdavIdDecoderAndEncoder.decodedIdToParent(decodedId);		
		this.addParentId(parentID);									
	}

	private void setWebdavContentDefaults() {
		ContentStreamImpl steam = (ContentStreamImpl) getContent();		
		steam.setFileName(this.getName());			
		steam.setLength(new BigInteger("-1"));				
		steam.setMimeType("application/octet-stream");	
		setInputStream(steam);
		this.setContent(steam);
	}
	
	public void setWebdavContentProperties(DavResource davResource) {		
		String mimeTyp = davResource.getContentType();
		Long fileLength = davResource.getContentLength();
		ContentStreamImpl steam = (ContentStreamImpl) getContent();
		steam.setFileName(this.getName());
		steam.setLength(BigInteger.valueOf(fileLength));		
		steam.setMimeType(mimeTyp);				
		setInputStream(steam);	
		setContent(steam);
	}

	private void setInputStream(ContentStreamImpl steam) {
		try {			
			WebdavEndpoint endpoint = objectStore.getOrRefreshSardineEndpoint();
			InputStream webdavBytes = endpoint.getSardine().get(endpoint.getEndpoint()+decodedId);
			ByteArrayInputStream tmpFile = new ByteArrayInputStream(IOUtils.toByteArray(webdavBytes));					
			steam.setStream(IOUtils.toBufferedInputStream(tmpFile));			
		} catch (IOException e) {	
			log.error("could not get inputstream for: " + this.decodedId);
			e.printStackTrace();
		}
	}
				
}
