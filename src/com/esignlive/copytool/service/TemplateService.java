package com.esignlive.copytool.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.view.Process3;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.AccountVo.SenderStatus;
import com.esignlive.copytool.vo.DocumentVo;
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
					String copyTemplateFromOldAccount = copyTemplateFromOldAccount(oldTemplateID, newSender, view);

					copySuccess = true;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
					errorMsg.append(oldTemplateID + " : " + e.getMessage()).append("\n");
				}
				view.setCopyStatus(oldTemplateID, copySuccess);
				result.put(oldTemplateID, copySuccess);
			}
		}
		view.setErrorMsg(errorMsg.toString());
		return result;
	}

	private String copyTemplateFromOldAccount(String oldTemplateID, AccountVo accountVo, Process3 view) throws Exception {
		String newPackageId;
		try {
			// prepare new template metadata
			JSONObject templateById = PackageService.getInstance().preparePackageMetadata(oldTemplateID, accountVo,
					"TEMPLATE");
			//view.setErrorMsg("received: "+templateById.toJSONString());
			// download and document content and remove default consent
			List<DocumentVo> prepareDocument = PackageService.getInstance().prepareDocument(templateById,
					oldTemplateID);
			
			// create new template in destination env
			newPackageId = PackageService.getInstance().createPackageInNewEnv(accountVo, prepareDocument, templateById);
			System.out.println("new package id: " + newPackageId);
			// ===========copy reminders=========
			try {
				PackageService.getInstance().copyReminders(oldTemplateID, newPackageId, accountVo);
				System.out.println("copy reminder successfully!");
				// ==========copy document visibility========
				PackageService.getInstance().copyVisibility(oldTemplateID, newPackageId, accountVo);
				System.out.println("copy visibility successfully!");
			} catch (Exception ex) {
				// to do
				throw new RuntimeException("Copy Reminder or Visibility fail for template: " + oldTemplateID);
			}
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
				if (apiKey.getSenderStatus() == SenderStatus.ACTIVE) {
					retrieveTemplatesCallback(apiKey, templateIdAndName);
				}
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
		System.out.println("Currently checking for sender: " + credential.getSenderVo().getEmail() + " : "
				+ credential.getCredential());
		JSONArray resultPage1;
		int pageNum = 1;
		do {
			String url = UserData.sourceApiUrl + "/packages?type=template&from=" + pageNum + "&to=" + (pageNum + UserData.pageSize - 1);
			try {
				JSONObject sendersJSON = RestService.getInstance().doGet(url, credential);

				resultPage1 = sendersJSON.getJSONArray("results");

				if (resultPage1.size() == 0) {
					break; // break loop
				}

				for (int index = 0; index < resultPage1.size(); index++) {
					JSONObject templateJSON = resultPage1.getJSONObject(index);

					if (templateJSON.getJSONObject("sender").getString("email")
							.equals(credential.getSenderVo().getEmail())) {
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
				}
			} catch (Exception e) {
				throw e;
			}

			pageNum += UserData.pageSize;
		} while (resultPage1.size() == UserData.pageSize);
	}

}
