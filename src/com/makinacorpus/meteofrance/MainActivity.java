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
import org.glob3.mobile.generated.Geodetic3D;
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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import com.makinacorpus.meteofrance.ui.SimpleSideDrawer;
import com.makinacorpus.meteofrance.ui.TextDayView;
import com.makinacorpus.meteofrance.ui.TextTimeView;

@SuppressLint("NewApi")
public class MainActivity extends RoboActivity implements ITextViewListener {
	private boolean is3dActivated = true;
	static String tokenToUse = "";
	private static final int to2DDistance = 10000;
	private static final int to3DDistance = 25600000;
	private static final String token_url = "http://query.yahooapis.com/v1/public/yql?q=use%20%22http%3A%2F%2Fwww.plomino.net%2Fpost-yql%22%20as%20htmlpost%3B%0Aselect%20*%20from%20htmlpost%20where%0Aurl%3D'http%3A%2F%2Fsynchrone.meteo.fr%2Fpublic%2Fapi%2Fcustom%2Ftokens%2F'%0Aand%20postdata%3D%22%22%20and%20xpath%3D%22%2F%2Fp%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	private Context activityContext;
	private SimpleSideDrawer mNav;
	@InjectView(R.id.buttonSlide)
	ImageButton buttonSlide;
	@InjectView(R.id.buttonTypeCatre)
	ImageButton buttonGlobeType;
	@InjectView(R.id.buttonTypeView)
	ImageButton buttonViewtype;
	@InjectView(R.id.layoutContainerImage)
	LinearLayout layoutContainer;
	@InjectResource(R.drawable.globe3d)
	Drawable glob3Ddrawable;
	@InjectResource(R.drawable.glob2d)
	Drawable glob2Ddrawable;

	private ListView listSliding;

	@InjectResource(R.string.type_view)
	String typeViewHeader;

	@InjectResource(R.drawable.glob2d)
	Drawable drawableVue;
	private G3MWidget_Android _g3mWidget;
	private LayerSet layerset;
	G3MBuilder_Android builder;
	@InjectView(R.id.g3mWidgetHolder)
	RelativeLayout _placeHolder;

	private Angle latitudeA;
	private Angle longitudeA;
	
	private PagerContainer mContainerDay;
	private PagerContainer mContainerTime;
	
	private MyPagerAdapterDay mPagerAdapterDay;
	private MyPagerAdapterTime mPagerAdapterTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNav = new SimpleSideDrawer(this);
		mNav.setLeftBehindContentView(R.layout.slide_content_layout);
		listSliding = (ListView) findViewById(R.id.listSliding);
		listSliding.setAdapter(new ActionsAdapter(this));
		activityContext = this;

		buttonSlide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNav.toggleLeftDrawer();
				buttonSlide.setVisibility(View.GONE);
			}
		});

		buttonViewtype.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		listSliding
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (layerset != null) {

							if (!mNav.isClosed()) {
								mNav.toggleLeftDrawer();
								buttonSlide.setVisibility(View.VISIBLE);
								final Layer layerToAdd = layerset
										.getLayerByTitle((String) view.getTag());
								if (layerToAdd != null) {
									TextView txtContainer = (TextView) view
											.findViewById(R.id.textLayer);

									Drawable[] drawablesCoumpounds = txtContainer
											.getCompoundDrawables();

									if (!layerToAdd.isEnable()) {
										ImageView imagetoAdd;
										imagetoAdd = new ImageView(view
												.getContext());
										imagetoAdd
												.setImageDrawable(drawablesCoumpounds[0]);
										imagetoAdd
												.setLayoutParams(new LayoutParams(
														LayoutParams.WRAP_CONTENT,
														LayoutParams.WRAP_CONTENT));
										imagetoAdd.setTag((String) view
												.getTag());
										layoutContainer.addView(imagetoAdd);

										layerToAdd.setEnable(true);

									} else {

										layerToAdd.setEnable(false);
										ArrayList<View> viewAll = getViewsByTag(layoutContainer);
										for (View view2 : viewAll) {
											if (view2.getTag().equals(
													(String) view.getTag())) {
												layoutContainer
														.removeView(view2);
												break;
											}

										}

									}
								}
							}
						}
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

		mContainerDay = (PagerContainer) findViewById(R.id.pager_container_day);

		ViewPager pager = mContainerDay.getViewPager();
		mPagerAdapterDay = new MyPagerAdapterDay(this, this);
		pager.setAdapter(mPagerAdapterDay);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		pager.setOffscreenPageLimit(mPagerAdapterDay.getCount());
		// A little space between pages
		pager.setPageMargin(15);

		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		pager.setClipChildren(false);
		
		mContainerTime = (PagerContainer) findViewById(R.id.pager_container_time);
		ViewPager pagerTime = mContainerTime.getViewPager();
		mPagerAdapterTime = new MyPagerAdapterTime(this, this);
		pagerTime.setAdapter(mPagerAdapterTime);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		pagerTime.setOffscreenPageLimit(mPagerAdapterTime.getCount());
		// A little space between pages
		pagerTime.setPageMargin(15);

		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		pagerTime.setClipChildren(false);

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

		buttonGlobeType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				if (is3dActivated) {

					is3dActivated = false;
					buttonGlobeType.setImageDrawable(glob3Ddrawable);
					_g3mWidget.setAnimatedCameraPosition(new Geodetic3D(
							latitudeA, longitudeA, to2DDistance));

				} else {
					is3dActivated = true;
					buttonGlobeType.setImageDrawable(glob2Ddrawable);
					_g3mWidget.setAnimatedCameraPosition(new Geodetic3D(
							latitudeA, longitudeA, to3DDistance));

				}
			}
		});

	}
	
	@Override
	public void hasSelectedText(View v, int pos) {
		if (v instanceof TextTimeView) {
			mPagerAdapterTime.timesViewPager.setSelected((TextTimeView) v);
		} else if (v instanceof TextDayView) {
			mPagerAdapterDay.daysViewPager.setSelected((TextDayView) v);
		}
	}
}
