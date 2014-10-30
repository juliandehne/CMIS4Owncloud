package org.up.liferay.test.owncloud;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.server.impl.CallContextImpl;
import org.junit.Test;
import org.up.liferay.owncloud.WebdavEndpoint;

import com.github.sardine.DavResource;

public class WebdavTests {

	@Test
	public void test() {
		CallContext context = new CallContext() {
			
			@Override
			public boolean isObjectInfoRequired() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getUsername() {
				return "test";
			}
			
			@Override
			public File getTempDirectory() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRepositoryId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPassword() {
				return "test";
			}
			
			@Override
			public BigInteger getOffset() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getMemoryThreshold() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long getMaxContentSize() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getLocale() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public BigInteger getLength() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public CmisVersion getCmisVersion() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getBinding() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object get(String key) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean encryptTempFiles() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		WebdavEndpoint endpoint = new WebdavEndpoint(context);
		try {
			List<DavResource> result = endpoint.getSardine().list(endpoint.getEndpoint()+"/");
			assert result != null;
			assert !result.isEmpty();
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
