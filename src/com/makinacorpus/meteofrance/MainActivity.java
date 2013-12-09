package com.makinacorpus.meteofrance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.Color;
import org.glob3.mobile.generated.Layer;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.Planet;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makinacorpus.meteofrance.adapter.ActionsAdapter;

@SuppressLint("NewApi")
public class MainActivity extends RoboActivity {
	// Within which the entire activity is enclosed
	@InjectView(R.id.drawer_layout)
	private DrawerLayout mDrawerLayout;

	// ListView represents Navigation Drawer
	@InjectView(R.id.drawer_list)
	private ListView mDrawerList;

	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the
	// action bar
	private ActionBarDrawerToggle mDrawerToggle;

	private boolean is3dActivated = true;
	static String tokenToUse = "";
	private static final int to2DDistance = 10000;
	private static final int to3DDistance = 25600000;
	private static final String token_url = "http://query.yahooapis.com/v1/public/yql?q=use%20%22http%3A%2F%2Fwww.plomino.net%2Fpost-yql%22%20as%20htmlpost%3B%0Aselect%20*%20from%20htmlpost%20where%0Aurl%3D'http%3A%2F%2Fsynchrone.meteo.fr%2Fpublic%2Fapi%2Fcustom%2Ftokens%2F'%0Aand%20postdata%3D%22%22%20and%20xpath%3D%22%2F%2Fp%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	private Context activityContext;

	@InjectView(R.id.layoutContainerImage)
	LinearLayout layoutContainer;
	@InjectResource(R.drawable.globe3d)
	Drawable glob3Ddrawable;
	@InjectResource(R.drawable.glob2d)
	Drawable glob2Ddrawable;


	@InjectResource(R.string.type_view)
	String typeViewHeader;

	@InjectResource(R.drawable.glob2d)
	Drawable drawableVue;
	private G3MWidget_Android _g3mWidget;
	LayerSet layerset;
	G3MBuilder_Android builder;
	@InjectView(R.id.g3mWidgetHolder)
	RelativeLayout _placeHolder;

	Angle latitudeA;
	Angle longitudeA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when drawer is closed */
			public void onDrawerClosed(View view) {

				invalidateOptionsMenu();
				getActionBar().setTitle("");

			}

			/** Called when a drawer is opened */
			public void onDrawerOpened(View drawerView) {

				invalidateOptionsMenu();
				getActionBar().setTitle(getResources().getString(R.string.app_name));
			}

		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setAdapter(new ActionsAdapter(this));
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("");
		
	
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.whitetransparent)));
		// Enabling Up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
		activityContext = this;
		// Setting item click listener for the listview mDrawerList
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (layerset != null) {

					final Layer layerToAdd = layerset
							.getLayerByTitle((String) view.getTag());
					if (layerToAdd != null) {
						TextView txtContainer = (TextView) view
								.findViewById(R.id.textLayer);

						Drawable[] drawablesCoumpounds = txtContainer
								.getCompoundDrawables();

						if (!layerToAdd.isEnable()) {
							ImageView imagetoAdd;
							imagetoAdd = new ImageView(view.getContext());
							imagetoAdd.setImageDrawable(drawablesCoumpounds[0]);
							imagetoAdd.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							imagetoAdd.setTag((String) view.getTag());
							layoutContainer.addView(imagetoAdd);

							layerToAdd.setEnable(true);

						} else {

							layerToAdd.setEnable(false);
							ArrayList<View> viewAll = getViewsByTag(layoutContainer);
							for (View view2 : viewAll) {
								if (view2.getTag().equals(
										(String) view.getTag())) {
									layoutContainer.removeView(view2);
									break;
								}

							}

						}
					}
				}

				// Closing the drawer
				mDrawerLayout.closeDrawer(mDrawerList);

			}
		});

		if (Utils.settings == null)
			Utils.settings = getSharedPreferences("settingsFile", MODE_PRIVATE);
		if (Utils.isNetworkConnected(activityContext)) {
			TokenTask getTokenTask = new TokenTask();
			getTokenTask.execute();
		} else {
			if (!Utils.settings.getString("token", "").equals("")) {

				layerset = SimpleRasterLayerBuilder.createLayerset(
						Utils.settings.getString("token", ""), activityContext);
				builder = new G3MBuilder_Android(activityContext);

				builder.getPlanetRendererBuilder().setLayerSet(layerset);
				builder.setBackgroundColor(Color
						.fromRGBA255(255, 255, 255, 255));
				_g3mWidget = builder.createWidget();

				_placeHolder.addView(_g3mWidget);

			} else {
				Toast.makeText(activityContext, R.string.token_error,
						Toast.LENGTH_LONG).show();
			}
		}

	}

	private static void getToken() {
		if (tokenToUse == "") {
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpGet request = new HttpGet(token_url);
				HttpResponse response = httpClient.execute(request);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				JSONObject json = new JSONObject(responseString);
				String inner_json = json.getJSONObject("query")
						.getJSONObject("results").getJSONObject("postresult")
						.getString("p");
				tokenToUse = (new JSONObject(inner_json)).getString("token");
				Log.i("token", tokenToUse);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	private class TokenTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			getToken();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			SharedPreferences.Editor editor = Utils.settings.edit();
			editor.putString("token", tokenToUse);
			editor.commit();
			layerset = SimpleRasterLayerBuilder.createLayerset(tokenToUse,
					activityContext);
			builder = new G3MBuilder_Android(activityContext);
			builder.setPlanet(Planet.createSphericalEarth());
			builder.getPlanetRendererBuilder().setLayerSet(layerset);
			builder.setBackgroundColor(Color.fromRGBA255(255, 255, 255, 255));
			bascilue2D3D();
			_g3mWidget = builder.createWidget();

			_placeHolder.addView(_g3mWidget);

		}
	}

	private static ArrayList<View> getViewsByTag(ViewGroup root) {
		ArrayList<View> views = new ArrayList<View>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);

			views.add(child);

		}
		return views;
	}

	public void bascilue2D3D() {
		latitudeA = Angle.fromDegreesMinutesSeconds(48, 52, 25.58);
		longitudeA = Angle.fromDegreesMinutesSeconds(2, 17, 42.12);

		// buttonGlobeType.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(final View v) {
		//
		// if (is3dActivated) {
		//
		// is3dActivated = false;
		// buttonGlobeType.setImageDrawable(glob3Ddrawable);
		// _g3mWidget.setAnimatedCameraPosition(new Geodetic3D(
		// latitudeA, longitudeA, to2DDistance));
		//
		// } else {
		// is3dActivated = true;
		// buttonGlobeType.setImageDrawable(glob2Ddrawable);
		// _g3mWidget.setAnimatedCameraPosition(new Geodetic3D(
		// latitudeA, longitudeA, to3DDistance));
		//
		// }
		// }
		// });

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.popup, menu);
		return true;
	}

}
