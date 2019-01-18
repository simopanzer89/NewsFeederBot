package org.telegram;

public class Article {

	private String url;
	private String title;
	private String description;
	
	public Article(String url, String title, String description) {
		this.url = url;
		this.title = title;
		this.description = description;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
	}
}
