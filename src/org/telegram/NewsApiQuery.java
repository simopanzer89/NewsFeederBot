package org.telegram;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsApiQuery {

	private String url;
	
	public NewsApiQuery() {
		this.url = "";
	}
	
	public NewsApiQuery(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setKeyword(String keyword) {
		String url = "";
		
		// initial part
		url = url.concat("https://newsapi.org/v2/everything?");
		
		// keyword
		url = url.concat("q=");
		url = url.concat("\"");
		try {
			url = url.concat(URLEncoder.encode(keyword, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url = url.concat("\"");
		url = url.concat("&");
		
		// sources
		url = url.concat("sources=");
		url = url.concat(NewsApiParams.SOURCES_LIST);
		url = url.concat("&");
		
		// search only news in the last 24 hours
		url = url.concat("from=");
		url = url.concat(getDateYesterday());
		url = url.concat("&");
		
		// add API key
		url = url.concat("apiKey=");
		url = url.concat(BotConfig.NEWSAPIKEY);
		
		this.url = url;
	}
	
	public Vector<Article> execute() {
		String result = getQueryResult();
		
		Vector<Article> articleVec = new Vector<Article>();
		JSONObject obj = new JSONObject(result);
		
		String status = obj.getString("status");
		int resultsNumber = obj.getInt("totalResults");
		
		if (status.compareTo("ok") == 0 && resultsNumber > 0) {
			JSONArray articlesJSONArray = obj.getJSONArray("articles");
			int articlesNumber = articlesJSONArray.length();
			for (int i = 0; i < articlesNumber; i++) {
				JSONObject articleJ = articlesJSONArray.getJSONObject(i);
				
				Article art = new Article(articleJ.getString("url"), 
						articleJ.getString("title"), 
						articleJ.getString("description"));
				articleVec.add(art);
			}
		}
			
		return articleVec;
	}
	
	private String getQueryResult() {
		URL urlObj;
		
		try {
			urlObj = new URL(this.url);
			URLConnection conn;
			conn = urlObj.openConnection();
			
			StringBuilder result = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			String r = result.toString();
			return r;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
	private String getDateYesterday() {
		// the truncatedTo() method was used to solve a bug: the formatter would output milliseconds as well (even though the doc says it only shows seconds)
		LocalDateTime yesterday = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS);
		
		return yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
	
}
