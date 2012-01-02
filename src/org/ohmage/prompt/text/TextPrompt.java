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
package org.ohmage.prompt.text;

import org.ohmage.R;
import org.ohmage.prompt.AbstractPrompt;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class TextPrompt extends AbstractPrompt {
	
	private String mText;
	private int mMinLength;
	private int mMaxLength;

	public TextPrompt() {
		super();
		mText = "";
	}
	
	public String getText(){
		return mText;
	}
	
	void setMinLength(int value) {
		mMinLength = value;
	}
	
	void setMaxLength(int value) {
		mMaxLength = value;
	}
	
	public int getMinLength(){
		return mMinLength;
	}
	
	public int getMaxLength(){
		return mMaxLength;
	}
	
	@Override
	protected void clearTypeSpecificResponseData() {
		if (mDefaultValue != null) {
			mText = getDefaultValue().trim();
		} else {
			mText = "";
		}
	}
	
	/**
	 * Returns true if the text for this prompt is not null nor an empty
	 * String.
	 */
	@Override
	public boolean isPromptAnswered() {
		return((mText != null) && (! "".equals(mText)) && (mText.length() >= mMinLength) && (mText.length() <= mMaxLength));
	}

	@Override
	protected String getTypeSpecificResponseObject() {
		if (mText.equals("")) {
			return null;
		}
		else {
			return mText;
		}
	}
	
	/**
	 * The text to be displayed to the user if the prompt is considered
	 * unanswered.
	 */
	@Override
	public String getUnansweredPromptText() {
		if (mMinLength == mMaxLength) {
			return("You must provide a response that is exactly " + mMinLength + " characters long.");
		} else {
			return("You must provide a response that is between " + mMinLength + " and " + mMaxLength + " characters long.");
		}
	}
	
	@Override
	protected Object getTypeSpecificExtrasObject() {
		return null;
	}

	@Override
	public View getView(Context context) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.prompt_text, null);
		
		EditText editText = (EditText) layout.findViewById(R.id.text);
		
		editText.setText(mText);
		
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mText = s.toString().trim();
			}
		});
		
		return layout;
	}

	@Override
	public void handleActivityResult(Context context, int requestCode,
			int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
	}

}
