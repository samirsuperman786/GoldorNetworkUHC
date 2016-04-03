package com.goldornetwork.uhc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.goldornetwork.uhc.UHC;

public class WeatherChange implements Listener{

	
	public WeatherChange(UHC plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	@EventHandler
	public void on(WeatherChangeEvent e){
		e.setCancelled(true);
	}
}
