package main;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;

import ui.Program;
import ui.IProgram;
import utils.MultiRegex;
import utils.Regex;
import utils.RegexBuilder;
import utils.RegexFileScanner;

public class Main2 {

	public static void main(String[] args) {
		RegexFileScanner scanner = new RegexFileScanner("irobot.log");
		try {
			scanner.addOutputHandler(new FileHandler("ergebnisse.log"));
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		scanner.addOutputHandler(new ConsoleHandler());
		
		Map<String, String> variables = buildRegexVariablesMap();
		final Regex[] regexList = {
			new Regex("SubRegexA", completeLogLine("Robot goes left")),
			new Regex("SubRegexB", completeLogLine("Robot goes right"), null, "+"),
			new Regex("SubRegexC", completeLogLine("Robot goes back"))
		};
		
		
		final MultiRegex[] patternList = {
			new MultiRegex("RegexA", regexList)
		};

		final String finalRegex = RegexBuilder.mergeRegexExpressions(patternList, variables);
		final List<String> nameList = new ArrayList<String>();
		nameList.add("RegexA");
		System.out.println(finalRegex);
		try {
			scanner.scan(finalRegex, nameList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String completeLogLine(final String message) {
		return "%datetime% - %name% - %types% - (?<message{ID}>" + message + ")%newline%";
	}
	
	private static Map<String, String> buildRegexVariablesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("%datetime%", "(?:\\d{4}+-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01]) (?:00|[0-9]|1[0-9]|2[0-3]):(?:[0-9]|[0-5][0-9]):(?:[0-9]|[0-5][0-9]),(?:[0-9]{3}))");
		map.put("%name%", "(?:irobot)");
		map.put("%types%", "(?:INFO|DEBUG)");
		map.put("%newline%", "(?:[\\n|\\r])");
		
		return map;
	}

}
