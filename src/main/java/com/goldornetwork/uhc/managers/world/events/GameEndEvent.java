package com.goldornetwork.uhc.managers.world.events;

import java.util.List;
import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();

	private List<UUID> winners;
	public GameEndEvent(List<UUID> winners) {
		this.winners=winners;
	}
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}


	public List<UUID> getWinners(){
		return winners;
	}
}
