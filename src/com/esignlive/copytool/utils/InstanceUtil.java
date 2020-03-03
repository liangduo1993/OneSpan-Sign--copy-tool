package com.esignlive.copytool.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class InstanceUtil {

	public static final Map<String,String> endPointList;
	static
    {
		endPointList = new LinkedHashMap<String, String>();
		endPointList.put("Sandbox US 2/11 (sandbox.esignlive.com)", "https://sandbox.esignlive.com/api/");
		endPointList.put("Sandbox US 1/10 (sandbox.e-signlive.com)", "https://sandbox.e-signlive.com/api/");
		endPointList.put("Sandbox CA (sandbox.e-signlive.ca)", "https://sandbox.e-signlive.ca/api/");
		endPointList.put("Production US 2/11 (apps.esignlive.com)", "https://apps.esignlive.com/api/");
		endPointList.put("Production US 1/10 (apps.e-signlive.com)", "https://apps.e-signlive.com/api/");
		endPointList.put("Production CA (apps.e-signlive.ca)", "https://apps.e-signlive.ca/api/");
		endPointList.put("Production EU (apps.esignlive.eu)", "https://apps.esignlive.eu/api/");
		endPointList.put("Production AU (apps.esignlive.com.au)", "https://apps.esignlive.com.au/api/");
		endPointList.put("Preview (preview.esignlive.com)", "https://preview.esignlive.com/api/");
		endPointList.put("FedRAMP Sandbox (sandbox-gov.esignlive.com)", "https://signer-sandbox-gov.esignlive.com/api");
		endPointList.put("FedRAMP Production (gov.esignlive.com)", "https://signer-gov.esignlive.com/api");
		
		
    }
	
	
	public static String getUrlByKey(String key) throws Exception{
		return endPointList.get(key);
	}
	
	
}
