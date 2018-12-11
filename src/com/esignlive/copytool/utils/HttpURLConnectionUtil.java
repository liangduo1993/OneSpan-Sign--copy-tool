package com.esignlive.copytool.utils;

import java.net.HttpURLConnection;

import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.AccountVo.CredentialType;

public class HttpURLConnectionUtil {

	public static HttpURLConnection addCredential(HttpURLConnection conn, AccountVo accountVo) {
		if(accountVo.getCredentialType() == CredentialType.API_KEY) {
			conn.setRequestProperty("Authorization", "Basic " + accountVo.getCredential());
		}else if(accountVo.getCredentialType() == CredentialType.CREDENTIAL){
			conn.setRequestProperty("Cookie", "ESIGNLIVE_SESSION_ID=" + accountVo.getCredential());
		}
		return conn;
	}
}
