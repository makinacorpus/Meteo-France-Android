package com.makinacorpus.meteofrance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

	public static LayerSet createLayerset(String tokenToUse, Context ctx) {
		final LayerSet layerSet = new LayerSet();
		final int matinIndicator = 0;
		final int amIndicator = 1;
		final int soirIndicator = 2;

		final WMSLayer globeLyer3D = new WMSLayer("physicalmap", new URL(
				"http://synchrone.meteo.fr/public/api/ogc/wms/raster_basemap/?token="
						+ tokenToUse + "&", false), WMSServerVersion.WMS_1_1_0,
				Sector.fullSphere(), "image/png", "EPSG:4326", "", false,
				new LevelTileCondition(0, 18), TimeInterval.fromDays(30), true);
		globeLyer3D.setTitle("globe3D");
		globeLyer3D.setEnable(true);
		layerSet.addLayer(globeLyer3D);

		final WMSLayer globeLyer2D = new WMSLayer("openstreetmap", new URL(
				"http://maps.opengeo.org/geowebcache/service/wms?", false),
				WMSServerVersion.WMS_1_1_0, Sector.fullSphere(), "image/png",
				"EPSG:4326", "", false, new LevelTileCondition(0, 18),
				TimeInterval.fromDays(30), true);
		globeLyer2D.setTitle("globe2D");
		globeLyer2D.setEnable(false);
		layerSet.addLayer(globeLyer2D);

		for (int i = 0; i < 7; i++) {
			final WMSLayer cloudsLayerDefilant = new WMSLayer(
					"geostationary_ir_mtl",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_mosaique_cinq-sats&token="
									+ tokenToUse + "&", false),
					WMSServerVersion.WMS_1_3_0, Sector.fullSphere(),
					"image/png", "EPSG:4326", "", true, new LevelTileCondition(
							0, 18), TimeInterval.fromDays(30), true, null, 0.5f);
			cloudsLayerDefilant.setTitle(ctx.getResources().getString(
					R.string.couverture_name_geo)
					+ "_" + i + "_" + matinIndicator);
			cloudsLayerDefilant.setEnable(false);

			layerSet.addLayer(cloudsLayerDefilant);

//			final WMSLayer cloudsLayerGeostatitionnaire = new WMSLayer(
//					"geostationary_hrv_cloud",
//					new URL(
//							"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_msg3-met10&token="
//									+ tokenToUse
//									+ "&time="
//									+ formatDateToUniversel(0, matinIndicator)
//									+ "&", false), WMSServerVersion.WMS_1_3_0,
//					Sector.fullSphere(), "image/png", "EPSG:4326", "", true,
//					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
//					true);
			
			final WMSLayer cloudsLayerWithTransparence= new WMSLayer("geostationary_hrv_cloud", new URL(
					"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_msg3-met10&token="
							+ tokenToUse
						
							+ "&", false), 
							WMSServerVersion.WMS_1_3_0, Sector.fullSphere(),  "image/png", 
							"EPSG:4326", "", true, new LevelTileCondition(0, 18),
							TimeInterval.fromDays(30), true, null, 0.5f);
		
			cloudsLayerWithTransparence.setTitle(ctx.getResources().getString(
					R.string.couverture_name_defilant)
					+ "_" + i + "_" + matinIndicator);
			cloudsLayerWithTransparence.setEnable(false);

			layerSet.addLayer(cloudsLayerWithTransparence);

			// définition des Layer set du matin
			final WMSLayer tmpLyer = new WMSLayer(
					"T__HEIGHT",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/model/?dim_process=PA__0.5&token="
									+ tokenToUse
									+ "&time="
									+ formatDateToUniversel(i, matinIndicator)
									+ "&", false), WMSServerVersion.WMS_1_3_0,
					Sector.fullSphere(), "image/png", "EPSG:4326",
					"T__HEIGHT__NO_SHADING", true,
					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
					true);
			tmpLyer.setTitle(ctx.getResources().getString(
					R.string.temperature_name)
					+ "_" + i + "_" + matinIndicator);
			tmpLyer.setEnable(false);

			layerSet.addLayer(tmpLyer);
			final WMSLayer ventLayer = new WMSLayer(
					"UV__HEIGHT",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/model/?dim_process=PA__0.5&token="
									+ tokenToUse
									+ "&time="
									+ formatDateToUniversel(i, matinIndicator)
									+ "&", false), WMSServerVersion.WMS_1_3_0,
					Sector.fullSphere(), "image/png", "EPSG:4326", "", true,
					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
					true);
			ventLayer.setTitle(ctx.getResources().getString(R.string.vent_name)
					+ "_" + i + "_" + matinIndicator);
			if(i==0)
			ventLayer.setEnable(true);
			else 	ventLayer.setEnable(false);

			layerSet.addLayer(ventLayer);

		}

		for (int i = 0; i < 7; i++) {
			final WMSLayer cloudsLayerDefilant = new WMSLayer(
					"geostationary_ir_mtl",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_mosaique_cinq-sats&token="
									+ tokenToUse + "&", false),
					WMSServerVersion.WMS_1_3_0, Sector.fullSphere(),
					"image/png", "EPSG:4326", "", true, new LevelTileCondition(
							0, 18), TimeInterval.fromDays(30), true ,null, 0.5f);
			cloudsLayerDefilant.setTitle(ctx.getResources().getString(
					R.string.couverture_name_geo)
					+ "_" + i + "_" + amIndicator);
			cloudsLayerDefilant.setEnable(false);

			layerSet.addLayer(cloudsLayerDefilant);

			final WMSLayer cloudsLayerWithTransparence= new WMSLayer("geostationary_hrv_cloud", new URL(
					"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_msg3-met10&token="
							+ tokenToUse
							
							+ "&", false), 
							WMSServerVersion.WMS_1_3_0, Sector.fullSphere(),  "image/png", 
							"EPSG:4326", "", true, new LevelTileCondition(0, 18),
							TimeInterval.fromDays(30), true, null, 0.5f);
		
			cloudsLayerWithTransparence.setTitle(ctx.getResources().getString(
					R.string.couverture_name_defilant)
					+ "_" + i + "_" + amIndicator);
			cloudsLayerWithTransparence.setEnable(false);

			layerSet.addLayer(cloudsLayerWithTransparence);

			// définition des Layer set de AM
			final WMSLayer tmpLyer = new WMSLayer(
					"T__HEIGHT",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/model/?dim_process=PA__0.5&token="
									+ tokenToUse
									+ "&time="
									+ formatDateToUniversel(i, amIndicator)
									+ "&", false), WMSServerVersion.WMS_1_3_0,
					Sector.fullSphere(), "image/png", "EPSG:4326",
					"T__HEIGHT__NO_SHADING", true,
					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
					true);
			tmpLyer.setTitle(ctx.getResources().getString(
					R.string.temperature_name)
					+ "_" + i + "_" + amIndicator);
			tmpLyer.setEnable(false);

			layerSet.addLayer(tmpLyer);
			final WMSLayer ventLayer = new WMSLayer(
					"UV__HEIGHT",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/model/?dim_process=PA__0.5&token="
									+ tokenToUse
									+ "&time="
									+ formatDateToUniversel(i, amIndicator)
									+ "&", false), WMSServerVersion.WMS_1_3_0,
					Sector.fullSphere(), "image/png", "EPSG:4326", "", true,
					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
					true);
			ventLayer.setTitle(ctx.getResources().getString(R.string.vent_name)
					+ "_" + i + "_" + amIndicator);
			ventLayer.setEnable(false);

			layerSet.addLayer(ventLayer);

		}

		for (int i = 0; i < 7; i++) {
			final WMSLayer cloudsLayerDefilant = new WMSLayer(
					"geostationary_ir_mtl",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_mosaique_cinq-sats&token="
									+ tokenToUse + "&", false),
					WMSServerVersion.WMS_1_3_0, Sector.fullSphere(),
					"image/png", "EPSG:4326", "", true, new LevelTileCondition(
							0, 18), TimeInterval.fromDays(30), true, null, 0.5f);
			cloudsLayerDefilant.setTitle(ctx.getResources().getString(
					R.string.couverture_name_geo)
					+ "_" + i + "_" + soirIndicator);
			cloudsLayerDefilant.setEnable(false);

			layerSet.addLayer(cloudsLayerDefilant);

			final WMSLayer cloudsLayerWithTransparence= new WMSLayer("geostationary_hrv_cloud", new URL(
					"http://synchrone.meteo.fr/public/api/ogc/wms/satellite/?dim_process=geostationary_msg3-met10&token="
							+ tokenToUse
						
							+ "&", false), 
							WMSServerVersion.WMS_1_3_0, Sector.fullSphere(),  "image/png", 
							"EPSG:4326", "", true, new LevelTileCondition(0, 18),
							TimeInterval.fromDays(30), true, null, 0.5f);
		
			cloudsLayerWithTransparence.setTitle(ctx.getResources().getString(
					R.string.couverture_name_defilant)
					+ "_" + i + "_" + soirIndicator);
			cloudsLayerWithTransparence.setEnable(false);

			layerSet.addLayer(cloudsLayerWithTransparence);

			// définition des Layer set du soir
			final WMSLayer tmpLyer = new WMSLayer(
					"T__HEIGHT",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/model/?dim_process=PA__0.5&token="
									+ tokenToUse
									+ "&time="
									+ formatDateToUniversel(i, soirIndicator)
									+ "&", false), WMSServerVersion.WMS_1_3_0,
					Sector.fullSphere(), "image/png", "EPSG:4326",
					"T__HEIGHT__NO_SHADING", true,
					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
					true);
			tmpLyer.setTitle(ctx.getResources().getString(
					R.string.temperature_name)
					+ "_" + i + "_" + soirIndicator);
			tmpLyer.setEnable(false);

			layerSet.addLayer(tmpLyer);
			final WMSLayer ventLayer = new WMSLayer(
					"UV__HEIGHT",
					new URL(
							"http://synchrone.meteo.fr/public/api/ogc/wms/model/?dim_process=PA__0.5&token="
									+ tokenToUse
									+ "&time="
									+ formatDateToUniversel(i, soirIndicator)
									+ "&", false), WMSServerVersion.WMS_1_3_0,
					Sector.fullSphere(), "image/png", "EPSG:4326", "", true,
					new LevelTileCondition(0, 18), TimeInterval.fromDays(30),
					true);
			ventLayer.setTitle(ctx.getResources().getString(R.string.vent_name)
					+ "_" + i + "_" + soirIndicator);
			ventLayer.setEnable(false);

			layerSet.addLayer(ventLayer);

		}
		// final Geodetic2D lower = new Geodetic2D(Angle.fromDegrees(-90),
		// Angle.fromDegrees(-180));
		// final Geodetic2D upper = new Geodetic2D(Angle.fromDegrees(90),
		// Angle.fromDegrees(180));
		// final Sector demSector = new Sector(lower, upper);
		//
		// final WMSLayer precipitationLayer = new WMSLayer(
		// "precipitation_amount_60mn", new URL(
		// "http://screamshot.makinacorpus.net/public/api/ogc/wms/radar/?token="
		// + tokenToUse + "&", false),
		// WMSServerVersion.WMS_1_3_0, demSector, "image/png",
		// "EPSG:4326", "", true, new LevelTileCondition(0, 18),
		// TimeInterval.fromDays(30), true);
		// precipitationLayer.setExtraParameter("BBOX=-90,-180,90,180");
		// precipitationLayer.setTitle(ctx.getResources().getString(
		// R.string.precipitation_name));
		// precipitationLayer.setEnable(false);
		//
		// layerSet.addLayer(precipitationLayer);

		return layerSet;
	}

	private static String formatDateToUniversel(int position, int timeIndicator) {
		Locale locale = Locale.getDefault();
		Date actuelle = new Date();
		if (position == 0)
			actuelle.setHours(actuelle.getHours());
		else
			actuelle.setHours(actuelle.getHours() + 24 * position);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// DateFormat dateFormattime = new SimpleDateFormat("HH:00:00");
		switch (timeIndicator) {
		case 0:
			return dateFormat.format(actuelle) + "T09:00:00Z";
		case 1:
			return dateFormat.format(actuelle) + "T15:00:00Z";
		default:
		case 2:
			return dateFormat.format(actuelle) + "T21:00:00Z";

		}

	}
	// -90.0,-180,90.0,180
}
