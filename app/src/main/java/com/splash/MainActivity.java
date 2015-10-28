package com.splash;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.splash.adapter.RecyclerViewAdapter;
import com.splash.bitmap.BitmapLoader;
import com.splash.library.Constants;
import com.splash.library.Toaster;
import com.splash.library.UriToUrl;
import com.splash.model.Item;

import java.util.ArrayList;

/**
 * Created by Hardik9850 on 10-Sep-15.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    ImageView imageView;
    CropImageView croppedImageView;
    RecyclerView recyclerView;
    Bitmap bitmap;
    private String options[] = {"Crop", "Brightness", "Blur", "Animate", "Flip"};
    private Button cropButton;
    private RecyclerViewAdapter adapter;
    private SeekBar brightnessSeekBar;
    private Uri imageUri;
    private String strImageUrl;
    private final int HIDE_VIEW = 8, SHOW_VIEW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        bindViews();
        setListener();
        loadImage();

    }

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        DisplayMetrics metrics;
        BitmapLoader bitmapLoader;

        public BitmapWorkerTask() {
            metrics = getResources().getDisplayMetrics();

            strImageUrl = UriToUrl.get(getApplicationContext(), imageUri);
            bitmapLoader = new BitmapLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            changeToolBoxVisibility(8);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... arg0) {
            try {
                return bitmapLoader.load(getApplicationContext(), new int[]{metrics.widthPixels, metrics.heightPixels}, strImageUrl);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                changeToolBoxVisibility(0);
                setImage(bitmap);
                setupAdapter();
            } else {
                Toaster.make(getApplicationContext(), "Image not found");

            }
        }
    }

    private void setImage(Bitmap bitmap) {
        try {
            imageView.setImageBitmap(bitmap);
            Toaster.make(getApplicationContext(), "Setting image here");
        } catch (Exception e) {
            Log.e("Error setting bitmpa,", "To image ", e);
        }
    }

    private void loadImage() {
        int source_id = getIntent().getExtras().getInt(Constants.EXTRA_KEY_IMAGE_SOURCE);
        imageUri = getIntent().getData();
        BitmapWorkerTask bitmaporker = new BitmapWorkerTask();
        bitmaporker.execute();
    }


    public void changeUI(int choice) {
        switch (choice) {
            case 0:
                cropImage();
                break;

            case 1:
                changeBrightness();
                break;

        }

    }


    private void changeBrightness() {
        changeToolBoxVisibility(View.GONE);
        changeBrightnessSeekbarVisibility();

    }

    private void changeBrightnessSeekbarVisibility() {
        brightnessSeekBar.setVisibility(brightnessSeekBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }


    private void cropImage() {
        changeToolBoxVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        croppedImageView.setImageBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        croppedImageView.setVisibility(View.VISIBLE);
        cropButton.setVisibility(View.VISIBLE);
    }

    private void onCropPressed() {
        adapter.bitmapImage = croppedImageView.getCroppedBitmap();
        bitmap = adapter.bitmapImage;

        imageView.setImageResource(0);
        imageView.setImageBitmap(croppedImageView.getCroppedBitmap());
        imageView.setVisibility(View.VISIBLE);
        croppedImageView.setImageBitmap(null);
        ((BitmapDrawable) croppedImageView.getDrawable()).getBitmap().recycle();
        croppedImageView.setVisibility(View.GONE);
        cropButton.setVisibility(View.GONE);
        changeToolBoxVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop:
                onCropPressed();
                break;

        }
    }

    private void bindViews() {
        imageView = (ImageView) findViewById(R.id.img_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_img_view);
        croppedImageView = (CropImageView) findViewById(R.id.cropped_img_view);
        cropButton = (Button) findViewById(R.id.btn_crop);
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightness_seekbar);
    }


    private void setupAdapter() {
        ArrayList mTitle = new ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            mTitle.add(new Item(options[i], false));
        }
        adapter = new RecyclerViewAdapter(MainActivity.this, mTitle, bitmap, recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void setListener() {
        cropButton.setOnClickListener(this);
    }

    SeekBar.OnSeekBarChangeListener brightnessListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void changeToolBoxVisibility(int option) {
        recyclerView.setVisibility(option == 8 ? View.GONE : View.VISIBLE);
    }

}
