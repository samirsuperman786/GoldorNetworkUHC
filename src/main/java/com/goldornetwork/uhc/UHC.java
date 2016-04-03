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
import com.goldornetwork.uhc.listeners.WeatherChange;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.Medic;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager.Gamemodes;
import com.goldornetwork.uhc.utils.AntiXray;
import com.goldornetwork.uhc.utils.CombatLog;

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
	private CommandHandler cmd;
	private MoveEvent moveE;
	
	public void instances(){
		//instances
		teamM= new TeamManager();
		
		moveE= new MoveEvent(plugin, teamM);
		
		scatterM= new ScatterManager(plugin, teamM, moveE);
		
		timerM = new TimerManager(plugin, scatterM, teamM);
		
		gameModeM= new GameModeManager(plugin);
		
		gameModeM.setupGamemodes(timerM, teamM, scatterM);
		
		boardM = new BoardManager(teamM);
		
		chunkG= new ChunkGenerator();
		
		
		
		//cmds
		cmd = new CommandHandler(plugin);
		
		cmd.registerCommands(teamM, timerM, chunkG);
		
		//listeners
		new SpectatorRegionManager(plugin, teamM, scatterM);
		new Medic(plugin, teamM);
		new ChatEvent(plugin);
		new DeathEvent(plugin, teamM, scatterM);
		new JoinEvent(plugin, teamM, scatterM);
		new LeaveEvent(plugin,teamM, scatterM);
		new WeatherChange(plugin);
		new AntiXray(plugin);
		//new CombatLog(plugin, scatterM, teamM);
		//timers
		BukkitScheduler sched = Bukkit.getServer().getScheduler();
		sched.runTaskTimer(plugin, chunkG, 0L, 20L);
		
		//setup
		teamM.setup();
		boardM.setup();
		scatterM.setup();
		timerM.setup();
		moveE.setup();
		gameModeM.setup();
		//TESTCODE
		gameModeM.enableGamemode(Gamemodes.SKYHIGH);
	}


	@Override
	public void onEnable(){
		plugin = this;
		instances();
	}
	


	@Override
	public void onDisable(){
		plugin=null;
	}


}
