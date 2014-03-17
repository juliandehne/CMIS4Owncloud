package cmis4owncloud;

import java.io.IOException;
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
}
