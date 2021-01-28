import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class ScoreManager {
    static final String url = "http://localhost:8000";

    public class ScoreInfo {
        public String name;
        public int score;
        public String created_at;
    }
    
    public static Boolean postScore(String name, int score) throws Exception {
        String payload = "{ \"name\": \"" + name + "\", " + "\"score\": " + score + "}";
        System.out.println(payload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/scores"))
                .POST(HttpRequest.BodyPublishers.ofString(payload)).build();

        var res = client.send(request, HttpResponse.BodyHandlers.ofString());

        return res.statusCode() == 202;
    }

    
    public static List<ScoreInfo> getScore() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/scores")).GET().build();

        var res = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            return null;
        }

        Gson gson = new Gson();
        var scores = gson.fromJson(res.body(), ScoreInfo[].class);

        return Arrays.asList(scores);
    }
}
