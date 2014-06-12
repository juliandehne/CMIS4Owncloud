package org.up.ple.dcim4owncloud.util;

import java.io.FileNotFoundException;

public interface MemFileSystem {
	void writeFile(String name, byte[] contents);

	byte[] readFile(String name) throws FileNotFoundException;
}
