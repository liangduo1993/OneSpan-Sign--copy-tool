package com.esignlive.copytool.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.SenderVo;
import com.esignlive.copytool.vo.TemplateVo;

public class UserData {
	public static String sourceApiUrl;
	public static AccountVo sourceCredential = new AccountVo();
	public static List<AccountVo> sourceApiKeys = new ArrayList<>();// don't include owner credential
//	public static OwnerVo sourceOwnerVo = new OwnerVo();
	
	
	public static AccountVo destinationCredential = new AccountVo();
	public static String destinationApiUrl;
	public static List<AccountVo> destinationApiKeys = new ArrayList<>();// don't include owner credential
//	public static OwnerVo destinationOwnerVo = new OwnerVo();
	
	
	public static Map<String, SenderVo> oldSenderList = new HashMap<>();
	public static Map<String, TemplateVo> oldEnvTemplates = new LinkedHashMap<>();// <template id, TemplateVo object>
	public static Map<String, LayoutVo> oldEnvLayouts = new LinkedHashMap<>();// <layout id, sender email>
	public static Map<String, SenderVo> oldAndNewSenderMap = new HashMap<>();//<old sender email, new SenderVo>

	public static boolean copySender;

	public static Map<String, String> originalDocumentMap = new LinkedHashMap<>();// <document name, document path>

}
