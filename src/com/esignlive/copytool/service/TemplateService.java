package com.esignlive.copytool.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.view.Process3;
import com.silanis.esl.sdk.Document;
import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;
import com.silanis.esl.sdk.Page;
import com.silanis.esl.sdk.PageRequest;
import com.silanis.esl.sdk.Sender;
import com.silanis.esl.sdk.Signer;
import com.silanis.esl.sdk.builder.SenderInfoBuilder;

public class TemplateService {
	private static TemplateService endpointService;

	private TemplateService() {
	}

	public static TemplateService getInstance() {
		return endpointService == null ? new TemplateService() : endpointService;
	}

	public String copyTemplateFromOldAccount(EslClient sourceClient, EslClient destClient, String oldTemplateID,
			String newSenderEmail) {
		DocumentPackage package1 = sourceClient.getPackage(new PackageId(oldTemplateID));

		// remove sender from roles attribute
		Signer sender = null;
		for (Signer signer : package1.getSigners()) {
			if (signer.getEmail().equals(package1.getSenderInfo().getEmail())) {
				sender = signer;
			}
		}
		package1.getSigners().remove(sender);

		// download and inject document content byte[]
		for (Document doc : package1.getDocuments()) {
			doc.setFileName(doc.getName() + ".pdf");
			doc.setContent(sourceClient.downloadDocument(new PackageId(oldTemplateID), doc.getId().getId()));
		}

		// set new sender
		package1.setSenderInfo(SenderInfoBuilder.newSenderInfo(newSenderEmail).build());

		// create new template in destination env
		PackageId createTemplate = destClient.getTemplateService().createTemplate(package1);
		return createTemplate.getId();
	}

	/**
	 * 
	 * @param String
	 *            upload original documents //to do
	 * @param String
	 *            specify partial template IDs //to do
	 */
	public Map<String,Boolean> copyTemplate(String originalDocumentsFolders, String templateIdCsv, Process3 view) {
		Map<String, Boolean> result = new LinkedHashMap<>();
		
		// build error msg
		StringBuilder errorMsg = new StringBuilder(200);
//		errorMsg.append("<html>");
		
		
		
		
		// deal with original documents
		
		// deal with partial template ids

		// deal with copy template
		EslClient sourceClient = UserData.sourceEslClient;
		EslClient destClient = UserData.destinationEslClient;

		// Page<DocumentPackage> resultPage1;
		// int pageNum = 1;
		// do {
		// resultPage1 = sourceClient.getPackageService().getTemplates(new
		// PageRequest(pageNum, 50));
		// if (resultPage1.getSize() > 0) {
		for (String templateId : UserData.oldEnvTemplates.keySet()) {

			String oldTemplateID = templateId;
			String oldSenderEmail = UserData.oldEnvTemplates.get(oldTemplateID);
			Sender sender = UserData.oldAndNewSenderMap.get(oldSenderEmail);
			String newSenderEmail = null;
			if (sender == null) {
				newSenderEmail = UserData.destinationOwnerEmail;
			} else {
				newSenderEmail = sender.getEmail();
			}
			System.out.println(newSenderEmail);

			boolean copySuccess = false;
			try {
				String copyTemplateFromOldAccount = copyTemplateFromOldAccount(sourceClient, destClient, oldTemplateID,
						newSenderEmail);
				System.out.println(copyTemplateFromOldAccount);
				copySuccess = true;
			} catch (Exception e) {
				// to do
				// add error msg
				errorMsg.append(e.getMessage()).append("\n");
			}
			// update view
			// to do
			view.setCopyStatus(oldTemplateID, copySuccess);
			result.put(oldTemplateID, copySuccess);
		}
		// }
		// pageNum += 50;
		// } while (resultPage1.hasNextPage());

		// return error msg
//		errorMsg.append("</html>");
		view.setErrorMsg(errorMsg.toString());
		return result;
	}

	public Map<String, String> getOldEnvTemplates() {
		// set template list in user data
		Map<String, String> tempalteIdAndName = new LinkedHashMap<>();
		Map<String, String> oldEnvTemplates = UserData.oldEnvTemplates;

		List<String> apiList = new ArrayList<>();
		apiList.add(UserData.sourceApiKey);
		Map<String, Sender> oldSenderList = UserData.oldSenderList;
		System.out.println("sender list: " + oldSenderList.values().size());
		
		for (Sender sender : oldSenderList.values()) {
			String apiKey;
			try {
				apiKey = getApiKey(sender.getId());
				apiList.add(apiKey);
			} catch (Exception e) {
				e.printStackTrace();
				//to do
				
				
			} 
		}

		for (String apiKey : apiList) {
			EslClient tempClient = new EslClient(apiKey, UserData.sourceApiUrl);
			Page<DocumentPackage> resultPage1;
			int pageNum = 1;
			do {
				resultPage1 = tempClient.getPackageService().getTemplates(new PageRequest(pageNum, 50));
				if (resultPage1.getSize() > 0) {
					for (DocumentPackage documentPackage : resultPage1) {
						tempalteIdAndName.put(documentPackage.getId().getId(), documentPackage.getName());
						oldEnvTemplates.put(documentPackage.getId().getId(),
								documentPackage.getSenderInfo().getEmail());
					}
				}
				pageNum += 50;
			} while (resultPage1.hasNextPage());

		}
		return tempalteIdAndName;
	}

	public String getApiKey(String senderId) throws IOException, JSONException {
		URL client = new URL(UserData.sourceApiUrl+"/account/senders/"+senderId+"/apiKey");
		HttpURLConnection conn = (HttpURLConnection) client.openConnection();
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + UserData.sourceApiKey);
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
