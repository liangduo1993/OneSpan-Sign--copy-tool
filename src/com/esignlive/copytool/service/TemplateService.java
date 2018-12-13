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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
import com.esignlive.copytool.vo.DocumentVo;
import com.esignlive.copytool.vo.SenderVo;
import com.esignlive.copytool.vo.TemplateVo;

public class TemplateService {
	private static TemplateService endpointService;

	private TemplateService() {
	}

	public static TemplateService getInstance() {
		return endpointService == null ? new TemplateService() : endpointService;
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
				AccountVo newSender = UserData.newSenderMap.get(oldSenderEmail) == null ? UserData.destinationCredential
						: UserData.newSenderMap.get(oldSenderEmail);

				boolean copySuccess = false;
				try {
					String copyTemplateFromOldAccount = copyTemplateFromOldAccount(oldTemplateID, newSender);
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

	private String copyTemplateFromOldAccount(String oldTemplateID, AccountVo accountVo) throws Exception {
		String newPackageId;
		try {
			// prepare new template metadata
			JSONObject templateById = PackageService.getInstance().preparePackageMetadata(oldTemplateID, accountVo,
					"TEMPLATE");

			// download and document content and remove default consent
			List<DocumentVo> prepareDocument = PackageService.getInstance().prepareDocument(templateById,
					oldTemplateID);

			// create new template in destination env
			newPackageId = PackageService.getInstance().createPackageInNewEnv(accountVo, prepareDocument, templateById);

			// ===========copy reminders=========
			PackageService.getInstance().copyReminders(oldTemplateID, newPackageId, accountVo);

			// ==========copy document visibility========
			PackageService.getInstance().copyVisibility(oldTemplateID, newPackageId, accountVo);

		} catch (Exception e) {
			throw e;
		}
		return newPackageId;

	}

	public Map<String, String> getOldEnvTemplates() throws Exception {
		// set template list in user data
		Map<String, String> templateIdAndName = new LinkedHashMap<>();

		// old env owner
		AccountVo ownerCredential = UserData.sourceCredential;
		try {
			retrieveTemplatesCallback(ownerCredential, templateIdAndName);

			// other senders
			for (AccountVo apiKey : UserData.oldSenderMap.values()) {
				retrieveTemplatesCallback(apiKey, templateIdAndName);
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
			try {
				JSONObject sendersJSON = RestService.getInstance().doGet(url, credential);

				resultPage1 = sendersJSON.getJSONArray("results");

				if (resultPage1.length() == 0) {
					break; // break loop
				}

				for (int index = 0; index < resultPage1.length(); index++) {
					JSONObject templateJSON = resultPage1.getJSONObject(index);

					System.out.println(templateJSON.getString("id") + " : " + templateJSON.getString("name"));

					tempalteIdAndName.put(templateJSON.getString("id"),
							templateJSON.getString("name") + " (from " + credential.getSenderVo().getEmail() + ")");
					TemplateVo templateVo = new TemplateVo();
					templateVo.setIsCopy(false); // initialize
					templateVo.setOldEnvSenderEmail(templateJSON.getJSONObject("sender").getString("email"));
					templateVo.setTemplateId(templateJSON.getString("id"));
					templateVo.setContent(templateJSON);
					oldEnvTemplateList.put(templateJSON.getString("id"), templateVo);
				}
			} catch (Exception e) {
				throw e;
			}

			pageNum += 50;
		} while (resultPage1.length() == 50);
	}

}
