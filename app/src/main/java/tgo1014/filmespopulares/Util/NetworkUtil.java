package tgo1014.filmespopulares.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tgo1014.filmespopulares.Filmes.Filme;
import tgo1014.filmespopulares.Filmes.FilmeProcessor;

/**
 * Created by tgo10 on 16/04/2017.
 */

public class NetworkUtil {

    public static boolean estaConectado(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static JSONArray getFilmesPopularesJSON(String jsonString) throws JSONException {
        return new JSONObject(jsonString).getJSONArray("results");
    }

    public static List<Filme> montaListFilmes(JSONArray filmesArray) {
        List<Filme> filmeList = new ArrayList<>();

        for (int i = 0; i < filmesArray.length(); i++) {
            try {
                JSONObject filmeJson = filmesArray.getJSONObject(i);
                Filme filme = FilmeProcessor.process(filmeJson);
                filmeList.add(filme);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return filmeList;
    }
}
