package com.korosmatick.sample.model.api;

public class Form {
	
	private String formID;
	private String name;
	private String majorMinorVersion;
	private String version;
	private String hash;
	private String descriptionText;
	private String downloadUrl;
	private String manifestUrl;
	
	public String getFormID() {
		return formID;
	}

	public void setFormID(String formID) {
		this.formID = formID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMajorMinorVersion() {
		return majorMinorVersion;
	}

	public void setMajorMinorVersion(String majorMinorVersion) {
		this.majorMinorVersion = majorMinorVersion;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getManifestUrl() {
		return manifestUrl;
	}

	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}
	
}
