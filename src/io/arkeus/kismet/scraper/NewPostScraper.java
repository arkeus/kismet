package io.arkeus.kismet.scraper;

import io.arkeus.kismet.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scraper containing logic to filter out previously known posts.
 */
public abstract class NewPostScraper implements Scraper {
	private final Set<Entity> knownPosts;

	public NewPostScraper() {
		this.knownPosts = new HashSet<>();
	}

	@Override
	public List<Entity> scrape() {
		final List<Entity> posts = getPosts();
		final List<Entity> entities = new ArrayList<>();
		for (final Entity post : posts) {
			if (!knownPosts.contains(post)) {
				knownPosts.add(post);
				entities.add(post);
				//System.out.println(post);
			}
		}
		return entities;
	}

	/**
	 * Implementation should return all current posts.
	 * @return all current posts
	 */
	protected abstract List<Entity> getPosts();
}
