/* 
 * Copyright (C) 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anyframe.logmanager.bundle.util;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator implemented class for file sorting with last modified date
 *
 * @author jaehyoung.eum
 * @param <File>
 *
 */
public class ModifiedDateComparator implements Comparator<File> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(File file1, File file2) {
        if(file1.lastModified() > file2.lastModified()) {
            return 1;
        }
   
        if(file1.lastModified() == file2.lastModified()) {
            return 0;
        }
        return -1;
	}

}
