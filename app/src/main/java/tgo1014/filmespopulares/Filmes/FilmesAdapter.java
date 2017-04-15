package tgo1014.filmespopulares.Filmes;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tgo1014.filmespopulares.R;

/**
 * Created by dev on 09/04/2017.
 */

public class FilmesAdapter extends ArrayAdapter<Filme> {

    static final String BASE_URL_IMAGENS = "http://image.tmdb.org/t/p";
    static final String TAMANHO_IMAGEM = "/w342"; //"w92", "w154", "w185", "w342", "w500", "w780", or "original"

    public FilmesAdapter(Activity mContext, List<Filme> mFilme) {
        super(mContext, 0, mFilme);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Filme filme = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_filme, parent, false);

        ImageView cartaz = (ImageView) convertView.findViewById(R.id.imgViewCartaz);
        Picasso.with(getContext()).load(BASE_URL_IMAGENS + TAMANHO_IMAGEM + filme.getPoster_path()).into(cartaz);

        return convertView;
    }
}
