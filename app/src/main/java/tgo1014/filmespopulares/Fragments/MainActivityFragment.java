package tgo1014.filmespopulares.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import tgo1014.filmespopulares.Activities.FilmeActivity;
import tgo1014.filmespopulares.Filmes.Filme;
import tgo1014.filmespopulares.Filmes.FilmesAdapter;
import tgo1014.filmespopulares.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivityFragment extends Fragment {

    FilmesAdapter mFilmesAdapter;
    ArrayList<Filme> listaFilmes;
    GridView gridFilmes;
    static final String PREF_MAIS_POPULARES = "1";
    static final String PREF_MELHORES_CLASSIFICADOS = "2";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        listaFilmes = new ArrayList<>();
        mFilmesAdapter = new FilmesAdapter(getActivity(), listaFilmes);
        gridFilmes = (GridView) view.findViewById(R.id.gridFilmes);

        gridFilmes.setAdapter(mFilmesAdapter);
        gridFilmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getContext(), FilmeActivity.class).putExtra(getString(R.string.extra_filme), mFilmesAdapter.getItem(position)));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (estaConectado())
            new FilmesPopulares().execute();
    }

    public boolean estaConectado() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private class FilmesPopulares extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader;

            try {
                String URL_BASE_FILMES_POPULARES = null;

                switch (getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_classificao_key), getString(R.string.pref_valor_padrao))) {
                    case PREF_MAIS_POPULARES:
                        URL_BASE_FILMES_POPULARES = "https://api.themoviedb.org/3/movie/popular?";
                        break;
                    case PREF_MELHORES_CLASSIFICADOS:
                        URL_BASE_FILMES_POPULARES = "https://api.themoviedb.org/3/movie/top_rated?";
                        break;
                }

                final String LINGUA_PARAM = "language";
                final String API_PARAM = "api_key";
                String lingua = "pt-br";

                Uri uri = Uri.parse(URL_BASE_FILMES_POPULARES).buildUpon()
                        .appendQueryParameter(API_PARAM, getString(R.string.MOVIE_DB_API_KEY))
                        .appendQueryParameter(LINGUA_PARAM, lingua)
                        .build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) return null;

                return getFilmesPopularesJSON(buffer.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray.length() != 0) {
                mFilmesAdapter.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject filmeJson = jsonArray.getJSONObject(i);
                        Filme filme = new Filme(filmeJson);
                        mFilmesAdapter.add(filme);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onPostExecute(jsonArray);
        }

        private JSONArray getFilmesPopularesJSON(String jsonString) throws JSONException {
            return new JSONObject(jsonString).getJSONArray("results");
        }
    }
}
