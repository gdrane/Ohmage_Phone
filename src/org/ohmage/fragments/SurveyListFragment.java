package org.ohmage.fragments;


import org.ohmage.R;
import org.ohmage.activity.SubActionClickListener;
import org.ohmage.adapters.SurveyListCursorAdapter;
import org.ohmage.db.DbContract.Campaigns;
import org.ohmage.db.DbContract.Surveys;
import org.ohmage.db.Models.Survey;
import org.ohmage.db.utils.SelectionBuilder;
import org.ohmage.ui.OhmageFilterable.CampaignFilter;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * <p>The {@link SurveyListFragment} shows a list of surveys</p>
 * 
 * <p>The {@link SurveyListFragment} accepts {@link CampaignFilter#EXTRA_CAMPAIGN_URN} as an extra</p>
 * @author cketcham
 *
 */
public class SurveyListFragment extends FilterableListFragment implements SubActionClickListener {

	private static final String TAG = "SurveyListFragment";
		
	private boolean mShowPending = false;
	
	private CursorAdapter mAdapter;
	private OnSurveyActionListener mListener;
	
	public interface OnSurveyActionListener {
		public void onSurveyActionView(Uri surveyUri);
        public void onSurveyActionStart(Uri surveyUri);
        public void onSurveyActionUnavailable(Uri surveyUri);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
        setShowPending();
		
		mAdapter = new SurveyListCursorAdapter(getActivity(), null, this, 0);
		setListAdapter(mAdapter);
		
		// Start out with a progress indicator.
        setListShown(false);
        
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSurveyActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSurveyActionListener");
        }
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		Cursor cursor = (Cursor) getListAdapter().getItem(position);
		
		Uri uri = Campaigns.buildSurveysUri(cursor.getString(cursor.getColumnIndex(Surveys.CAMPAIGN_URN)), cursor.getString(cursor.getColumnIndex(Surveys.SURVEY_ID)));
		mListener.onSurveyActionView(uri);
	}
	
	@Override
	public void onSubActionClicked(Uri uri) {
		
		mListener.onSurveyActionStart(uri);
		
//		mListener.onSurveyActionUnavailable();
	}

	public void setShowPending(boolean showPending) {
		mShowPending = showPending;
		if(isVisible()) {
			setShowPending();
			getLoaderManager().restartLoader(0, null, this);
		}
	}
	
	private void setShowPending() {
		if (mShowPending) {
			setEmptyText(getString(R.string.surveys_empty_pending));
		} else {
			setEmptyText(getString(R.string.surveys_empty_all));
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i(TAG, "Creating loader - filter: " + getCampaignUrn());
		Uri baseUri = Surveys.CONTENT_URI;
		
		SelectionBuilder builder = new SelectionBuilder();
		
		if (getCampaignUrn() != null) {
			builder.where(Surveys.CAMPAIGN_URN + "= ?", getCampaignUrn());
		}
		
		if (mShowPending) {
			builder.where(Surveys.SURVEY_STATUS + "=" + Survey.STATUS_TRIGGERED);
		} 
		
		return new CursorLoader(getActivity(), baseUri, null, builder.getSelection(), builder.getSelectionArgs(), Surveys.SURVEY_TITLE);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
		
		// The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
}
