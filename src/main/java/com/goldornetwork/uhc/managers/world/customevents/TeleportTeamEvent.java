package com.goldornetwork.uhc.managers.world.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeleportTeamEvent extends Event{

	
	private static final HandlerList HANDLERS = new HandlerList();

	
	public HandlerList getHandlers(){
		return HANDLERS;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
}
