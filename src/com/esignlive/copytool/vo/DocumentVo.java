package com.esignlive.copytool.vo;

public class DocumentVo {
private String id;
private String name;
private boolean isOriginal;
private byte[] content;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public boolean isOriginal() {
	return isOriginal;
}
public void setOriginal(boolean isOriginal) {
	this.isOriginal = isOriginal;
}
public byte[] getContent() {
	return content;
}
public void setContent(byte[] content) {
	this.content = content;
}
}
