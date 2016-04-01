package com.goldornetwork.uhc.managers.GameModeManager;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.goldornetwork.uhc.UHC;

public abstract class Gamemode{

	private boolean enabled = false;
	
	private String description;
	private String name;
	
	protected Gamemode(String name, String description){
		this.description=description;
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	public String getDescription() {
		return description;
	}
	public boolean enable(UHC plugin) {
		if (isEnabled()) {
			return false;
		}

		if (this instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) this, plugin);
		}

		enabled = true;
		onEnable();
		return true;
	}
	public boolean disable() {
		if (!isEnabled()) {
			return false;
		}
		
		if (this instanceof Listener) {
			HandlerList.unregisterAll((Listener) this);
		}

		enabled = false;
		onDisable();
		return true;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void onDisable() {}
	public void onEnable() {}
}
