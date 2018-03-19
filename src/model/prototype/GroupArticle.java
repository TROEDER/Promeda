/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.prototype;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author troeder
 */
public class GroupArticle {

	private String number;
	private Article[] articles;
	private List<Article> articlesList;

	public GroupArticle(String number) {
		this.number = number;
	}

	public GroupArticle(String number, Article[] articles) {
		this.number = number;
		this.articles = articles;
	}

	public GroupArticle(String number, List<Article> articlesList) {
		this.number = number;
		this.articlesList = articlesList;
	}

	public GroupArticle(String number, ResultSet articlesList) {
		this.number = number;
		initArticles(articlesList);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Article[] getArticles() {
		return articles;
	}

	public void setArticles(Article[] articles) {
		this.articles = articles;
	}

	public List<Article> getArticlesList() {
		return articlesList;
	}

	public void setArticlesList(List<Article> articlesList) {
		this.articlesList = articlesList;
	}

	private void initArticles(ResultSet articlesList) {
		this.articlesList = new ArrayList<Article>();
		try {
			while (articlesList.next()) {
				this.articlesList.add(new Article(articlesList.getString("child_id")));
			}
		} catch (Exception ex) {
		}
	}

}
