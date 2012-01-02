package org.ohmage.prompt.timestamp;

import java.util.ArrayList;

import org.ohmage.Utilities.KVLTriplet;
import org.ohmage.prompt.Prompt;
import org.ohmage.prompt.PromptBuilder;


public class TimestampPromptBuilder implements PromptBuilder {

	@Override
	public void build(Prompt prompt, String id, String displayType,
			String displayLabel, String promptText, String abbreviatedText,
			String explanationText, String defaultValue, String condition,
			String skippable, String skipLabel, ArrayList<KVLTriplet> properties) {
		
		TimestampPrompt timestampPrompt = (TimestampPrompt) prompt;
		timestampPrompt.setId(id);
		timestampPrompt.setDisplayType(displayType);
		timestampPrompt.setDisplayLabel(displayLabel);
		timestampPrompt.setPromptText(promptText);
		timestampPrompt.setAbbreviatedText(abbreviatedText);
		timestampPrompt.setExplanationText(explanationText);
		timestampPrompt.setDefaultValue(defaultValue);
		timestampPrompt.setCondition(condition);
		timestampPrompt.setSkippable(skippable);
		timestampPrompt.setSkipLabel(skipLabel);
		timestampPrompt.setProperties(properties);
		
		
	}

}
