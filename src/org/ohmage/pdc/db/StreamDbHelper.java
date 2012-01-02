package org.ohmage.pdc.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.ccnx.ccn.CCNHandle;
import org.ohmage.db.DbHelper;


import edu.ucla.cens.pdc.libpdc.iApplication;
import edu.ucla.cens.pdc.libpdc.core.GlobalConfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StreamDbHelper extends DbHelper {

	public StreamDbHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		Log.i(TAG, "Stream Db Helper called");
		super.onCreate(db);
	}
	
	private final String TAG = "STREAM_DB_HELPER";
}
	/*	
	@Override
	public void onCreate(SQLiteDatabase db) {
		assert db != null;
		super.onCreate(db);	
		try {
			GlobalConfig config = GlobalConfig.loadState();
			if (config == null) {
				config = GlobalConfig.getInstance();
			}
			// Add DataStreams to the application
			CCNHandle handle = config.getCCNHandle();
			Log.i(TAG, "Application Name:" + _app_name);
			iApplication app = config.getApplication(_app_name);
			AndDataStream _ds_responses = new AndDataStream(handle, 
					app, "andwellness.db.responses");
			app.addDataStream(_ds_responses);
			AndDataStream _ds_campaigns = new AndDataStream(handle, 
				app,
				"andwellness.db.campaigns");
			app.addDataStream(_ds_campaigns);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void clearAll() {
		SQLiteDatabase db = openDb();
		
		if (db == null) {
			return;
		}
		super.clearAll(db);
		onCreate(db);
		super.closeDb(db);
	}
	
	public byte[] getConfig(String group, String name)
	{
		SQLiteDatabase db = openDb();

		if (db == null) {
			return null;
		}

		Cursor cursor = db.query(TABLE_CONFIG_MAP, null, "GROUPID='" + group
				+ "' AND KEYID='" + name + "'", null, null, null, null);
		byte[] data = null;
		if (cursor.moveToFirst()) {
			data = cursor.getBlob(cursor.getColumnIndex("DATA"));
		}

		closeDb(db);
		return data;
	}

	public void addUpdateConfig(String group, String name, byte[] data)
	{
		SQLiteDatabase db = openDb();

		if (db == null) {
			return;
		}

		 db.delete(TABLE_CONFIG_MAP, "GROUPID='" + group
				+ "' AND KEYID='" + name + "'", null);

		ContentValues values = new ContentValues();
		values.put("GROUPID", group);
		values.put("KEYID", name);
		values.put("DATA", data);

		db.insert(TABLE_CONFIG_MAP, null, values);

		closeDb(db);
	}

	public void removeConfig(String group, String name)
	{
		SQLiteDatabase db = openDb();

		if (db == null) {
			return;
		}

		db.delete(TABLE_CONFIG_MAP, "GROUPID='" + group
				 		+ "' AND KEYID='" + name + "'", null);

		closeDb(db);
	}

	public Long getIDForBSONID(String bid)
	{
		SQLiteDatabase db = openDb();

		if (db == null) {
			return null;
		}

		Cursor cursor = db.query(TABLE_IDS_MAP, null, "BSONID='" + bid + "'", null,
				null, null, null);
		Long id = null;
		if (cursor.moveToFirst()) {
			id = cursor.getLong(cursor.getColumnIndex(Response._ID));
		}

		closeDb(db);
		return id;
	}

	public Response getSurveyResponse(long id)
	{
		SQLiteDatabase db = openDb();

		if (db == null) {
			return null;
		}

		Cursor cursor = db.query(TABLE_RESPONSES, null, Response._ID + "=" + id,
				null, null, null, null);

		List<Response> responses = readResponseRows(cursor);

		closeDb(db);

		if (responses.size() > 0)
			return responses.get(0);

		return null;
	}
	
	public Response getLastSurveyResponse() {
		SQLiteDatabase db = openDb();

		if (db == null) {
			return null;
		}
		Cursor lastInsertedRow = db.rawQuery("SELECT last_insert_rowid() FROM" + TABLE_RESPONSES , null);
		lastInsertedRow.moveToFirst();
		int lastRowId = lastInsertedRow.getInt(0);
		Cursor cursor = db.query(TABLE_RESPONSES, null, "rowid =" + lastRowId ,
				null, null, null, null);
		List<Response> responses = readResponseRows(cursor);
		
		closeDb(db);
		
		if (responses.size() > 0)
			return responses.get(0);

		return null;
	}
	
	public List<Response> getRangeSurveyResponse(long start, long end) {
		SQLiteDatabase db = openDb();

		if (db == null) {
			return null;
		}
		
		Cursor cursor = db.query(TABLE_RESPONSES, null, 
				Response._ID + ">"  + start + " AND " + Response._ID + " <=" + end ,
				null, null, null, null);
		List<Response> responses = readResponseRows(cursor);
		
		closeDb(db);
		return responses;
	}
	
	public Cursor getCampaignTuple(String campaignUrn) {
		SQLiteDatabase db = openDb();
		if (db == null) {
			return null;
		}
		Cursor cursor = db.query(
				TABLE_CAMPAIGNS, null, Campaign.URN + "=" + campaignUrn, 
				null, campaignUrn, campaignUrn, campaignUrn);
		closeDb(db);
		return cursor;	
	}

	public List<String> getBIds(List<Long> ids)
	{
		List<String> bids = new ArrayList<String>();
		SQLiteDatabase db = openDb();
		if (db == null) {
			return bids;
		}

		for (Long id : ids) {
			String bid = getBSONIDForID(db, id);
			if (bid != null) {
				bids.add(bid);
				Log.i(TAG, "Found BID for ID: " + id);
			} else {
				bid = ObjectId.get().toStringMongod();
				bids.add(bid);

				ContentValues values = new ContentValues();
				values.put(Response._ID, id);
				values.put("BSONID", bid);
				db.insert(TABLE_IDS_MAP, null, values);
				Log.i(TAG, "Added BID for ID: " + id);
			}
		}
		Log.e(TAG, "The actual BIDS:" + bids.toString());
		closeDb(db);
		return bids;
	}

	private String getBSONIDForID(SQLiteDatabase db, Long id)
	{
		Cursor cursor;
		String bid = null;

		cursor = db.query(TABLE_IDS_MAP, null, Response._ID + "=" + id, null, null,
				null, null);
		try {
			if (cursor.moveToFirst())
				bid = cursor.getString(cursor.getColumnIndex("BSONID"));

			return bid;
		}
		finally {
			cursor.close();
		}
	}

	public List<Long> getIds(long cutoffTime, Long s_id, Long e_id)
	{

		SQLiteDatabase db = openDb();

		if (db == null) {
			return null;
		}

		String[] cols = { Response._ID };

		String where = Response.TIME + " < " + Long.toString(cutoffTime);
		if (s_id != null) {
			where += " and " + Response._ID + ">" + s_id;
		}

		if (e_id != null) {
			where += " and " + Response._ID + "<" + e_id;
		}

		Log.e(TAG, where);

		Cursor cursor = db.query(TABLE_RESPONSES, cols, where, null, null, null,
				Response._ID);

		ArrayList<Long> ids = new ArrayList<Long>();
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			Long id = cursor.getLong(cursor.getColumnIndex(Response._ID));
			ids.add(id);
			cursor.moveToNext();
		}
		cursor.close();

		closeDb(db);

		return ids;
	}
	
	private String _app_name = "AndWellness";
	
	private static final String TABLE_CONFIG_MAP = "configMap";
	
	private static final String TABLE_IDS_MAP = "idMap";
	
	private String TAG = "STREAM_DB_HELPER_CLASS";

}
*/