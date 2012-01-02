package org.ohmage.ui;

import org.ohmage.R;
import org.ohmage.controls.FilterControl;
import org.ohmage.db.DbContract.Surveys;
import org.ohmage.ui.OhmageFilterable.CampaignSurveyFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.View;

/**
 * CampaignSurveyFilterActivity can be extended by classes which have campaign and survey filters
 * @author cketcham
 *
 */
public class CampaignSurveyFilterActivity extends CampaignFilterActivity implements LoaderManager.LoaderCallbacks<Cursor> {


	protected static final int SURVEY_LOADER = 1;

	protected FilterControl mSurveyFilter;
	protected String mDefaultSurvey;

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		mSurveyFilter = (FilterControl) findViewById(R.id.survey_filter);
		if(mSurveyFilter == null)
			throw new RuntimeException("Your activity must have a FilterControl with the id survey_filter");

		mSurveyFilter.setVisibility(View.INVISIBLE);
		mSurveyFilter.setOnChangeListener(new FilterControl.FilterChangeListener() {

			@Override
			public void onFilterChanged(boolean selfChange, String curValue) {
				if(!selfChange)
					onSurveyFilterChanged(curValue);
			}
		});

		mDefaultSurvey = getIntent().getStringExtra(CampaignSurveyFilter.EXTRA_SURVEY_ID);
		//		mDefaultSurvey = "alcohol";

		if(mDefaultCampaign != null) {
			getSupportLoaderManager().initLoader(SURVEY_LOADER, null, this);
		}
		
		if(mDefaultSurvey == null)
			mSurveyFilter.add(0, new Pair<String, String>("All Surveys", null));
	}
	
	public String getSurveyId() {
		return mSurveyFilter.getValue();
	}

	@Override
	protected void onCampaignFilterChanged(String filter) {
		getSupportLoaderManager().restartLoader(SURVEY_LOADER, null, CampaignSurveyFilterActivity.this);
	}

	protected void onSurveyFilterChanged(String filter) {
		// Do whatever when the filter changes
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case CAMPAIGN_LOADER:
				return super.onCreateLoader(id, args);
			case SURVEY_LOADER:

				String campaignFilter = String.valueOf(mCampaignFilter.getValue());
				if(mDefaultCampaign != null)
					campaignFilter = mDefaultCampaign;

				return new CursorLoader(this, Surveys.CONTENT_URI, new String [] { Surveys.SURVEY_ID, Surveys.SURVEY_TITLE }, 
						Surveys.CAMPAIGN_URN + "=?", new String[] { campaignFilter }, Surveys.SURVEY_TITLE);
			default:
				return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		mCampaignFilter.setVisibility(View.VISIBLE);
		mSurveyFilter.setVisibility(View.VISIBLE);
		
		switch(loader.getId()) {
			case CAMPAIGN_LOADER:
				super.onLoadFinished(loader, data);

				break;
			case SURVEY_LOADER:

				// Populate the filter
				mSurveyFilter.populate(data, Surveys.SURVEY_TITLE, Surveys.SURVEY_ID);
				mSurveyFilter.add(0, new Pair<String, String>("All Surveys", null));

				if(mDefaultSurvey != null) {
					mSurveyFilter.setValue(mDefaultSurvey);
					mDefaultSurvey = null;
				}

				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(loader.getId() == SURVEY_LOADER) {
			mSurveyFilter.clearAll();
		} else {
			super.onLoaderReset(loader);
		}
	}
}
