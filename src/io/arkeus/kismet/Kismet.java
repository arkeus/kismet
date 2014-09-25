package io.arkeus.kismet;

import io.arkeus.kismet.entity.Entity;
import io.arkeus.kismet.notification.Notification;
import io.arkeus.kismet.scraper.Scraper;
import io.arkeus.kismet.scraper.d2jsp.D2JSPScraper;
import io.arkeus.kismet.scraper.hackernews.HackerNewsScraper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Library for parsing and showing notifications on posts from popular sites.
 */
public class Kismet implements Runnable {
	private static final long DEFAULT_INITIAL_DELAY = 0l;
	private static final long DEFAULT_PERIOD = 60l;

	private final ScheduledExecutorService executor;
	private final List<Scraper> scrapers;
	private final long initialDelay;
	private final long period;

	public Kismet(final ScheduledExecutorService executor, final List<Scraper> scrapers, final long initialDelay, final long period) {
		this.executor = executor;
		this.scrapers = scrapers;
		this.initialDelay = initialDelay;
		this.period = period;
	}

	/**
	 * Schedules the scrapers to begin.
	 */
	public void start() {
		executor.scheduleAtFixedRate(this, initialDelay, period, TimeUnit.SECONDS);
	}

	/**
	 * Initializes scrapers by running each to seed any existing data.
	 * @return self
	 */
	public Kismet initialize() {
		for (final Scraper scraper : scrapers) {
			scraper.scrape();
		}
		return this;
	}

	@Override
	public void run() {
		System.out.println("Running tick " + new Date());
		for (final Scraper scraper : scrapers) {
			try {
				System.out.println("Scraping " + scraper);
				final List<Entity> entities = scraper.scrape();
				for (final Entity entity : entities) {
					System.out.println("New post: " + entity);
					Notification.show(entity);
				}
			} catch (final RuntimeException e) {
				System.out.println("Failed running scraper " + scraper);
				e.printStackTrace();
			}
		}
	}

	public static void main(final String[] args) {
		final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		final List<Scraper> scrapers = new ArrayList<>();
		scrapers.add(new HackerNewsScraper());
		scrapers.add(new D2JSPScraper(153));
		new Kismet(executor, scrapers, DEFAULT_INITIAL_DELAY, DEFAULT_PERIOD).initialize().start();
	}
}
