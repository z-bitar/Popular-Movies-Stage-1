package net.magic_packets.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent =getActivity().getIntent();
        View root=inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent!= null && intent.hasExtra("title")&& intent.hasExtra("poster")&& intent.hasExtra("rating")&& intent.hasExtra("date")&& intent.hasExtra("overview")){

            String title=intent.getExtras().getString("title");
            String poster=intent.getExtras().getString("poster");
            String rating=intent.getExtras().getString("rating");
            String date=intent.getExtras().getString("date");
            String overview=intent.getExtras().getString("overview");


            ((TextView) root.findViewById(R.id.title)).setText(title);
            ((TextView) root.findViewById(R.id.user_rating_value)).setText(rating);
            ((TextView) root.findViewById(R.id.release_date_value)).setText(date);
            ((TextView) root.findViewById(R.id.overview)).setText(Html.fromHtml("<b><font color=#000000 >Plot Synopsis:</font></b>&nbsp;"+overview));


            ImageView image=(ImageView)root.findViewById(R.id.imageView);
           Picasso.with(getContext()).load(poster).noPlaceholder().error(R.drawable.error).into(image);


        }
        return root;
    }
}
