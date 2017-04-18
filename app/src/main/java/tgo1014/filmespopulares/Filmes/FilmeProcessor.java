package tgo1014.filmespopulares.Filmes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tgo10 on 17/04/2017.
 */

public class FilmeProcessor {
    public static Filme process(JSONObject filmeJson) {

        Filme filme = new Filme();

        try {
            filme.setVote_average(filmeJson.getString("vote_average"));
            filme.setBackdrop_path(filmeJson.getString("backdrop_path"));
            filme.setAdult(filmeJson.getString("adult"));
            filme.setId(filmeJson.getString("id"));
            filme.setTitle(filmeJson.getString("title"));
            filme.setOverview(filmeJson.getString("overview"));
            filme.setOriginal_language(filmeJson.getString("original_language"));
            filme.setGenre_ids(null);
            filme.setRelease_date(filmeJson.getString("release_date"));
            filme.setOriginal_title(filmeJson.getString("original_title"));
            filme.setVote_count(filmeJson.getString("vote_count"));
            filme.setPoster_path(filmeJson.getString("poster_path"));
            filme.setVideo(filmeJson.getString("video"));
            filme.setPopularity(filmeJson.getString("popularity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return filme;
    }
}
