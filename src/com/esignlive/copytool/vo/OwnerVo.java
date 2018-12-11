package com.esignlive.copytool.vo;

public class OwnerVo {
	private String email;
	private String id;

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
		return "OwnerVo [email=" + email + ", id=" + id + "]";
	}

}
