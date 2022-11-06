package com.example.jsondogs;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static final String BASE_URL = "https://dog.ceo/api/breeds/image/random";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";
    private static final String TAG = "MainViewModel";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<DogImage> dogImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();


    public LiveData<DogImage> getDogImage() {
        return dogImage;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public void loadDogImage(){
        Disposable disposable =  loadDogImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        isError.setValue(false);
                        isLoading.setValue(true); // Когда только начинается загрузка, вызывается этот метод
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Throwable {
                        isLoading.setValue(false);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        isError.setValue(true);

                    }
                })
                .subscribe(new Consumer<DogImage>() {
                    @Override
                    public void accept(DogImage image) throws Throwable {
                        dogImage.setValue(image);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d(TAG, "Error:" + throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<DogImage> loadDogImageRx(){

        return ApiFactory.getApiService().loadDogImage();
//        return Single.fromCallable(new Callable<DogImage>() {
//            @Override
//            public DogImage call() throws Exception {
//                URL url = new URL(BASE_URL);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = urlConnection.getInputStream(); // byte
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // symbols
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // string
//                StringBuilder data = new StringBuilder();
//                String result;
//                do {
//                    result = bufferedReader.readLine();
//                    if (result != null)
//                        data.append(result); // при data+=result забивается память, так как строка неизменяемый обьект,
//                    // при добавлении строк, создается новая строка, а старая строка остается в памяти,
//
//                } while (result != null);
//
//                JSONObject jsonObject = new JSONObject(data.toString());
//                String message = jsonObject.getString(KEY_MESSAGE);
//                String status = jsonObject.getString(KEY_STATUS);
//
//                return new DogImage(message,status);
//
//            }
//        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
