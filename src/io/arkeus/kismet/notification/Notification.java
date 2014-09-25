package io.arkeus.kismet.notification;

import io.arkeus.kismet.entity.Entity;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.event.INotificationEventListener;
import ch.swingfx.twinkle.event.NotificationEvent;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.LightDefaultNotification;
import ch.swingfx.twinkle.window.Positions;

/**
 * Static helper class for showing notifications.
 */
public class Notification {
	private static final INotificationStyle STYLE = new LightDefaultNotification().withWidth(400).withAlpha(0.9f);
	private static final int DISPLAY_TIME = 10000;

	public static void show(final Entity entity) {
		new NotificationBuilder().withStyle(STYLE).withTitle(entity.title()).withMessage(entity.description()).withDisplayTime(DISPLAY_TIME)
				.withPosition(Positions.SOUTH_EAST).withListener(new NotificationListener(entity)).showNotification();
	}

	private static final class NotificationListener implements INotificationEventListener {
		private final Entity entity;

		public NotificationListener(final Entity entity) {
			this.entity = entity;
		}

		@Override
		public void clicked(final NotificationEvent event) {
			try {
				Desktop.getDesktop().browse(new URL(entity.target()).toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void closed(final NotificationEvent event) {

		}

		@Override
		public void mouseOver(final NotificationEvent event) {

		}

		@Override
		public void mouseOut(final NotificationEvent event) {

		}

		@Override
		public void opened(final NotificationEvent event) {

		}
	}

}
