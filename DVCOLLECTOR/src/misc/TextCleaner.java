package misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TextCleaner {
	
	private final static String STOPWORDS_FILE = "/stopwords.txt";
	
	public static Collection<String> stopwords;
	
	public static List<String> cleanText(String text){
		// Method order matters!
		text = text.toLowerCase();
		text = removeLinks(text);
		text = removeNewLines(text);
		text = removeAccents(text);
		text = removeNonAlphabet(text);
		text = removeSingleCharacters(text);
		text = removeWhitespace(text);
		List<String> words = new LinkedList<String>(Arrays.asList(text.split(" ")));
		removeStopwords(words);

		return words;		
	}
	
	private static String removeAccents(String text) {
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = text.replaceAll("\\p{M}", "");
		return text;
	}

	private static String removeSingleCharacters(String text) {
		text = text.replaceAll("\\s.\\s", " ");
		return text;
	}

	private static String removeLinks(String text) {
		text = text.replaceAll("https?:?[\\-a-zA-Z0-9@:%_\\+.~#?&\\/]*", " ");
		text = text.replaceAll("www\\.[\\-a-zA-Z0-9@:%_\\+.~#?&z\\/]*", " ");
		return text;
	}

	private static String removeNewLines(String text) {
		return text.replaceAll("[\\r\\n]", " ");
	}

	private static String removeWhitespace(String rawText) {
		rawText.replaceAll("\\s+", " ");
		return rawText.trim();
	}

	private static String removeNonAlphabet(String text) {
		return text.replaceAll("[^a-zA-Z'0-9#@_\\s]", " ");		
	}
	
	private static void removeStopwords(List<String> words){
		if(stopwords == null) acquireStopwords();
		
		words.removeAll(stopwords);
		
		System.out.println(words);
	}

	private static void acquireStopwords() {
		stopwords = new ArrayList<String>();
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(TextCleaner.class.getResourceAsStream(STOPWORDS_FILE)));){
			String line;
			while((line = reader.readLine()) != null){
				stopwords.add(line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}		
	}
}
