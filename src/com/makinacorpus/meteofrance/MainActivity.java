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
import org.glob3.mobile.generated.AltitudeMode;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.Camera;
import org.glob3.mobile.generated.Color;
import org.glob3.mobile.generated.Geodetic2D;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.ICameraConstrainer;
import org.glob3.mobile.generated.Layer;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.Planet;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makinacorpus.meteofrance.adapter.ActionsAdapter;
import com.makinacorpus.meteofrance.adapter.MyPagerAdapterDay;
import com.makinacorpus.meteofrance.adapter.MyPagerAdapterTime;
import com.makinacorpus.meteofrance.listener.ITextViewListener;
import com.makinacorpus.meteofrance.ui.PagerContainer;
import com.makinacorpus.meteofrance.ui.TextDayView;
import com.makinacorpus.meteofrance.ui.TextTimeView;

@SuppressLint("NewApi")
public class MainActivity extends RoboActivity implements ITextViewListener {
	// Pour l'affichage de la position de l'utilisateur
	ViewPager pagerDate, pagerHour;
	private boolean mode3DActivated = true;
	private static final String nomLayer3D = "globe3D";
	private static final String nomLayer2D = "globe2D";
	Menu menuToManage;
	MarksRenderer userMarkers;
	private static final int limit2D = 6500000;
	ArrayList<String> listLayerActivated;
	private static final String markerUrl = "https://cdn1.iconfinder.com/data/icons/Map-Markers-Icons-Demo-PNG/64/Map-Marker-Marker-Outside-Azure.png";
	private static final String token_url = "http://query.yahooapis.com/v1/public/yql?q=use%20%22http%3A%2F%2Fwww.plomino.net%2Fpost-yql%22%20as%20htmlpost%3B%0Aselect%20*%20from%20htmlpost%20where%0Aurl%3D'http%3A%2F%2Fsynchrone.meteo.fr%2Fpublic%2Fapi%2Fcustom%2Ftokens%2F'%0Aand%20postdata%3D%22%22%20and%20xpath%3D%22%2F%2Fp%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	ProgressDialog progress;
	@InjectView(R.id.drawer_layout)
	private DrawerLayout mDrawerLayout;
	@InjectView(R.id.drawer_list)
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private boolean isMarkerPositionActivated = false;
	static String tokenToUse = "";
	private static final int to2DDistance = 300000;
	private static final int to3DDistance = 22000000;
	private static final double latitudeToulouse = 43.605256;
	private static final double longitudeToulouse = 1.444988;
	private static final double maxZoomIn2D = 1.7E7;

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
	@InjectResource(R.string.token_loading)
	String tokenLoading;
	@InjectResource(R.string.your_position)
	String yourPoisiton;
	@InjectResource(R.string.geolocation_error)
	String geolocationError;
	@InjectResource(R.string.nondispo_layer)
	String layerNotAvalaible;
	@InjectResource(R.string.precipitation_name)
	String layerPrecepitationName;
	@InjectResource(R.drawable.check_off)
	Drawable chechOffDrawable;
	@InjectResource(R.drawable.check_on)
	Drawable chechOnDrawable;
	@InjectResource(R.string.vent_name)
	String ventName;

	Angle latitudeA;
	Angle longitudeA;
	Location userLocation;
	// Pour la page de date
	@InjectView(R.id.pager_container_Time)
	private PagerContainer mContainerTime;
	@InjectView(R.id.pager_container_day)
	PagerContainer mContainerDay;

	private MyPagerAdapterDay mPagerAdapterDay;
	private MyPagerAdapterTime mPagerAdapterTime;

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

			}

			/** Called when a drawer is opened */
			public void onDrawerOpened(View drawerView) {

				invalidateOptionsMenu();

			}

		};
		listLayerActivated = new ArrayList<String>();
		listLayerActivated.add(ventName);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setAdapter(new ActionsAdapter(this));
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(
						R.color.blacktransparent)));
		// Enabling Up navigation

		activityContext = this;
		chechOffDrawable.setBounds(0, 0, chechOffDrawable.getIntrinsicWidth(),
				chechOffDrawable.getIntrinsicHeight());
		chechOnDrawable.setBounds(0, 0, chechOnDrawable.getIntrinsicWidth(),
				chechOnDrawable.getIntrinsicHeight());
		// Setting item click listener for the listview mDrawerList
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (layerset != null) {

					int positionRollerDate = pagerDate.getCurrentItem();
					int positionRollerHour = pagerHour.getCurrentItem();

					final Layer layerToAdd = layerset
							.getLayerByTitle((String) view.getTag() + "_"
									+ positionRollerDate + "_"
									+ positionRollerHour);

					if (layerToAdd != null) {
						TextView txtContainer = (TextView) view
								.findViewById(R.id.textLayer);

						Drawable[] drawablesCoumpounds = txtContainer
								.getCompoundDrawables();

						if (!layerToAdd.isEnable()) {
							txtContainer.setCompoundDrawables(
									drawablesCoumpounds[0], null,
									chechOnDrawable, null);
							listLayerActivated.add((String) view.getTag());
							ImageView imagetoAdd;
							imagetoAdd = new ImageView(view.getContext());
							imagetoAdd.setImageDrawable(drawablesCoumpounds[0]);
							imagetoAdd.setLayoutParams(new LayoutParams(
									(int) Utils.convertDpToPixel(40,
											activityContext), (int) Utils
											.convertDpToPixel(40,
													activityContext)));
							imagetoAdd.setTag((String) view.getTag());
							layoutContainer.addView(imagetoAdd);
							layerToAdd.initialize(_g3mWidget.getG3MContext());
							layerToAdd.setEnable(true);

						} else {
							txtContainer.setCompoundDrawables(
									drawablesCoumpounds[0], null,
									chechOffDrawable, null);
							layerToAdd.setEnable(false);
							removeIconFromMap((String) view.getTag());
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
				builder.setBackgroundColor(Color.fromRGBA255(0, 0, 0, 0));

				builder.getPlanetRendererBuilder().setLayerSet(layerset);

				addMarkerPosition();
				_g3mWidget = builder.createWidget();
				// activeUpdateIconWhenTouch();

				_placeHolder.addView(_g3mWidget);

			} else {
				Toast.makeText(activityContext, R.string.token_error,
						Toast.LENGTH_LONG).show();
			}
		}
		userLocation = Utils.getCurrentLocation(this);
		pagerDate = mContainerDay.getViewPager();
		mPagerAdapterDay = new MyPagerAdapterDay(this, this);
		pagerDate.setAdapter(mPagerAdapterDay);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		pagerDate.setOffscreenPageLimit(mPagerAdapterDay.getCount());
		// A little space between pages
		pagerDate.setPageMargin(10);

		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		pagerDate.setClipChildren(false);

		pagerDate.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
			}

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			public void onPageSelected(int position) {
				layerset.disableAllLayers();

				if (!mode3DActivated) {
					layerset.getLayerByTitle(nomLayer3D).setEnable(false);
					layerset.getLayerByTitle(nomLayer2D).setEnable(true);

				} else {

					layerset.getLayerByTitle(nomLayer2D).setEnable(false);
					layerset.getLayerByTitle(nomLayer3D).setEnable(true);

				}
				for (int i = 0; i < listLayerActivated.size(); i++) {
					final Layer layerToAdd = layerset
							.getLayerByTitle(listLayerActivated.get(i) + "_"
									+ position + "_"
									+ pagerHour.getCurrentItem());
					layerToAdd.setEnable(true);
				}
				userLocation = Utils.getCurrentLocation(activityContext);

			}
		});
		pagerHour = mContainerTime.getViewPager();
		mPagerAdapterTime = new MyPagerAdapterTime(this, this);
		pagerHour.setAdapter(mPagerAdapterTime);

		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		pagerHour.setOffscreenPageLimit(mPagerAdapterTime.getCount());
		// A little space between pages
		pagerHour.setPageMargin(0);

		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		pagerHour.setClipChildren(false);

		pagerHour.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
			}

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			public void onPageSelected(int position) {
				layerset.disableAllLayers();

				if (!mode3DActivated) {
					layerset.getLayerByTitle(nomLayer3D).setEnable(false);
					layerset.getLayerByTitle(nomLayer2D).setEnable(true);

				} else {

					layerset.getLayerByTitle(nomLayer2D).setEnable(false);
					layerset.getLayerByTitle(nomLayer3D).setEnable(true);

				}
				for (int i = 0; i < listLayerActivated.size(); i++) {
					final Layer layerToAdd = layerset
							.getLayerByTitle(listLayerActivated.get(i) + "_"
									+ pagerDate.getCurrentItem() + "_"
									+ position);
					layerToAdd.setEnable(true);
				}
				userLocation = Utils.getCurrentLocation(activityContext);

			}
		});

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
			progress = new ProgressDialog(activityContext);
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			progress.setMessage(tokenLoading);
			progress.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			getToken();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			userLocation = Utils.getCurrentLocation(activityContext);
			SharedPreferences.Editor editor = Utils.settings.edit();
			editor.putString("token", tokenToUse);
			editor.commit();

			layerset = SimpleRasterLayerBuilder.createLayerset(tokenToUse,
					activityContext);
			builder = new G3MBuilder_Android(activityContext);
			builder.setBackgroundColor(Color.fromRGBA255(0, 0, 0, 0));
			builder.setPlanet(Planet.createSphericalEarth());
			builder.getPlanetRendererBuilder().setLayerSet(layerset);

			builder.addCameraConstraint(new ICameraConstrainer() {

				@Override
				public boolean onCameraChange(Planet planet,
						Camera previousCamera, Camera nextCamera) {

					if (!mode3DActivated) {
						nextCamera.setHeading(Angle.zero());
						// if (geo2D._height > limit2D)
						// nextCamera.setGeodeticPosition(geo2D._latitude,
						// geo2D._longitude, limit2D);
					}
					return false;
				}

				@Override
				public void dispose() {
					// TODO Auto-generated method stub

				}
			});
			addMarkerPosition();
			_g3mWidget = builder.createWidget();
			_placeHolder.addView(_g3mWidget);
			// activeUpdateIconWhenTouch();

			if (progress.isShowing()) {
				progress.dismiss();
			}
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
		if (userLocation != null) {

			latitudeA = Angle.fromDegrees(userLocation.getLatitude());
			longitudeA = Angle.fromDegrees(userLocation.getLongitude());

		} else {
			latitudeA = Angle.fromDegrees(latitudeToulouse);
			longitudeA = Angle.fromDegrees(longitudeToulouse);

		}

		switch (item.getItemId()) {

		case R.id.zoominzoomout: {

			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			Camera cameraaa = _g3mWidget.getNextCamera();
			Geodetic3D geo2D = cameraaa.getGeodeticPosition();

			if (geo2D._height > limit2D) {

				_g3mWidget.setAnimatedCameraPosition(new Geodetic3D(latitudeA,
						longitudeA, to2DDistance));

			} else {

				_g3mWidget.setAnimatedCameraPosition(new Geodetic3D(latitudeA,
						longitudeA, to3DDistance));

			}
		}

			break;

		case R.id.switchViewItem: {
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}

			// Camera cameraaa = _g3mWidget.getNextCamera();
			// Geodetic3D geo2D = cameraaa.getGeodeticPosition();

			if (mode3DActivated) {

				mode3DActivated = false;

				builder = new G3MBuilder_Android(activityContext);

				builder.setBackgroundColor(Color.fromRGBA255(0, 0, 0, 0));
				builder.setPlanet(Planet.createFlatEarth());

				builder.getPlanetRendererBuilder().setLayerSet(layerset);

				builder.addCameraConstraint(new ICameraConstrainer() {

					@Override
					public boolean onCameraChange(Planet planet,
							Camera previousCamera, Camera nextCamera) {
						// TODO Auto-generated method stub

						nextCamera.setHeading(Angle.zero());

						if (nextCamera.getGeodeticCenterOfView().isNan()) {
							nextCamera.setGeodeticPosition(new Geodetic2D(
									latitudeA, longitudeA), nextCamera
									.getGeodeticPosition()._height);

						}
						if (nextCamera.getGeodeticPosition()._height > maxZoomIn2D) {
							nextCamera.setGeodeticPosition(
									new Geodetic2D(Angle.zero(), Angle.zero()),
									maxZoomIn2D);
						}

						return false;
					}

					@Override
					public void dispose() {
						// TODO Auto-generated method stub

					}
				});
				addMarkerPosition();
				_g3mWidget = builder.createWidget();

				// activeUpdateIconWhenTouch();
				_placeHolder.removeAllViews();
				_placeHolder.addView(_g3mWidget);

				item.setIcon(glob3Ddrawable);
				layerset.getLayerByTitle(nomLayer3D).setEnable(false);
				layerset.getLayerByTitle(nomLayer2D).setEnable(true);
				_g3mWidget.setAnimatedCameraPosition(new Geodetic3D(latitudeA,
						longitudeA, to2DDistance));

			} else {
				mode3DActivated = true;

				builder = new G3MBuilder_Android(activityContext);

				builder.setBackgroundColor(Color.fromRGBA255(0, 0, 0, 0));
				builder.setPlanet(Planet.createSphericalEarth());
				builder.getPlanetRendererBuilder().setLayerSet(layerset);
				builder.addCameraConstraint(new ICameraConstrainer() {

					@Override
					public boolean onCameraChange(Planet planet,
							Camera previousCamera, Camera nextCamera) {
						// TODO Auto-generated method stub

						return false;
					}

					@Override
					public void dispose() {
						// TODO Auto-generated method stub

					}
				});

				addMarkerPosition();
				_g3mWidget = builder.createWidget();
				// activeUpdateIconWhenTouch();
				_placeHolder.removeAllViews();
				_placeHolder.addView(_g3mWidget);

				layerset.getLayerByTitle(nomLayer3D).setEnable(true);
				layerset.getLayerByTitle(nomLayer2D).setEnable(false);

				item.setIcon(glob2Ddrawable);

			}
		}
			break;
		case R.id.switchAddPosition: {

			if (!isMarkerPositionActivated) {

				isMarkerPositionActivated = true;
				userMarkers.setEnable(true);

			} else {
				isMarkerPositionActivated = false;
				userMarkers.setEnable(false);

			}
		}
			break;
		default:
			break;
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
		menuToManage = menu;
		return true;
	}

	private void addMarkerPosition() {
		userMarkers = new MarksRenderer(false);
		Geodetic2D position = null;
		if (userLocation != null) {

			position = new Geodetic2D(Angle.fromDegrees(userLocation
					.getLatitude()), Angle.fromDegrees(userLocation
					.getLongitude()));

		} else {
			position = new Geodetic2D(Angle.fromDegrees(latitudeToulouse),
					Angle.fromDegrees(longitudeToulouse));
			Toast.makeText(activityContext, geolocationError, Toast.LENGTH_LONG)
					.show();

		}
		userMarkers.addMark(new Mark(yourPoisiton, //
				new URL(markerUrl, false), //
				new Geodetic3D(position, 0), //
				AltitudeMode.RELATIVE_TO_GROUND, 0, //
				true, //
				14));
		builder.addRenderer(userMarkers);
		userMarkers.setEnable(false);

	}

	@Override
	public void hasSelectedText(View v, int pos) {
		if (v instanceof TextDayView) {
			mPagerAdapterDay.daysViewPager.setSelected((TextDayView) v);
		} else if (v instanceof TextTimeView) {
			mPagerAdapterTime.timesViewPager.setSelected((TextTimeView) v);
		}
	}

	private void removeIconFromMap(String tag) {
		ArrayList<View> viewAll = getViewsByTag(layoutContainer);
		int counter = 0;
		for (View view2 : viewAll) {

			if (view2.getTag().equals(tag)) {
				layoutContainer.removeView(view2);
				listLayerActivated.remove(counter);
				break;
			} else
				counter++;

		}
	}

}
