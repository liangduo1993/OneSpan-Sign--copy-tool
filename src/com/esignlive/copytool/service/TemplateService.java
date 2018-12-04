package com.esignlive.copytool.service;

import java.util.LinkedHashMap;
import java.util.Map;

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

	private String copyTemplateFromOldAccount(EslClient sourceClient, EslClient destClient, String oldTemplateID,
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
		Document defaultDoc = null;
		for (Document doc : package1.getDocuments()) {
			if(!doc.getId().toString().equals("default-consent")) {
				doc.setFileName(doc.getName() + ".pdf");
				doc.setContent(sourceClient.downloadDocument(new PackageId(oldTemplateID), doc.getId().getId()));
			}else {
				defaultDoc= doc;
			}
		}
		if(defaultDoc != null) {
			package1.getDocuments().remove(defaultDoc);
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

		//old env owner
		EslClient ownerClient = new EslClient(UserData.sourceApiKey, UserData.sourceApiUrl);
		retrieveTemplatesCallback(ownerClient,tempalteIdAndName,oldEnvTemplates);
		
		// other senders
		for (String apiKey : UserData.sourceApiKeys) {
			EslClient tempClient = new EslClient(apiKey, UserData.sourceApiUrl);
			retrieveTemplatesCallback(tempClient,tempalteIdAndName,oldEnvTemplates);
		}
		return tempalteIdAndName;
	}


	public void retrieveTemplatesCallback(EslClient ownerClient,Map<String, String> tempalteIdAndName,Map<String, String> oldEnvTemplates) {
		Page<DocumentPackage> resultPage1;
		int pageNum = 1;
		do {
			resultPage1 = ownerClient.getPackageService().getTemplates(new PageRequest(pageNum, 50));
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
	

}
