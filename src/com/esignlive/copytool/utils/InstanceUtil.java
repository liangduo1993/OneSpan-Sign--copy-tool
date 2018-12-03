package com.esignlive.copytool.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class InstanceUtil {

	public static final Map<String,String> endPointList;
	static
    {
		endPointList = new LinkedHashMap<String, String>();
		endPointList.put("Sandbox US 2/11", "https://sandbox.esignlive.com/api/");
		endPointList.put("Sandbox US 1/10", "https://sandbox.e-signlive.com/api/");
		endPointList.put("Sandbox CA", "https://sandbox.e-signlive.ca/api/");
		endPointList.put("Production US 2/11", "https://apps.esignlive.com/api/");
		endPointList.put("Production US 1/10", "https://apps.e-signlive.com/api/");
		endPointList.put("Production CA", "https://apps.e-signlive.ca/api/");
		endPointList.put("Production EU", "https://apps.esignlive.eu/api/");
		endPointList.put("Production AU", "https://apps.esignlive.com.au/api/");
    }
	
	
	public static String getUrlByKey(String key) throws Exception{
		return endPointList.get(key);
	}
	
	
}
