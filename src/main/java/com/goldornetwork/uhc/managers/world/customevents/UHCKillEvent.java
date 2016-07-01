package com.goldornetwork.uhc.managers.world.customevents;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCKillEvent extends Event{


	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private OfflinePlayer p;

	
	public UHCKillEvent(OfflinePlayer p){
		this.p=p;
	}

	public HandlerList getHandlers(){
		return HANDLERS;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}

	public OfflinePlayer getOfflinePlayer(){
		return p;
	}
}
