package com.esignlive.copytool.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.utils.SenderUtil;
import com.esignlive.copytool.view.Process2;
import com.silanis.esl.sdk.Direction;
import com.silanis.esl.sdk.EslClient;
import com.silanis.esl.sdk.PageRequest;
import com.silanis.esl.sdk.Sender;
import com.silanis.esl.sdk.SenderType;

public class SenderService {
	private static SenderService endpointService;

	private SenderService() {
	}

	public static SenderService getInstance() {
		return endpointService == null ? new SenderService() : endpointService;
	}

	public List<String> getOldEnvSenders() {
		// get list
		EslClient sourceEslClient = UserData.sourceEslClient;
		int i = 1;

		Map<String, Sender> allMembers = new LinkedHashMap<>();
		Map<String, Sender> accountMembers = sourceEslClient.getAccountService().getSenders(Direction.ASCENDING,
				new PageRequest(i, 5));

		while (!accountMembers.isEmpty()) {
			for (Map.Entry<String, Sender> entry : accountMembers.entrySet()) {
				System.out.println(
						entry.getKey() + " / " + entry.getValue().getId() + " / " + entry.getValue().getType());
				if (entry.getValue().getType().equals(SenderType.REGULAR)) {
					allMembers.put(entry.getKey(), entry.getValue());
				}
				i++;
			}
			accountMembers = sourceEslClient.getAccountService().getSenders(Direction.ASCENDING, new PageRequest(i, 5));
		}

		// set user data
		UserData.oldSenderList = allMembers;
		return new ArrayList<String>(allMembers.keySet());
	}

	public String getOwnerEmail(EslClient eslClient) {
		int i = 1;

		Map<String, Sender> accountMembers = eslClient.getAccountService().getSenders(Direction.ASCENDING,
				new PageRequest(i, 5));

		while (!accountMembers.isEmpty()) {
			for (Map.Entry<String, Sender> entry : accountMembers.entrySet()) {

				if (entry.getValue().getType().equals(SenderType.MANAGER)) {
					return entry.getValue().getEmail();
				}
				i++;
			}
			accountMembers = eslClient.getAccountService().getSenders(Direction.ASCENDING, new PageRequest(i, 5));
		}

		throw new RuntimeException("Can't find owner email! ");
	}

	public Map<JLabel, Boolean> inviteSenders(Map<JLabel, String> senderEmails, Process2 view) {
		// try invite senders
		EslClient destinationEslClient = UserData.destinationEslClient;
		Map<JLabel, Boolean> result = new LinkedHashMap<>();
		Map<String, Sender> oldAndNewSenderMap = new LinkedHashMap<>();

		StringBuilder errorMsg = new StringBuilder(200);
//		errorMsg.append("<html>");

		for (JLabel oldSenderEmail : senderEmails.keySet()) {
			String newSenderEmail = senderEmails.get(oldSenderEmail);
			if (newSenderEmail != null && !newSenderEmail.trim().equals("")) {	//new sender email is not null
				Sender sender = UserData.oldSenderList.get(oldSenderEmail.getText());
				sender.setEmail(newSenderEmail);
				try {
					Sender inviteUser = destinationEslClient.getAccountService()
							.inviteUser(SenderUtil.toAccontMember(sender));
					result.put(oldSenderEmail, true);
					oldAndNewSenderMap.put(oldSenderEmail.getText(), inviteUser);
				} catch (Exception e) {
//					errorMsg.append(e.getMessage()).append("<br/>");
					errorMsg.append(e.getMessage()).append("\n");
					result.put(oldSenderEmail, false);
				}
			}else {																//new sender email is null
				//we consider invite successfully
				result.put(oldSenderEmail, true);
				//no mapping old to new sender
				oldAndNewSenderMap.put(oldSenderEmail.getText(), null);
			}
			
			view.setInvitationStatus(oldSenderEmail, result.get(oldSenderEmail));
			
		}

		// return error msg
//		errorMsg.append("</html>");
		view.setErrorMsg(errorMsg.toString());

		// set user data
		UserData.oldAndNewSenderMap = oldAndNewSenderMap;
		
		return result;
	}
	
	public void setNewEnvOwnerEmail() {
		//set new env owner email
		UserData.destinationOwnerEmail = getOwnerEmail(UserData.destinationEslClient);
		System.out.println("set dest owenr email: "  +UserData.destinationOwnerEmail);
		
		//set old env sender emails
		if(UserData.oldSenderList == null || UserData.oldSenderList.size() == 0) {
			getOldEnvSenders();
		}
	}

}
