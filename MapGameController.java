import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MapGameController implements Initializable {
    public static Stage stage;
    public MapData mapData;
    public Chara chara;
    public GridPane mapGrid, itemGrid;
    public ImageView[] mapImageViews;
    public Text scoreText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initialize(int row, int col) {
        mapData = new MapData(col, row);

        chara = new Chara(1, 1, mapData);
        mapImageViews = new ImageView[mapData.getHeight() * mapData.getWidth()];
        for (int y = 0; y < mapData.getHeight(); y++) {
            for (int x = 0; x < mapData.getWidth(); x++) {
                setMapImageViews(x, y);
            }
        }

        scoreText.setText(String.valueOf(col * row));

        drawMap(chara, mapData);
    }

    public void setMapImageViews(int x, int y) {
        int index = y * mapData.getWidth() + x;
        mapImageViews[index] = mapData.getImageView(x, y);
    }

    // Draw the map
    public void drawMap(Chara c, MapData m) {
        int cx = c.getPosX();
        int cy = c.getPosY();

        mapGrid.getChildren().clear();
        for (int y = 0; y < mapData.getHeight(); y++) {
            for (int x = 0; x < mapData.getWidth(); x++) {
                int index = y * mapData.getWidth() + x;
                if (x == cx && y == cy) {
                    mapGrid.add(c.getCharaImageView(), x, y);
                } else {
                    mapGrid.add(mapImageViews[index], x, y);
                }
            }
        }

        itemGrid.getChildren().clear();
        itemGrid.setStyle("-fx-grid-lines-visible: true;");
        int[] inventory = c.getInventry();

        for (int i = 0, id = 1; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                if (id == 6)
                    break;
                VBox tmp = new VBox();

                ImageView imgviw = m.getItemImageView(id);
                imgviw.setFitHeight(40.0);
                imgviw.setFitWidth(40.0);

                Button button = new Button(String.valueOf(id), imgviw);
                button.setOnMouseClicked(event -> {
                    handleItems(Integer.valueOf(button.getText()), chara.getPosX(), chara.getPosY());
                    drawMap(c, m);
                });

                Text txt = new Text(String.valueOf(inventory[id]));
                txt.setFont(new Font(30));
                txt.setTextAlignment(TextAlignment.RIGHT);

                tmp.getChildren().addAll(button, txt);
                itemGrid.add(tmp, j, i);
                id++;
            }
        }

    }

    // Get users key actions
    public void keyAction(KeyEvent event) {
        KeyCode key = event.getCode();
        System.out.println("keycode:" + key);

        if (key == KeyCode.H || key == KeyCode.A) {
            leftButtonAction();
        } else if (key == KeyCode.J || key == KeyCode.S) {
            downButtonAction();
        } else if (key == KeyCode.K || key == KeyCode.W) {
            upButtonAction();
        } else if (key == KeyCode.L || key == KeyCode.D) {
            rightButtonAction();
        }

        String s = String.valueOf(chara.getScore());
        scoreText.setText(s);

        int item = mapData.getItem(chara.getPosX(), chara.getPosY());
        if (item == MapData.ITEM_NONE) {
            chara.addScore(-1);
        }
        incrementItems(item);

        mapData.setMap(chara.getPosX(), chara.getPosY(), MapData.TYPE_SPACE);
        mapData.setItem(chara.getPosX(), chara.getPosY(), MapData.ITEM_NONE);
        mapData.setImageViews();
        setMapImageViews(chara.getPosX(), chara.getPosY());

        drawMap(chara, mapData);

        if (mapData.is_goal(chara.getPosX(), chara.getPosY())) {
            if (chara.existsItem(MapData.ITEM_GOAL_FLG)) {
                System.out.println("goal");
                mapData.stopAudio();

                MapGame.resultController.scoreText.setText(String.valueOf(chara.getScore()));
                MapGame.resultController.audioClip.play();

                stage.setScene(MapGame.resultScene);
            }
        }
    }

    public void incrementItems(int item_id) {
        if (item_id == MapData.ITEM_NONE)
            return;
        chara.setInventory(item_id);
    }

    public void handleItems(int item_id, int x, int y) {
        String se_path = "";

        if (!chara.existsItem(item_id))
            return;

        chara.getInventry()[item_id]--;

        switch (item_id) {
            case MapData.ITEM_NONE:
                break;
            case MapData.ITEM_GOAL_FLG:
                se_path = "se/get_item.mp3";
                break;
            case MapData.ITEM_SCORE_P:
                se_path = "se/get_item.mp3";
                chara.addScore(5);
                break;
            case MapData.ITEM_SCORE_M:
                se_path = "se/gorilla.mp3";
                chara.addScore(-5);
                break;
            case MapData.ITEM_WARP:
                se_path = "se/warp.mp3";
                Random rnd = new Random();
                while (true) {
                    int x_ = rnd.nextInt(mapData.getWidth());
                    int y_ = rnd.nextInt(mapData.getHeight());
                    if (chara.setCharaPos(x_, y_)) {
                        break;
                    }
                }
                break;
            case MapData.ITEM_HINT:
                ArrayList<int[]> route = mapData.search_min_route(chara.getPosX(), chara.getPosY(), mapData.getGoalX(),
                        mapData.getGoalY());

                Timeline task = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
                    int cnt = 0;
                    @Override
                    public void handle(ActionEvent event) {

                        chara.setCharaPos(route.get(cnt)[0], route.get(cnt)[1]);
                        drawMap(chara, mapData);
                        cnt++;
                    }
                }));
                task.setCycleCount(20);
                task.play();

                drawMap(chara, mapData);

                break;
            default:
                System.out.println("no such item");
                return;
        }

        if (item_id != MapData.ITEM_NONE && !se_path.equals("")) {
            AudioClip audioClip = new AudioClip(new File(se_path).toURI().toString());
            audioClip.play();
        }

        if (chara.getScore() < 0)
            chara.addScore(-chara.getScore());
    }

    // Operations for going the cat down
    public void upButtonAction() {
        printAction("UP");
        chara.setCharaDirection(Chara.TYPE_UP);
        chara.move(0, -1);
        drawMap(chara, mapData);
    }

    // Operations for going the cat down
    public void downButtonAction() {
        printAction("DOWN");
        chara.setCharaDirection(Chara.TYPE_DOWN);
        chara.move(0, 1);
        drawMap(chara, mapData);
    }

    // Operations for going the cat right
    public void leftButtonAction() {
        printAction("LEFT");
        chara.setCharaDirection(Chara.TYPE_LEFT);
        chara.move(-1, 0);
        drawMap(chara, mapData);
    }

    // Operations for going the cat right
    public void rightButtonAction() {
        printAction("RIGHT");
        chara.setCharaDirection(Chara.TYPE_RIGHT);
        chara.move(1, 0);
        drawMap(chara, mapData);
    }

    public void func1ButtonAction(ActionEvent event) {
        Boolean res = chara.setCharaPos(mapData.getGoalX(), mapData.getGoalY());
        chara.setInventory(MapData.ITEM_GOAL_FLG);
        drawMap(chara, mapData);
        System.out.println("Debug mode: FAST GOAL " + res);
    }

    public void func2ButtonAction(ActionEvent event) {
        for (int i = 0; i <= 5; i++) {
            chara.setInventory(i);
        }
        drawMap(chara, mapData);
    }

    // Print actions of user inputs
    public void printAction(String actionString) {
        System.out.println("Action: " + actionString);
    }

}
