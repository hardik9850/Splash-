package com.splash;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import com.splash.bitmap.BitmapProcessing;
import com.splash.library.Constants;
import com.splash.library.Toaster;
import com.splash.library.UriToUrl;
import com.splash.model.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.mrapp.android.dialog.MaterialDialogBuilder;

/**
 * Created by Hardik9850 on 10-Sep-15.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    ImageView imageView;
    CropImageView croppedImageView;
    RecyclerView recyclerView;
    Bitmap bitmap;
    int brightnessValue;
    private String options[] = {"Crop", "Brightness", "Noise", "Gaussian", "Grayscale" , "Save"};
    private Button applyButton;
    private RecyclerViewAdapter adapter;
    private SeekBar brightnessSeekBar;
    private Uri imageUri;
    private String strImageUrl;
    private final int HIDE_VIEW = 8, SHOW_VIEW = 0;
    private final int CROP=0;
    private final int BRIGHTNESS=1;
    private final int NOISE=2;
    private final int GAUSSIAN=3;
    private final int GRAYSCALE=4;
    private final int SAVEIMAGE=5;
    private Bitmap globalBitmap=null;

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
                storeBitmap(bitmap);
                setupAdapter();
            } else {
                Toaster.make(getApplicationContext(), "Image not found");

            }
        }
    }

    private void storeBitmap(Bitmap bitmap) {
        globalBitmap = bitmap;
    }

    Bitmap getBitmap(){
        return globalBitmap;
    }

    private void setImage(Bitmap bitmap) {
        try {
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toaster.make(getApplicationContext(), "Error setting Image ! ");

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
            case CROP:
                cropImage();
                break;

            case BRIGHTNESS:
                changeBrightness();
                break;

            case NOISE:
                applyNoise();
                break;

            case GAUSSIAN:
                applyGaussian();
                break;

            case GRAYSCALE:
                applyGrayscale();
                break;

            case SAVEIMAGE:
                try {
                    saveImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }


    private void changeBrightness() {
        changeToolBoxVisibility(View.GONE);
        applyButton.setText("brightness");
        changeBrightnessSeekbarVisibility();
        applyButton.setVisibility(View.VISIBLE);
    }

    private void changeBrightnessSeekbarVisibility() {
        brightnessSeekBar.setVisibility(brightnessSeekBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }


    private void cropImage() {
        applyButton.setText("crop");
        changeToolBoxVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        croppedImageView.setImageBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        croppedImageView.setVisibility(View.VISIBLE);
        applyButton.setVisibility(View.VISIBLE);
    }

    private void onCropPressed() {
        adapter.bitmapImage = croppedImageView.getCroppedBitmap();
        bitmap = adapter.bitmapImage;

        imageView.setImageResource(0);
        imageView.setImageBitmap(croppedImageView.getCroppedBitmap());
        imageView.setVisibility(View.VISIBLE);
        croppedImageView.setImageBitmap(null);
        try {
            ((BitmapDrawable) croppedImageView.getDrawable()).getBitmap().recycle();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        croppedImageView.setVisibility(View.GONE);
        applyButton.setVisibility(View.GONE);
        changeToolBoxVisibility(View.VISIBLE);
        storeBitmap(croppedImageView.getCroppedBitmap());
    }

    private void applyNoise() {
        BitmapDrawable bitmapDrawable=(BitmapDrawable)imageView.getDrawable();
        bitmap = BitmapProcessing.noise(bitmapDrawable.getBitmap());
        setImage(bitmap);
        storeBitmap(bitmap);
    }

    private void applyGaussian(){
        BitmapDrawable bitmapDrawable=(BitmapDrawable)imageView.getDrawable();
        bitmap = BitmapProcessing.gaussian(bitmapDrawable.getBitmap());
        setImage(bitmap);
        storeBitmap(bitmap);
    }


    private void applyGrayscale(){
        BitmapDrawable bitmapDrawable=(BitmapDrawable)imageView.getDrawable();
        bitmap = BitmapProcessing.grayscale(bitmapDrawable.getBitmap());
        setImage(bitmap);
        storeBitmap(bitmap);
    }

    private void saveImage() throws Exception{
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap=imageView.getDrawingCache();
        isDirectoryExist();
        FileOutputStream fos=new FileOutputStream(Environment.getExternalStorageDirectory()
                .toString()+"/Photo lab/"+getFilename());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
        Toast.makeText(getApplicationContext(),"Saving image",Toast.LENGTH_LONG).show();
    }

    private void isDirectoryExist(){
        File photolabDir=new File(Environment.getExternalStorageDirectory()+"/Photo lab/");
        if(!(photolabDir.exists() || photolabDir.isDirectory())){
            photolabDir.mkdir();
        }
    }

    private String getFilename(){
        return "Photo lab_"+System.currentTimeMillis()+".jpg";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop:
                if(((Button)v).getText().equals("crop"))
                onCropPressed();
                else
                onBrightnessPressed();
                break;

        }
    }

    private void onBrightnessPressed(){
        changeBrightnessSeekbarVisibility();
        changeToolBoxVisibility(View.VISIBLE);
        applyButton.setVisibility(View.GONE);
    }

    private void bindViews() {
        imageView = (ImageView) findViewById(R.id.img_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_img_view);
        croppedImageView = (CropImageView) findViewById(R.id.cropped_img_view);
        applyButton = (Button) findViewById(R.id.btn_crop);
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
        applyButton.setOnClickListener(this);
        brightnessSeekBar.setOnSeekBarChangeListener(brightnessListener);
        brightnessSeekBar.setProgress(200);
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
            Log.d("Scale",""+seekBar.getProgress());
            applyBrightness(seekBar.getProgress());
        }
    };

    private void changeToolBoxVisibility(int option) {
        recyclerView.setVisibility(option == 8 ? View.GONE : View.VISIBLE);
    }

    private void applyBrightness(int brightValue){
        bitmap = globalBitmap.copy(globalBitmap.getConfig(), true);
        bitmap = BitmapProcessing.brightness(bitmap, brightValue - 200);
        setImage(bitmap);
    }

}
