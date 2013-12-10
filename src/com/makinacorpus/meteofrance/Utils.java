package com.makinacorpus.meteofrance;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;

public class Utils {
	public static SharedPreferences settings;//pour le parametrage de l'app
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected());
		}
	
	public static Location getCurrentLocation(Context context) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = null;
        try {
                currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
                if(currentLocation == null) {
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
                }
        } catch(Exception ex) {
                // GPS and wireless networks are disabled
        }
        return currentLocation;
}
	
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

}
