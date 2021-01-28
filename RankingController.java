import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RankingController implements Initializable {
    public VBox rankVbox, nameVbox, scoreVbox;
    public Button returnBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        returnBtn.setOnAction(event -> {
            MapGameController.stage.setScene(MapGame.resultScene);
        });
    }

    public void BuildRankingScene(List<ScoreManager.ScoreInfo> scores) {
        rankVbox.getChildren().clear();
        nameVbox.getChildren().clear();
        scoreVbox.getChildren().clear();
        
        for (int i = 0; i < scores.size(); i++) {
            rankVbox.getChildren().add(new Label(String.valueOf(i + 1)));
            nameVbox.getChildren().add(new Label(scores.get(i).name));
            scoreVbox.getChildren().add(new Label(String.valueOf(scores.get(i).score)));
        }
    }
}
