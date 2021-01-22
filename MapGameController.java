import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
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

public class MapGameController implements Initializable {
    public static Stage stage;
    public MapData mapData;
    public Chara chara;
    public GridPane mapGrid, itemGrid;
    public ImageView[] mapImageViews;
    public Text scoreText;
    private AudioClip audioClip;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapData = new MapData(21, 15);
        
        chara = new Chara(1, 1, mapData);
        mapImageViews = new ImageView[mapData.getHeight() * mapData.getWidth()];
        for (int y = 0; y < mapData.getHeight(); y++) {
            for (int x = 0; x < mapData.getWidth(); x++) {
                setMapImageViews(x, y);
            }
        }

        scoreText.setText(String.valueOf(21 * 15));

        drawMap(chara, mapData);
    }

    public void setMapImageViews(int x, int y) {
        int index = y * mapData.getWidth() + x;
        mapImageViews[index] = mapData.getImageView(x, y);
    }

    public void initialize() {
        mapData = new MapData(21, 15);
        chara = new Chara(1, 1, mapData);
        mapImageViews = new ImageView[mapData.getHeight() * mapData.getWidth()];
        for (int y = 0; y < mapData.getHeight(); y++) {
            for (int x = 0; x < mapData.getWidth(); x++) {
                int index = y * mapData.getWidth() + x;
                mapImageViews[index] = mapData.getImageView(x, y);
            }
        }

        scoreText.setText(String.valueOf(21 * 15));

        drawMap(chara, mapData);
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
                imgviw.setFitHeight(80.0);
                imgviw.setFitWidth(80.0);

                Text txt = new Text(String.valueOf(inventory[id]));
                txt.setFont(new Font(30));
                txt.setTextAlignment(TextAlignment.RIGHT);

                tmp.getChildren().addAll(imgviw, txt);
                itemGrid.add(tmp, j, i);
                id++;
            }
        }

    }

    // Get users key actions
    public void keyAction(KeyEvent event) {
        KeyCode key = event.getCode();
        System.out.println("keycode:" + key);

        if (key == KeyCode.H) {
            leftButtonAction();
        } else if (key == KeyCode.J) {
            downButtonAction();
        } else if (key == KeyCode.K) {
            upButtonAction();
        } else if (key == KeyCode.L) {
            rightButtonAction();
        }

        System.out.println(chara.getScore());
        String s = String.valueOf(chara.getScore());
        System.out.println(s);
        scoreText.setText(s);

        handleItems(mapData.getItem(chara.getPosX(), chara.getPosY()), chara.getPosX(), chara.getPosY());
        drawMap(chara, mapData);

        if (mapData.is_goal(chara.getPosX(), chara.getPosY())) {
            if (chara.existsItem(MapData.ITEM_GOAL_FLG)) {
                System.out.println("goal");
                mapData.stopAudio();

                ResultController.scoreText.setText(String.valueOf(chara.getScore()));
                
                stage.setScene(MapGame.resultScene);
            }
        }
    }

    public void handleItems(int item_id, int x, int y) {
        String se_path = "";
        switch (item_id) {
            case MapData.ITEM_NONE:
                chara.addScore(-1);
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
                // todo
                break;
            default:
                System.out.println("no such item");
                return;
        }
       
        if (item_id != MapData.ITEM_NONE && !se_path.equals("")) {
            AudioClip audioClip = new AudioClip(new File(se_path).toURI().toString());
            audioClip.play();
        }

        chara.setInventory(item_id);

        if (chara.getScore() < 0)
            chara.addScore(-chara.getScore());

        mapData.setMap(x, y, MapData.TYPE_SPACE);
        mapData.setItem(x, y, MapData.ITEM_NONE);
        mapData.setImageViews();
        setMapImageViews(x, y);
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
        System.out.println("func1: Nothing to do");
    }

    // Print actions of user inputs
    public void printAction(String actionString) {
        System.out.println("Action: " + actionString);
    }

}
