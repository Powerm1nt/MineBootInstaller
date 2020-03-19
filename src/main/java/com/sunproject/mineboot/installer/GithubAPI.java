package com.sunproject.mineboot.installer;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@SuppressWarnings("unused")

public class GithubAPI {
	private Request req;
	private OkHttpClient client;
	private Call call;
	private Response res;
	private String latestBin, staticJsonFile, latestBinName, releaseVersion;
	private JSONArray latestAssets;
	private Release latestRelease;
	private static boolean isInstancied = false;

	public GithubAPI(String url) throws IOException {
		req = new Request.Builder().url(url).build();
		client = new OkHttpClient();
		call = client.newCall(req);
		JSONObject githubJson;

		res = call.execute();
		if (res.isSuccessful()) {
			String jsonFile = res.body().string();
			githubJson = (JSONObject) JSONValue.parse(jsonFile);
			staticJsonFile = githubJson.toJSONString();
			latestAssets = (JSONArray) githubJson.get("assets");
			JSONObject asset = (JSONObject) latestAssets.get(0); // Tableau d'assets
			latestRelease = new Release(asset);
			releaseVersion = (String) githubJson.get("tag_name");
		}
		isInstancied = true;
	}

	public String getJson() {
		return staticJsonFile;
	}

	public String getVersionNumber() {
		return this.releaseVersion;
	}

	public Release getLatestRelease() {
		return this.latestRelease;
	}
	
	public static boolean isInstancied() {
		return isInstancied;
	}

}
