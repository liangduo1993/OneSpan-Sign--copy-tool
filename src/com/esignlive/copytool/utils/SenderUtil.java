package com.esignlive.copytool.utils;

import com.silanis.esl.sdk.AccountMember;
import com.silanis.esl.sdk.SenderStatus;

public class SenderUtil {

	public static AccountMember toAccontMember(com.silanis.esl.sdk.Sender sender) {
		AccountMember accountMember = new AccountMember();
		accountMember.setCompany(sender.getCompany());
		accountMember.setEmail(sender.getEmail());
		accountMember.setFirstName(sender.getFirstName());
		accountMember.setLanguage(sender.getLanguage());
		accountMember.setLastName(sender.getLastName());
		accountMember.setPhoneNumber(sender.getPhone());
		accountMember.setStatus(SenderStatus.ACTIVE);
		accountMember.setTitle(sender.getTitle());
		return accountMember;
	}
	
}
