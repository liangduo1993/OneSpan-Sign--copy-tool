package com.esignlive.copytool.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.utils.StringUtil;
import com.esignlive.copytool.view.Process2;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.SenderVo;
import com.esignlive.copytool.vo.AccountVo.CredentialType;
import com.esignlive.copytool.vo.AccountVo.SenderStatus;

public class SenderService {
	private static SenderService endpointService;

	private SenderService() {
	}

	public static SenderService getInstance() {
		return endpointService == null ? new SenderService() : endpointService;
	}

	public List<SenderVo> getOldEnvSenders() throws IOException, JSONException {
		List<SenderVo> senderVos = new ArrayList<>();
		int pageIndex = 1;
		try {
			while (true) {
				if (pageIndex > 10) {
					break;
				}

				String url = UserData.sourceApiUrl + "/account/senders?from=" + pageIndex + "&to=" + (pageIndex + 49);
				URL client = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) client.openConnection();
				conn.setRequestProperty("Content-Type", "application/json");
				HttpURLConnectionUtil.addCredential(conn, UserData.sourceCredential);
				conn.setRequestProperty("Accept", "application/json");

				int responseCode = ((HttpURLConnection) conn).getResponseCode();

				if (responseCode == 200) {
					System.out.println(responseCode + " OK!");
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}

					in.close();
					conn.disconnect();

					JSONObject sendersJSON = new JSONObject(response.toString());

					JSONArray jsonArray = sendersJSON.getJSONArray("results");

					if (jsonArray.length() == 0) {
						break; // break loop
					}

					boolean flag = true;
					for (int index = 0; index < jsonArray.length(); index++) {
						JSONObject senderJSON = jsonArray.getJSONObject(index);

						SenderVo senderVo = new SenderVo();
						senderVo.setContent(senderJSON);

						if (flag) {
							JSONObject ownerAccount = senderJSON.getJSONObject("account");
							UserData.sourceCredential.getSenderVo().setId(ownerAccount.getString("owner"));
							UserData.sourceCredential.setSenderStatus(SenderStatus.ACTIVE);
							// UserData.sourceCredential.setId(ownerAccount.getString("owner"));
							flag = false;
						}

						String email = senderJSON.getString("email");
						String id = senderJSON.getString("id");
						senderVo.setEmail(email);
						senderVo.setId(id);

						String type = senderJSON.getString("type");
						if (type.equals("REGULAR")) {
							senderVo.setSenderType(SenderVo.SenderType.REGULAR);
						} else if (type.equals("MANAGER")) {
							if (id.equals(UserData.sourceCredential.getSenderVo().getId())) {
								// type owner
								senderVo.setSenderType(SenderVo.SenderType.OWNER);
								UserData.sourceCredential.getSenderVo().setEmail(email);
							} else {
								// type manager
								senderVo.setSenderType(SenderVo.SenderType.MANAGER);
							}
						}
						if (senderVo.getSenderType() != SenderVo.SenderType.OWNER) {
							AccountVo accountVo = new AccountVo();
							accountVo.setCredentialType(CredentialType.API_KEY);
							accountVo.setSenderVo(senderVo);

							try {
								accountVo.setSenderStatus(
										SenderStatus.valueOf(senderVo.getContent().getString("status")));
							} catch (Exception e) {
								// to do
							}

							UserData.oldSenderMap.put(senderVo.getEmail(), accountVo);
							senderVos.add(senderVo);
						} else {
							UserData.sourceCredential.setSenderVo(senderVo);
							// UserData.sourceOwnerVo.setId(senderVo.getId());
						}
					}
				} else {
					throw new RuntimeException("Request for sender list fail!");
				}
				pageIndex += 50;
			}
		} catch (

		Exception e) {
			throw e;
		}

		for (AccountVo sender : UserData.oldSenderMap.values()) {
			String apiKey;
			try {
				apiKey = getApiKey(true, sender.getSenderVo().getId());
				sender.setCredential(apiKey);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error retrieving all senders' api key, please try again!");
			}
		}
		System.out.println("old sender map: ");
		Map<String, AccountVo> oldSenderMap = UserData.oldSenderMap;
		for (Map.Entry<String, AccountVo> senderVo2 : oldSenderMap.entrySet()) {
			System.out.println(senderVo2.getValue());
		}
		System.out.println("=====");

		return senderVos;
	}

	public void setNewEnvOwner() throws IOException, JSONException {
		int pageIndex = 1;
		try {
			while (true) {
				if (pageIndex > 10) {
					break;
				}
				String url = UserData.destinationApiUrl + "/account/senders?from=" + pageIndex + "&to="
						+ (pageIndex + 49);
				URL client = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) client.openConnection();
				conn.setRequestProperty("Content-Type", "application/json");
				HttpURLConnectionUtil.addCredential(conn, UserData.destinationCredential);
				conn.setRequestProperty("Accept", "application/json");

				int responseCode = ((HttpURLConnection) conn).getResponseCode();

				if (responseCode == 200) {
					System.out.println(responseCode + " OK!");
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}

					in.close();
					conn.disconnect();

					JSONObject sendersJSON = new JSONObject(response.toString());

					JSONArray jsonArray = sendersJSON.getJSONArray("results");

					if (jsonArray.length() == 0) {
						break; // break loop
					}

					boolean flag = true;
					for (int index = 0; index < jsonArray.length(); index++) {
						JSONObject senderJSON = jsonArray.getJSONObject(index);

						if (flag) {
							JSONObject ownerAccount = senderJSON.getJSONObject("account");
							UserData.destinationCredential.getSenderVo().setId(ownerAccount.getString("owner"));
							flag = false;
						}

						String email = senderJSON.getString("email");
						String id = senderJSON.getString("id");

						String type = senderJSON.getString("type");
						if (type.equals("MANAGER")) {
							if (id.equals(UserData.destinationCredential.getSenderVo().getId())) {
								// type owner
								SenderVo ownerSenderVo = new SenderVo();
								ownerSenderVo.setContent(senderJSON);
								ownerSenderVo.setEmail(email);
								ownerSenderVo.setId(id);
								ownerSenderVo.setSenderType(SenderVo.SenderType.OWNER);
								UserData.destinationCredential.setSenderVo(ownerSenderVo);
								System.out.println("new env owner: " + UserData.destinationCredential);
								return;
							}
						}
					}
				} else {
					throw new RuntimeException("Request for sender list fail!");
				}
				pageIndex += 50;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public Map<JButton, Boolean> inviteSenders(Map<JButton, String> senderEmails, Process2 view) {
		// try invite senders
		Map<JButton, Boolean> result = new LinkedHashMap<>();
		Map<String, AccountVo> oldAndNewSenderMap = UserData.newSenderMap;

		StringBuilder errorMsg = new StringBuilder(200);

		for (Map.Entry<JButton, String> entry : senderEmails.entrySet()) {
			String newSenderEmail = entry.getValue();
			JButton oldSenderEmail = entry.getKey();
			if (!StringUtil.isEmpty(newSenderEmail)) {// new sender email is not null
				try {
					String oldEmail = oldSenderEmail.getText().substring(0,
							oldSenderEmail.getText().lastIndexOf(":") - 1);
					SenderVo oldSender = UserData.oldSenderMap.get(oldEmail).getSenderVo();
					SenderVo sender = new SenderVo();
					sender.setContent(oldSender.getContent());
					sender.setId(oldSender.getId());
					sender.setSenderType(oldSender.getSenderType());
					sender.setEmail(newSenderEmail);
					sender.getContent().put("email", newSenderEmail);

					SenderVo invitedSender = inviteSender(UserData.destinationApiUrl + "/account/senders",
							sender.getContent(), sender);
					try {
						String apiKey = getApiKey(false, invitedSender.getId());
						AccountVo senderAccountVo = new AccountVo();
						senderAccountVo.setApiKey(apiKey);
						senderAccountVo.setCredential(apiKey);
						senderAccountVo.setCredentialType(AccountVo.CredentialType.API_KEY);
						senderAccountVo.setSenderVo(invitedSender);
						senderAccountVo.setSenderStatus(SenderStatus.ACTIVE);

						result.put(oldSenderEmail, true);
						oldAndNewSenderMap.put(oldEmail, senderAccountVo);
					} catch (Exception ex) {
						throw ex;
					}
				} catch (Exception e) {
					errorMsg.append(e.getMessage()).append("\n");
					result.put(oldSenderEmail, false);
				}
			} else { // new sender email is null
				result.put(oldSenderEmail, null);// we don't return any result by returning null
				oldAndNewSenderMap.put(oldSenderEmail.getText(), null);// no mapping old to new sender
			}
			if (result.get(oldSenderEmail) != null) {
				view.setInvitationStatus(oldSenderEmail, result.get(oldSenderEmail));
			}
		}

		// return error msg
		view.setErrorMsg(errorMsg.toString());

		return result;
	}

	// to check
	private SenderVo inviteSender(String url, JSONObject sender, SenderVo senderVo) throws Exception {
		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, UserData.destinationCredential);
		sourceConn.setRequestProperty("Accept", "application/json");
		sourceConn.setRequestMethod("POST");
		sourceConn.setDoOutput(true);
		sourceConn.setDoInput(true);

		System.out.println(sender.toString());

		OutputStream os = sourceConn.getOutputStream();
		os.write(sender.toString().getBytes());
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
			JSONObject json = new JSONObject(response.toString());
			System.out.println(json);
			SenderVo invitedSender = new SenderVo();
			invitedSender.setContent(json);
			invitedSender.setSenderType(senderVo.getSenderType());
			invitedSender.setEmail(json.getString("email"));
			invitedSender.setId(json.getString("id"));
			System.out.println(invitedSender);
			return invitedSender;
		} else {
			throw new RuntimeException(
					"Fail to invite sender: " + senderVo.getEmail() + " , Reason: " + response.toString());
		}
	}

	// to check
	public void senderNextProcessCallback() throws IOException, JSONException {
		// set old env sender
		if (UserData.oldSenderMap == null || UserData.oldSenderMap.size() == 0) {
			List<SenderVo> oldEnvSenders = getOldEnvSenders();
			System.out.println("=== " + oldEnvSenders);

		}

		// set new env owner
		if (StringUtil.isEmpty(UserData.destinationCredential.getSenderVo().getId())) {
			setNewEnvOwner();
		}

	}

	public String getApiKey(boolean isSource, String senderId) throws IOException, JSONException {
		String apiUrl = null;
		AccountVo accountVo = null;
		if (isSource) {
			apiUrl = UserData.sourceApiUrl;
			accountVo = UserData.sourceCredential;
		} else {
			apiUrl = UserData.destinationApiUrl;
			accountVo = UserData.destinationCredential;
		}

		URL client = new URL(apiUrl + "/account/senders/" + senderId + "/apiKey");
		HttpURLConnection conn = (HttpURLConnection) client.openConnection();
		conn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(conn, accountVo);
		conn.setRequestProperty("Accept", "application/json");

		int responseCode = ((HttpURLConnection) conn).getResponseCode();

		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			conn.disconnect();

			JSONObject json = new JSONObject(response.toString());
			System.out.println(json.getString("apiKey"));
			return json.getString("apiKey");
		} else {
			throw new RuntimeException("Request did not succeed.");
		}
	}

}
