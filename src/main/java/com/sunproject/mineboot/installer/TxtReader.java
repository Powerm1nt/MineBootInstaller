package com.sunproject.mineboot.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * Simple Text-Loader on java.
 * 
 * @author sundev79
 *
 */
public class TxtReader {

	public static String getContent(InputStream path, String charsetName) throws IOException, URISyntaxException {

		InputStreamReader inR = new InputStreamReader(path);
		try (BufferedReader bReader = new BufferedReader(inR)) {
			String line;
			String content = "";

			while ((line = bReader.readLine()) != null) {
				content += line + "\n";
			}

			return content;
		}
	}
}
