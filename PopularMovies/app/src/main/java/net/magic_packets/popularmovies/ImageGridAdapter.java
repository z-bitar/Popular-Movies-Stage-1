package net.magic_packets.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageGridAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> imageUrls;

    ImageView image;

    public ImageGridAdapter(Context context, List<String> imageUrls) {
        super(context,0,imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if (convertView == null) {
            image = new ImageView(context);
            image.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            image = (ImageView) convertView;
        }

        Picasso
                .with(context)
                .load(imageUrls.get(position))
                .noPlaceholder()
                .into(image);

        return image;
    }

}