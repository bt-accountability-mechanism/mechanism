package utils;

import java.util.Map;
import java.util.Map.Entry;

public class RegexBuilder {

	public static String mergeRegexExpressions(MultiRegex[] regExpressions, Map<String, String> variables) {
		StringBuilder regex = new StringBuilder("(?:");
		
		if (regExpressions != null && regExpressions.length > 0) {
			for (int index = 0; index < regExpressions.length; index++) {
				regex.append(replaceVariables(regExpressions[index].toString(), variables)
						.replace("{ID_PATTERN}", regExpressions[index].id));
				regex.append("|");
			}
			regex = regex.deleteCharAt(regex.length()-1);
		} else {
			regex.append(".");
		}
		regex.append(")");
		return regex.toString();
	}
	
	private static String replaceVariables(String data, Map<String, String> variables) {
		String finalRegex = data;
		if (variables != null) {
			for (Entry<String, ?> entry : variables.entrySet()) {
				finalRegex = finalRegex.replace(entry.getKey(), entry.getValue().toString());
			}
		}
		return finalRegex;
	}
	
}
