package tgo1014.filmespopulares.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tgo1014.filmespopulares.Activities.FilmeActivity;
import tgo1014.filmespopulares.Filmes.Filme;
import tgo1014.filmespopulares.Filmes.FilmesAdapter;
import tgo1014.filmespopulares.R;
import tgo1014.filmespopulares.Util.NetworkUtil;

public class MainActivityFragment extends Fragment {

    private static final String PREF_MAIS_POPULARES = "0";
    private static final String PREF_MELHORES_CLASSIFICADOS = "1";

    FilmesAdapter mFilmesAdapter;
    ArrayList<Filme> listaFilmes;
    GridView gridFilmes;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        listaFilmes = new ArrayList<>();
        mFilmesAdapter = new FilmesAdapter(getActivity(), listaFilmes);
        gridFilmes = (GridView) view.findViewById(R.id.gridFilmes);

        gridFilmes.setAdapter(mFilmesAdapter);
        gridFilmes.setColumnWidth(Integer.parseInt(getString(R.string.tamanho_img_grid))); //define o tamanho do grid conforme o tamanho da img que ele baixar
        gridFilmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getContext(), FilmeActivity.class).putExtra(getString(R.string.extra_filme), mFilmesAdapter.getItem(position)));
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        carregaFilmes();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregaFilmes();
    }

    public void carregaFilmes() {
        if (NetworkUtil.estaConectado(getContext())) {
            requisicaoDeFilme(getContext(), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Código não esperado: " + response);

                    try {
                        //salva o resultado nas preferencias para ser utilizado pelo adapter
                        List<Filme> filmes = NetworkUtil.montaListFilmes(NetworkUtil.getFilmesPopularesJSON(response.body().string()));

                        //Essa variável já foi associada ao adapter anteriormente, entao basta atualizá-la e notificar o adapter que houve mudanca na lista
                        listaFilmes.clear();
                        listaFilmes.addAll(filmes);

                        //No Android, somente a thread que criou o componente de view pode acessá-lo. Por isso precisamos do Handler e do Looper neste cenário, tá? :)
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //neste ponto notificamos o adapter sobre a mudanca e ela eh refletida na tela
                                mFilmesAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            Snackbar snackbar = Snackbar.make(getView(), getString(R.string.txt_sem_conexao), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.txt_tentar_novamente, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carregaFilmes();
                }
            });
            snackbar.show();
        }
    }

    public static void requisicaoDeFilme(final Context context, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String URL_BASE_FILMES;
        final String LINGUA_PARAM = "language";
        final String API_PARAM = "api_key";
        String lingua = "pt-br";

        String ordem_classificacao = Hawk.get(context.getString(R.string.pref_classificao_key), context.getString(R.string.pref_classificao_valor_padrao));

        switch (ordem_classificacao) {
            case PREF_MAIS_POPULARES:
                URL_BASE_FILMES = "https://api.themoviedb.org/3/movie/popular?";
                break;
            case PREF_MELHORES_CLASSIFICADOS:
                URL_BASE_FILMES = "https://api.themoviedb.org/3/movie/top_rated?";
                break;
            default:
                URL_BASE_FILMES = "https://api.themoviedb.org/3/movie/popular?";
        }

        HttpUrl.Builder builder = HttpUrl.parse(URL_BASE_FILMES).newBuilder();
        builder.addQueryParameter(LINGUA_PARAM, lingua);
        builder.addQueryParameter(API_PARAM, context.getString(R.string.MOVIE_DB_API_KEY));
        String url = builder.build().toString();

        Request request = new Request.Builder().url(url).build();

        //chamada assincrona
        client.newCall(request).enqueue(callback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_populares:
                Hawk.put(getString(R.string.pref_classificao_key), "0");
                break;
            case R.id.id_melhores_avaliados:
                Hawk.put(getString(R.string.pref_classificao_key), "1");
                break;
        }
        carregaFilmes();
        return super.onOptionsItemSelected(item);
    }
}
