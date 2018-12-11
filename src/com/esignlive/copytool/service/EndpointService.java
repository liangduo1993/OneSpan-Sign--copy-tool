package com.esignlive.copytool.service;

import java.net.HttpURLConnection;
import java.net.URL;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.AccountVo.CredentialType;

public class EndpointService {
	private static EndpointService endpointService;

	private EndpointService() {
	}

	public static EndpointService getInstance() {
		return endpointService == null ? new EndpointService() : endpointService;
	}

	public void testConnection() throws Exception {
		AccountVo sourceAccountVo = UserData.sourceCredential;
		AccountVo destinationAccountVo = UserData.destinationCredential;
		try {
			System.out.println(sourceAccountVo);
			// according to apikey/credential, inject AccountVo
			injectAccountVo(sourceAccountVo);
			injectAccountVo(destinationAccountVo);
			
			// try getting sys info
			testConnectionEnvironment(UserData.sourceApiUrl + "/sysinfo", sourceAccountVo);
			testConnectionEnvironment(UserData.destinationApiUrl + "/sysinfo", destinationAccountVo);
		} catch (Exception e) {
			throw e;
		}
	}

	private void testConnectionEnvironment(String url, AccountVo accountVo) throws Exception {
		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, accountVo);
		sourceConn.setRequestProperty("Accept", "application/json");
		
		System.out.println(sourceConn.getRequestProperties());
		
		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();

		if (sourceResponseCode == 200) {
			sourceConn.disconnect();
		} else {
			throw new RuntimeException("Credential " + accountVo.getCredential() + " went wrong!");
		}
	}

	private void injectAccountVo(AccountVo accountVo) {
		if (accountVo.getCredentialType() == CredentialType.API_KEY) {
			accountVo.setCredential(accountVo.getApiKey());
		} else if (accountVo.getCredentialType() == CredentialType.CREDENTIAL) {
			// generate session token
			// to do

		} else {
			throw new RuntimeException("Please choose a credential type!");
		}
	}

}
