package com.sunproject.mineboot.installer;

import com.sunproject.mineboot.installer.ui.InstUi;
import com.sunproject.sunupdate.DownloadUpdate;
import com.sunproject.sunupdate.GithubAPI;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Installer {
    public final static String instName = "MineBootLauncher";
    private static File tmpDir = new File(System.getProperty("user.home") + "/mineboot/tmp/");
    private static File buildKitFile = new File(System.getProperty("user.home") + "/mineboot/tmp/buildkit.zip");
    private static File buildDir = new File(tmpDir + "/" + instName);
    private static GithubAPI githubAPI;


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


    public static void preInstall() throws IOException {
        System.out.print("Pre-install ... ");
        if (!tmpDir.exists()) tmpDir.mkdirs();
        else {
            FileUtils.deleteDirectory(tmpDir);
        }
        System.out.println("done !");
    }

    public static void startDownload() throws IOException {
        System.out.print("Download Starting ... ");
        DownloadUpdate.download(getApiRepo().getLatestRelease().getBinUrl(), buildKitFile);
        System.out.println("done !");
    }

    public static void unpackPackage() {
        System.out.print("Unpacking ... ");
        ZipUtil.unpack(buildKitFile, new File(buildKitFile.getParent() + "/"));
        System.out.println("done !");
    }

    public static void configureInstall() throws IOException, InterruptedException {
        System.out.print("Post-install ... ");
        buildPackage(new File(buildKitFile.getParent() + "/" + instName));

        if (buildDir.exists()) FileUtils.moveFile(new File(buildDir + "/target/MineBoot-" + getApiRepo().getVersionNumber() + "-jar-with-dependencies.jar"),
                new File(buildDir.getParent() + "../"));

        if (buildKitFile.exists()) buildKitFile.delete();
        System.out.println("done !");
    }

    private static void buildPackage(File workDir) throws IOException, InterruptedException {
        if (!workDir.exists()) {
            throw new FileNotFoundException();
        } else {
            Runtime.getRuntime().exec(new String[]{
                    "xterm", "-geometry", "100x50", "-T", ("Building " + instName + " ..."), "-e", "bash", buildKitFile.getParent() + "/" + instName + "/build.sh"
            }).waitFor();
        }
    }


    public static GithubAPI getApiRepo() throws IOException {
        if (githubAPI == null) {
            githubAPI = new GithubAPI("https://api.github.com/repos/sundev79/MinebootLauncher/releases/latest");
        }
        return githubAPI;
    }
}
