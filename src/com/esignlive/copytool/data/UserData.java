package com.esignlive.copytool.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.OwnerVo;
import com.esignlive.copytool.vo.SenderVo;
import com.esignlive.copytool.vo.TemplateVo;

public class UserData {
	public static String sourceApiUrl;
	public static AccountVo sourceCredential = new AccountVo();
	public static Set<String> sourceApiKeys = new LinkedHashSet<>();// don't include owner credential
	public static OwnerVo sourceOwnerVo = new OwnerVo();
	
	
	public static AccountVo destinationCredential = new AccountVo();
	public static String destinationApiUrl;
	public static Map<String, String> destinationApiKeys = new LinkedHashMap<>();// don't include owner credential
//	public static String destinationOwnerEmail;
	public static OwnerVo destinationOwnerVo = new OwnerVo();
	
	
	public static Map<String, SenderVo> oldSenderList = new HashMap<>();
	public static Map<String, TemplateVo> oldEnvTemplates = new LinkedHashMap<>();// <template id, TemplateVo object>
	public static Map<String, LayoutVo> oldEnvLayouts = new LinkedHashMap<>();// <layout id, sender email>
	public static Map<String, SenderVo> oldAndNewSenderMap = new HashMap<>();//<old sender email, new SenderVo>

	public static boolean copySender;

	public static Map<String, String> originalDocumentMap = new LinkedHashMap<>();// <document name, document path>

}
