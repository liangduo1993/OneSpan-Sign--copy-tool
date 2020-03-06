package com.esignlive.copytool.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.StringUtil;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.DocumentVo;

public class PackageService {
	private static PackageService endpointService;

	private PackageService() {
	}

	public static PackageService getInstance() {
		return endpointService == null ? new PackageService() : endpointService;
	}

	public JSONObject getPackageById(String packageId, AccountVo accountVo) throws IOException, JSONException {
		System.out.println("API URL: " + UserData.sourceApiUrl + "/packages/" + packageId);
		return RestService.getInstance().doGet(UserData.sourceApiUrl + "/packages/" + packageId, accountVo);
	}

	public void injectSenderPersonalInfo(JSONObject signerJSON, AccountVo accountVo, String key) throws JSONException {
		if (accountVo.getSenderVo().getContent().containsKey(key)) {
			Object string = accountVo.getSenderVo().getContent().get(key);
			System.out.println(key + " : " + (string != null ? string : ""));
			if (string == null || StringUtil.isEmpty((String) string)) {
				signerJSON.put(key, "");
				System.out.println(key + " is null");
			} else {
				signerJSON.put(key, string);
			}
		}
	}

	public JSONObject preparePackageMetadata(String packageId, AccountVo accountVo, String packageType)
			throws Exception{
		try {
			// get template metadata
			JSONObject newPackage = getPackageById(packageId, UserData.sourceCredential);
			
			System.out.println("template metadata: " + newPackage.toString());
			
			newPackage.put("sender", JSON.parseObject("{\"email\":\"" + accountVo.getSenderVo().getEmail() + "\"}"));
			newPackage.put("status", "DRAFT");
			newPackage.put("due", null);
			newPackage.remove("id");
			newPackage.put("type", packageType);
			// replace firstname,lastname,company,title for sender
			JSONArray roleArray = newPackage.getJSONArray("roles");
			for (int i = 0; i < roleArray.size(); i++) {
				JSONObject roleJSON = roleArray.getJSONObject(i);
	
				String signerType = roleJSON.getString("type");
				if (signerType.equals("SENDER")) {
					JSONObject signerJSON = roleJSON.getJSONArray("signers").getJSONObject(0);
	
					List<String> keys = Arrays.asList("firstName", "lastName", "email", "company", "title");
					for (String key : keys) {
						try {
							PackageService.getInstance().injectSenderPersonalInfo(signerJSON, accountVo, key);
						} catch (Exception e) {
							// to do
							// add error msg
							// continue
						}
					}
					System.out.println("========");
					System.out.println(accountVo.getSenderVo().getContent());
					System.out.println(signerJSON.toString());
					System.out.println("========");
					break;
				}
			}
			
			//remove external attribute for documents
			JSONArray documentArray = newPackage.getJSONArray("documents");
			for (int i = 0; i < documentArray.size(); i++) {
				JSONObject documentJSON = documentArray.getJSONObject(i);
				documentJSON.put("external", null);
			}
				
			return newPackage;
		}catch(Exception e) {
			throw new RuntimeException("Fail to prepare Package Metadata!");
		}
	}

	public List<DocumentVo> prepareDocument(JSONObject packageJSON, String oldPackageId) throws Exception {
		List<DocumentVo> documentVos = new ArrayList<>();

		Map<String, String> documentIds = new LinkedHashMap<>();// <id,name>
		JSONArray documentArray = packageJSON.getJSONArray("documents");
		String consent = packageJSON.getString("consent");
		boolean checkConsent = !StringUtil.isEmpty(consent) && consent.equals("default-consent");
		
		int defaultIndex = -1;
		for (int i = 0; i < documentArray.size(); i++) {
			JSONObject documentJSON = documentArray.getJSONObject(i);
			String docId = documentJSON.getString("id");
			String docName = documentJSON.getString("name");
			if (!docId.equals("default-consent")) {
				documentIds.put(docId, docName);
			} else if(checkConsent){
				defaultIndex = i;
			}
		}
		if (defaultIndex >= 0) {
			documentArray.remove(defaultIndex);
		}

		for (Map.Entry<String, String> entry : documentIds.entrySet()) {
			boolean isOriginal = false;
			String id = entry.getKey();
			String name = entry.getValue();
			byte[] byteArray;
			Map.Entry<String, String> docEntry;
			if ((docEntry = getOriginalDocumentName(name)) == null) {
				String url = UserData.sourceApiUrl + "/packages/" + oldPackageId + "/documents/" + id + "/pdf";
				byteArray = RestService.getInstance().doGetByteArray(url, UserData.sourceCredential);
			} else {
				System.out.println(docEntry.getKey() + " : " + docEntry.getValue());
				byteArray = IOUtils.toByteArray(new FileInputStream(docEntry.getValue()));
				name = docEntry.getKey();
				isOriginal = true;
			}

			DocumentVo documentVo = new DocumentVo();
			documentVo.setContent(byteArray);
			documentVo.setId(id);
			documentVo.setName(name);
			documentVo.setOriginal(isOriginal);
			documentVos.add(documentVo);
		}
		return documentVos;
	}

	public Map.Entry<String, String> getOriginalDocumentName(String documentName) {
		for (Map.Entry<String, String> entry : UserData.originalDocumentMap.entrySet()) {
			String withoutExtension = "";
			try {
				withoutExtension = entry.getKey().substring(0, entry.getKey().lastIndexOf("."));
				if (documentName.trim().equals(withoutExtension.trim())) {
					return entry;
				}
			} catch (Exception e) {
				// to do
			}

		}

		// if still can't find original document, try finding documentName with
		// extension
		for (Map.Entry<String, String> entry : UserData.originalDocumentMap.entrySet()) {
			if (documentName.trim().equals(entry.getKey().trim())) {
				return entry;
			}
		}

		return null;
	}

	public String createPackageInNewEnv(AccountVo accountVo, List<DocumentVo> prepareDocument, JSONObject packageJSON)
			throws Exception {
		String requestURL = UserData.destinationApiUrl + "/packages";
		JSONObject doPostMultipart = RestService.getInstance().doPostMultipart(requestURL, accountVo, packageJSON,
				prepareDocument);
		return doPostMultipart.getString("id");
	}

	public String createLayoutInNewEnv(AccountVo accountVo, String packageId, String documentId, JSONObject layoutJSON)
			throws Exception {
		String requestURL = UserData.destinationApiUrl + "/layouts";
		String payload = "{\r\n" + "  \"type\": \"LAYOUT\",\r\n" + "  \"description\": \""
				+ layoutJSON.getString("description") + "\",\r\n" + "  \"name\": \"" + layoutJSON.getString("name")
				+ "\",\r\n" + "  \"visibility\": \"" + layoutJSON.getString("visibility") + "\",\r\n" + "  \"id\": \""
				+ packageId + "\",\r\n" + "  \"documents\": [\r\n" + "    {\r\n" + "      \"id\": \"" + documentId
				+ "\"\r\n" + "    }\r\n" + "  ]\r\n" + "}";
		JSONObject payloadJSON = JSON.parseObject(payload);
		System.out.println(payload);
		JSONObject doPost = RestService.getInstance().doPost(requestURL, accountVo, payloadJSON);
		return doPost.getString("id");
	}

	public void copyReminders(String oldTemplateID, String newPackageId, AccountVo accountVo)
			throws Exception, JSONException {
		JSONObject reminder = RestService.getInstance()
				.doGet(UserData.sourceApiUrl + "/packages/" + oldTemplateID + "/reminders", UserData.sourceCredential);
		// replace packageId
		if (reminder != null) {
			reminder.put("packageId", newPackageId);
			RestService.getInstance().doPost(UserData.destinationApiUrl + "/packages/" + newPackageId + "/reminders",
					accountVo, reminder);
		}
	}

	public void copyVisibility(String oldTemplateID, String newPackageId, AccountVo accountVo)
			throws Exception, JSONException {
		JSONObject visibility = RestService.getInstance().doGet(
				UserData.sourceApiUrl + "/packages/" + oldTemplateID + "/documents/visibility",
				UserData.sourceCredential);
		if (visibility != null) {
			RestService.getInstance().doPost(
					UserData.destinationApiUrl + "/packages/" + newPackageId + "/documents/visibility", accountVo,
					visibility);
		}
	}

}
