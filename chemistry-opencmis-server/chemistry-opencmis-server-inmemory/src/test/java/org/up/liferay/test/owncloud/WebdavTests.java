package org.up.liferay.test.owncloud;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.List;

import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.server.impl.CallContextImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.up.liferay.webdav.WebdavDocumentImpl;
import org.up.liferay.webdav.WebdavEndpoint;
import org.up.liferay.webdav.WebdavIdDecoderAndEncoder;

import com.github.sardine.DavResource;

public class WebdavTests {

	private CallContext context = new CallContext() {

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

	@Test
	public void testEndpointConnection() {

		WebdavEndpoint endpoint = new WebdavEndpoint(context);
		try {
			List<DavResource> result = endpoint.getSardine().list(
					endpoint.getEndpoint() + "/");
			assertTrue(result != null);
			assertTrue(!result.isEmpty());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testWebdavToIdNotEncoded() {

		WebdavEndpoint endpoint = new WebdavEndpoint(context);
		try {
			List<DavResource> result = endpoint.getSardine().list(
					endpoint.getEndpoint() + "/documents/stuff/");
			assertTrue(result != null);
			assertTrue(!result.isEmpty());

			for (DavResource davResource : result) {
				String convertResult = WebdavIdDecoderAndEncoder.webdavToIdNotEncoded(davResource);
				convertResult.equals(
						"/documents/stuff/");				
				assertFalse((convertResult.contains("webdav")));				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIdtoName() {
		String id = WebdavIdDecoderAndEncoder.encode("/documents/hello/");
		assertTrue(WebdavIdDecoderAndEncoder.encodedIdToName(id).equals("hello"));
		
		String id2 = WebdavIdDecoderAndEncoder.encode("/documents/greatexpextations/hello.pdf");
		assertTrue(WebdavIdDecoderAndEncoder.encodedIdToName(id2).equals("hello.pdf"));		
	}
	
	@Test
	public void idToParent() {
		String id = "/documents/hello/stuff/youno/undso/";
		String toTest = WebdavIdDecoderAndEncoder.decodedIdToParent(id);
		assertTrue(toTest.equals("/documents/hello/stuff/youno/"));
		
		String id2 = "/documents/hello/stuff/youno/undso/masterofdesaster.odf";		
		assertTrue(WebdavIdDecoderAndEncoder.decodedIdToParent(id2).equals("/documents/hello/stuff/youno/undso/"));
		
		String id3 = "/documents";
		assertTrue(WebdavIdDecoderAndEncoder.decodedIdToParent(id3).equals("/"));
		
		String id4 = "/";
		assertNull(WebdavIdDecoderAndEncoder.decodedIdToParent(id4));
		
	}
	
	@Test
	public void testPDFStream() throws IOException {
		WebdavEndpoint endpoint = new WebdavEndpoint(context);
		WebdavDocumentImpl documentImpl = new WebdavDocumentImpl("/ownCloudUserManual.pdf", endpoint);
		InputStream input = documentImpl.getContent().getStream();
		OutputStream outputStream = (OutputStream) System.out;
		IOUtils.copy(input, outputStream);	
	}

}
