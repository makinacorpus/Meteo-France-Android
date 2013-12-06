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
import org.glob3.mobile.generated.Layer;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.makinacorpus.meteofrance.ui.SimpleSideDrawer;
import com.u1aryz.android.lib.newpopupmenu.MenuItem;
import com.u1aryz.android.lib.newpopupmenu.PopupMenu;
import com.u1aryz.android.lib.newpopupmenu.PopupMenu.OnItemSelectedListener;

public class MainActivity extends RoboActivity {
	static String tokenToUse = "";
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
	private ListView listSliding;
	private static final int idTraffic = 2;
	private static final int idSatellite = 1;
	@InjectResource(R.string.type_view)
	String typeViewHeader;

	@InjectResource(R.drawable.glob2d)
	Drawable drawableVue;
	private G3MWidget_Android _g3mWidget;
	LayerSet layerset;
	G3MBuilder_Android builder;
	@InjectView(R.id.g3mWidgetHolder)
	RelativeLayout _placeHolder;
	private OnItemSelectedListener onselect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
			case idSatellite:
				break;

			case idTraffic:

				break;
			}

		}
	};

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
		buttonGlobeType.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		buttonViewtype.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu menu = new PopupMenu(v.getContext());
				menu.setHeaderTitle(typeViewHeader);
				// Set Listener
				menu.setOnItemSelectedListener(onselect);
				// Add Menu (Android menu like style)
				menu.add(idSatellite, R.string.type_view_satellite).setIcon(
						drawableVue);
				menu.add(idTraffic, R.string.type_view_traffic).setIcon(
						drawableVue);

				menu.show();
			}
		});
		listSliding
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

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

				_g3mWidget = builder.createWidget();

				_g3mWidget.setBackgroundColor(activityContext.getResources()
						.getColor(R.color.whitetransparent));
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

			builder.getPlanetRendererBuilder().setLayerSet(layerset);

			_g3mWidget = builder.createWidget();
			_g3mWidget.setBackgroundColor(activityContext.getResources()
					.getColor(R.color.whitetransparent));

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
}
