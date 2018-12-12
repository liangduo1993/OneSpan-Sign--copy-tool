package com.esignlive.copytool.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.HttpURLConnectionUtil;
import com.esignlive.copytool.view.Process4;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.SenderVo;

public class LayoutService {
	private static LayoutService endpointService;

	private LayoutService() {
	}

	public static LayoutService getInstance() {
		return endpointService == null ? new LayoutService() : endpointService;
	}

	private JSONObject getLayoutById(String LayoutId, AccountVo accountVo) throws IOException, JSONException {
		String url = UserData.sourceApiUrl + "/packages/" + LayoutId;
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

			JSONObject LayoutJSON = new JSONObject(response.toString());
			return LayoutJSON;
		} else {
			throw new RuntimeException("Request for Layout list fail!");
		}

	}

	private JSONObject createPackageFromOldEnvLayout(String oldLayoutID, String newSenderEmail) throws Exception {
		try {
			// get Layout metadata
			JSONObject layoutById = getLayoutById(oldLayoutID, UserData.sourceCredential);
			layoutById.put("sender", new JSONObject("{\"email\":\"" + newSenderEmail + "\"}"));
			layoutById.put("type", "PACKAGE");

			// download and document content and remove default consent
			Map<String, String> documentIds = new LinkedHashMap<>();// <id,name>

			JSONArray documentArray = layoutById.getJSONArray("documents");
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
			Map<String, byte[]> docContent = new LinkedHashMap<>();
			for (Map.Entry<String, String> entry : documentIds.entrySet()) {
				String id = entry.getKey();
				// download document
				URL client = new URL(UserData.sourceApiUrl + "/packages/" + oldLayoutID + "/documents/" + id + "/pdf");
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

				docContent.put(documentIds.get(id), buffer.toByteArray());
			}
			System.out.println(layoutById);

			// create new Layout in destination env
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
				writer.append(CRLF).append(layoutById.toString()).append(CRLF).flush();

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
				JSONObject packageJSON = new JSONObject(response.toString());
				layoutById.put("id", packageJSON.getString("id"));
				return layoutById;
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

	private String copyLayoutFromOldAccount(String oldLayoutID, String newSenderEmail) throws Exception {
		JSONObject packageJSON = null;
		JSONObject layoutJSON = null;
		try {
			// #step1. create a package with layout
			packageJSON = createPackageFromOldEnvLayout(oldLayoutID, newSenderEmail);
			// #step2. create layout from package
			layoutJSON = createLayoutFromPackage(packageJSON);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// #step3. delete package
			if (packageJSON != null && packageJSON.getString("id") != null) {
				deletePackage(packageJSON.getString("id"));
			}
		}
		return layoutJSON.getString("id");
	}

	private void deletePackage(String packageId) throws IOException {
		URL sourceClient = new URL(UserData.destinationApiUrl + "/packages/"+packageId);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, UserData.destinationCredential);
		sourceConn.setRequestProperty("Accept", "application/json");
		sourceConn.setRequestMethod("DELETE");

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

		if (sourceResponseCode != 200) {
			throw new RuntimeException(response.toString());
		}
	}

	private JSONObject createLayoutFromPackage(JSONObject packageJSON) throws IOException, JSONException {
		URL sourceClient = new URL(UserData.destinationApiUrl + "/layouts");
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Content-Type", "application/json");
		HttpURLConnectionUtil.addCredential(sourceConn, UserData.destinationCredential);
		sourceConn.setRequestProperty("Accept", "application/json");
		sourceConn.setRequestMethod("POST");
		sourceConn.setDoOutput(true);
		sourceConn.setDoInput(true);
		
		System.out.println(packageJSON.toString());
		StringBuilder payload = new StringBuilder(400);
		payload.append("{\"id\":\"").append(packageJSON.getString("id")).append("\",\"documents\":[");
		for (int i = 0; i < packageJSON.getJSONArray("documents").length(); i++) {
			payload.append("{\"id\":\"").append(packageJSON.getJSONArray("documents").getJSONObject(i).getString("id"))
					.append("\"},");
		}
		payload.deleteCharAt(payload.length() - 1);
		payload.append("]}");
		System.out.println(payload.toString());
		
		OutputStream os = sourceConn.getOutputStream();
		os.write(payload.toString().getBytes());
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
			return json;
		} else {
			throw new RuntimeException(response.toString());
		}
	}

	/**
	 * 
	 * @param String
	 *            upload original documents //to do
	 * @param String
	 *            specify partial Layout IDs //to do
	 */
	public Map<String, Boolean> copyLayout(Process4 view) {
		Map<String, Boolean> result = new LinkedHashMap<>();

		// build error msg
		StringBuilder errorMsg = new StringBuilder(200);

		// deal with copy Layout
		for (LayoutVo LayoutVo : UserData.oldEnvLayouts.values()) {
			if (LayoutVo.getIsCopy()) {
				String oldLayoutID = LayoutVo.getLayoutId();
				String oldSenderEmail = LayoutVo.getOldEnvSenderEmail();
				SenderVo sender = UserData.oldAndNewSenderMap.get(oldSenderEmail);
				String newSenderEmail = null;
				if (sender == null) {
					newSenderEmail = UserData.destinationCredential.getSenderVo().getEmail();
				} else {
					newSenderEmail = sender.getEmail();
				}
				System.out.println(newSenderEmail);

				boolean copySuccess = false;
				try {
					String copyLayoutFromOldAccount = copyLayoutFromOldAccount(oldLayoutID, newSenderEmail);
					System.out.println(copyLayoutFromOldAccount);
					copySuccess = true;
				} catch (Exception e) {
					// to do
					// add error msg
					System.out.println(e.getMessage());
					errorMsg.append(e.getMessage()).append("\n");
				}
				// update view
				// to do
				view.setCopyStatus(oldLayoutID, copySuccess);
				result.put(oldLayoutID, copySuccess);
			}
		}
		view.setErrorMsg(errorMsg.toString());
		return result;
	}

	public Map<String, String> getOldEnvLayouts() throws Exception {
		// set Layout list in user data
		Map<String, String> LayoutIdAndName = new LinkedHashMap<>();

		// old env owner
		AccountVo ownerCredential = UserData.sourceCredential;
		try {
			retrieveLayoutsCallback(ownerCredential, LayoutIdAndName);

			// other senders
			for (AccountVo apiKey : UserData.sourceApiKeys) {
				retrieveLayoutsCallback(apiKey, LayoutIdAndName);
			}
		} catch (Exception e) {
			// to do
			throw e;
		}
		return LayoutIdAndName;
	}

	public void retrieveLayoutsCallback(AccountVo credential, Map<String, String> layoutIdAndName)
			throws IOException, JSONException {
		Map<String, LayoutVo> oldEnvLayoutList = UserData.oldEnvLayouts;

		JSONArray resultPage1;
		int pageNum = 1;
		do {
			String url = UserData.sourceApiUrl + "layouts?from=" + pageNum + "&to=" + (pageNum + 49);
			URL client = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) client.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			HttpURLConnectionUtil.addCredential(conn, credential);
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
					JSONObject LayoutJSON = resultPage1.getJSONObject(index);

					layoutIdAndName.put(LayoutJSON.getString("id"), LayoutJSON.getString("name") + " (from "+ credential.getSenderVo().getEmail()+")");
					LayoutVo LayoutVo = new LayoutVo();
					LayoutVo.setIsCopy(false); // initialize
					LayoutVo.setOldEnvSenderEmail(LayoutJSON.getJSONObject("sender").getString("email"));
					LayoutVo.setLayoutId(LayoutJSON.getString("id"));
					LayoutVo.setContent(LayoutJSON);
					oldEnvLayoutList.put(LayoutJSON.getString("id"), LayoutVo);
				}
			} else {
				throw new RuntimeException("Request for Layout list fail!");
			}
			pageNum += 50;
		} while (resultPage1.length() == 50);
	}

}
