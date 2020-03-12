package com.sunproject.mineboot.installer;

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

}
