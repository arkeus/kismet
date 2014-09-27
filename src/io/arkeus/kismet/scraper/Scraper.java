package io.arkeus.kismet.scraper;

import io.arkeus.kismet.entity.Entity;

import java.util.List;

/**
 * Interface for a single scraper that knows how to scrape a target and return
 * the results as a list of entities.
 */
public interface Scraper {
	/**
	 * Scrapes the target and creates a list of all entities found.
	 * @return the entities
	 */
	public List<Entity> scrape();
}
