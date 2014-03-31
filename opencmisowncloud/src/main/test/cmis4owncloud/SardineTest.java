package cmis4owncloud;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class SardineTest {

	@Test
	public void test() throws IOException {
		Sardine sardine = SardineFactory.begin();
		sardine.setCredentials("root", "voyager");

		List<DavResource> resources = sardine
				.list("http://uzuzjmd.de/owncloud/remote.php/webdav/");
		for (DavResource res : resources) {
			System.out.println(res); // calls the .toString() method.
		}
	}

	@Test
	public void testExists() throws IOException {
		Sardine sardine = SardineFactory.begin();
		sardine.setCredentials("root", "voyager");
		assertTrue(sardine
				.exists("http://uzuzjmd.de/owncloud/remote.php/webdav/music/"));
		assertTrue(sardine
				.exists("http://uzuzjmd.de/owncloud/remote.php/webdav/music/projekteva-letitrain.mp3"));
	}

	@Test
	public void testgetPDF() throws IOException {
		Sardine sardine = SardineFactory.begin();
		sardine.setCredentials("root", "voyager");
		InputStream stream = sardine
				.get("http://uzuzjmd.de/owncloud/remote.php/webdav/ownCloudUserManual.pdf");
		sardine.shutdown();
	}
}
