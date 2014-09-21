package io.arkeus.kismet.scraper.d2jsp;

import io.arkeus.kismet.entity.Entity;
import io.arkeus.kismet.scraper.NewPostScraper;
import io.arkeus.kismet.web.WebRequester;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class D2JSPScraper extends NewPostScraper {
	private static final String URL_FORMAT = "http://forums.d2jsp.org/forum.php?f=%s";
	private static final String RELATIVE_URL_FORMAT = "http://forums.d2jsp.org/%s";
	private static final String SELECTOR = "table.ftb.ftlt tr";

	private final WebRequester requester;

	public D2JSPScraper(final int forum) {
		this.requester = new WebRequester(String.format(URL_FORMAT, forum), SELECTOR);
	}

	@Override
	protected List<Entity> getPosts() {
		final Elements elements = requester.request();
		final List<Entity> posts = new ArrayList<>();
		final Iterator<Element> iterator = elements.iterator();
		iterator.next();
		iterator.next();

		while (iterator.hasNext()) {
			final Element row = iterator.next();
			final Element mainTd = row.select("td").get(1);
			final Element titleLink = mainTd.select("a").get(1);
			final String url = String.format(RELATIVE_URL_FORMAT, titleLink.attr("href"));
			final String title = titleLink.text();
			final String description = mainTd.select("span.desc").text();
			final Entity post = new D2JSPPost(url, title, description);
			posts.add(post);
		}

		return posts;
	}

	private static class D2JSPPost implements Entity {
		private final String url;
		private final String title;
		private final String description;
		private final Date createdAt;

		public D2JSPPost(final String url, final String title, final String description) {
			this.url = url;
			this.title = title;
			this.description = description;
			this.createdAt = new Date();
		}

		@Override
		public String title() {
			return title;
		}

		@Override
		public String description() {
			return description;
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
			return "D2JSPPost [url=" + url + ", title=" + title + ", description=" + description + ", createdAt=" + createdAt + "]";
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
			final D2JSPPost other = (D2JSPPost) obj;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}
	}
}
