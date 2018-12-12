package com.esignlive.copytool.vo;

public class AccountVo {
	public enum CredentialType {
		API_KEY, CREDENTIAL
	}

	private CredentialType credentialType = CredentialType.API_KEY;
	private String credential;
	private String apiKey;
	private String username;
	private String password;
	private SenderVo senderVo = new SenderVo();

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

	@Override
	public String toString() {
		return "AccountVo [credentialType=" + credentialType + ", credential=" + credential + ", apiKey=" + apiKey
				+ ", username=" + username + ", password=" + password + "]";
	}

}
