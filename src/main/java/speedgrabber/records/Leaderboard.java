package speedgrabber.records;

import java.util.ArrayList;

public record Leaderboard(
        String weblink,

        String categoryLink,
        String levelLink,
        String gameLink,

        String timing,
        int numOfRunsInJson,
        ArrayList<String> runLinks,
        ArrayList<Integer> runPlaces

) implements Identifiable {
    @Override
    public String identify() {
        String categoryID = categoryLink.split("/")[categoryLink.split("/").length - 1];
        String gameID = gameLink.split("/")[gameLink.split("/").length - 1];

        if (levelLink != null) {
            String levelID = levelLink.split("/")[levelLink.split("/").length - 1];
            return "https://www.speedrun.com/api/v1/leaderboards/" + gameID + "/level/" + levelID + "/" + categoryID;
        }

        return "https://www.speedrun.com/api/v1/leaderboards/" + gameID + "/category/" + categoryID;
    }
}
