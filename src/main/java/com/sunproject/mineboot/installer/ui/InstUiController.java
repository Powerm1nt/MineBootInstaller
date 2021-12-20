package com.sunproject.mineboot.installer.ui;

import com.sunproject.mineboot.installer.Installer;
import com.sunproject.mineboot.installer.TxtReader;
import com.sunproject.sunupdate.GithubAPI;
import static javafx.application.Platform.runLater;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class InstUiController implements Initializable {

	private int currentSlideMumber = 0;
	public static boolean isAborted = false;
	private TextArea area;
	private boolean isRepoIsInstancied = false;

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
		area.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
		area.setText("Loading ...");
		btn_next.setOnAction(e -> changeSlide(++currentSlideMumber));
		btn_previous.setOnAction(e -> changeSlide(--currentSlideMumber));

		// Init The first Slide

		changeSlide(currentSlideMumber);

		/////////////////////////////////////

	}

	private void changeSlide(int currentSlide) {

		btn_previous.setDisable(currentSlide <= 0);

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
			area.setText("Je vais installer MinebootLauncher dans votre dossier personnel, juste ici :\n");
			area.appendText(System.getProperty("user.home"));
			showLoading(true);
			new Thread(() -> {

				try {
					runLater(() -> {
						btn_next.setDisable(true);
						btn_next.setText("Install");
					});


					long contentLength = (Installer.getApiRepo().getLatestRelease().getFileSize());
					area.appendText("\nCeci va télécharger les fichiers requis sur github.com, environ "
							+ (contentLength / 1000_024) + " Mo.");

					showLoading(false);
					runLater(() -> btn_next.setDisable(false));
				} catch (IOException e) {
					area.clear();
					area.setDisable(true);
					btn_cancel.setDisable(true);
					btn_previous.setDisable(true);
					btn_next.setDisable(true);

					runLater(() -> {
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

			area.setText("Installation en cours ... ");
			btn_next.setDisable(true);
			btn_previous.setDisable(true);
			showLoading(true);
			Thread installThread = new Thread(() -> {
				try {
					runLater(() -> area.appendText("\nPre-install ... "));
					Installer.preInstall();
					runLater(() -> area.appendText("done !"));
					runLater(() -> area.appendText("\nDownloading package ... "));
					Installer.startDownload();
					runLater(() -> area.appendText("done !"));
					runLater(() -> area.appendText("\nUnpacking package ... "));
					Installer.unpackPackage();
					runLater(() -> area.appendText("done !"));

					runLater(() -> area.appendText("\n!!! warning, a compilation script will launch, please do not close the window !!!"));
					try { Thread.sleep(5000); } catch (InterruptedException ignored) {}

					runLater(() -> area.appendText("\nPost-install ... "));
					Installer.configureInstall();
					runLater(() -> area.appendText("done !"));
					isAborted = false;
				} catch (IOException | InterruptedException e) {
					isAborted = true;
					area.appendText("\n\nError Info : " + (e.getMessage() == null ? "Unknown error !" : e.getMessage()));
					e.printStackTrace();
				}
				Platform.runLater(() -> changeSlide(-1));
			});
			installThread.setName("MinebootInstaller Thread");
			installThread.setDaemon(true);
			installThread.start();
			break;
		default:
			if (isAborted) {
				area.appendText("\nL'installation à échoué !");
				area.appendText("\n \nJe Suis désolé, mais l'installation a eu un problème !!!");
			} else {
				area.appendText("\nL'installation est terminé !");
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
