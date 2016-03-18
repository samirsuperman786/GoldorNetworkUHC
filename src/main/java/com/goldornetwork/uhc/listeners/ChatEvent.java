package com.goldornetwork.uhc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.goldornetwork.uhc.UHC;



public class ChatEvent implements Listener{

	
	public ChatEvent(UHC plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent e){
		e.setFormat("%s: %s");
	}
	
}
