package tgo1014.filmespopulares.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import tgo1014.filmespopulares.Activities.FilmeActivity;
import tgo1014.filmespopulares.Filmes.Filme;
import tgo1014.filmespopulares.Filmes.FilmesAdapter;
import tgo1014.filmespopulares.R;
import tgo1014.filmespopulares.Util.NetworkUtil;

public class MainActivityFragment extends Fragment {

    FilmesAdapter mFilmesAdapter;
    ArrayList<Filme> listaFilmes;
    GridView gridFilmes;

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
    public void onStart() {
        super.onStart();
        carregaFilmes();
    }

    private void carregaFilmes() {
        if (NetworkUtil.estaConectado(getContext())) {
            NetworkUtil.requisicaoDeFilme(getContext());
            List<Filme> filmes = Hawk.get("ARRAY_FILMES");
            mFilmesAdapter.clear();
            for (Filme filme : filmes) {
                mFilmesAdapter.add(filme);
            }
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
}
