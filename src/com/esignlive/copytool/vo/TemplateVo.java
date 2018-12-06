package com.esignlive.copytool.vo;

public class TemplateVo {
	private Boolean isCopy;
	private String templateId;
	private String oldEnvSenderEmail;

	public Boolean getIsCopy() {
		return isCopy;
	}

	public void setIsCopy(Boolean isCopy) {
		this.isCopy = isCopy;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getOldEnvSenderEmail() {
		return oldEnvSenderEmail;
	}

	public void setOldEnvSenderEmail(String oldEnvSenderEmail) {
		this.oldEnvSenderEmail = oldEnvSenderEmail;
	}

}
