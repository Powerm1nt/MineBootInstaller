package com.sunproject.mineboot.installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class DownloadUpdate {
	static Request req;
	static OkHttpClient client;
	static Call call;
	static Response res;
	static double percent;

	public static void download(String url, String updatePath) throws IOException {
		req = new Request.Builder().url(url).build();
		client = new OkHttpClient();
		call = client.newCall(req);

		res = call.execute();
		if (res.isSuccessful()) {

			double FileSize = Double.parseDouble(res.headers().get("Content-Length"));
			double total = 0;
			int count;
			byte[] dataBytes = new byte[1024];

			File updateFile = new File(updatePath);
			if (!updateFile.exists()) {
				updateFile.getParentFile().mkdirs();
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			OutputStream output = new FileOutputStream(updateFile);
			InputStream dataStream = res.body().byteStream();
			BufferedInputStream bfStream = new BufferedInputStream(dataStream);

			while ((count = bfStream.read(dataBytes)) != -1) {
				total += count;
				percent = (total / FileSize) * 100.0;
				output.write(dataBytes, 0, count);
				// System.out.println(Math.round(percent) + " %");
			}

			bfStream.close();
			dataStream.close();
			output.close();
			if (new File(updatePath.replace("updates/Mineboot.jar.tmp", "") + "Mineboot.jar").exists()) {
				new File(updatePath.replace("updates/Mineboot.jar.tmp", "") + "Mineboot.jar").delete();
			}
			FileUtils.moveFile(updateFile,
					new File(updatePath.replace("updates/Mineboot.jar.tmp", "") + "Mineboot.jar")); // TODO revoir
																									// le systeme de
																									// chemin.
		}
	}

	public static double getPercent() {
		return percent;
	}
}
