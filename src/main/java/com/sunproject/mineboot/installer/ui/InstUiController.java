package com.sunproject.mineboot.installer.ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sunproject.mineboot.installer.GithubAPI;
import com.sunproject.mineboot.installer.Installer;
import com.sunproject.mineboot.installer.TxtReader;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class InstUiController implements Initializable {

	private int currentSlideMumber = 0;
	private boolean isAborded = false;
	private TextArea area;
	GithubAPI remoteRepo;

	@FXML
	private Text txt_welcome = new Text();
	@FXML
	private Button btn_next, btn_cancel, btn_previous = new Button();
	@FXML
	private BorderPane instView = new BorderPane();
	@FXML
	private HBox loadingStatus = new HBox();

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// Init components
		txt_welcome.setText("Welcome to " + InstUi.getInstName() + " !");
		btn_next.setDisable(false);
		btn_cancel.setDisable(false);
		btn_cancel.setOnAction(e -> InstUi.cancel());
		area = (TextArea) instView.getChildren().get(0);
		area.setDisable(false);
		area.setText("Loading ...");
		btn_next.setOnAction(e -> changeSlide(++currentSlideMumber));
		btn_previous.setOnAction(e -> changeSlide(--currentSlideMumber));

		// Init a new instance of GithubApi
		
//		try {
//			this.remoteRepo = new GithubAPI("https://api.github.com/repos/sundev79/MinebootLauncher/releases/latest");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//////////////////////////////////////

		// Init The first Slide

		changeSlide(currentSlideMumber);

		/////////////////////////////////////

	}

	private void changeSlide(int currentSlide) {

		btn_previous.setDisable((currentSlide > 0) ? false : true);

		switch (currentSlide) {
		case 0:
			try {
				area.clear();
				area.setText(TxtReader.getContent(getClass().getResourceAsStream("/rsrc/TXTs/INFO.txt"), "UTF8"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
			break;
		case 1:
			try {
				area.clear();
				area.setText(TxtReader.getContent(getClass().getResourceAsStream("/rsrc/TXTs/LICENCE.txt"), "UTF8"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
			btn_next.setText("Next");
			break;
		case 2:
			area.clear();
			area.setText("Installation Setup : ");
			area.appendText("\t Installation de l\'application dans le répertoire utilisateur.");
			showLoading(true);
			new Thread(() -> {

				try {
					Platform.runLater(() -> {
						btn_next.setDisable(true);
						btn_next.setText("Install");
					});

					
					if (!GithubAPI.isInstancied()) this.remoteRepo = new GithubAPI("https://api.github.com/repos/sundev79/MinebootLauncher/releases/latest");
					
					long contentLength = (this.remoteRepo.getLatestRelease().getFileSize());
					area.appendText("\nCeci va télécharger les fichiers requis sur github.com, environ " + (contentLength / 1000_024) + " Mo.");

					showLoading(false);
					Platform.runLater(() -> btn_next.setDisable(false));
				} catch (IOException e) {
					area.clear();
					area.setDisable(true);
					btn_cancel.setDisable(true);
					btn_previous.setDisable(true);
					btn_next.setDisable(true);

					Platform.runLater(() -> {
						showLoading(false);
						Alert alrt = new Alert(AlertType.ERROR);
						alrt.setContentText("L'installation à échoué, Aucune connexion internet.");
						alrt.showAndWait();
						Platform.exit();
						System.exit(1);
					});
				}
			}).start();

			break;
		case 3:

			area.setText("Installing ...");
			btn_next.setDisable(true);
			btn_previous.setDisable(true);
			showLoading(true);
			Thread installThread = new Thread(() -> {
				try {
					Installer.startInstall();
					this.isAborded = false;
				} catch (IOException e) {
					this.isAborded = true;
					System.err.println("timeout !!!".toUpperCase());
					e.printStackTrace();
				}
				Platform.runLater(() -> changeSlide(-1));
			});
			installThread.setName("Installer Thread");
			installThread.setDaemon(true);
			installThread.start();
			break;
		default:
			if (this.isAborded) {
				area.setText("L'installation à échoué !");
			} else {
				area.setText("L'installation est terminé !");
			}
			btn_next.setDisable(true);
			btn_previous.setDisable(true);
			btn_cancel.setText("Close");
			btn_cancel.setOnAction(e -> InstUi.stopApp());
			showLoading(false);
			break;
		}
	}

	private void showLoading(boolean value) {
		loadingStatus.setVisible(value);
	}
}
