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

		String path = "archive/anyframe-logmanager-web.war";
		File warF = new File(path);
		if (warF.exists()) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}

		replaceFile(warF, new File("conf/log4j.xml"));
		replaceFile(warF, new File("conf/context.properties"));
		replaceFile(warF, new File("conf/mongo.properties"));
		
		System.out.println("end");
	}

	/**
	 * @param jar
	 * @param file
	 * @throws IOException
	 */
	public static void replaceFile(File jar, File file) throws IOException {
		InputStream fis = null;
		
		byte[] buf = new byte[1024];
		JarInputStream jis = null;
		JarOutputStream jos = null;
		String name = null;
		File temp = null;
		try{
			temp = File.createTempFile(jar.getName(), null);
			temp.delete();

			if (!renameTo(jar, temp)) {
				throw new IOException(jar.getName() + " --> " + temp.getName());
			}
			
			jis = new JarInputStream(new FileInputStream(temp));
			jos = new JarOutputStream(new FileOutputStream(jar));
			
			ZipEntry entry = jis.getNextEntry();
			
			while (entry != null) {
				name = entry.getName();
				if(name.indexOf(file.getName()) != -1) {
					entry = jis.getNextEntry();
					continue;
				}
				if (!file.getName().equals(name)) {
					jos.putNextEntry(new JarEntry(name));
					int len;
					while ((len = jis.read(buf)) > 0) {
						jos.write(buf, 0, len);
					}
				}
				entry = jis.getNextEntry();
			}
			jis.close();
			fis = new FileInputStream(file);
			jos.putNextEntry(new JarEntry(PRE_FIX + file.getName()));
			int len;
			while ((len = fis.read(buf)) > 0) {
				jos.write(buf, 0, len);
			}
			
		}catch(IOException ioe) {
			throw ioe;
		}finally{
			jos.closeEntry();
			fis.close();
			jos.close();
			temp.delete();
		}
		
	}
	
	private static boolean renameTo(File fromFile, File toFile) throws IOException {

		if (fromFile.isDirectory()) {
			File[] files = fromFile.listFiles();
			if (files == null) {
				// 디렉토리 내 아무것도 없는 경우
				return fromFile.renameTo(toFile);
			} else {
				// 디렉토리내 파일 또는 디렉토리가 존재하는 경우
				if (!toFile.mkdirs()) {
					return false;
				}

				for (File eachFile : files) {
					File toFileChild = new File(toFile, eachFile.getName());
					if (eachFile.isDirectory()) {
						if (!renameTo(eachFile, toFileChild)) {
							return false;
						}
					} else {
						if (!eachFile.renameTo(toFileChild)) {
							return false;
						}
					}
				}
				return fromFile.delete();
			}
		} else {
			// 파일인 경우
			if (fromFile.getParent() != null) {
				if (!toFile.mkdirs()) {
					return false;
				}
			}

			return fromFile.renameTo(toFile);
		}
	}
}
