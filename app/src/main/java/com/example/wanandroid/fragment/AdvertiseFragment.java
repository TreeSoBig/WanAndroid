package com.example.wanandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wanandroid.activity.AdvertiseDetailsActivity;
import com.example.wanandroid.R;
import com.example.wanandroid.custom.UrlImageView;
public class AdvertiseFragment extends Fragment {
    private UrlImageView tvShow;
    private TextView mText;
    public AdvertiseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advertise, container, false);
        tvShow = view.findViewById(R.id.img_advertise);
        mText = view.findViewById(R.id.tv_advertise);
        Bundle arguments = getArguments();
        String Url = arguments.getString("Url");
        String title = arguments.getString("title");
        String link = arguments.getString("link");
        tvShow.setImageURL(Url);
        mText.setText(title);
        tvShow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdvertiseDetailsActivity.class);
                intent.putExtra("Ban_data",link);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });

        return view;
    }
}
