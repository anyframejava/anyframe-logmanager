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
package org.anyframe.logmanager.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.domain.LogCollection;
import org.anyframe.logmanager.domain.LogCollectionResult;
import org.anyframe.logmanager.service.LogCollectionService;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
@Service("logCollectionService")
public class LogCollectionServiceImpl implements LogCollectionService {
	
	@Inject
	@Named("logCollectionDao")
	private LogCollectionDao dao;

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogCollectionService#saveLogCollection(org.anyframe.logmanager.domain.LogCollection)
	 */
	public void saveLogCollection(LogCollection param) throws Exception {
		dao.saveLogCollection(param);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogCollectionService#getLogCollectionListByAppName(org.anyframe.logmanager.domain.LogCollection)
	 */
	public List<LogCollection> getLogCollectionListByAppName(LogCollection param) throws Exception {
		List<LogCollection> list = dao.getLogCollectionListByAppName(param);
		if(list != null) {
			int size = list.size();
			for(int i=0;i<size;i++) {
				LogCollection collection = list.get(i);
				LogCollectionResult r = dao.getLogCollectionResult(collection.getId());
				if(r == null) {
					collection.setResult(new LogCollectionResult(param.getId(), 0));
				}else{
					collection.setResult(r);
				}
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogCollectionService#getLogCollection(java.lang.String)
	 */
	public LogCollection getLogCollection(String id) throws Exception {
		return dao.getLogCollectionById(id);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogCollectionService#deleteLogCollection(java.lang.String)
	 */
	public void deleteLogCollection(String id) throws Exception {
		dao.deleteLogCollection(id);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogCollectionService#getLogCollectionResultList(java.lang.String)
	 */
	public List<LogCollectionResult> getLogCollectionResultList(String collectionId) throws Exception {
		return dao.getLogCollectionResultList(collectionId);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogCollectionService#getResultMessage(java.lang.String, int, java.lang.String)
	 */
	public List<String> getResultMessage(String collectionId, int iterationOrder, String messageType) throws Exception {
		LogCollectionResult result = dao.getResultMessage(collectionId, iterationOrder, messageType);
		return result.getMessage().get(messageType);
	}

}
