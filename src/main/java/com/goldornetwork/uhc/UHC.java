package com.goldornetwork.uhc;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.utils.AntiXray;
import com.goldornetwork.uhc.utils.Medic;

public class UHC extends JavaPlugin {

	/*
	 * TODO config file
	 */
	private File configf, teamf;
	private FileConfiguration config, team;
	
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
	private WorldManager worldM;
	private BackGround backG;
	
	public void instances(){
		
		//instances
		teamM= new TeamManager(plugin);
		
		moveE= new MoveEvent(plugin, teamM);

		backG = new BackGround(plugin);
		
		scatterM= new ScatterManager(plugin, teamM, moveE, backG);
		
		gameModeM= new GameModeManager(plugin);
		
		voteM = new VoteManager(plugin, gameModeM);
		
		timerM = new TimerManager(plugin, scatterM, teamM, voteM, backG);
		
		boardM = new BoardManager(plugin, teamM);
		
		chunkG= new ChunkGenerator(plugin);
		
		worldM = new WorldManager(plugin, scatterM);
		
		medic= new Medic(plugin, teamM);
		//cmds
		cmd = new CommandHandler(plugin);
		
		cmd.registerCommands(teamM, timerM, chunkG, voteM);
		
		//listeners
		new SpectatorRegionManager(plugin, teamM, scatterM);
		
		new DeathEvent(plugin, teamM, scatterM, worldM);
		new JoinEvent(plugin, teamM, scatterM);
		new LeaveEvent(plugin,teamM, scatterM);
		new WeatherChange(plugin);
		new AntiXray(plugin);
		//new CombatLog(plugin, scatterM, teamM);
		
		//setup
		gameModeM.setupGamemodes(teamM, scatterM);
		teamM.setup();
		boardM.setup();
		scatterM.setup();
		timerM.setup();
		moveE.setup();
		voteM.setup();
		worldM.setup();
		
	}


	private void createConfig() {
		 teamf = new File(getDataFolder(), "teams.yml");
		 configf= new File(getDataFolder(), "config.yml");
		 
		 if (!configf.exists()) {
			 configf.getParentFile().mkdirs();
			 saveResource("config.yml", false);
		 } 
		 
		 
		 if (!teamf.exists()) {
		     teamf.getParentFile().mkdirs();
		     saveResource("teams.yml", false);
		 }
		 
		 config = new YamlConfiguration();
		 team = new YamlConfiguration();
		 try{
			 config.load(configf);
			 team.load(teamf);
		 }catch(IOException | InvalidConfigurationException e){
			 e.printStackTrace();
		 }
		 getConfig().options().copyDefaults(true);
		 getTeamConfig().options().copyDefaults(true);
	}

	public FileConfiguration getTeamConfig(){
		return this.team;
	}
	public void saveTeamConfig(){
		try {
			team.save(teamf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public FileConfiguration getConfig(){
		return this.config;
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
