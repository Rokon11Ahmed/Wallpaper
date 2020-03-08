package com.example.wallpaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import static com.example.wallpaper.MyUtils.counter;
import static com.example.wallpaper.MyUtils.getContactBitmapFromURI;
import static com.example.wallpaper.MyUtils.getInternalFile;
import static com.example.wallpaper.MyUtils.setBgOrDownload;
import static com.example.wallpaper.MyUtils.setHomeOrLock;
import static com.example.wallpaper.MyUtils.saveCropedImage;

import java.io.IOException;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_set_bg, fab_home, fab_lock;
    private SpinKitView spinKitView;

    private String url;
    private InterstitialAd mInterstitialAd;
    private AdListener interstitialAdListener = new AdListener(){
        public void onAdClosed() {
            super.onAdClosed();
            loadImage();
            spinKitView.setVisibility(View.GONE);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            spinKitView.setVisibility(View.GONE);
            mInterstitialAd.show();

        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            spinKitView.setVisibility(View.GONE);
            loadImage();
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);
        MobileAds.initialize(this, getResources().getString(R.string.app_id));

        imageView = findViewById(R.id.preview_imageview);
        fab_menu = findViewById(R.id.menu_labels_right);
        fab_set_bg = findViewById(R.id.fab_set_bg);
        spinKitView = findViewById(R.id.spin_kit);
        fab_home = findViewById(R.id.fab_set_home);
        fab_lock = findViewById(R.id.fab_set_lock);
        url = getIntent().getStringExtra("image_url");

        fab_set_bg.setOnClickListener(this);
        fab_home.setOnClickListener(this);
        fab_lock.setOnClickListener(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));
        mInterstitialAd.setAdListener(interstitialAdListener);

        if (counter % 5 == 0) {
            spinKitView.setVisibility(View.VISIBLE);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        } else {
            loadImage();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_set_bg:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);

                setBgOrDownload(url, this, false);

                break;

            case R.id.fab_set_home:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);

                setHomeOrLock(url, this, false);
                break;
            case R.id.fab_set_lock:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);

                setHomeOrLock(url, this, true);

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
    private void loadImage() {
        if (url != null && !url.equals("")) {
            Picasso.get().load(url).fit().centerCrop().into(imageView);
        }
    }

    public void wallpaperSet(boolean b) {
        runOnUiThread(() -> {
            if (b) {
                Toast.makeText(this, "Background is set", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();

            }
            spinKitView.setVisibility(View.GONE);
        });

    }

    public void homeOrLockSet(boolean lock)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(lock)
                {
                    Toast.makeText(PreviewActivity.this, "Set as Lockscreen background", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(PreviewActivity.this, "Set as Home background", Toast.LENGTH_SHORT).show();
                }
                spinKitView.setVisibility(View.GONE);
            }
        });
    }

    public void downloadComplete(boolean downloaded) {
        if (downloaded) {
            Toast.makeText(this, "Download Complete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Download Failed! Please try again", Toast.LENGTH_SHORT).show();
        }
        spinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("Save Cropped Image")
                        .setMessage("Do you want to save your cropped image?")


                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            Bitmap bitmap = getContactBitmapFromURI(this, resultUri);
                            saveCropedImage(this, url, bitmap);
                            dialog.dismiss();
                        })

                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                        .show();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}