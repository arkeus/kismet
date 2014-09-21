package io.arkeus.kismet.scraper.hackernews;

import io.arkeus.kismet.entity.Entity;
import io.arkeus.kismet.scraper.NewPostScraper;
import io.arkeus.kismet.web.WebRequester;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HackerNewsScraper extends NewPostScraper {
	private static final String URL = "https://news.ycombinator.com/";
	private static final String SELECTOR = "table table tr";

	private final WebRequester requester;

	public HackerNewsScraper() {
		this.requester = new WebRequester(URL, SELECTOR);
	}

	@Override
	protected List<Entity> getPosts() {
		final Elements elements = requester.request();
		final List<Entity> posts = new ArrayList<>();
		final Iterator<Element> iterator = elements.iterator();
		iterator.next();
		while (iterator.hasNext()) {
			final Elements title = iterator.next().select("td.title");
			if (title.size() < 2) {
				continue;
			}
			final Element main = title.get(1);
			final Element sub = iterator.next();
			posts.add(new HackerNewsPost(main, sub));
			iterator.next();
		}
		return posts;
	}

	private static class HackerNewsPost implements Entity {
		private final String url;
		private final String title;
		private final String site;
		private final int points;
		private final String author;
		private final int comments;
		private final Date createdAt;

		public HackerNewsPost(final Element main, final Element sub) {
			final Element link = main.select("a").first();
			this.url = link.attr("href");
			this.title = link.text();

			final Element siteElement = main.select("span.comhead").first();
			this.site = siteElement == null ? null : siteElement.text();

			final Element subtext = sub.select("td.subtext").first();

			final String[] subtextSpan = subtext.select("span").text().split(" ");
			if (subtextSpan.length == 2) {
				this.points = Integer.parseInt(subtextSpan[0]);
				this.author = subtext.select("a").first().text();
			} else {
				this.points = -1;
				this.author = "Unknown";
			}

			final Elements commentsElements = subtext.select("a");
			if (commentsElements.size() == 2) {
				final String[] commentsParts = commentsElements.get(1).text().split(" ");
				if (commentsParts.length == 2) {
					this.comments = Integer.parseInt(commentsParts[0]);
				} else {
					this.comments = 0;
				}
			} else {
				this.comments = -1;
			}

			this.createdAt = new Date();
		}

		@Override
		public String title() {
			return title;
		}

		@Override
		public String description() {
			return points + " points, " + comments + " comments - by " + author;
		}

		@Override
		public Date createdAt() {
			return createdAt;
		}

		@Override
		public Date updatedAt() {
			return createdAt;
		}

		@Override
		public String target() {
			return url;
		}

		@Override
		public String toString() {
			return "HackerNewsPost [url=" + url + ", title=" + title + ", site=" + site + ", points=" + points + ", author=" + author + ", comments=" + comments + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final HackerNewsPost other = (HackerNewsPost) obj;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}
	}
}
