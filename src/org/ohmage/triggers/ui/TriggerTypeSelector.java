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
package org.ohmage.triggers.ui;


import java.util.Collection;

import org.ohmage.R;
import org.ohmage.triggers.base.TriggerTypeMap;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TriggerTypeSelector extends Dialog {

	private Context mContext;
	private TriggerTypeMap mTrigTypeMap;
	private OnClickListener mClickListener; 
	private OnListItemChangeListener mItemChangeListener; 
	ArrayAdapter<String> mAdapter;
	
	public interface OnClickListener {
		public void onClick(String trigType); 
	}
	
	public interface OnListItemChangeListener {
		public boolean onAddItem(String trigType); 
	}
	
	public void setOnClickListener(TriggerTypeSelector.OnClickListener listener) {
		mClickListener = listener;
	}
	
	public void setOnListItemChangeListener(
				TriggerTypeSelector.OnListItemChangeListener listener) {
		mItemChangeListener = listener;
	}
	
	public TriggerTypeSelector(Context context) {
		super(context);
		
		initialize(context);
	}
	
	protected TriggerTypeSelector(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);

		initialize(context);
	}
	
	@Override
	protected void onStart() {
		mAdapter.clear();
		
		Collection<String> types = mTrigTypeMap.getAllTriggerTypes();
		for(String type : types) {
			if(mItemChangeListener != null) {
				if(!mItemChangeListener.onAddItem(type)) {
					continue;
				}
			}
			
			mAdapter.add(type);
		}
		
		super.onStart();
	}
	
	private void initialize(Context context) {
		mContext = context;
		mTrigTypeMap = new TriggerTypeMap();
		
		setContentView(R.layout.trigger_type_picker);
		
		setCancelable(true);
		
		mAdapter = new ArrayAdapter<String>(context, 
												R.id.label_trigger_type) {
			@Override
			public View getView(int pos, View convertView, ViewGroup parent) {
				LayoutInflater inf = (LayoutInflater) 
					mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				View v = inf.inflate(R.layout.trigger_type_picker_row, null);
				
				TextView tv = (TextView) v.findViewById(R.id.label_trigger_type);
				tv.setText(mAdapter.getItem(pos));

				
				ImageView iv = (ImageView) v.findViewById(R.id.icon_trigger_type);
				iv.setImageResource(mTrigTypeMap.getTrigger(mAdapter.getItem(pos))
												.getIcon());
				return v;
			}
		};
		
		ListView lv = (ListView) findViewById(R.id.trigger_type_list);
		lv.setAdapter(mAdapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
													long arg3) {
				
				if(mClickListener != null) {
					mClickListener.onClick(mAdapter.getItem(pos));
				}
				
				dismiss();
			}
			
		});
	}
}
