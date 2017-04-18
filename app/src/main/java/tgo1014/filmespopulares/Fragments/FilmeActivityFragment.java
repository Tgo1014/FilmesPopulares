package tgo1014.filmespopulares.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tgo1014.filmespopulares.Filmes.Filme;
import tgo1014.filmespopulares.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FilmeActivityFragment extends Fragment {

    Filme filme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filme, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            filme = intent.getExtras().getParcelable(getString(R.string.extra_filme));
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        configuraFilme(filme);
    }

    public void configuraFilme(Filme filme) {
        final String BASE_URL_IMAGENS = "http://image.tmdb.org/t/p";
        final String TAMANHO_IMAGEM = "/w500"; //"w92", "w154", "w185", "w342", "w500", "w780", or "original"

        Picasso.with(getActivity()).load(BASE_URL_IMAGENS + TAMANHO_IMAGEM + filme.getPoster_path()).into((ImageView) getActivity().findViewById(R.id.imgPoster));
        ((TextView) getActivity().findViewById(R.id.txtNomeFilme)).setText(filme.getOriginal_title());
        ((TextView) getActivity().findViewById(R.id.txtSinopse)).setText(filme.getOverview());
        ((TextView) getActivity().findViewById(R.id.txtAvaliacao)).setText(filme.getVote_average());
        ((TextView) getActivity().findViewById(R.id.txtDataLancamento)).setText(filme.getRelease_date());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(filme.getTitle());

    }
}
