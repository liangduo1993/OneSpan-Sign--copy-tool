package com.esignlive.copytool.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.view.Process3;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.SenderVo;
import com.esignlive.copytool.vo.TemplateVo;

public class TemplateService {
	private static TemplateService endpointService;

	private TemplateService() {
	}

	public static TemplateService getInstance() {
		return endpointService == null ? new TemplateService() : endpointService;
	}

	private JSONObject getTemplateById(String templateId, AccountVo accountVo) throws IOException, JSONException {
		String url = UserData.sourceApiUrl + "/packages/" + templateId;
		URL client = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) client.openConnection();
		conn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(conn, accountVo);
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

			JSONObject templateJSON = new JSONObject(response.toString());
			return templateJSON;
		} else {
			throw new RuntimeException("Request for template list fail!");
		}

	}

	private String copyTemplateFromOldAccount(String oldTemplateID, String newSenderEmail) throws Exception {
		try {

			// get template metadata
			JSONObject templateById = getTemplateById(oldTemplateID, UserData.sourceCredential);
			templateById.put("sender", new JSONObject("{\"email\":\"" + newSenderEmail + "\"}"));
			templateById.put("type", "TEMPLATE");

			// download and document content and remove default consent
			Map<String, String> documentIds = new LinkedHashMap<>();// <id,name>

			JSONArray documentArray = templateById.getJSONArray("documents");
			int defaultIndex = -1;
			for (int i = 0; i < documentArray.length(); i++) {
				JSONObject documentJSON = documentArray.getJSONObject(i);
				String docId = documentJSON.getString("id");
				String docName = documentJSON.getString("name");
				if (!docId.equals("default-consent")) {
					documentIds.put(docId, docName);
				} else {
					defaultIndex = i;
				}
			}
			if (defaultIndex >= 0) {
				documentArray.remove(defaultIndex);
			}
			boolean isOriginal = false;
			Map<String, byte[]> docContent = new LinkedHashMap<>();
			for (Map.Entry<String, String> entry : documentIds.entrySet()) {
				String id = entry.getKey();
				String name = entry.getValue();
				byte[] byteArray;
				String filepath;
				if ((filepath = containsOriginalDocument(name)) == null) {
					URL client = new URL(
							UserData.sourceApiUrl + "/packages/" + oldTemplateID + "/documents/" + id + "/pdf");
					HttpURLConnection conn = (HttpURLConnection) client.openConnection();
					conn.setRequestProperty("Content-Type", "application/json");
					HttpURLConnectionUtil.addCredential(conn, UserData.sourceCredential);
					conn.setRequestProperty("Accept", "application/pdf");

					InputStream inputStream = conn.getInputStream();
					inputStream = conn.getInputStream();
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int nRead;
					byte[] data = new byte[1024];
					while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}

					buffer.flush();
					buffer.close();
					inputStream.close();
					conn.disconnect();

					byteArray = buffer.toByteArray();
				} else {
					byteArray = IOUtils.toByteArray(new FileInputStream(filepath));
					isOriginal = true;
				}
				docContent.put(documentIds.get(id), byteArray);
			}
			System.out.println(isOriginal + " is original");
			System.out.println(templateById);

			// create new template in destination env
			String requestURL = UserData.destinationApiUrl + "/packages";
			String charset = "UTF-8";
			String boundary = Long.toHexString(System.currentTimeMillis());
			String CRLF = "\r\n"; // Line separator used in multipart/form-data.

			HttpsURLConnection connection = null;
			URL url = new URL(requestURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			HttpURLConnectionUtil.addCredential(connection, UserData.destinationCredential);
			connection.setRequestProperty("Accept", "application/json; esl-api-version=11.21");
			OutputStream output = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

			try {
				for (Map.Entry<String, byte[]> entry : docContent.entrySet()) {
					String docName = documentIds.get(entry.getKey());
					// Add pdf file.
					writer.append("--" + boundary).append(CRLF);
					writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + docName + "\"")
							.append(CRLF);
					String contentType = "application/json";
					try {
						if (!isOriginal) {
							contentType = URLConnection.guessContentTypeFromName(docName);
						}
					} catch (Exception e) {
						// to do
					}
					System.out.println(contentType + " content type");

					writer.append("Content-Type: " + contentType).append(CRLF);
					writer.append("Content-Transfer-Encoding: application/pdf").append(CRLF);
					writer.append(CRLF).flush();
					output.write(entry.getValue());
					output.flush();
					writer.append(CRLF).flush();
				}
				// add json payload
				writer.append("--" + boundary).append(CRLF);
				writer.append("Content-Disposition: form-data; name=\"payload\"").append(CRLF);
				writer.append("Content-Type: application/json; charset=" + charset).append(CRLF);
				writer.append(CRLF).append(templateById.toString()).append(CRLF).flush();

				// End of multipart/form-data.
				writer.append("--" + boundary + "--").append(CRLF).flush();
			} catch (IOException ex) {
				System.err.println(ex);
			}

			int responseCode = ((HttpURLConnection) connection).getResponseCode();
			if (responseCode == 200) {
				// get and write out response
				System.out.println("create successfully!");
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				return response.toString();
			} else {
				// get and write out response
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				throw new RuntimeException(response.toString());
			}

		} catch (Exception e) {
			// todo
			throw e;
		}
	}

	/**
	 * 
	 * @param String
	 *            upload original documents //to do
	 * @param String
	 *            specify partial template IDs //to do
	 */
	public Map<String, Boolean> copyTemplate(Process3 view) {
		Map<String, Boolean> result = new LinkedHashMap<>();

		// build error msg
		StringBuilder errorMsg = new StringBuilder(200);

		// deal with copy template
		for (TemplateVo templateVo : UserData.oldEnvTemplates.values()) {
			if (templateVo.getIsCopy()) {
				String oldTemplateID = templateVo.getTemplateId();
				String oldSenderEmail = templateVo.getOldEnvSenderEmail();
				SenderVo sender = UserData.oldAndNewSenderMap.get(oldSenderEmail);
				String newSenderEmail = null;
				if (sender == null) {
					newSenderEmail = UserData.destinationOwnerVo.getEmail();
				} else {
					newSenderEmail = sender.getEmail();
				}
				System.out.println(newSenderEmail);

				boolean copySuccess = false;
				try {
					String copyTemplateFromOldAccount = copyTemplateFromOldAccount(oldTemplateID, newSenderEmail);
					System.out.println(copyTemplateFromOldAccount);
					copySuccess = true;
				} catch (Exception e) {
					// to do
					// add error msg
					System.out.println(e.getMessage());
					errorMsg.append(e.getMessage()).append("\n");
				}
				// update view
				// to do
				view.setCopyStatus(oldTemplateID, copySuccess);
				result.put(oldTemplateID, copySuccess);
			}
		}
		view.setErrorMsg(errorMsg.toString());
		return result;
	}

	public Map<String, String> getOldEnvTemplates() throws Exception {
		// set template list in user data
		Map<String, String> templateIdAndName = new LinkedHashMap<>();

		// old env owner
		AccountVo ownerCredential = UserData.sourceCredential;
		try {
			retrieveTemplatesCallback(ownerCredential, templateIdAndName);

			// other senders
			for (String apiKey : UserData.sourceApiKeys) {
				AccountVo senderCredential = new AccountVo();
				senderCredential.setCredentialType(AccountVo.CredentialType.API_KEY);
				senderCredential.setCredential(apiKey);
				retrieveTemplatesCallback(senderCredential, templateIdAndName);
			}
		} catch (Exception e) {
			// to do
			throw e;
		}
		return templateIdAndName;
	}

	public void retrieveTemplatesCallback(AccountVo credential, Map<String, String> tempalteIdAndName)
			throws IOException, JSONException {
		Map<String, TemplateVo> oldEnvTemplateList = UserData.oldEnvTemplates;

		JSONArray resultPage1;
		int pageNum = 1;
		do {
			String url = UserData.sourceApiUrl + "/packages?type=template&from=" + pageNum + "&to=" + (pageNum + 49);
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

				resultPage1 = sendersJSON.getJSONArray("results");

				if (resultPage1.length() == 0) {
					break; // break loop
				}

				for (int index = 0; index < resultPage1.length(); index++) {
					JSONObject templateJSON = resultPage1.getJSONObject(index);

					tempalteIdAndName.put(templateJSON.getString("id"), templateJSON.getString("name"));
					TemplateVo templateVo = new TemplateVo();
					templateVo.setIsCopy(false); // initialize
					templateVo.setOldEnvSenderEmail(templateJSON.getJSONObject("sender").getString("email"));
					templateVo.setTemplateId(templateJSON.getString("id"));
					templateVo.setContent(templateJSON);
					oldEnvTemplateList.put(templateJSON.getString("id"), templateVo);
				}
			} else {
				throw new RuntimeException("Request for template list fail!");
			}

			pageNum += 50;
		} while (resultPage1.length() == 50);
	}

	public String containsOriginalDocument(String documentName) {
		Set<String> keySet = UserData.originalDocumentMap.keySet();
		System.out.println("document name: " + documentName);
		for (String string : keySet) {
			String withoutExtension = string.substring(0, string.lastIndexOf("."));
			System.out.println("without extension: " + withoutExtension);

			if (documentName.trim().equals(withoutExtension.trim())) {
				return UserData.originalDocumentMap.get(string);
			}
		}
		return null;
	}

}
