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

    public FilmesAdapter(Activity mContext, List<Filme> mFilme) {
        super(mContext, 0, mFilme);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        //criando a view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_filme, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgViewCartaz);
            convertView.setTag(holder);
        }
        //reciclando a view
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Filme filme = getItem(position);
        Picasso.with(getContext()).load(BASE_URL_IMAGENS + "/w" + getContext().getString(R.string.tamanho_img_grid) + filme.getPoster_path()).into(holder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
