package com.esignlive.copytool.data;

import java.net.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.TemplateVo;

public class UserData {
	public static String sourceApiUrl;
	public static AccountVo sourceCredential = new AccountVo();
	
	public static AccountVo destinationCredential = new AccountVo();
	public static String destinationApiUrl;
	public static Proxy proxy;
	
	//1:copy all senders and match senders
	//2:copy all senders create all by owner
	//3:copy current sender
	public static int copyMode = 0;
	
	
	public static Map<String, AccountVo> oldSenderMap = new HashMap<>();//<old sender email, old AccountVo>
	public static Map<String, AccountVo> newSenderMap = new HashMap<>();//<old sender email, new AccountVo>
	public static Map<String, TemplateVo> oldEnvTemplates = new LinkedHashMap<>();// <template id, TemplateVo object>
	public static Map<String, LayoutVo> oldEnvLayouts = new LinkedHashMap<>();// <layout id, sender email>

//	public static boolean copySender;

	public static Map<String, String> originalDocumentMap = new LinkedHashMap<>();// <document name, document path>

	public static final int pageSize = 100;
	
}
