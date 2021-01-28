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
    public static Scene titleScene, gameScene, resultScene, rankingScene;
    public static ResultController resultController;
    public static MapGameController mapGameController;
    public static RankingController rankingController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        TitleController.stage = stage;
        MapGameController.stage = stage;

        primaryStage.setTitle("MAP GAME");

        FXMLLoader tFxmlLoader = new FXMLLoader(getClass().getResource("Title.fxml"));
        FXMLLoader mapGFxmlLoader = new FXMLLoader(getClass().getResource("MapGame.fxml"));
        FXMLLoader rFxmlLoader = new FXMLLoader(getClass().getResource("Result.fxml"));
        FXMLLoader rankingFxmlLoader = new FXMLLoader(getClass().getResource("Ranking.fxml"));

        Pane titlePane = tFxmlLoader.load();
        titlePane.setBackground(new Background(new BackgroundImage(new Image("png/title_usagi.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        Pane gamePane = mapGFxmlLoader.load();
        Pane resultPane = rFxmlLoader.load();
        Pane rankingPane = rankingFxmlLoader.load();

        titleScene = new Scene(titlePane);
        gameScene = new Scene(gamePane);
        resultScene = new Scene(resultPane);
        rankingScene = new Scene(rankingPane);

        mapGameController = mapGFxmlLoader.getController();
        resultController = rFxmlLoader.getController();
        rankingController = rankingFxmlLoader.getController();

        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
