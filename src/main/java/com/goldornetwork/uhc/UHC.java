package com.goldornetwork.uhc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.goldornetwork.uhc.commands.CommandHandler;
import com.goldornetwork.uhc.listeners.ChatEvent;
import com.goldornetwork.uhc.listeners.DeathEvent;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.listeners.LeaveEvent;
import com.goldornetwork.uhc.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.utils.AntiXray;

public class UHC extends JavaPlugin {

	/*
	 * TODO config file
	 */
	//instances
	private static UHC plugin;
	private GameModeManager gameModeM;
	private TeamManager teamM;
	private TimerManager timerM;
	private BoardManager boardM;
	private ChunkGenerator chunkG;
	private ScatterManager scatterM;
	private SpectatorRegionManager spectatorM;
	private CommandHandler cmd;
	private MoveEvent moveE;
	
	public void instances(){
		teamM= new TeamManager();
		
		moveE= new MoveEvent(plugin, teamM);
		
		scatterM= new ScatterManager(teamM, moveE);
		
		timerM = new TimerManager(scatterM, teamM);
		
		spectatorM= new SpectatorRegionManager(teamM, scatterM);
		
		gameModeM= new GameModeManager(plugin);
		
		gameModeM.setupGamemodes(timerM, teamM, scatterM);
		
		boardM = new BoardManager(teamM);
		
		chunkG= new ChunkGenerator();
		
		cmd = new CommandHandler(plugin);
		
		cmd.registerCommands(teamM, timerM, chunkG);
		
		new ChatEvent(plugin);
		new DeathEvent(plugin, teamM);
		new JoinEvent(plugin, teamM, scatterM);
		new LeaveEvent(plugin, teamM, timerM);
		new AntiXray(plugin);
		BukkitScheduler sched = Bukkit.getServer().getScheduler();
		sched.runTaskTimer(plugin, spectatorM, 0L, 40L);
		sched.runTaskTimer(plugin, timerM, 0L, 20L);
		sched.runTaskTimer(plugin, chunkG, 0L, 20L);
		sched.runTaskTimer(plugin, scatterM, 0L, 20L);
	}


	@Override
	public void onEnable(){
		plugin = this;
		instances();
		setup();
	}
	private void setup() {
		teamM.setup();
		boardM.setup();
		
	}


	@Override
	public void onDisable(){
		plugin=null;
	}


}
