package com.esignlive.copytool.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.view.Process4;
import com.esignlive.copytool.vo.LayoutVo;
import com.esignlive.copytool.vo.TemplateVo;
import com.silanis.esl.api.model.Package;
import com.silanis.esl.sdk.Direction;
import com.silanis.esl.sdk.Document;
import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PackageId;
import com.silanis.esl.sdk.PageRequest;
import com.silanis.esl.sdk.Sender;

public class LayoutService {
	private static LayoutService endpointService;

	private LayoutService() {
	}

	public static LayoutService getInstance() {
		return endpointService == null ? new LayoutService() : endpointService;
	}

	private String copyLayoutsFromOldAccount(EslClient sourceClient, EslClient destClient, String oldTemplateID,
			String newSenderEmail) {

		Package apiPackage = sourceClient.getPackageService().getApiPackage(oldTemplateID);

		com.silanis.esl.api.model.Sender newSender = new com.silanis.esl.api.model.Sender();
		newSender.setEmail(newSenderEmail);
		apiPackage.setSender(newSender);
		apiPackage.setType("PACKAGE");

		List<com.silanis.esl.api.model.Document> documents = apiPackage.getDocuments();
		com.silanis.esl.api.model.Document defaultConsent = null;
		for (com.silanis.esl.api.model.Document document : documents) {
			if (document.getId().equals("default-consent")) {
				defaultConsent = document;
			}
		}
		if (defaultConsent != null) {
			documents.remove(defaultConsent);
		}

		// download and inject document content byte[]
		DocumentPackage package1 = sourceClient.getPackage(new PackageId(oldTemplateID));
		Document defaultDoc = null;
		for (Document doc : package1.getDocuments()) {
			if (!doc.getId().toString().equals("default-consent")) {
				doc.setFileName(doc.getName() + ".pdf");
				doc.setContent(sourceClient.downloadDocument(new PackageId(oldTemplateID), doc.getId().getId()));
			} else {
				defaultDoc = doc;
			}
		}
		if (defaultDoc != null) {
			package1.getDocuments().remove(defaultDoc);
		}

		PackageId createPackageOneStep = destClient.getPackageService().createPackageOneStep(apiPackage,
				package1.getDocuments());
		package1.setId(createPackageOneStep);
		System.out.println(createPackageOneStep);
		// create new layout in destination env
		String createLayout = null;
		try {
			createLayout = destClient.getLayoutService().createLayout(package1);
		} catch (Exception e) {
			throw e;
		} finally {
			// destClient.getPackageService().deletePackage(createPackageOneStep);
		}
		return createLayout;
	}

	/**
	 * 
	 * @param String
	 *            upload original documents //to do
	 * @param String
	 *            specify partial template IDs //to do
	 */
	public Map<String, Boolean> copyLayouts(String layoutCSV, Process4 view) {
		Map<String, Boolean> result = new LinkedHashMap<>();

		// build error msg
		StringBuilder errorMsg = new StringBuilder(200);

		// deal with partial layout ids

		// deal with copy layout
		EslClient sourceClient = UserData.sourceEslClient;

		for (LayoutVo layoutVo : UserData.oldEnvLayouts.values()) {
			if (layoutVo.getIsCopy()) {
				EslClient destClient = UserData.destinationEslClient;

				String oldLayoutID = layoutVo.getLayoutId();
				String oldSenderEmail = layoutVo.getOldEnvSenderEmail();
				Sender sender = UserData.oldAndNewSenderMap.get(oldSenderEmail);
				String newSenderEmail = null;
				if (sender == null) {
					newSenderEmail = UserData.destinationOwnerEmail;
				} else {
					newSenderEmail = sender.getEmail();
					destClient = new EslClient(UserData.destinationApiKeys.get(newSenderEmail),
							UserData.destinationApiUrl);
				}
				System.out.println(newSenderEmail);

				boolean copySuccess = false;
				try {
					String copyLayoutsFromOldAccount = copyLayoutsFromOldAccount(sourceClient, destClient, oldLayoutID,
							newSenderEmail);
					System.out.println(copyLayoutsFromOldAccount);
					copySuccess = true;
				} catch (Exception e) {
					// to do
					// add error msg
					e.printStackTrace();
					errorMsg.append(e.getMessage()).append("\n");
				}
				// update view
				// to do
				view.setCopyStatus(oldLayoutID, copySuccess);
				result.put(oldLayoutID, copySuccess);
			}
		}
		view.setErrorMsg(errorMsg.toString());
		System.out.println(errorMsg.toString());
		return result;
	}

	// <layout id, layout name>
	public Map<String, String> getOldEnvLayouts() {
		// set layout list in user data
		Map<String, String> layoutIdAndName = new LinkedHashMap<>();

		// retrieve all api keys
		EslClient ownerClient = new EslClient(UserData.sourceApiKey, UserData.sourceApiUrl);
		retrieveAllLayoutsCallback(ownerClient, layoutIdAndName);

		// loop through api list and get all layouts
		for (String apiKey : UserData.sourceApiKeys) {
			EslClient tempClient = new EslClient(apiKey, UserData.sourceApiUrl);
			retrieveAllLayoutsCallback(tempClient, layoutIdAndName);
		}
		return layoutIdAndName;
	}

	public void retrieveAllLayoutsCallback(EslClient tempClient, Map<String, String> layoutIdAndName) {
		Map<String, LayoutVo> oldEnvLayouts = UserData.oldEnvLayouts;

		List<DocumentPackage> resultPage1;
		int pageNum = 1;
		resultPage1 = tempClient.getLayoutService().getLayouts(Direction.ASCENDING, new PageRequest(pageNum, 50));
		while (!resultPage1.isEmpty()) {
			for (DocumentPackage documentPackage : resultPage1) {
				layoutIdAndName.put(documentPackage.getId().getId(), documentPackage.getName());
				LayoutVo layoutVo = new LayoutVo();
				layoutVo.setIsCopy(false); // initialize
				layoutVo.setOldEnvSenderEmail(documentPackage.getSenderInfo().getEmail());
				layoutVo.setLayoutId(documentPackage.getId().getId());
				oldEnvLayouts.put(documentPackage.getId().getId(), layoutVo);
			}
			pageNum += 50;
			resultPage1 = tempClient.getLayoutService().getLayouts(Direction.ASCENDING, new PageRequest(pageNum, 50));
		}
	}

}
