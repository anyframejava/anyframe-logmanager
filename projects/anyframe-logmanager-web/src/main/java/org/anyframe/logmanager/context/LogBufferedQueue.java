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
package org.anyframe.logmanager.context;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jaehyoung Eum
 *
 */
public class LogBufferedQueue {

	private static final int QUEUE_CAPACITY = 5000;
	private static HashMap<String, BlockingQueue<String>> queues = new HashMap<String, BlockingQueue<String>>();
	
	/**
	 * @param queueName
	 */
	public static void addQueue(String queueName) {
		queues.put(queueName, new ArrayBlockingQueue<String>(QUEUE_CAPACITY));
	}
	
	/**
	 * @param queueName
	 * @param queue
	 */
	public static void addQueue(String queueName, BlockingQueue<String> queue) {
		queues.put(queueName, queue);
	}
	
	/**
	 * @param queueName
	 * @return
	 */
	public static BlockingQueue<String> getQueue(String queueName) {
		return queues.get(queueName);
	}
	
	/**
	 * @param queueName
	 */
	public static void removeQueue(String queueName) {
		queues.remove(queueName);
	}
	
	/**
	 * @param data
	 * @throws Exception
	 */
	public static void put(String queueName, String data) throws Exception{
		if(queues.containsKey(queueName)) {
			if(queues.get(queueName).remainingCapacity() == 0) {
				queues.get(queueName).take();
			}
			queues.get(queueName).put(data);	
		}else{
			throw new Exception("does not exist queue : " + queueName);
		}
			
	}
	
	/**
	 * @return
	 */
	public static int remainingCapacity(String queueName) throws Exception {
		int r = -1;
		if(queues.containsKey(queueName)) {
			r = queues.get(queueName).remainingCapacity();	
		}else{
			throw new Exception("does not exist queue : " + queueName);
		}
		return r;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static String take(String queueName) throws Exception {
		String r = null;
		if(queues.containsKey(queueName)) {
			r = queues.get(queueName).take();
		}else{
			throw new Exception("does not exist queue : " + queueName);
		}
		return r;
	}

}
