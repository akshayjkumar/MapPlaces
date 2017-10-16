package com.ajdev.aroundme.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by akshay.jayakumar on 26-Apr-17.
 *
 * This util class helps to manage FontAwesome icons.
 * This utility class contains method that can render the font-awesome uni-code
 * into beautiful icons.
 */

public class FontManager {

    // Statics for directory, root and path.
    public static final String ROOT = "fonts/", FONTAWESOME = ROOT + "fontawesome.ttf";
    // Star patterns for ratings
    public static String star = "\uf005 ", starHalf = "\uf123 ", starEmpty = "\uf006 ";
    // Cache font. Once font is read from the resource it need not be pulled from the resource again.
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    /**
     * Convenience method for fetching font from resources and caching it.
     * @param font
     * @param context
     * @return
     */
    public static Typeface getTypeface(String font, Context context) {
        Typeface tf = fontCache.get(font);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), font);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(font, tf);
        }
        return tf;
    }

    /**
     * This is a convenience method that will render font of a view or child views of a viewGroup
     * into font-awesome icons.
     * @param v
     * @param typeface
     */
    public static void convertToFontAwesome(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                convertToFontAwesome(child, typeface);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        }
    }

    /**
     * Be judicious when using static. Make sure to clear the cache after use.
     * Performing clean up after use helps from memory leak.
     * Empty the cache after use.
     */
    public static void clearFontCache(){
        if(fontCache != null && !fontCache.isEmpty())
            fontCache.clear();
    }

    /**
     * This utility method will convert the rating value into visual presentation of
     * the rating.
     * @param rating
     * @return
     */
    public static String createVisualRatings(float rating){
        int maxRating = 5;
        String ratingDefault = createStarPatterns(starEmpty,maxRating);
        if(rating > -1 && rating <=maxRating){
            int floor = (int) Math.floor(rating);
            ratingDefault = createStarPatterns(star,floor);
            if((rating % 1) >= 0.5)
                ratingDefault += createStarPatterns(starHalf,1) + createStarPatterns(starEmpty,maxRating - floor - 1);
            else
                ratingDefault += createStarPatterns(starEmpty,maxRating - floor);

        }
        return ratingDefault;
    }

    /**
     * Create patterns of empty stars for rating
     * @param count
     * @return
     */
    public static String createStarPatterns(String stars,int count){
        String pattern = "";
        for(int i=0; i < count; i++){
            pattern+=stars;
        }
        return pattern;
    }

}
