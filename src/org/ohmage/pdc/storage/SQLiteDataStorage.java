package org.ohmage.pdc.storage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.ohmage.OhmageApplication;
import org.ohmage.SharedPreferencesHelper;
import org.ohmage.db.DbContract.Campaigns;
import org.ohmage.db.DbContract.PromptResponses;
import org.ohmage.db.DbContract.SurveyPrompts;
import org.ohmage.db.DbHelper;
import org.ohmage.db.DbContract.Responses;
import org.ohmage.db.DbHelper.Tables;
import org.ohmage.db.Models.Campaign;
import org.ohmage.db.Models.Response;
import org.ohmage.pdc.OhmagePDVManager;
import org.ohmage.service.SurveyGeotagService;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


import edu.ucla.cens.pdc.libpdc.datastructures.DataRecord;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCDatabaseException;
import edu.ucla.cens.pdc.libpdc.stream.Storage;

public class SQLiteDataStorage extends Storage {

	public SQLiteDataStorage(OhmageApplication app, String data_stream_id) {
		super(app, data_stream_id);
		_app = app;
		_data_stream_id = data_stream_id;
	}

	@Override
	public String getLastEntry() throws PDCDatabaseException {
		/*
		StreamDbHelper streamDbHelper = 
				new StreamDbHelper(_app.getApplicationContext());
		Response response = 
				streamDbHelper.getLastSurveyResponse();
		Campaign campaign = streamDbHelper.getCampaign(
				response.campaignUrn);
		JSONObject responseJson = new JSONObject();
		if (response != null) {
			try {
				responseJson.put("header_c", campaign.NAME);
				responseJson.put("header_u", response.username);
				responseJson.put("header_ci", "android");
				responseJson.put("date", response.date);
				responseJson.put("time", response.time);
				responseJson.put("timezone", response.timezone);
				responseJson.put("location_status", response.locationStatus);
				if (!response.locationStatus
						.equals(SurveyGeotagService.LOCATION_UNAVAILABLE)) {
					JSONObject locationJson = new JSONObject();
					locationJson.put("latitude", response.locationLatitude);
					locationJson.put("longitude", response.locationLongitude);
					locationJson.put("provider", response.locationProvider);
					locationJson.put("accuracy", response.locationAccuracy);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String locationTimestamp = dateFormat.format(new Date(
							response.locationTime));
					locationJson.put("timestamp", locationTimestamp);
					responseJson.put("location", locationJson);
				}
				responseJson.put("survey_id", response.surveyId);
				responseJson.put("survey_launch_context", new JSONObject(
						response.surveyLaunchContext));
				responseJson.put("responses", new JSONArray(response.response));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		} 
		return responseJson.toString();
		*/
		return null;
	}

	@Override
	public List<String> getRangeIds() throws PDCDatabaseException {
		return getRangeIds(null, null);
	}

	@Override
	public List<String> getRangeIds(String start) throws PDCDatabaseException {
		return getRangeIds(start, null);
	}

	@Override
	public List<String> getRangeIds(String start, String end)
			throws PDCDatabaseException {
		String tablename = getTableName();
		if(tablename == "MOBILITY_DATA_STREAM")
		{
			
		} else if(tablename == "SURVEY_DATA_STREAM")
		{
			if(start != null && end != null)
			{
				if(Integer.parseInt(start) > Integer.parseInt(end))
					return null;
			}
			SharedPreferencesHelper helper = new SharedPreferencesHelper(_app);
			Uri dataUri = Responses.CONTENT_URI;
			
			ContentResolver cr = _app.getContentResolver();
			
			String [] projection = new String [] {
											Tables.RESPONSES + "." + 
			Responses._ID,
											};
			
			String select =  Responses.RESPONSE_STATUS + "!=" + 
			Response.STATUS_DOWNLOADED + " AND " + 
							Responses.RESPONSE_STATUS + "!=" + 
			Response.STATUS_UPLOADED + " AND " + 
							Responses.RESPONSE_STATUS + "!=" + 
			Response.STATUS_WAITING_FOR_LOCATION;
			
			Cursor cursor = cr.query(dataUri, projection, select, null, null);

			cursor.moveToFirst();
			
			ContentValues cv = new ContentValues();
			cv.put(Responses.RESPONSE_STATUS, Response.STATUS_QUEUED);
			cr.update(dataUri, cv, select, null);
			List<String> responseIds= new ArrayList<String>();
			for (int i = 0; i < cursor.getCount(); i++) {
				
				long responseId = cursor.getLong(cursor.getColumnIndex(
						Responses._ID));
				while((start != null) && (Integer.parseInt(start) < responseId))
				{
					cursor.moveToNext();
					responseId = cursor.getLong(cursor.getColumnIndex(
							Responses._ID));
					i++;
				}
				responseIds.add("" + responseId);
				if((end != null) && (Integer.parseInt(end) == responseId))
					break;
				cursor.moveToNext();
			}
			return responseIds;
		}
		/*
		StreamDbHelper streamDbHelper = 
				new StreamDbHelper(_app.getApplicationContext());
		List<Response> responses;
		if( start != null && end !=  null) {
			responses =
					streamDbHelper.getRangeSurveyResponse(
							Integer.parseInt(start), Integer.parseInt(end));
		} else if(start != null) {
			responses =
					streamDbHelper.getRangeSurveyResponse(
							Integer.parseInt(start), -1);
		} else {
			responses =
					streamDbHelper.getRangeSurveyResponse(-1, -1);
		}
		Campaign campaign = streamDbHelper.getCampaign(Response.CAMPAIGN_URN);
		ArrayList<String> responseStrArr = new ArrayList<String>();
		for (Response response : responses) {
			try {
				JSONObject responseJson = new JSONObject();
				responseJson.put("header_c", campaign.NAME);
				responseJson.put("header_u", response.username);
				responseJson.put("header_ci", "android");
				responseJson.put("date", response.date);
				responseJson.put("time", response.time);
				responseJson.put("timezone", response.timezone);
				responseJson.put("location_status", response.locationStatus);
				if (!response.locationStatus
						.equals(SurveyGeotagService.LOCATION_UNAVAILABLE)) {
					JSONObject locationJson = new JSONObject();
					locationJson.put("latitude", response.locationLatitude);
					locationJson.put("longitude", response.locationLongitude);
					locationJson.put("provider", response.locationProvider);
					locationJson.put("accuracy", response.locationAccuracy);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String locationTimestamp = dateFormat.format(new Date(
							response.locationTime));
					locationJson.put("timestamp", locationTimestamp);
					responseJson.put("location", locationJson);
				}
				responseJson.put("survey_id", response.surveyId);
				responseJson.put("survey_launch_context", new JSONObject(
						response.surveyLaunchContext));
				responseJson.put("responses", new JSONArray(response.response));
				responseStrArr.add(responseJson.toString());
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		} 
		if (responses.isEmpty()) {
			throw new PDCDatabaseException("Could not find data record");
		}
		return responseStrArr;
		*/
		return null;
	}

	@Override
	/* 
	 * parameters:
	 * id : Id of the record which differs from stream to stream
	 */
	public DataRecord getRecord(String id) throws PDCDatabaseException { 
		String tablename = getTableName();
		if(tablename == "SURVEY_DATA_STREAM")
		{
			
			SharedPreferencesHelper helper = new SharedPreferencesHelper(
					_app.getApplicationContext());
			String username = helper.getUsername();
			String hashedPassword = helper.getHashedPassword();
			
			Uri dataUri = Responses.CONTENT_URI;
					
			ContentResolver cr = _app.getContentResolver();
			
			String [] projection = new String [] {
											Tables.RESPONSES +
											"." + Responses._ID,
											Responses.RESPONSE_DATE,
											Responses.RESPONSE_TIME,
											Responses.RESPONSE_TIMEZONE,
											Responses.RESPONSE_LOCATION_STATUS,
											Responses.RESPONSE_LOCATION_LATITUDE,
											Responses.RESPONSE_LOCATION_LONGITUDE,
											Responses.RESPONSE_LOCATION_PROVIDER,
											Responses.RESPONSE_LOCATION_ACCURACY,
											Responses.RESPONSE_LOCATION_TIME,
											Tables.RESPONSES + "." + 
											Responses.SURVEY_ID,
											Responses.
											RESPONSE_SURVEY_LAUNCH_CONTEXT,
											Responses.RESPONSE_JSON,
											Tables.RESPONSES + "." +
											Responses.CAMPAIGN_URN,
											Campaigns.CAMPAIGN_CREATED};
			
			String select =  Tables.RESPONSES + "." + Responses._ID + " == "
			+ Integer.parseInt(id);
			
			Cursor cursor = cr.query(dataUri, projection, select, null, null);
	
			cursor.moveToFirst();
			
			ContentValues cv = new ContentValues();
			cv.put(Responses.RESPONSE_STATUS, Response.STATUS_QUEUED);
			cr.update(dataUri, cv, select, null);
				
			long responseId = cursor.getLong(cursor.getColumnIndex(
					Responses._ID));
			
			ContentValues values = new ContentValues();
			values.put(Responses.RESPONSE_STATUS, Response.STATUS_UPLOADING);
			cr.update(Responses.buildResponseUri(responseId),
					values, null, null);
				
			//JSONArray responsesJsonArray = new JSONArray(); 
			JSONObject responseJson = new JSONObject();
			final ArrayList<String> photoUUIDs = new ArrayList<String>();
	            
			try {
				responseJson.put("date", 
						cursor.getString(cursor.getColumnIndex(
								Responses.RESPONSE_DATE)));
				responseJson.put("time", 
						cursor.getLong(cursor.getColumnIndex(
								Responses.RESPONSE_TIME)));
				responseJson.put("timezone", 
						cursor.getString(cursor.getColumnIndex(
								Responses.RESPONSE_TIMEZONE)));
				String locationStatus = 
						cursor.getString(cursor.getColumnIndex(
								Responses.RESPONSE_LOCATION_STATUS));
				responseJson.put("location_status", locationStatus);
				if (! locationStatus.equals(
						SurveyGeotagService.LOCATION_UNAVAILABLE)) {
					JSONObject locationJson = new JSONObject();
					locationJson.put("latitude", 
							cursor.getDouble(cursor.getColumnIndex(
									Responses.RESPONSE_LOCATION_LATITUDE)));
					locationJson.put("longitude", 
							cursor.getDouble(cursor.getColumnIndex(
									Responses.RESPONSE_LOCATION_LONGITUDE)));
					locationJson.put("provider", 
							cursor.getString(cursor.getColumnIndex(
									Responses.RESPONSE_LOCATION_PROVIDER)));
					locationJson.put("accuracy", 
							cursor.getFloat(cursor.getColumnIndex(
									Responses.RESPONSE_LOCATION_ACCURACY)));
					SimpleDateFormat dateFormat = 
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String locationTimestamp = dateFormat.format(
							new Date(cursor.getLong(cursor.getColumnIndex(
									Responses.RESPONSE_LOCATION_TIME))));
					locationJson.put("timestamp", locationTimestamp);
					responseJson.put("location", locationJson);
				}
				responseJson.put("survey_id", cursor.getString(
						cursor.getColumnIndex(Responses.SURVEY_ID)));
				responseJson.put("survey_launch_context", new JSONObject(
						cursor.getString(cursor.getColumnIndex(
								Responses.RESPONSE_SURVEY_LAUNCH_CONTEXT))));
				responseJson.put("responses", new JSONArray(
						cursor.getString(cursor.getColumnIndex(
								Responses.RESPONSE_JSON))));
				
				ContentResolver cr2 = _app.getContentResolver();
				Cursor promptsCursor = cr2.query(
						Responses.buildPromptResponsesUri(responseId), 
						new String [] {PromptResponses.PROMPT_RESPONSE_VALUE,
							SurveyPrompts.SURVEY_PROMPT_TYPE}, 
							SurveyPrompts.SURVEY_PROMPT_TYPE + "='photo'",
							null, 
							null);
				
				while (promptsCursor.moveToNext()) {
					photoUUIDs.add(promptsCursor.getString(
							promptsCursor.getColumnIndex(
									PromptResponses.PROMPT_RESPONSE_VALUE)));
				}
				
				promptsCursor.close();
				
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
				
			//responsesJsonArray.put(responseJson);
			
			String campaignUrn = cursor.getString(cursor.getColumnIndex(
					Responses.CAMPAIGN_URN));
			String campaignCreationTimestamp = cursor.getString(
					cursor.getColumnIndex(Campaigns.CAMPAIGN_CREATED));
			/*
			File [] photos = Campaign.getCampaignImageDir(
					_app.getApplicationContext(), campaignUrn).listFiles(
							new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (photoUUIDs.contains(filename.split("\\.")[0])) {
						return true;
					}
					return false;
				}
			});	*/	
			HashMap<String, String> responseDr = 
					new HashMap<String, String>();
			responseDr.put("campaign_urn", new String(campaignUrn));
			responseDr.put("campaign_creation_timestamp",
					new String(campaignCreationTimestamp));
			responseDr.put("user", username);
			responseDr.put("password", hashedPassword);
			responseDr.put("client", 
					SharedPreferencesHelper.CLIENT_STRING);
			responseDr.put("surveys", 
					responseJson.toString());
			/*	if (photos != null) {
				for (int i = 0; i < photos.length; i++) {
					responseDr.put(photos[i].getName().split("\\.")[0], 
					new FileBody(photos[i], "image/jpeg"));
				}
			}*/
			DataRecord dr = new DataRecord();
			dr.putAll(responseDr);
			return dr;
		}
		return null;
	}
	
	private String getTableName()
	{
		String hashedUsername = OhmagePDVManager.
				getHashedUsername();
		int hashIndex = _data_stream_id.indexOf(hashedUsername);
		return _data_stream_id.substring(0, hashIndex);
	}
	
	

	@Override
	public boolean insertRecord(DataRecord record) throws PDCDatabaseException {
		return false;
	}
	
	// Application Object of the associated app
	private OhmageApplication _app;
	
	// Id of the associated DataStream
	private String _data_stream_id;
	
}
