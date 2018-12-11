package com.esignlive.copytool.vo;

import org.json.JSONObject;

public class SenderVo {
	public enum SenderType {
		OWNER, MANAGER, REGULAR
	}

	private JSONObject content;
	private SenderType senderType;
	private String email;
	private String id;
	public JSONObject getContent() {
		return content;
	}

	public void setContent(JSONObject content) {
		this.content = content;
	}

	public SenderType getSenderType() {
		return senderType;
	}

	public void setSenderType(SenderType senderType) {
		this.senderType = senderType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SenderVo [content=" + content + ", senderType=" + senderType + ", email=" + email + "]";
	}

}
