package com.sunproject.mineboot.installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class DownloadUpdate {
    static Request req;
    static OkHttpClient client;
    static Call call;
    static Response res;

    public static void download(String url, String updatePath) {

        req = new Request.Builder().url(url).build();
        client = new OkHttpClient();
        call = client.newCall(req);

        try {
            res = call.execute();
            if (res.isSuccessful()) {

                new Thread(() -> {
                    double FileSize = Double.parseDouble(res.headers().get("Content-Length"));
                    double total = 0;
                    double percent;
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

                    try (OutputStream output = new FileOutputStream(updateFile);
                         InputStream dataStream = res.body().byteStream();
                         BufferedInputStream bfStream = new BufferedInputStream(dataStream)) {

                        while ((count = bfStream.read(dataBytes)) != -1) {
                            total += count;
                            percent = (total / FileSize) * 100.0;
                            output.write(dataBytes, 0, count);
                            System.out.println(Math.round(percent) + " %");
                        }
                        System.out.println("Download Done !");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println("An error occurred !");
            e.getLocalizedMessage();
        }
    }
}

