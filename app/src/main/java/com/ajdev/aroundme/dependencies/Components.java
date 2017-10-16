package com.ajdev.aroundme.dependencies;

import com.ajdev.aroundme.view.activity.NearbyPlacesActivity;
import com.ajdev.aroundme.view.fragment.PlaceDetailsBSFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Akshay.Jayakumar on 10/13/2017.
 *
 * This Component class is used by Dagger for dependency injection.
 * Component interface defines modules and modules provides the necessary
 * dependencies.
 */

@Singleton
@Component(modules = DIModules.class)
public interface Components {
    void inject(NearbyPlacesActivity nearbyPlacesActivity);
    void inject(PlaceDetailsBSFragment placeDetailsBSFragment);
}
