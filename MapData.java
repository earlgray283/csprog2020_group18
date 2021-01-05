import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

public class MapData {
    // enum にしたいな
    public static final int TYPE_SPACE = 0;
    public static final int TYPE_WALL = 1;
    public static final int TYPE_OTHERS = 2;

    public static final int ITEM_NONE = 0;
    public static final int ITEM_GOAL_FLG = 1;
    public static final int ITEM_SCORE_P = 2;
    public static final int ITEM_SCORE_M = 3;
    public static final int ITEM_WARP = 4;
    public static final int ITEM_HINT = 5;

    private static final String mapImageFiles[] = { "png/map/SPACE.png", "png/map/WALL.png" };
    private static final String itemImageFiles[] = { "png/map/SPACE.png", "png/items/key.png", "png/items/milk.png",
            "png/items/gorilla.png", "png/items/ufo.png", "png/items/hint.png" };

    private AudioClip audioClip;
    private Image[] mapImages, itemImages;
    private ImageView[][] mapImageViews;
    private int[][] maps;
    /*
     * item_map ... item を管理する用の map 0 ... itemなし 1 ... goal-flg 2 ... score+ 3 ...
     * score- 4 ... warp 5 ... show hint
     */
    private int[][] item_map;
    
    private int width;
    private int height;
    private int[][] route_to_goal;
    private int[] goal;

    MapData(int x, int y) {
        audioClip = new AudioClip(new File("bgm/map.mp3").toURI().toString());
        audioClip.setVolume(0.5);
        audioClip.setCycleCount(AudioClip.INDEFINITE);
        audioClip.play();

        mapImages = new Image[2];
        for (int i = 0; i < 2; i++)
            mapImages[i] = new Image(mapImageFiles[i]);

        itemImages = new Image[6];
        for (int i = 0; i < 6; i++)
            itemImages[i] = new Image(itemImageFiles[i]);

        mapImageViews = new ImageView[y][x];

        width = x;
        height = y;
        maps = new int[y][x];

        fillMap(MapData.TYPE_WALL);
        digMap(1, 3);

        item_map = setItemMap();

        goal = find_goal();

        setImageViews();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void stopAudio() {
        audioClip.stop();
    }

    private int[][] setItemMap() {
        int[][] item_map = new int[height][width];
        Random rand = new Random();

        int[] cnts = { 0, 1, 2, 3, 2, 2 };

        for (int i = 1; i <= 5; i++) {
            for (int cnt = 0; cnt < cnts[i]; cnt++) {
                int x, y;
                do {
                    y = rand.nextInt(height);
                    x = rand.nextInt(width);
                } while (getMap(x, y) == TYPE_WALL || item_map[y][x] != ITEM_NONE || x == 1 || y == 1);
                item_map[y][x] = i;
            }
        }

        return item_map;
    }

    public int getItem(int x, int y) {
        return item_map[y][x];
    }

    public void setItem(int x, int y, int type) {
        item_map[y][x] = type;
    }

    public int getMap(int x, int y) {
        return x < 0 || width <= x || y < 0 || height <= y ? -1 : maps[y][x];
    }

    public ImageView getImageView(int x, int y) {
        return mapImageViews[y][x];
    }

    public ImageView getItemImageView(int id) {
        return new ImageView(itemImages[id]);
    }

    public void setMap(int x, int y, int type) {
        if (x < 1 || width <= x - 1 || y < 1 || height <= y - 1)
            return;

        maps[y][x] = type;
    }

    // set images based on two-dimentional arrays (maps[y][x])
    public void setImageViews() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapImageViews[y][x] = new ImageView(
                        getItem(x, y) == ITEM_NONE ? mapImages[maps[y][x]] : itemImages[getItem(x, y)]);
            }
        }
    }

    // fill two-dimentional arrays with a given number (maps[y][x])
    public void fillMap(int type) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maps[y][x] = type;
            }
        }
    }

    // dig walls for creating trails
    public void digMap(int x, int y) {
        setMap(x, y, MapData.TYPE_SPACE);

        int[][] dl = shuffle();

        for (int i = 0; i < dl.length; i++) {
            int dx = dl[i][0];
            int dy = dl[i][1];
            if (getMap(x + dx * 2, y + dy * 2) == MapData.TYPE_WALL) {
                setMap(x + dx, y + dy, MapData.TYPE_SPACE);
                digMap(x + dx * 2, y + dy * 2);
            }
        }
    }

    // shuffle dx and dy
    public int[][] shuffle() {
        int[][] dl = { { 0, 1 }, { 0, -1 }, { -1, 0 }, { 1, 0 } };
        int[] tmp;

        for (int i = 0; i < dl.length; i++) {
            int r = (int) (Math.random() * dl.length);
            tmp = dl[i];
            dl[i] = dl[r];
            dl[r] = tmp;
        }

        return dl;
    }

    // find goal. The goal is defined as the maximum manhattan distance and the
    // maximum dist.
    public int[] find_goal() {
        int[] dx = { 1, 0, -1, 0 };
        int[] dy = { 0, -1, 0, 1 };
        int max_manhattan = -1;
        int max_dist = -1;
        int[] ans = { -1, -1 };
        int[][] dists = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                dists[i][j] = -1;
            }
        }

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] { 1, 1 });
        dists[1][1] = 0;

        // bfs
        while (!queue.isEmpty()) {
            int[] now = queue.poll();
            for (int i = 0; i < 4; i++) {
                if (!(0 <= now[0] + dx[i] && now[0] + dx[i] < width && 0 <= now[1] + dy[i]
                        && now[1] + dy[i] < height)) {
                    continue;
                }

                int next_x = now[0] + dx[i];
                int next_y = now[1] + dy[i];

                if (dists[next_y][next_x] != -1 && dists[next_y][next_x] < dists[now[1]][now[0]] + 1) {
                    continue;
                }
                if (getMap(next_x, next_y) == MapData.TYPE_SPACE) {
                    queue.add(new int[] { next_x, next_y });
                    dists[next_y][next_x] = dists[now[1]][now[0]] + 1;
                    if (max_dist < dists[next_y][next_x] || (max_dist == dists[next_y][next_x]
                            && max_manhattan < manhattan_dist(1, 1, next_x, next_y))) {
                        max_manhattan = manhattan_dist(1, 1, next_x, next_y);
                        max_dist = dists[next_y][next_x];
                        ans = new int[] { next_x, next_y };
                    }
                }
            }
        }

        System.out.println("goal: " + ans[0] + ", " + ans[1]);

        route_to_goal = restore_route(dists, ans);

        return ans;
    }

    // restore route to the goal.
    public int[][] restore_route(int[][] dists, int[] goal) {
        int[] dx = { 1, 0, -1, 0 };
        int[] dy = { 0, -1, 0, 1 };

        int[][] route = new int[dists[goal[1]][goal[0]] + 1][2];
        int route_i = dists[goal[1]][goal[0]];

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] { goal[0], goal[1] });

        // bfs
        while (!queue.isEmpty() && route_i > 1) {
            int[] now = queue.poll();
            for (int i = 0; i < 4; i++) {
                if (!(0 <= now[0] + dx[i] && now[0] + dx[i] < width && 0 <= now[1] + dy[i]
                        && now[1] + dy[i] < height)) {
                    continue;
                }

                int next_x = now[0] + dx[i];
                int next_y = now[1] + dy[i];
                if (dists[next_y][next_x] == dists[now[1]][now[0]] - 1) {
                    queue.add(new int[] { next_x, next_y });
                    route[route_i--] = new int[] { next_x, next_y };
                    break;
                }
            }
        }

        route[0] = new int[] { 1, 1 };

        return route;
    }

    // return abs(a_x - b_x) + abs(a_y - b_y)
    private int manhattan_dist(int a_x, int a_y, int b_x, int b_y) {
        return Math.abs(a_x - b_x) + Math.abs(a_y - b_y);
    }

    public boolean is_goal(int x, int y) {
        return goal[0] == x && goal[1] == y;
    }
}
