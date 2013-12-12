package com.makinacorpus.meteofrance;

import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.Geodetic2D;
import org.glob3.mobile.generated.LayerBuilder;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.LevelTileCondition;
import org.glob3.mobile.generated.Sector;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.WMSLayer;
import org.glob3.mobile.generated.WMSServerVersion;

import android.content.Context;

public class SimpleRasterLayerBuilder extends LayerBuilder {

	public static LayerSet createLayerset(String tokenToUse, String dateToUse,
			Context ctx) {
		final LayerSet layerSet = new LayerSet();

		final WMSLayer globeLyer = new WMSLayer("openstreetmap", new URL(
				"http://maps.opengeo.org/geowebcache/service/wms?", false),
				WMSServerVersion.WMS_1_1_0, Sector.fullSphere(), "image/png",
				"EPSG:4326", "", false, new LevelTileCondition(0, 18),
				TimeInterval.fromDays(30), true);
		globeLyer.setTitle("globe");
		globeLyer.setEnable(true);
		layerSet.addLayer(globeLyer);

		final WMSLayer tmpLyer = new WMSLayer("T__HEIGHT", new URL(
				"http://screamshot.makina-corpus.net/public/api/ogc/wms/model/?token="
						+ tokenToUse + "&time=" + dateToUse + "&", false),
				WMSServerVersion.WMS_1_3_0, Sector.fullSphere(), "image/png",
				"EPSG:4326", "", true, new LevelTileCondition(0, 18),
				TimeInterval.fromDays(30), true);
		tmpLyer.setTitle(ctx.getResources()
				.getString(R.string.temperature_name));
		tmpLyer.setEnable(false);

		layerSet.addLayer(tmpLyer);

		final WMSLayer cloudsLayer = new WMSLayer("geostationary_hrv_cloud",
				new URL(
						"http://screamshot.makina-corpus.net/public/api/ogc/wms/satellite/?token="
								+ tokenToUse + "&", false),
				WMSServerVersion.WMS_1_3_0, Sector.fullSphere(), "image/png",
				"EPSG:4326", "", true, new LevelTileCondition(0, 18),
				TimeInterval.fromDays(30), true);
		cloudsLayer.setTitle(ctx.getResources().getString(
				R.string.couverture_name));
		cloudsLayer.setEnable(false);

		layerSet.addLayer(cloudsLayer);
		final WMSLayer ventLayer = new WMSLayer("UV__HEIGHT", new URL(
				"http://screamshot.makinacorpus.net/public/api/ogc/wms/model/?token="
						+ tokenToUse + "&time=" + dateToUse + "&", false),
				WMSServerVersion.WMS_1_3_0, Sector.fullSphere(), "image/png",
				"EPSG:4326", "", true, new LevelTileCondition(0, 18),
				TimeInterval.fromDays(30), true);
		ventLayer.setTitle(ctx.getResources().getString(R.string.vent_name));
		ventLayer.setEnable(false);

		layerSet.addLayer(ventLayer);

		final Geodetic2D lower = new Geodetic2D(Angle.fromDegrees(-90),
				Angle.fromDegrees(-180));
		final Geodetic2D upper = new Geodetic2D(Angle.fromDegrees(90),
				Angle.fromDegrees(180));
		final Sector demSector = new Sector(lower, upper);

		final WMSLayer precipitationLayer = new WMSLayer(
				"precipitation_amount_60mn", new URL(
						"http://screamshot.makinacorpus.net/public/api/ogc/wms/radar/?token="
								+ tokenToUse + "&", false),
				WMSServerVersion.WMS_1_3_0, demSector, "image/png",
				"EPSG:4326", "", true, new LevelTileCondition(0, 18),
				TimeInterval.fromDays(30), true);
	    precipitationLayer.setExtraParameter("BBOX=-90,-180,90,180");
		precipitationLayer.setTitle(ctx.getResources().getString(
				R.string.precipitation_name));
		precipitationLayer.setEnable(false);

		layerSet.addLayer(precipitationLayer);

		return layerSet;
	}
	// -90.0,-180,90.0,180
}
