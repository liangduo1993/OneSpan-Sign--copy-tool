package com.esignlive.copytool.service;

import java.awt.event.ActionListener;
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

	private SenderService()  {
	}

	public static SenderService getInstance()  {
		return endpointService == null ? new SenderService() : endpointService;
	}

	public List<String> getOldEnvSenders() {
		// get list
		EslClient sourceEslClient = UserData.sourceEslClient;
		int i = 1;

		Map<String, Sender> allMembers = new LinkedHashMap<>();
		Map<String, Sender> accountMembers = sourceEslClient.getAccountService().getSenders(Direction.ASCENDING,
				new PageRequest(i, 5));
		allMembers.putAll(accountMembers);
		while (!accountMembers.isEmpty()) {
			for (Map.Entry<String, Sender> entry : accountMembers.entrySet()) {
				System.out.println(
						entry.getKey() + " / " + entry.getValue().getId() + " / " + entry.getValue().getType());
				if (entry.getValue().getType() == SenderType.REGULAR) {
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

	public Map<JLabel, Boolean> inviteSenders(Map<JLabel, String> senderEmails, Process2 view) {
		// try invite senders
		EslClient destinationEslClient = UserData.destinationEslClient;
		Map<JLabel, Boolean> result = new LinkedHashMap<>();
		Map<String, Sender> oldAndNewSenderMap = new LinkedHashMap<>();
		
		StringBuilder errorMsg = new StringBuilder(200);
		errorMsg.append("<html>");
		
		for (JLabel oldSenderEmail : senderEmails.keySet()) {
			Sender sender = UserData.oldSenderList.get(oldSenderEmail.getText());
			sender.setEmail(senderEmails.get(oldSenderEmail));
			try {
				Sender inviteUser = destinationEslClient.getAccountService()
						.inviteUser(SenderUtil.toAccontMember(sender));
				result.put(oldSenderEmail, true);
				oldAndNewSenderMap.put(oldSenderEmail.getText(), inviteUser);
			} catch (Exception e) {
				errorMsg.append(e.getMessage()).append("<br/>");
				result.put(oldSenderEmail, false);
			}
		}
		
		
		//return error msg
		errorMsg.append("</html>");
		view.setErrorMsg(errorMsg.toString());
		
		
		// set user data
		UserData.oldAndNewSenderMap = oldAndNewSenderMap;
		return result;
	}

}
