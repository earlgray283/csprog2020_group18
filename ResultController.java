import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.text.Text;

public class ResultController implements Initializable {
    public Button rankingBtn;
    public Text scoreText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    }
}
