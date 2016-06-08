package com.goldornetwork.uhc.managers.world.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScatterEndEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
}
