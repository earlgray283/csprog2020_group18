import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TitleController {
    public static Stage stage;
    public Button playBtn;

    public void playBtnAction(ActionEvent event) throws IOException {
        System.out.println("playBtn clicked");

        stage.setScene(MapGame.gameScene);
    }
}
