package io.arkeus.kismet.web;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebRequester {
	private final String selector;
	private final Connection connection;

	public WebRequester(final String url, final String selector) {
		this.selector = selector;
		this.connection = Jsoup.connect(url).userAgent("Mozilla").referrer("http://fate.io");
	}

	public Elements request() {
		try {
			final Document document = connection.get();
			return document.select(selector);
		} catch (final IOException e) {
			e.printStackTrace();
			return new Elements();
		}
	}
}
