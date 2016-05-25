package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFileScanner {

	private final String logFilename;
	private final Logger log;
	
	public RegexFileScanner(final String logFilename) {
		this.logFilename = logFilename;
		log = Logger.getLogger( RegexFileScanner.class.getName() );
	}
	
	/**
	 * scan the file with a regex and output the results on console and in the outputFilename file (if not null)
	 * @param regex regex pattern
	 * @param regexIDs
	 * @throws IOException
	 */
	public void scan(final String regex, List<String> regexIDs) throws IOException {
		if (regex == null) {
			return;
		}
		
		// Create matcher on file
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fromFile(logFilename));
                
        // Find all matches
        while (matcher.find()) {
        	for (String matchedName : getMatchedNames(regexIDs, matcher)) {
        		log.log(Level.INFO, "{0};{1};{2};{3};{4}",
                	    new Object[] {matcher.group(2), matcher.group(matcher.groupCount()), matchedName, matcher.start(matchedName), matcher.end(matchedName)});
        	}
        }
	}
	
	private static List<String> getMatchedNames(List<String> regexIDs, Matcher matcher) {
		List<String> matchedNames = new ArrayList<String>();
		for (String id : regexIDs) {
			if (matcher.group(id) != null) {
				matchedNames.add(id);
			}
		}
		return matchedNames;
	}
	
	/**
	 * add handler where matched results should be output/saved
	 * 
	 * @param handler
	 */
	public void addOutputHandler(Handler handler) {
		log.addHandler(handler);
	}
	
	/**
	 * Creates a character stream from a given file
	 * 
	 * @see http://www.java-tips.org/java-se-tips-100019/37-java-util-regex/1716-how-to-apply-regular-expressions-on-the-contents-of-a-file.html
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private CharSequence fromFile(final String filename) throws IOException {
        @SuppressWarnings("resource")
		FileInputStream input = new FileInputStream(filename);
        FileChannel channel = input.getChannel();
     
        // Create a read-only CharBuffer on the file
        ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int)channel.size());
        CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
        return cbuf;
    }
	
}
