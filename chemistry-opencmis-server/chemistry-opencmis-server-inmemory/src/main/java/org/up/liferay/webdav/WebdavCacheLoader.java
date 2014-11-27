package org.up.liferay.webdav;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;

import com.github.sardine.DavResource;
import com.google.common.cache.CacheLoader;

public class WebdavCacheLoader extends CacheLoader<WebdavResourceKey, List<DavResource>> implements Callable<List<DavResource>>{
	
	private WebdavObjectStore objectStore;
	private WebdavResourceKey key;
	private CallContext context;

	public WebdavCacheLoader(WebdavObjectStore objectStore, WebdavResourceKey key, CallContext context) {
		this.objectStore = objectStore;
		this.key = key;
		this.context = context;
	}


	@Override
	public List<DavResource> load(WebdavResourceKey key) throws Exception {
		return objectStore.getResourcesForIDintern(key.getEncodedId(), key.getGetDirectory(), context);
	}


	@Override
	public List<DavResource> call() throws Exception {		
		return objectStore.getResourcesForIDintern(key.getEncodedId(), key.getGetDirectory(), context);	
	}

}
