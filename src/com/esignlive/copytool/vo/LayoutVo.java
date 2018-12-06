package com.esignlive.copytool.vo;

public class LayoutVo {
	private Boolean isCopy;
	private String layoutId;
	private String oldEnvSenderEmail;

	public Boolean getIsCopy() {
		return isCopy;
	}

	public void setIsCopy(Boolean isCopy) {
		this.isCopy = isCopy;
	}

	public String getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(String templateId) {
		this.layoutId = templateId;
	}

	public String getOldEnvSenderEmail() {
		return oldEnvSenderEmail;
	}

	public void setOldEnvSenderEmail(String oldEnvSenderEmail) {
		this.oldEnvSenderEmail = oldEnvSenderEmail;
	}

}
