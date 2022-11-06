package com.example.jsondogs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private static final String TAG = "MainActivity";
    private ImageView imageViewDog;
    private ProgressBar progressBar;
    private Button buttonLoadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_views();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.loadDogImage();
        viewModel.getIsError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if (isError){
                    Toast.makeText(MainActivity.this, R.string.errorLoading, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (loading)
                    progressBar.setVisibility(View.VISIBLE);
                else
                    progressBar.setVisibility(View.GONE);

            }
        });
        viewModel.getDogImage().observe(this, new Observer<DogImage>() {
            @Override
            public void onChanged(DogImage dogImage) {
                Glide.with(MainActivity.this) // для загрузки картинки
                        .load(dogImage.getMessage())
                        .into(imageViewDog);
            }
        });
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.loadDogImage();
            }
        });

    }


    private void init_views(){
        imageViewDog = findViewById(R.id.imageViewDog);
        progressBar = findViewById(R.id.progressBar);;
        buttonLoadImage = findViewById(R.id.buttonLoadImage);;
    }



}

