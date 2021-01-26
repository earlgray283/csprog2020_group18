import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

public class TitleController implements Initializable {
    public static Stage stage;
    public Button playBtn, playBtn1, playBtn2;
    public AudioClip audioClip;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        audioClip = new AudioClip(new File("bgm/title.mp3").toURI().toString());
        audioClip.setVolume(0.5);
        audioClip.setCycleCount(AudioClip.INDEFINITE);
        audioClip.play();
    }

    public void playBtnAction(ActionEvent event) throws IOException {
        MapGame.mapGameController.initialize(11, 21);
        audioClip.stop();
        MapGame.mapGameController.mapData.playAudio();

        stage.setScene(MapGame.gameScene);
    }

    public void playBtn1Action(ActionEvent event) throws IOException {
        MapGame.mapGameController.initialize(31, 51);
        audioClip.stop();
        MapGame.mapGameController.mapData.playAudio();

        stage.setScene(MapGame.gameScene);
    }

    public void playBtn2Action(ActionEvent event) throws IOException {
        MapGame.mapGameController.initialize(61, 101);
        audioClip.stop();
        MapGame.mapGameController.mapData.playAudio();
 
        stage.setScene(MapGame.gameScene);
    }
}
