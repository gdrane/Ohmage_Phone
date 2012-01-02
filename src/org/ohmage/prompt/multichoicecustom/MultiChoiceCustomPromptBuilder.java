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
package org.ohmage.prompt.multichoicecustom;

import java.util.ArrayList;

import org.ohmage.Utilities.KVLTriplet;
import org.ohmage.prompt.Prompt;
import org.ohmage.prompt.PromptBuilder;


public class MultiChoiceCustomPromptBuilder implements PromptBuilder {

	@Override
	public void build(	Prompt prompt, String id, String displayType,
						String displayLabel, String promptText, String abbreviatedText,
						String explanationText, String defaultValue, String condition,
						String skippable, String skipLabel, ArrayList<KVLTriplet> properties) {
		
		// TODO deal with null arguments
		
		MultiChoiceCustomPrompt multiChoiceCustomPrompt = (MultiChoiceCustomPrompt) prompt;
		multiChoiceCustomPrompt.setId(id);
		multiChoiceCustomPrompt.setDisplayType(displayType);
		multiChoiceCustomPrompt.setDisplayLabel(displayLabel);
		multiChoiceCustomPrompt.setPromptText(promptText);
		multiChoiceCustomPrompt.setAbbreviatedText(abbreviatedText);
		multiChoiceCustomPrompt.setExplanationText(explanationText);
		multiChoiceCustomPrompt.setDefaultValue(defaultValue);
		multiChoiceCustomPrompt.setCondition(condition);
		multiChoiceCustomPrompt.setSkippable(skippable);
		multiChoiceCustomPrompt.setSkipLabel(skipLabel);
		multiChoiceCustomPrompt.setProperties(properties);
		
		//add entries from db to properties
		
		multiChoiceCustomPrompt.setChoices(properties);
	}

}
