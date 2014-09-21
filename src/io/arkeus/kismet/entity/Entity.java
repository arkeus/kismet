package io.arkeus.kismet.entity;

import java.util.Date;

public interface Entity {
	public String title();
	public String description();
	public Date createdAt();
	public Date updatedAt();
	public String target();
}
