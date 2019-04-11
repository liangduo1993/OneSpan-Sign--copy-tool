package com.esignlive.copytool.vo;

public class AccountVo {
	public enum CredentialType {
		API_KEY, CREDENTIAL
	}

	public enum SenderStatus {
		INVITED, ACTIVE, LOCKED
	}

	// both manager and owner has full access
	public enum SenderType {
		REGULAR, MANAGER, OWNER
	}

	private CredentialType credentialType = CredentialType.API_KEY;
	private String credential;
	private String apiKey;
	private String username;
	private String password;
	private SenderVo senderVo = new SenderVo();
	private SenderStatus senderStatus;
	private SenderType senderType;

	public CredentialType getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(CredentialType credentialType) {
		this.credentialType = credentialType;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SenderVo getSenderVo() {
		return senderVo;
	}

	public void setSenderVo(SenderVo senderVo) {
		this.senderVo = senderVo;
	}

	public SenderStatus getSenderStatus() {
		return senderStatus;
	}

	public void setSenderStatus(SenderStatus senderStatus) {
		this.senderStatus = senderStatus;
	}

	public SenderType getSenderType() {
		return senderType;
	}

	public void setSenderType(SenderType senderType) {
		this.senderType = senderType;
	}

	@Override
	public String toString() {
		return "AccountVo [credentialType=" + credentialType + ", credential=" + credential + ", apiKey=" + apiKey
				+ ", username=" + username + ", password=" + password + ", senderVo=" + senderVo + ", senderStatus="
				+ senderStatus + ", senderType=" + senderType + "]";
	}

}
