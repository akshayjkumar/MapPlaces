# Around Me

![App icon](https://github.com/akshayjkumar/MapPlaces/blob/master/screenshots/ic_launcher.png?raw=true)

This is a simple map application that lets user search for locations or places around them. Data for location and places are fetched from Google Maps & Places APIs using Retrofit networking library for Android.

## Fundamentals:

Application architecture follows MVP pattern with a focus on improved testability. This app leverages the power of reactive programming because of its dynamic nature. Search functionality is powered by Google Maps and Places API. Persistance, though not a requirement for an application of this kind, is implemented using Realm, which is a light-weight and fast alternative to SQLite or other Android ORMs. Parallelism and concurrency achieved through reactive programing helps to react to multiple asynchronous sources of data input such as API response, realm data or any new custom datasources.

## Libraries used:

Retrofit2, Lambok, Dagger, Butter Knife, RxJava/ RxAndroid, Picasso, Android Support libraries and Google Play Services.

## Unit Tests:

Unit tests have been implemented to get maximum coverage. Implemented tests verify Presenter-view interactions, Model and its data consistency and Data query and reterival mechanism of data source. Tests implemented in this project uses Robolectric, Mockito and PowerMocks.

## Configuration details:

Minimum SDK Support - Andorid v21
Compiled SDK - Android v26
Android Studio (2.3)
Gradle 2.3.3

## Test Devices:

Google Nexus 6P, Google Nexus 5, Samsung Galaxy S4

## Tested Android Versions:

Android L, Android M, Andorid N, Android O

## FAQs:

1. If getters and setters are not resolvable in Android Studio?
Please configure Lambok plugin

2. If Robolectric tests fails due to Manifest.xml issues?
Please configure the test to point to right working directories