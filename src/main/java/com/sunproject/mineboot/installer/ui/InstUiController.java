package com.sunproject.mineboot.installer.ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.sunproject.mineboot.installer.DownloadUpdate;
import com.sunproject.mineboot.installer.TxtReader;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class InstUiController implements Initializable {

	private int maxSlideNumber;
	private int currentSlideMumber = 0;
	private TextArea area;

	@FXML
	private Text txt_welcome = new Text();
	@FXML
	private Button btn_next, btn_cancel, btn_previous = new Button();
	@FXML
	private BorderPane instView = new BorderPane();

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


		//////////////////////////////////////

		// Init The first Slide
		setMaxSlideNumber(3);
		changeSlide(currentSlideMumber);

	}

	private void changeSlide(int currentSlide) {
		
		if (currentSlide > 0) {
			btn_previous.setDisable(false);
		} else {
			btn_previous.setDisable(true);
		}
		
			switch (currentSlide) {
				case 0:
					try {
						area.setText(TxtReader.getContent(getClass().getResourceAsStream("/rsrc/TXTs/INFO.txt"),"UTF8"));
					} catch (IOException | URISyntaxException e1) { e1.printStackTrace();}
					break;
				case 1:
					try {
						area.setText(TxtReader.getContent(getClass().getResourceAsStream("/rsrc/TXTs/LICENCE.txt"), "UTF8"));
					} catch (IOException | URISyntaxException e) { e.printStackTrace();}
					btn_next.setText("Next");
					break;
				case 2:
					area.setText("Preparing ...");
					area.setText("Installation Setup : ");
					area.appendText("\t Installation de l\'application dans le répertoire utilisateur.");
					try {
						int contentLength = Integer.parseInt(getHttpHeaders("https://github.com/sundev79/MineBootLauncher/releases/download/1.0.1/MineBoot.jar"));
						area.appendText("\n\n Ceci va télécharger les fichiers requis sur github.com, " + (contentLength / 1_024) );
					} catch (IOException e) {
						area.clear();
						area.setDisable(true);
						btn_cancel.setDisable(true);
						btn_previous.setDisable(true);
						btn_next.setDisable(true);
						Alert alrt = new Alert(AlertType.ERROR);
						alrt.setContentText("L'installation à échoué, Aucune connexion internet.");
						alrt.showAndWait();
						Platform.exit();
						System.exit(1);
					}
	
					btn_next.setText("Install");
					break;
				case 3:
					area.setText("Installing ...");
					btn_next.setDisable(true);
					btn_previous.setDisable(true);
					break;
				default:
					break;
			}
		}

	public int getMaxSlideNumber() {
		return maxSlideNumber;
	}

	public void setMaxSlideNumber(int maxSlideNumber) {
		this.maxSlideNumber = maxSlideNumber;
	}
	public String getHttpHeaders(String url) throws IOException {
		Request req = new Request.Builder().url(url).build();
		OkHttpClient client = new OkHttpClient();
		Call call = client.newCall(req);
		Response res = call.execute();
		return res.header("Content-Length");
		
	}
}
