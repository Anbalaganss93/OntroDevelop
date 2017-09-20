package com.ontro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class VenueImageView extends AppCompatActivity implements View.OnClickListener {
    private TouchImageView imageViewfullsize;
    private String tournamentAdPicture = "";
    private ImageView backicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        setContentView(R.layout.activity_venue_image_view);
        Intent intent = getIntent();

        imageViewfullsize = (TouchImageView) findViewById(R.id.imageViewfullsize);
        backicon = (ImageView) findViewById(R.id.backicon);

        if (intent != null && intent.hasExtra("image")) {
            tournamentAdPicture = intent.getStringExtra("image");
            if (tournamentAdPicture.length() != 0) {
                Glide.with(VenueImageView.this).load(tournamentAdPicture).placeholder(R.drawable.match_default).dontAnimate().into(imageViewfullsize);
            } else {
                Glide.with(VenueImageView.this).load(R.drawable.match_default).dontAnimate().into(imageViewfullsize);
            }
        }

        backicon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backicon:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
