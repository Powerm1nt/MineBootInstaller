package com.sunproject.mineboot.installer;

import org.json.simple.JSONObject;

public class Release {

	private final String latestBinName, latestBinUrl;
	private final long contentLength;

	public Release(JSONObject assetArray) {
		this.latestBinName = (String) assetArray.get("name");
		this.latestBinUrl = (String) assetArray.get("browser_download_url");
		this.contentLength = (long) assetArray.get("size");
	}

	public final String getBinName() {
		return latestBinName;
	}

	public final String getBinUrl() {
		return latestBinUrl;
	}
	
	public final long getFileSize() {
		return this.contentLength;
	}
}
