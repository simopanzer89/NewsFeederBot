package org.telegram;

// in this class you can set-up query parameters for newsapi (keywords and sources)
public class NewsApiParams {

	// a list of keywords to be individually searched for
	public static final String[] KEYWORD_LIST = {"trump",
			"silvio berlusconi"};

	// news sources https://newsapi.org/sources
	public static final String SOURCES_LIST = "ansa" + "," + 
			"la-repubblica" + "," + 
			"bbc-news";

}
