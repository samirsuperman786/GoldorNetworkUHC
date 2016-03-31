package com.goldornetwork.uhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldornetwork.uhc.commands.CancelCommand;
import com.goldornetwork.uhc.commands.ChunkGenerateCommand;
import com.goldornetwork.uhc.commands.CreateCommand;
import com.goldornetwork.uhc.commands.InvitePlayerCommand;
import com.goldornetwork.uhc.commands.JoinCommand;
import com.goldornetwork.uhc.commands.StartCommand;
import com.goldornetwork.uhc.commands.UnInvitePlayerCommand;
import com.goldornetwork.uhc.listeners.BreakEvent;
import com.goldornetwork.uhc.listeners.ChatEvent;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.listeners.LeaveEvent;
import com.goldornetwork.uhc.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.LocationListener;
import com.goldornetwork.uhc.managers.ModifierManager.ModifierManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.BowListener;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.DeathEvent;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.DisabledCrafting;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.TheHobbitManager;

public class UHC extends JavaPlugin {

	private static UHC plugin;

	@Override
	public void onDisable(){

	}

	@Override
	public void onEnable(){
		plugin = this;
		registerListeners();
		registerCommands();
		setup();
		registerTimers();
	}
	
	public static UHC getInstance(){
		return plugin;
	}

	private void registerTimers() {
		Bukkit.getServer().getScheduler().runTaskTimer(this, ScatterManager.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, TimerManager.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, SpectatorRegionManager.getInstance(), 0L, 40L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, LocationListener.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, ChunkGenerator.getInstance(), 0L, 20L);
		
	}

	private void setup() {
		TeamManager.getInstance().setup();
		ScatterManager.getInstance().setup();
		ModifierManager.getInstance().setup(plugin);
		//DeathEvent.getInstance().setup();
		//BowListener.getInstance().setup();
		TimerManager.getInstance().setup();
		JoinEvent.getInstance().setup();
		BreakEvent.getInstance().setup();
		LocationListener.getInstance().setup();
		MoveEvent.getInstace().setup();
	}

	private void registerCommands() {
		getCommand("start").setExecutor(new StartCommand());
		getCommand("cancel").setExecutor(new CancelCommand());
		getCommand("create").setExecutor(new CreateCommand());
		getCommand("join").setExecutor(new JoinCommand());
		getCommand("invite").setExecutor(new InvitePlayerCommand());
		getCommand("uninvite").setExecutor(new UnInvitePlayerCommand());
		getCommand("render").setExecutor(new ChunkGenerateCommand());
	}

	private void registerListeners() {
		new ChatEvent(this);
		Bukkit.getServer().getPluginManager().registerEvents(JoinEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(BreakEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(BowListener.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(DeathEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(TheHobbitManager.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(DisabledCrafting.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(LeaveEvent.getInstace(), this);
		Bukkit.getServer().getPluginManager().registerEvents(MoveEvent.getInstace(), this);
	
	}









}
