package com.goldornetwork.uhc;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldornetwork.uhc.commands.CommandHandler;
import com.goldornetwork.uhc.listeners.BackGround;
import com.goldornetwork.uhc.listeners.DeathEvent;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.listeners.LeaveEvent;
import com.goldornetwork.uhc.listeners.MoveEvent;
import com.goldornetwork.uhc.listeners.WeatherChange;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.utils.AntiXray;
import com.goldornetwork.uhc.utils.Medic;

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
	private VoteManager voteM;
	private Medic medic;
	
	public void instances(){
		
		//instances
		teamM= new TeamManager(plugin);
		
		moveE= new MoveEvent(plugin, teamM);
		
		scatterM= new ScatterManager(plugin, teamM, moveE);
		
		gameModeM= new GameModeManager(plugin);
		
		voteM = new VoteManager(plugin, gameModeM);
		
		timerM = new TimerManager(plugin, scatterM, teamM, voteM);
		
		gameModeM.setupGamemodes(teamM, scatterM);
		
		boardM = new BoardManager(teamM);
		
		chunkG= new ChunkGenerator(plugin);
		
		medic= new Medic(plugin, teamM);
		
		//cmds
		cmd = new CommandHandler(plugin);
		
		cmd.registerCommands(teamM, timerM, chunkG, voteM);
		
		//listeners
		new SpectatorRegionManager(plugin, teamM, scatterM);
		new BackGround(plugin);
		new DeathEvent(plugin, teamM, scatterM);
		new JoinEvent(plugin, teamM, scatterM);
		new LeaveEvent(plugin,teamM, scatterM);
		new WeatherChange(plugin);
		new AntiXray(plugin);
		//new CombatLog(plugin, scatterM, teamM);
		
		//setup
		teamM.setup();
		boardM.setup();
		scatterM.setup();
		timerM.setup();
		moveE.setup();
		voteM.setup();
		
		
	}


	private void createConfig() {
		 try {
		        if (!getDataFolder().exists()) {
		            getDataFolder().mkdirs();
		        }
		        File file = new File(getDataFolder(), "config.yml");
		        if (!file.exists()) {
		            getLogger().info("Config.yml not found, creating!");
		            saveDefaultConfig();
		        } else {
		            getLogger().info("Config.yml found, loading!");
		        }
		    } catch (Exception e) {
		        e.printStackTrace();

		    }
		 
		 File file = new File(getDataFolder(), "config.yml");
		 if (!file.exists()) {
		     getLogger().info("config.yml not found, creating!");
		     saveDefaultConfig();
		 } else {
		     getLogger().info("config.yml found, loading!");
		 }
		 getConfig().options().copyDefaults(true);
		 
	}


	@Override
	public void onEnable(){
		plugin = this;
		createConfig();
		instances();
	}
	


	@Override
	public void onDisable(){
		plugin=null;
	}


}
