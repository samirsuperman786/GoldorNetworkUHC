package com.goldornetwork.uhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldornetwork.uhc.commands.CancelCommand;
import com.goldornetwork.uhc.commands.CreateCommand;
import com.goldornetwork.uhc.commands.InvitePlayerCommand;
import com.goldornetwork.uhc.commands.JoinCommand;
import com.goldornetwork.uhc.commands.StartCommand;
import com.goldornetwork.uhc.listeners.BreakEvent;
import com.goldornetwork.uhc.listeners.ChatEvent;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.LocationListener;
import com.goldornetwork.uhc.managers.ModifierManager.ModifierManager;
import com.goldornetwork.uhc.managers.ModifierManager.actions.BowListener;
import com.goldornetwork.uhc.managers.ModifierManager.actions.DealDamage;
import com.goldornetwork.uhc.managers.ModifierManager.actions.DeathEvent;
import com.goldornetwork.uhc.managers.ModifierManager.actions.KingsManager;
import com.goldornetwork.uhc.managers.ModifierManager.actions.PotionSwap;

public class UHC extends JavaPlugin {

	public static UHC plugin;
	
	@Override
	public void onDisable(){
		
	}
	
	@Override
	public void onEnable(){
		plugin = this;
		setup();
		registerListeners();
		registerCommands();
	}

	private void setup() {
		TeamManager.getInstance().setup();
		ScatterManager.getInstance().setup();
		ModifierManager.getInstance().setup();
		PotionSwap.getInstance().setup();
		KingsManager.getInstance().setup();
		DeathEvent.getInstance().setup();
		BowListener.getInstance().setup();
		ModifierManager.getInstance().setup();
		TimerManager.getInstance().setup();
		TeamManager.getInstance().setup();
		ScatterManager.getInstance().setup();
		JoinEvent.getInstance().setup();
		BreakEvent.getInstance().setup();
		Bukkit.getServer().getScheduler().runTaskTimer(this, ScatterManager.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, TimerManager.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, SpectatorRegionManager.getInstance(), 0L, 40L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, LocationListener.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, PotionSwap.getInstance(), 0L, 6000L);
	}
	
	private void registerCommands() {
		getCommand("start").setExecutor(new StartCommand());
		getCommand("cancel").setExecutor(new CancelCommand());
		getCommand("create").setExecutor(new CreateCommand());
		getCommand("join").setExecutor(new JoinCommand());
		getCommand("invite").setExecutor(new InvitePlayerCommand());
	}

	private void registerListeners() {
		new ChatEvent(this);
		Bukkit.getServer().getPluginManager().registerEvents(JoinEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(BreakEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(BowListener.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(DeathEvent.getInstance(), this);
	}

	
	
	
	
	
	
	
	
}
