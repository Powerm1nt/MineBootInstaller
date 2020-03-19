package com.sunproject.mineboot.installer;

import java.io.IOException;

import com.sunproject.mineboot.installer.ui.InstUi;

public class Installer {

	public static void main(String[] args) {
		// System.out.println("Launching jar MinebootApp ...");
		
		InstUi.launch(InstUi.class);

		
//		try {
//			Runtime.getRuntime().exec("java -jar " + System.getenv("appdata") + "/.mineboot/Mineboot.jar");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void startInstall() throws IOException {
		
		DownloadUpdate.download(new GithubAPI("https://api.github.com/repos/sundev79/MinebootLauncher/releases/latest").getLatestRelease().getBinUrl(), System.getenv("appdata") + "/.mineboot/updates/Mineboot.jar" + ".tmp");
		
	}

}
