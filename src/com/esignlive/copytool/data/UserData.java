package com.esignlive.copytool.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.Sender;

public class UserData {
	public static String sourceApiKey;
	
	public static String sourceApiUrl;
	
	public static EslClient sourceEslClient;
	
	public static String destinationApiKey;
	
	public static String destinationApiUrl;
	
	public static EslClient destinationEslClient;
	
	
	public static Map<String, Sender> oldSenderList = new HashMap<>();
	
	public static Map<String,Sender> oldAndNewSenderMap = new HashMap<>();
	
	public static boolean copySender;
	
	
}
