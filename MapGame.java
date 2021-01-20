import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class MapGame extends Application {
    Stage stage;
    public static Scene titleScene, gameScene, resultScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        TitleController.stage = stage;
        MapGameController.stage = stage;

        primaryStage.setTitle("MAP GAME");
        
        Pane titlePane = (Pane) FXMLLoader.load(getClass().getResource("Title.fxml"));
        titlePane.setBackground(
            new Background(
                new BackgroundImage(
                    new Image("png/title_usagi.png"), 
                    BackgroundRepeat.REPEAT, 
                    BackgroundRepeat.REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT
                )
            )
        );

        Pane gamePane = (Pane) FXMLLoader.load(getClass().getResource("MapGame.fxml"));

        Pane resultPane = (Pane) FXMLLoader.load(getClass().getResource("Result.fxml"));;

        titleScene = new Scene(titlePane);
        gameScene = new Scene(gamePane);
        resultScene = new Scene(resultPane);

        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
