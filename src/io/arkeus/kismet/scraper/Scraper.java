package io.arkeus.kismet.scraper;

import io.arkeus.kismet.entity.Entity;

import java.util.List;

public interface Scraper {
	public List<Entity> scrape();
}
