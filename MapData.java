import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.Queue;

public class MapData {
    public static final int TYPE_SPACE = 0;
    public static final int TYPE_WALL = 1;
    public static final int TYPE_OTHERS = 2;
    private static final String mapImageFiles[] = {
        "png/SPACE.png",
        "png/WALL.png"
    };

    private Image[] mapImages;
    private ImageView[][] mapImageViews;
    private int[][] maps;
    private int width;
    private int height;
    private int[][] route_to_goal;
    private int[] goal;

    MapData(int x, int y){
        mapImages = new Image[2];
        mapImageViews = new ImageView[y][x];
        for (int i=0; i<2; i++) {
            mapImages[i] = new Image(mapImageFiles[i]);
        }

        width = x;
        height = y;
        maps = new int[y][x];

        fillMap(MapData.TYPE_WALL);
        digMap(1, 3);

        goal = find_goal();

        setImageViews();
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public int getMap(int x, int y) {
        if (x < 0 || width <= x || y < 0 || height <= y) {
            return -1;
        }
        return maps[y][x];
    }

    public ImageView getImageView(int x, int y) {
        return mapImageViews[y][x];
    }

    public void setMap(int x, int y, int type){
        if (x < 1 || width <= x-1 || y < 1 || height <= y-1) {
            return;
        }
        maps[y][x] = type;
    }

	// set images based on two-dimentional arrays (maps[y][x])
    public void setImageViews() {
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                mapImageViews[y][x] = new ImageView(mapImages[maps[y][x]]);
            }
        }
    }

	// fill two-dimentional arrays with a given number (maps[y][x])
    public void fillMap(int type){
        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                maps[y][x] = type;
            }
        }
    }

	// dig walls for creating trails
    public void digMap(int x, int y){
        setMap(x, y, MapData.TYPE_SPACE);

        int[][] dl = shuffle();

        for (int i=0; i<dl.length; i++){
            int dx = dl[i][0];
            int dy = dl[i][1];
            if (getMap(x+dx*2, y+dy*2) == MapData.TYPE_WALL){
                setMap(x+dx, y+dy, MapData.TYPE_SPACE);
                digMap(x+dx*2, y+dy*2);
            }
        }
    }

    // shuffle dx and dy
    public int[][] shuffle() {
        int[][] dl = {{0,1},{0,-1},{-1,0},{1,0}};
        int[] tmp;

        for (int i=0; i<dl.length; i++) {
            int r = (int)(Math.random()*dl.length);
            tmp = dl[i];
            dl[i] = dl[r];
            dl[r] = tmp;
        }

        return dl;
    }

    // find goal. The goal is defined as the maximum manhattan distance and the maximum dist.
    public int[] find_goal() {
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, -1, 0, 1};
        int max_manhattan= -1;
        int max_dist = -1;
        int[] ans = {-1, -1};
        int[][] dists = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                dists[i][j] = -1;
            }
        }

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {1, 1});
        dists[1][1] = 0;

        // bfs
        while (!queue.isEmpty()) {
            int[] now = queue.poll();
            for (int i = 0; i < 4; i++) {
                if (!(0 <= now[0] + dx[i] && now[0] + dx[i] < width &&
                        0 <= now[1] + dy[i] && now[1] + dy[i] < height)) {
                    continue;
                }

                int next_x = now[0] + dx[i];
                int next_y = now[1] + dy[i];

                if (dists[next_y][next_x] != -1 && dists[next_y][next_x] < dists[now[1]][now[0]] + 1) {
                    continue;
                }
                if (getMap(next_x, next_y) == MapData.TYPE_SPACE) {
                    queue.add(new int[]{next_x, next_y});
                    dists[next_y][next_x] = dists[now[1]][now[0]] + 1;
                    if (max_dist < dists[next_y][next_x] ||
                            (max_dist == dists[next_y][next_x] && max_manhattan < manhattan_dist(1, 1, next_x, next_y))) {
                        max_manhattan = manhattan_dist(1, 1, next_x, next_y);
                        max_dist = dists[next_y][next_x];
                        ans = new int[]{next_x, next_y};
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
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, -1, 0, 1};

        int[][] route = new int[dists[goal[1]][goal[0]] + 1][2];
        int route_i = dists[goal[1]][goal[0]];

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {goal[0], goal[1]});

        // bfs
        while (!queue.isEmpty() && route_i > 1) {
            int[] now = queue.poll();
            for (int i = 0; i < 4; i++) {
                if (!(0 <= now[0] + dx[i] && now[0] + dx[i] < width &&
                        0 <= now[1] + dy[i] && now[1] + dy[i] < height)) {
                    continue;
                }

                int next_x = now[0] + dx[i];
                int next_y = now[1] + dy[i];
                if (dists[next_y][next_x] == dists[now[1]][now[0]] - 1) {
                    queue.add(new int[]{next_x, next_y});
                    route[route_i--] = new int[]{next_x, next_y};
                    break;
                }
            }
        }

        route[0] = new int[] {1, 1};

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
