package com.ajdev.aroundme.dependencies;

import android.app.Application;

import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.adapters.SearchResultAdapter;
import com.ajdev.aroundme.dao.NearByPlacesDAO;
import com.ajdev.aroundme.dao.PlaceDetailsDAO;
import com.ajdev.aroundme.datasource.NearByPlacesDS;
import com.ajdev.aroundme.datasource.PlaceDetailsDS;
import com.ajdev.aroundme.network.ConnectionAPIInterface;
import com.ajdev.aroundme.presenter.NearByPlacesPresenter;
import com.ajdev.aroundme.presenter.PlaceDetailsPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * This class provides objects that can be injected.
 * Modules contains Providers that are available for dependency injection
 */

@Module
public class DIModules {

    private Application application;

    public DIModules(Application application) {
        this.application = application;
    }

    /**
     * Provides a dependency of NearByPlacesDAO
     */
    @Provides
    NearByPlacesDAO provideNearByPlacesDAO(){
        return new NearByPlacesDAO();
    }

    /**
     * Provides a dependency of NearByPlacesPresenter
     */
    @Provides
    NearByPlacesPresenter provideNearByPlacesPresenter(NearByPlacesDS nearByPlacesDS){
        return new NearByPlacesPresenter(nearByPlacesDS);
    }

    /**
     * Provides a dependency of NearByPlaces data source
     */
    @Provides
    NearByPlacesDS provideNearByPlacesDS(
            ConnectionAPIInterface connectionAPIInterface, NearByPlacesDAO nearByPlacesDAO){
        return new NearByPlacesDS(connectionAPIInterface, nearByPlacesDAO);
    }

    /**
     * Provides a dependency of PlaceDetailsDAO
     */
    @Provides
    PlaceDetailsDAO providePlaceDetailsDAO(){
        return new PlaceDetailsDAO();
    }

    /**
     * Provides a dependency of PlaceDetailsPresenter
     */
    @Provides
    PlaceDetailsPresenter providePlaceDetailsPresenter(PlaceDetailsDS placeDetailsDS){
        return new PlaceDetailsPresenter(placeDetailsDS);
    }

    /**
     * Provides a dependency of Place details data source
     */
    @Provides
    PlaceDetailsDS providePlaceDetailsDS(
            ConnectionAPIInterface connectionAPIInterface, PlaceDetailsDAO placeDetailsDAO){
        return new PlaceDetailsDS(connectionAPIInterface, placeDetailsDAO);
    }

    /**
     * Provides a dependency of ConnectionAPI Interface for Retrofit
     */
    @Provides
    ConnectionAPIInterface provideConnectionAPIInterface(OkHttpClient client){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.GOOGLE_MAPS_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
        return retrofit.create(ConnectionAPIInterface.class);
    }

    /* Provides a dependency for OkHttpclient. To be used by Retrofit */
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(getLogginInterceptor()).build();
    }

    /**
     * Provides dependency of HttpLoggingInterceptor. This is
     * used with OkHttpClient for logging the request
     */
    @Provides
    @Singleton
     HttpLoggingInterceptor getLogginInterceptor() {
         // set the desired log level required
         return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /* Data adapter for search result recyclerView */
    @Provides
    SearchResultAdapter provideSearchResultAdapter() {
        return new SearchResultAdapter(application.getApplicationContext());
    }

}
