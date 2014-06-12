package org.up.ple.dcim4owncloud.util;

import java.io.FileNotFoundException;
import java.util.Hashtable;

public class MemFileSystemImpl implements MemFileSystem {
	Hashtable files = new Hashtable(); // maps Strings to byte arrays

	public void writeFile(String name, byte[] contents) {
		files.put(name, contents);
	}

	public byte[] readFile(String name) throws FileNotFoundException {
		byte[] contents = (byte[]) files.get(name);
		if (contents == null)
			throw new FileNotFoundException(name);
		else
			return contents;
	}
}
