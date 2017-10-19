package com.ajdev.aroundme;

import android.app.Application;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * Created by Akshay.Jayakumar on 10/16/2017.
 *
 * This is an application base class. User for Robolectric unit testing.
 *
 * ONLY FOR TESTING
 */

public class BaseApplicationTest extends Application{

}