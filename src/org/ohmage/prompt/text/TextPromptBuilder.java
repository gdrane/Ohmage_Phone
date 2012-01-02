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

import java.util.ArrayList;

import org.ohmage.Utilities.KVLTriplet;
import org.ohmage.prompt.Prompt;
import org.ohmage.prompt.PromptBuilder;


public class TextPromptBuilder implements PromptBuilder {

	@Override
	public void build(Prompt prompt, String id, String displayType,
			String displayLabel, String promptText, String abbreviatedText,
			String explanationText, String defaultValue, String condition,
			String skippable, String skipLabel, ArrayList<KVLTriplet> properties) {
		
		TextPrompt textPrompt = (TextPrompt) prompt;
		textPrompt.setId(id);
		textPrompt.setDisplayType(displayType);
		textPrompt.setDisplayLabel(displayLabel);
		textPrompt.setPromptText(promptText);
		textPrompt.setAbbreviatedText(abbreviatedText);
		textPrompt.setExplanationText(explanationText);
		textPrompt.setDefaultValue(defaultValue);
		textPrompt.setCondition(condition);
		textPrompt.setSkippable(skippable);
		textPrompt.setSkipLabel(skipLabel);
		textPrompt.setProperties(properties);
		
		for (KVLTriplet property : properties) {
			if (property.key.equals("min")) {
				textPrompt.setMinLength(Integer.parseInt(property.label));
			} else if (property.key.equals("max")) {
				textPrompt.setMaxLength(Integer.parseInt(property.label));
			}
		}
		
		textPrompt.clearTypeSpecificResponseData();
		
	}

}
