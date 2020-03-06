package com.esignlive.copytool.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.utils.SSLFix;
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
			setCredential(UserData.sourceApiUrl.subSequence(0, UserData.sourceApiUrl.lastIndexOf("/api")) + "/a/auth",
					sourceAccountVo);
			setCredential(UserData.destinationApiUrl.subSequence(0, UserData.destinationApiUrl.lastIndexOf("/api")) + "/a/auth",
					destinationAccountVo);

			// try getting sys info
			testConnectionEnvironment(UserData.sourceApiUrl + "/session", sourceAccountVo);
			testConnectionEnvironment(UserData.destinationApiUrl + "/session", destinationAccountVo);
		} catch (Exception e) {
			throw e;
		}
	}

	private void testConnectionEnvironment(String url, AccountVo accountVo) throws Exception {
		SSLFix.execute();

		System.out.println(url);
		URL sourceClient = new URL(url);
		
		HttpURLConnection sourceConn;
		if(UserData.proxy != null) {
			sourceConn = (HttpURLConnection) sourceClient.openConnection(UserData.proxy);
		}else {
			sourceConn = (HttpURLConnection) sourceClient.openConnection();
		}
		
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, accountVo);
		sourceConn.setRequestProperty("Accept", "application/json");

		System.out.println(sourceConn.getRequestProperties());

		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
		Reader ir = sourceResponseCode == 200 ? new InputStreamReader(sourceConn.getInputStream())
				: new InputStreamReader(sourceConn.getErrorStream());
		BufferedReader in = new BufferedReader(ir);
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		sourceConn.disconnect();
		if (sourceResponseCode == 200) {
			JSONObject json = JSON.parseObject(response.toString());
			JSONObject user = (JSONObject)json.get("user");
			String email = user.getString("email");
			
			accountVo.getSenderVo().setEmail(email);
		
		
		
		} else {
			throw new RuntimeException("Credential " + accountVo.getCredential() + " went wrong!");
		}
	}

	private void setCredential(String url, AccountVo accountVo) throws IOException, JSONException {
		SSLFix.execute();

		System.out.println(url);
		if (accountVo.getCredentialType() == CredentialType.API_KEY) {
			accountVo.setCredential(accountVo.getApiKey());
		} else if (accountVo.getCredentialType() == CredentialType.CREDENTIAL) {
			// generate session token
			// to do
			URL sourceClient = new URL(url);
			
			HttpURLConnection sourceConn;
			if(UserData.proxy != null) {
				sourceConn = (HttpURLConnection) sourceClient.openConnection(UserData.proxy);
			}else {
				sourceConn = (HttpURLConnection) sourceClient.openConnection();
			}
			
			sourceConn.setRequestProperty("Content-Type", "application/json");
			sourceConn.setRequestProperty("Accept", "application/json");
			sourceConn.setRequestMethod("POST");
			sourceConn.setDoOutput(true);
			sourceConn.setDoInput(true);

			String payload = "{\r\n" + "	\"email\":\"" + accountVo.getUsername() + "\",\r\n" + "	\"password\":\""
					+ accountVo.getPassword() + "\"\r\n" + "}";
			OutputStream os = sourceConn.getOutputStream();
			System.out.println(payload);
			os.write(payload.getBytes());
			os.flush();
			os.close();

			int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
			System.out.println(sourceResponseCode);

			Reader ir = sourceResponseCode == 200 ? new InputStreamReader(sourceConn.getInputStream())
					: new InputStreamReader(sourceConn.getErrorStream());
			BufferedReader in = new BufferedReader(ir);
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			sourceConn.disconnect();

			if (sourceResponseCode == 200) {
				JSONObject json = JSON.parseObject(response.toString());
				String sessionToken = json.getString("sessionToken");
				accountVo.setCredential(sessionToken);
				
//				
//				SenderVo senderVo = new SenderVo();
//				senderVo.setEmail(accountVo.getUsername());
//				accountVo.setSenderVo(senderVo);
//				
//				accountVo.getSenderVo().setEmail(accountVo.getUsername());
				
			} else {
				throw new RuntimeException(
						"Credentials for email " + accountVo.getUsername() + " is not correct, please check again! Reason: " + response.toString());
			}

		} else {
			throw new RuntimeException("Please choose a credential type!");
		}
	}

}
