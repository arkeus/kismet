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
			final Entity parsed = parse(main, sub);
			if (parsed != null) {
				posts.add(parsed);
			}
			iterator.next();
		}
		return posts;
	}

	private Entity parse(final Element main, final Element sub) {
		try {
			final Element link = main.select("a").first();
			final String url = link.attr("href");
			final String title = link.text();

			final Element siteElement = main.select("span.comhead").first();
			final String site = siteElement == null ? null : siteElement.text();

			final Element subtext = sub.select("td.subtext").first();

			final int points;
			final String author;
			final String[] subtextSpan = subtext.select("span").text().split(" ");
			if (subtextSpan.length == 2) {
				points = Integer.parseInt(subtextSpan[0]);
				author = subtext.select("a").first().text();
			} else {
				points = -1;
				author = "Unknown";
			}

			final Elements commentsElements = subtext.select("a");
			final int comments;
			if (commentsElements.size() == 2) {
				final String[] commentsParts = commentsElements.get(1).text().split(" ");
				if (commentsParts.length == 2) {
					comments = Integer.parseInt(commentsParts[0]);
				} else {
					comments = 0;
				}
			} else {
				comments = -1;
			}

			return new HackerNewsPost(url, title, site, points, author, comments);
		} catch (final RuntimeException e) {
			System.out.println("Failed to parse post\n\tMain: " + main + "\n\tSub: " + sub);
			return null;
		}
	}

	private static class HackerNewsPost implements Entity {
		private final String url;
		private final String title;
		private final String site;
		private final int points;
		private final String author;
		private final int comments;
		private final Date createdAt;

		public HackerNewsPost(final String url, final String title, final String site, final int points, final String author, final int comments) {
			this.url = url;
			this.title = title;
			this.site = site;
			this.points = points;
			this.author = author;
			this.comments = comments;
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
