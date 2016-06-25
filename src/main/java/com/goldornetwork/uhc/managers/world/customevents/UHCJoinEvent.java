package com.goldornetwork.uhc.managers.world.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCJoinEvent extends Event{
	
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private Player target;
	
	
	public UHCJoinEvent(Player target) {
		this.target=target;
	}
	
	public HandlerList getHandlers(){
		return HANDLERS;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	
	public Player getPlayer(){
		return target;
	}
}
