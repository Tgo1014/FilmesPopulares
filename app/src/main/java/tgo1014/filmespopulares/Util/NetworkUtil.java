package tgo1014.filmespopulares.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orhanobut.hawk.Hawk;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tgo1014.filmespopulares.Filmes.Filme;
import tgo1014.filmespopulares.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by tgo10 on 16/04/2017.
 */

public class NetworkUtil {

    private static final String PREF_MAIS_POPULARES = "1";
    private static final String PREF_MELHORES_CLASSIFICADOS = "2";

    public static boolean estaConectado(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void requisicaoDeFilme(final Context context) {
        OkHttpClient client = new OkHttpClient();
        String URL_BASE_FILMES = null;
        final String LINGUA_PARAM = "language";
        final String API_PARAM = "api_key";
        String lingua = "pt-br";

        switch (getDefaultSharedPreferences(context.getApplicationContext()).getString(context.getString(R.string.pref_classificao_key), context.getString(R.string.pref_valor_padrao))) {
            case PREF_MAIS_POPULARES:
                URL_BASE_FILMES = "https://api.themoviedb.org/3/movie/popular?";
                break;
            case PREF_MELHORES_CLASSIFICADOS:
                URL_BASE_FILMES = "https://api.themoviedb.org/3/movie/top_rated?";
                break;
        }

        HttpUrl.Builder builder = HttpUrl.parse(URL_BASE_FILMES).newBuilder();
        builder.addQueryParameter(LINGUA_PARAM, lingua);
        builder.addQueryParameter(API_PARAM, context.getString(R.string.MOVIE_DB_API_KEY));
        String url = builder.build().toString();

        Request request = new Request.Builder().url(url).build();

        //chamada assincrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Código não esperado: " + response);
                try {
                    JSONArray filmeList = getFilmesPopularesJSON(response.body().string());
                    //salva o resultado nas preferencias para ser utilizado pelo adapter
                    Hawk.put(context.getString(R.string.pref_array_filmes), montaListFilmes(filmeList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static JSONArray getFilmesPopularesJSON(String jsonString) throws JSONException {
        return new JSONObject(jsonString).getJSONArray("results");
    }

    private static List<Filme> montaListFilmes(JSONArray filmesArray) {
        List<Filme> filmeList = new ArrayList<>();

        for (int i = 0; i < filmesArray.length(); i++) {
            try {
                JSONObject filmeJson = filmesArray.getJSONObject(i);
                Filme filme = new Filme(filmeJson);
                filmeList.add(filme);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return filmeList;
    }
}
