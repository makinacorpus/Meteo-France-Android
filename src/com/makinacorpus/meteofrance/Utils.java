package com.makinacorpus.meteofrance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

public class Utils {
	public static SharedPreferences settings;//pour le parametrage de l'app
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected());
		}
}
