package org.up.liferay.webdav;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;

import com.github.sardine.DavResource;
import com.google.common.cache.CacheLoader;

public class WebdavCacheLoader extends CacheLoader<WebdavResourceKey, List<DavResource>> implements Callable<List<DavResource>>{
	
	private WebdavObjectStore objectStore;
	private WebdavResourceKey key;

	public WebdavCacheLoader(WebdavObjectStore objectStore, WebdavResourceKey key) {
		this.objectStore = objectStore;
		this.key = key;
	}


	@Override
	public List<DavResource> load(WebdavResourceKey key) throws Exception {
		return objectStore.getResourcesForIDintern(key.getEncodedId(), key.getGetDirectory());
	}


	@Override
	public List<DavResource> call() throws Exception {
		return objectStore.getResourcesForIDintern(key.getEncodedId(), key.getGetDirectory());	
	}

}
