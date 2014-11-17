package org.up.liferay.webdav;

public class WebdavResourceKey {
	private String encodedId;
	private Boolean getDirectory;
	
	public WebdavResourceKey(String encodedId, Boolean getDirectory) {		
		this.encodedId = encodedId;
		this.getDirectory = getDirectory;
	}
	
	public String getEncodedId() {
		return encodedId;
	}
	public void setEncodedId(String encodedId) {
		this.encodedId = encodedId;
	}
	public Boolean getGetDirectory() {
		return getDirectory;
	}
	public void setGetDirectory(Boolean getDirectory) {
		this.getDirectory = getDirectory;
	}
	
	@Override
	public boolean equals(Object obj) {
		WebdavResourceKey toCompare = (WebdavResourceKey) obj;
		return toCompare.getGetDirectory().equals(this.getDirectory) && toCompare.encodedId.equals(this.encodedId);
	}
	
	@Override
	public int hashCode() {
		return (this.encodedId+this.getDirectory.toString()).hashCode();
	}
	
	
	
}
