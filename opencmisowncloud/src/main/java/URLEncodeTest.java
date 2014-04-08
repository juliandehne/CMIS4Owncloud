import java.net.URLEncoder;

import org.junit.Test;

public class URLEncodeTest {

	@Test
	public void test() {
		System.out.println("root is" + URLEncoder.encode("/"));
	}

}
