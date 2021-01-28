import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;

public class ResultController implements Initializable {
    public Button rankingBtn, sendBtn;
    public Text scoreText, doneText;
    public AudioClip audioClip;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        audioClip = new AudioClip(new File("se/result.mp3").toURI().toString());
        audioClip.setVolume(0.5);

        rankingBtn.setBackground(
            new Background(
                new BackgroundImage(
                    new Image("png/ranking.png"), 
                    BackgroundRepeat.REPEAT, 
                    BackgroundRepeat.REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT
                )
            )
        );

        doneText.setVisible(false);

        rankingBtn.setOnAction(event -> {
            try {
                var scores = ScoreManager.getScore();
                scores.sort((a, b) -> a.score - b.score);

                System.out.println(MapGame.rankingController);
                System.out.println(MapGame.mapGameController);

                MapGame.rankingController.BuildRankingScene(scores);
                MapGameController.stage.setScene(MapGame.rankingScene);

            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        sendBtn.setOnAction(event -> {
            String name;
            int score = MapGame.mapGameController.chara.getScore();

            TextInputDialog dialog = new TextInputDialog("guest");
            name = dialog.showAndWait().get();

            try {
                var res = ScoreManager.postScore(name, score);
                if (!res) {
                    System.out.println("Error occuar!");
                } else {
                    sendBtn.setVisible(false);
                    doneText.setVisible(true);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
