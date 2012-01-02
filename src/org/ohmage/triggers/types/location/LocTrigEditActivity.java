/*******************************************************************************
 * Copyright 2011 The Regents of the University of California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohmage.triggers.types.location;

import org.ohmage.R;
import org.ohmage.triggers.base.TriggerActionDesc;
import org.ohmage.triggers.config.TrigUserConfig;
import org.ohmage.triggers.ui.TriggerListActivity;
import org.ohmage.triggers.utils.TimePickerPreference;
import org.ohmage.triggers.utils.TrigListPreference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * Editor activity for location based trigger. This activity is
 * invoked for creating a new location trigger and also for editing
 * an existing trigger. 
 */
public class LocTrigEditActivity extends PreferenceActivity 
								implements OnPreferenceClickListener, 
								OnPreferenceChangeListener, 
								OnClickListener {
	
	private static final String DEBUG_TAG = "LocTrigEditActivity"; 

	public static final String KEY_TRIG_DESC = 
			LocTrigEditActivity.class.getName() + ".trigger_descriptor";
	public static final String KEY_TRIG_ID = 
			LocTrigEditActivity.class.getName() + ".trigger_id";
	public static final String KEY_ADMIN_MODE = 
			LocTrigEditActivity.class.getName() + ".admin_mode";
	public static final String KEY_ACT_DESC = 
			LocTrigEditActivity.class.getName() + ".act_desc";
	
	private static final String PREF_KEY_LOCATION = "trigger_location";
	private static final String PREF_KEY_ENABLE_RANGE = "enable_time_range";
	private static final String PREF_KEY_START_TIME = "interval_start_time";
	private static final String PREF_KEY_END_TIME = "interval_end_time";
	private static final String PREF_KEY_TRIGGER_ALWAYS = "trigger_always";
	private static final String PREF_KEY_RENETRY_INTERVAL = "minimum_reentry";
	private static final String PREF_KEY_ACTIONS = "actions";
	
	private static final int DIALOG_ID_INVALID_TIME_ALERT = 0;
	private static final int DIALOG_ID_DEFINE_LOC_PROMPT = 1;
	private static final int DIALOG_ID_ACTION_SEL = 2;
	private static final int DIALOG_ID_NO_SURVEYS_SELECTED = 3;
	
	private boolean mAdminMode = false;
	
	/*
	 * Interface for the callback which is invoked when the
	 * done button is pressed. 
	 */
	public interface ExitListener {
		
		public void onDone(Context context, int trigId, String trigDesc, String actDesc);
	}
	
	private LocTrigDesc mTrigDesc;
	private TriggerActionDesc mActDesc;
	private static ExitListener mExitListener = null;
	private int mTrigId = 0;
	private String[] mCategories;
	private String[] mActions;
	private boolean[] mActSelected = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.trig_loc_edit_preferences);
		setContentView(R.layout.trigger_editor);
		
		mTrigDesc = new LocTrigDesc();
		mActDesc = new TriggerActionDesc();
		
		if(getIntent().hasExtra(TriggerListActivity.KEY_ACTIONS)) {
			mActions = getIntent().getStringArrayExtra(TriggerListActivity.KEY_ACTIONS);
		}
		else {
			Log.e(DEBUG_TAG, "LocTrigEditActivity: Invoked with out passing surveys");
			finish();
			return;
		}
		
		initializeCategories();
		TrigListPreference locPref = (TrigListPreference) getPreferenceScreen()
										.findPreference(PREF_KEY_LOCATION);
		locPref.setEntries(mCategories);
		locPref.setEntryValues(mCategories);
		
		if(mCategories.length == 0) {
			locPref.setEnabled(false);
		}
		else {
			locPref.setValue(mCategories[0]);
			locPref.setSummary(mCategories[0]);
		}
		
		//The cancel button prompts the user to
		//launch the settings activity
		locPref.setOnCancelListener(
				new TrigListPreference.onCancelListener() {
			
			@Override
			public void onCancel() {
				//TODO - currently this exits this activity
				//In order to come back to this screen after editing
				//locations, we need to reinitialize the UI as there
				//could be changes in the location list. Also, the currently
				//selected location might have been deleted.
				new LocationTrigger().launchSettingsEditActivity(
						LocTrigEditActivity.this, mAdminMode);
				LocTrigEditActivity.this.finish();
			}
		});

		PreferenceScreen screen = getPreferenceScreen();
		int prefCount = screen.getPreferenceCount();
		for(int i = 0; i < prefCount; i++) {
			screen.getPreference(i).setOnPreferenceClickListener(this);
			screen.getPreference(i).setOnPreferenceChangeListener(this);
		}
		
		((Button) findViewById(R.id.trig_edit_done)).setOnClickListener(this);
		((Button) findViewById(R.id.trig_edit_cancel)).setOnClickListener(this);
		
		
		//Set global value for minimum reentry interval
		Preference minIntervalPref = getPreferenceScreen()
									.findPreference(PREF_KEY_RENETRY_INTERVAL);
		minIntervalPref.setSummary(LocTrigDesc.getGlobalMinReentryInterval(this) + 
									" minutes");

		mAdminMode = getIntent().getBooleanExtra(KEY_ADMIN_MODE, false);
		
		String config = null;
		String action = null;
		
		if(savedInstanceState == null) {
			config = getIntent().getStringExtra(KEY_TRIG_DESC);
			action = getIntent().getStringExtra(KEY_ACT_DESC);
		}
		else {
			config = savedInstanceState.getString(KEY_TRIG_DESC);
			action = savedInstanceState.getString(KEY_ACT_DESC);
		}
		
		if(config != null) {	
			mTrigId = getIntent().getIntExtra(KEY_TRIG_ID, 0);
			
			if(mTrigDesc.loadString(config) && mActDesc.loadString(action)) {
				initializeGUI();
			}
			else {
				getPreferenceScreen().setEnabled(false);
				Toast.makeText(this, "Invalid trigger settings!", 
								Toast.LENGTH_SHORT).show();
			}
		}
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		updateTriggerDesc();
		outState.putString(KEY_TRIG_DESC, mTrigDesc.toString());
		outState.putString(KEY_ACT_DESC, mActDesc.toString());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_ID_ACTION_SEL:
			return createEditActionDialog();
		
		case DIALOG_ID_INVALID_TIME_ALERT: 
			final String msgErr = 
				"Make sure that the End Time is after the Start Time";

			return new AlertDialog.Builder(this)
				.setTitle("Invalid time settings!")
				.setNegativeButton("Cancel", null)
				.setMessage(msgErr)
				.create();

		case DIALOG_ID_NO_SURVEYS_SELECTED:
			final String msgErrNoSurveys = 
				"Make sure that at least one survey is selected";

			return new AlertDialog.Builder(this)
				.setTitle("No survey selected!")
				.setNegativeButton("Cancel", null)
				.setMessage(msgErrNoSurveys)
				.create();
			
		case DIALOG_ID_DEFINE_LOC_PROMPT:
			final String msgPrompt = 
				"'" + mTrigDesc.getLocation() + "' "
				+ "is not defined yet. Do you want to define it now?";

			return new AlertDialog.Builder(this)
				.setTitle("Location not defined")
				.setNegativeButton("No", 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LocTrigEditActivity.this.finish();
					}
				})
				.setPositiveButton("Yes", 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						launchMapsActivity(mTrigDesc.getLocation());
						LocTrigEditActivity.this.finish();
					}
				})
				.setMessage(msgPrompt)
				.create();
			
		}
	
		return null;
	}
	
	private void initializeCategories() {
		LocTrigDB db = new LocTrigDB(this);
		db.open();
		
		Cursor c = db.getAllCategories();
		mCategories = new String[c.getCount()];
		
		int i = 0;
		if(c.moveToFirst()) {
			do {
				mCategories[i++] = c.getString(
								   c.getColumnIndexOrThrow(LocTrigDB.KEY_NAME));
			} while(c.moveToNext());
		}
		
		c.close();
		db.close();
	}
	
	private int getLocationCount(String categName) {
		LocTrigDB db = new LocTrigDB(this);
		db.open();
		
		Cursor cCateg = db.getCategory(categName);
		
		int categId = -1;
		if(cCateg.moveToFirst()) {
			categId = cCateg.getInt(
					  cCateg.getColumnIndexOrThrow(LocTrigDB.KEY_ID));
		}
		cCateg.close();
		
		int count = 0;
		if(categId != -1) {
			Cursor cLocs = db.getLocations(categId);
			if(cLocs.moveToFirst()) {
				count = cLocs.getCount();
			}
			cLocs.close();
		}
		
		db.close();
		
		return count;
	}
	
	public static void setOnExitListener(ExitListener listener) {
		
		mExitListener = listener;
	}
	
	private void initializeGUI() {
		ListPreference locPref = (ListPreference) getPreferenceScreen()
		 					.findPreference(PREF_KEY_LOCATION);
		CheckBoxPreference rangePref = (CheckBoxPreference) getPreferenceScreen()
							.findPreference(PREF_KEY_ENABLE_RANGE);
		CheckBoxPreference alwaysPref = (CheckBoxPreference) getPreferenceScreen()
							.findPreference(PREF_KEY_TRIGGER_ALWAYS);
		TimePickerPreference startPref = (TimePickerPreference) getPreferenceScreen()
							.findPreference(PREF_KEY_START_TIME);
		TimePickerPreference endPref = (TimePickerPreference) getPreferenceScreen()
							.findPreference(PREF_KEY_END_TIME);
		
		Preference actionsPref = getPreferenceScreen().findPreference(PREF_KEY_ACTIONS);

		if(!mAdminMode && !TrigUserConfig.editTriggerActions) {
		
			actionsPref.setEnabled(false);
		}
		
		updateActionsPrefStatus();
		
		locPref.setValue(mTrigDesc.getLocation());
		locPref.setSummary(mTrigDesc.getLocation());
		
		if(mTrigDesc.isRangeEnabled()) {
			rangePref.setChecked(true);
			
			if(mTrigDesc.shouldTriggerAlways()) {
				alwaysPref.setChecked(true);
			}
			
			startPref.setTime(mTrigDesc.getStartTime());
			endPref.setTime(mTrigDesc.getEndTime());
		}
		
		locPref.setEnabled(mAdminMode || TrigUserConfig.editLocationTrigger);
		rangePref.setEnabled(mAdminMode || TrigUserConfig.editLocationTrigger);
		alwaysPref.setEnabled(mAdminMode || TrigUserConfig.editLocationTrigger);
		startPref.setEnabled(mAdminMode || TrigUserConfig.editLocationTrigger);
		endPref.setEnabled(mAdminMode || TrigUserConfig.editLocationTrigger);
		((Button) findViewById(R.id.trig_edit_done))
				.setEnabled(mAdminMode || TrigUserConfig.editLocationTrigger);
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		if(pref.getKey().equals(PREF_KEY_ACTIONS)) {
			removeDialog(DIALOG_ID_ACTION_SEL);
			showDialog(DIALOG_ID_ACTION_SEL);
		} 
		
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object val) {
		
		if(pref.getKey().equals(PREF_KEY_LOCATION)) {
			
			if(val instanceof String) {
				pref.setSummary((String) val);
			}
		}
		
		return true;
	}
	
	private void updateTriggerDesc() {
		ListPreference locPref = (ListPreference) getPreferenceScreen()
									.findPreference(PREF_KEY_LOCATION);
		CheckBoxPreference rangePref = (CheckBoxPreference) getPreferenceScreen()
									.findPreference(PREF_KEY_ENABLE_RANGE);
		CheckBoxPreference alwaysPref = (CheckBoxPreference) getPreferenceScreen()
									.findPreference(PREF_KEY_TRIGGER_ALWAYS);
		TimePickerPreference startPref = (TimePickerPreference) getPreferenceScreen()
									.findPreference(PREF_KEY_START_TIME);
		TimePickerPreference endPref = (TimePickerPreference) getPreferenceScreen()
									.findPreference(PREF_KEY_END_TIME);
		
		mTrigDesc.setLocation(locPref.getValue());
		//Currently, there is only a global setting for the reentry interval
		mTrigDesc.setMinReentryInterval(LocTrigDesc.getGlobalMinReentryInterval(this));
		
		if(rangePref.isChecked()) {
			mTrigDesc.setRangeEnabled(true);
			mTrigDesc.setTriggerAlways(alwaysPref.isChecked());
			
			mTrigDesc.setStartTime(startPref.getTime());
			mTrigDesc.setEndTime(endPref.getTime());
		}
		else {
			mTrigDesc.setRangeEnabled(false);
			mTrigDesc.setTriggerAlways(false);
		}
	}
	
	private void updateActionsPrefStatus() {
		Preference actionsPref = getPreferenceScreen()
		 						  	.findPreference(PREF_KEY_ACTIONS);
		if (mActDesc.getSurveys().length > 0) {
			actionsPref.setSummary(stringArrayToString(mActDesc.getSurveys()));
		} else {
			actionsPref.setSummary("None");
		}
		
	}
	
	private String stringArrayToString(String [] strings) {
		if (strings.length == 0) {
			return "";
		}
		String string = "";
		for (String s : strings) {
			string = string.concat(s).concat(", ");
		}
		return string.substring(0, string.length() - 2);
	}

	private void launchMapsActivity(String categName) {
		
		LocTrigDB db = new LocTrigDB(this);
		db.open();
		
		Cursor c = db.getCategory(categName);
		if(c.moveToFirst()) {
			int categId = c.getInt(
					      c.getColumnIndexOrThrow(LocTrigDB.KEY_ID));
			Intent i = new Intent(this, LocTrigMapsActivity.class);
	        i.putExtra(LocTrigDB.KEY_ID, categId);
			startActivity(i);
		}
		c.close();
		db.close();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
		case R.id.trig_edit_done:
			if(mExitListener != null) {
				updateTriggerDesc();
				
				if(!mTrigDesc.validate()) {
					showDialog(DIALOG_ID_INVALID_TIME_ALERT);
					return;
				}
				else if (mActDesc.getSurveys().length <= 0) {
					// if no surveys were selected, tell the user and abort
					showDialog(DIALOG_ID_NO_SURVEYS_SELECTED);
					return;
				}
				
				mExitListener.onDone(this, mTrigId, mTrigDesc.toString(), mActDesc.toString());
				
				//If the location is not defined yet, prompt the
				//user to do so
				if(getLocationCount(mTrigDesc.getLocation()) == 0) {
					showDialog(DIALOG_ID_DEFINE_LOC_PROMPT);
					return;
				}
			}
			break;
		}
		
		finish();
	}

private Dialog createEditActionDialog() {
		
		if(mActSelected == null) {
			mActSelected = new boolean[mActions.length];
			for(int i = 0; i < mActSelected.length; i++) {
				mActSelected[i] = mActDesc.hasSurvey(mActions[i]);
			}
		}
		
		AlertDialog.Builder builder = 
	 			new AlertDialog.Builder(this)
			   .setTitle("Select surveys")
			   .setNegativeButton("Cancel", null)
			   .setMultiChoiceItems(mActions, mActSelected, 
					   new DialogInterface.OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, 
										boolean isChecked) {
					
					mActSelected[which] = isChecked;
				}
			});

		if(mAdminMode || TrigUserConfig.editTriggerActions) {
			 builder.setPositiveButton("Done", 
					 new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mActDesc.clearAllSurveys();
					
					for(int i = 0; i < mActSelected.length; i++) {
						if(mActSelected[i]) {
							mActDesc.addSurvey(mActions[i]);
						}
					}
					dialog.dismiss();
					updateActionsPrefStatus();
//					handleActionSelection(mDialogTrigId, desc);
				}
			});
		}

	
		return builder.create();
	}
}
