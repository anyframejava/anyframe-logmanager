/* 
 * Copyright (C) 2002-2012 Robert Stewart (robert@wombatnation.com)
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
package org.anyframe.logmanager.service;

import static org.junit.Assert.assertNotNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.domain.AnalysisLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:./src/main/resources/spring/context-*.xml" })
public class MongoOperationsTest {

	private String collectionName = "log4jlogs";
	
	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;
	
	@Test
	public void testFind() throws Exception {
		
		Query query = query(where("level").is("DEBUG"));
		
//		List<TestLog> list = mongoOperations.findAll(TestLog.class, collectionName);
		List<AnalysisLog> list = mongoOperations.find(query.limit(10), AnalysisLog.class, collectionName);
		
		int size = list.size();
		System.out.println("testFind():size=" + size);
		
		for(int i=0;i<size;i++) {
			AnalysisLog log = list.get(i);
			System.out.println(log);
		}
		
		assertNotNull("fail to fetch list", list);
		
	}
	
}
