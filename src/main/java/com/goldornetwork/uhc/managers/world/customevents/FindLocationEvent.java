package com.goldornetwork.uhc.managers.world.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FindLocationEvent extends Event{

	
	private static final HandlerList HANDLERS = new HandlerList();
	private int numberOfLocations;

	
	public FindLocationEvent(int numberOfLocations){
		this.numberOfLocations=numberOfLocations;
	}
	
	public HandlerList getHandlers(){
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	
	public int getNumberOfLocations(){
		return numberOfLocations;
	}
}
