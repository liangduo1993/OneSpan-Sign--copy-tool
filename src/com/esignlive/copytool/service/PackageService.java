package com.esignlive.copytool.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		return RestService.getInstance().doGet(UserData.sourceApiUrl + "/packages/" + packageId, accountVo);
	}

	public void injectSenderPersonalInfo(JSONObject signerJSON, AccountVo accountVo, String key) throws JSONException {
		if (accountVo.getSenderVo().getContent().has(key)) {
			String string = accountVo.getSenderVo().getContent().getString(key);
			if (StringUtil.isEmpty(string)) {
				string = null;
			}
			signerJSON.put(key, string);
		}
	}

	public JSONObject preparePackageMetadata(String packageId, AccountVo accountVo, String packageType)
			throws Exception, JSONException {
		// get template metadata
		JSONObject templateById = getPackageById(packageId, UserData.sourceCredential);
		templateById.put("sender", new JSONObject("{\"email\":\"" + accountVo.getSenderVo().getEmail() + "\"}"));
		templateById.put("status", "DRAFT");
		templateById.put("type", packageType);
		// replace firstname,lastname,company,title for sender
		JSONArray roleArray = templateById.getJSONArray("roles");
		for (int i = 0; i < roleArray.length(); i++) {
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
				break;
			}
		}
		return templateById;
	}

	public List<DocumentVo> prepareDocument(JSONObject packageJSON, String oldPackageId) throws Exception {
		List<DocumentVo> documentVos = new ArrayList<>();

		Map<String, String> documentIds = new LinkedHashMap<>();// <id,name>
		JSONArray documentArray = packageJSON.getJSONArray("documents");
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
			String withoutExtension = entry.getKey().substring(0, entry.getKey().lastIndexOf("."));
			if (documentName.trim().equals(withoutExtension.trim())) {
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

	public String createLayoutInNewEnv(AccountVo accountVo, JSONObject packageJSON) throws Exception {
		String requestURL = UserData.destinationApiUrl + "/layouts";
		packageJSON.put("type", "LAYOUT");
		JSONObject doPost = RestService.getInstance().doPost(requestURL, accountVo, packageJSON);
		return doPost.getString("id");
	}

	public void copyReminders(String oldTemplateID, String newPackageId, AccountVo accountVo)
			throws Exception, JSONException {
		JSONObject reminder = RestService.getInstance()
				.doGet(UserData.sourceApiUrl + "/packages/" + oldTemplateID + "/reminders", UserData.sourceCredential);
		// replace packageId
		reminder.put("packageId", newPackageId);
		RestService.getInstance().doPost(UserData.destinationApiUrl + "/packages/" + newPackageId + "/reminders",
				accountVo, reminder);
	}

	public void copyVisibility(String oldTemplateID, String newPackageId, AccountVo accountVo)
			throws Exception, JSONException {
		JSONObject visibility = RestService.getInstance().doGet(
				UserData.sourceApiUrl + "/packages/" + oldTemplateID + "/documents/visibility",
				UserData.sourceCredential);
		RestService.getInstance().doPost(
				UserData.destinationApiUrl + "/packages/" + newPackageId + "/documents/visibility", accountVo,
				visibility);
	}

}
