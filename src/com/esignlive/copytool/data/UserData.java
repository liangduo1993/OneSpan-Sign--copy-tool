package com.esignlive.copytool.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.TemplateVo;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.Sender;

public class UserData {
	public static String sourceApiKey;
	
	public static String sourceApiUrl;
	
	public static EslClient sourceEslClient;
	
	public static Set<String> sourceApiKeys = new LinkedHashSet<>();//don't include owner credential
	public static Map<String, String> destinationApiKeys = new LinkedHashMap<>();//don't include owner credential <sender email, sender api key>
	
	public static String destinationApiKey;
	
	public static String destinationApiUrl;
	
	public static String destinationOwnerEmail;
	
	public static EslClient destinationEslClient;
	
	public static Map<String, Sender> oldSenderList = new HashMap<>();
	public static Map<String, TemplateVo> oldEnvTemplates = new LinkedHashMap<>();//<template id, TemplateVo object>
	public static Map<String,LayoutVo> oldEnvLayouts = new LinkedHashMap<>();//<layout id, sender email>
	
	public static Map<String,Sender> oldAndNewSenderMap = new HashMap<>();
	
	public static boolean copySender;
	
	public static Map<String,String> originalDocumentMap = new LinkedHashMap<>();//<document name, document path>
	
}
