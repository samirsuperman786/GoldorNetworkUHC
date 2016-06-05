package com.goldornetwork.uhc.managers.world.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.goldornetwork.uhc.UHC;

public class WeatherChange implements Listener{

	
	public WeatherChange(UHC plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	@EventHandler
	public void on(WeatherChangeEvent e){
		if (e.toWeatherState()) {
			e.setCancelled(true);
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void on(ThunderChangeEvent e) {
		if (e.toThunderState()) {
			e.setCancelled(true);
			return;
		}

	}
}
