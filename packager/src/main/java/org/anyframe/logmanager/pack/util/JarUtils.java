/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.logmanager.pack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author arumb-laptop
 *
 */
public class JarUtils {
	private static final String PRE_FIX = "WEB-INF/classes/";

	public static void main(String[] args) throws Exception {

		File[] files = {new File("conf/log4j.xml"), 
				new File("conf/context.properties"), 
				new File("conf/mongo.properties")};
		
		String path = "archive/anyframe-logmanager-web.war";
		File warF = new File(path);
		if (warF.exists()) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}

		addFilesToExistingJar(warF, files);
		System.out.println("end");
	}

	public static void addFilesToExistingJar(File jarFile, File[] files) throws IOException {
		// get a temp file
		File tempFile = File.createTempFile(jarFile.getName(), null);
		// delete it, otherwise you cannot rename your existing jar to it.
		tempFile.delete();

		boolean renameOk = jarFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file " + jarFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		JarInputStream jin = new JarInputStream(new FileInputStream(tempFile));
		JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile));

		ZipEntry entry = jin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			if(name.indexOf(files[0].getName()) != -1 
					|| name.indexOf(files[1].getName()) != -1 
					|| name.indexOf(files[2].getName()) != -1) {
				entry = jin.getNextEntry();
				continue;
			}
			boolean notInFiles = true;
			for (File f : files) {
				if (f.getName().equals(name)) {
					notInFiles = false;
					break;
				}
			}
			if (notInFiles) {
				// Add ZIP entry to output stream.
				out.putNextEntry(new JarEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = jin.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			entry = jin.getNextEntry();
		}
		// Close the streams
		jin.close();
		// Compress the files
		for (int i = 0; i < files.length; i++) {
			InputStream in = new FileInputStream(files[i]);
			// Add ZIP entry to output stream.
			out.putNextEntry(new JarEntry(PRE_FIX + files[i].getName()));
			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			// Complete the entry
			out.closeEntry();
			in.close();
		}
		// Complete the ZIP file
		out.close();
		tempFile.delete();
	}
}
