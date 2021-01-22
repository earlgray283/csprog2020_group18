import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TitleController {
    public static Stage stage;
    public Button playBtn;

    public void playBtnAction(ActionEvent event) throws IOException {
        System.out.println("playBtn clicked");

        stage.setScene(MapGame.gameScene);
    }
}
