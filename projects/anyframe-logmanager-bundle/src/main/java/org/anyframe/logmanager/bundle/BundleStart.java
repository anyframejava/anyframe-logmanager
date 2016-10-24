/* 
 * Copyright (C) 2010 Robert Stewart (robert@wombatnation.com)
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
package org.anyframe.logmanager.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class BundleStart {
	
	private static String configFile = "conf/logagent.ini";
	
	private static String cache = "meta";
	
	private final static String BUNDLE_LOC = "bundle";
	
	private final static int AGENT_PORT = 9870;
	
	private static Bundle framework = null;
	
	private static String shellPort;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if(args.length > 0){
			configFile = args[0];
			cache = "meta-agent";
    	}
    	
        try {            
            printWelcome();
            
            File home = new File(Felix.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
            startFramework(setFelixProperties(new File(home, configFile), new File(home, cache)) );
            
            installBundles(new File(home, BUNDLE_LOC)); 
            
        } catch (Exception e) {
        	System.err.println("Could not create framework:");
            e.printStackTrace();
            System.exit(-1);
        }        
	}
	

	/**
	 * 
	 */
	private static void printWelcome() {
        System.out.println("\n::: Welcome to Anyframe Log Manager Agent 1.7.0 :::");
        System.out.println("===============================================\n");
	}

	/**
	 * method to start a embedded osgi framework
	 * 
	 * @param props
	 * @return
	 * @throws BundleException
	 */
	public static Bundle startFramework(Map<String, String> props) throws BundleException {
		framework = new Felix(props);
		framework.start();
		return framework;
	}

	/**
	 * @param config
	 * @param cache
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> setFelixProperties(File config, File cache) throws Exception{
		Map configMap = new StringMap(false);
        
        // This information is from felix 1.8.0
//        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
//            "org.osgi.framework; version=1.4.0," +
//            "org.osgi.service.packageadmin; version=1.2.0," +
//            "org.osgi.service.startlevel; version=1.1.0," +
//            "org.osgi.service.url; version=1.0.0," +
//            "org.osgi.util.tracker; version=1.3.3");
        configMap.put(BundleCache.CACHE_ROOTDIR_PROP, cache.getPath());
        configMap.put("osgi.shell.telnet", "on");
        configMap.put("obr.repository.url", "http://felix.apache.org/obr/releases.xml");
        configMap.put("org.osgi.framework.storage.clean", "onFirstInit");
//        configMap.put("org.osgi.framework.system.packages.extra", 
//        		"javax.naming, javax.naming.spi");
        
        Properties thirdProp = loadINI(config);
        if(thirdProp != null){
        	configMap.putAll(thirdProp);
        	if(thirdProp.getProperty("http.port") == null)	{ // if there's no http.port
        		configMap.put("http.port", String.valueOf(AGENT_PORT) );
        	}
        }
        
        // override properties from program's arguments
//        args2map(configMap, args);
        
        replaceKey(configMap, "log.level", "felix.log.level");
        replaceKey(configMap, "http.port", "org.osgi.service.http.port");
        replaceKey(configMap, "shell.ip", "osgi.shell.telnet.ip");
        Object o = configMap.get("shell.port");
        shellPort = o == null ? null : o.toString();
        replaceKey(configMap, "shell.port", "osgi.shell.telnet.port");
        replaceKey(configMap, "shell.maxconn", "osgi.shell.telnet.maxconn");
        return configMap;
    }
    
	/**
     * replace map's key
     * @param map object containing key
     * @param key0 target key to be replaced
     * @param key1 destination key 
     */
    private static void replaceKey(Map<String, String> map, String key0, String key1){
    	if(map.containsKey(key0)){
    		map.put(key1, map.get(key0));
    		map.remove(key0);
    	}
    	
    }
    
    /**
     * @param loc
     * @throws IOException
     * @throws BundleException
     */
    private static void installBundles(File loc) throws IOException, BundleException{
    	// install all bundles in loc folder
    	File[] files =loc.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
    	
    	for(File file : files){
    		String name = file.getName();
    		if(file.isFile() && !"felix.jar".equals(name)){
    			framework.getBundleContext().installBundle("file:"+loc.getPath()+"/"+name);
    		}	
    	}
    	
    	String[] libraries = libBundles();
    	// start bundles which are not belonging to lib
		for(Bundle bnd : framework.getBundleContext().getBundles()) {
			if(contains(libraries, bnd)) {
				continue;
			}
			bnd.start();
		}
		
		// if remote shell is loaded, show some guides.
    	for(Bundle bnd : framework.getBundleContext().getBundles()) {
    		if(shellPort != null &&
    				bnd.getSymbolicName().equals("org.apache.felix.org.apache.felix.shell.remote") &&
    				bnd.getState() == Bundle.ACTIVE){
    			String ip = framework.getBundleContext().getProperty("osgi.shell.telnet.ip");
    			System.out.println("::: You can access Oden by Telnet. (e.g. telnet " + (ip == null ? "localhost" : ip) + " " + shellPort +") :::\n");
    		}
    	}
    	    	
    }
    
	/**
	 * @param list
	 * @param b
	 * @return
	 */
	private static boolean contains(String[] list, Bundle b){
    	for(String s : list){
			if(b.getLocation().endsWith(s)) {
				return true;
			}
		}
    	return false;
    }

    /**
     * @return
     */
    private static String[] libBundles() {
    	String bnds = framework.getBundleContext().getProperty("bundle.libs");
    	if(bnds == null) {
    		return new String[0];
    	}
    	return bnds.split("\\s");
    }
    
    /**
     * @param iniFile
     * @return
     * @throws Exception
     */
    private static Properties loadINI(File iniFile) throws Exception{
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(iniFile));
		}catch(FileNotFoundException e){
			return null;
		}
		
		Properties prop = null;
		if(in != null){
			prop = new Properties();
			try{
				prop.load(in);
			}catch(Exception e){
				throw new Exception("Illegal format error: " + iniFile);
			}
			in.close();
		}
		return prop;
		
	}
    
    /**
     * @throws BundleException
     */
    public static void stopFramework() throws BundleException{
    	framework.stop();
    	System.out.println("framework was stop.");
    }
    
    /**
     * @return
     */
    public static Bundle getFramework(){
    	return framework;
    }

}
