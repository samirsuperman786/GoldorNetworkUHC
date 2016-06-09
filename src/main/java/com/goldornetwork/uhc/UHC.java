package com.goldornetwork.uhc;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.commands.CommandHandler;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.board.BoardManager;
import com.goldornetwork.uhc.managers.world.ChunkGenerator;
import com.goldornetwork.uhc.managers.world.UHCBan;
import com.goldornetwork.uhc.managers.world.UHCServer;
import com.goldornetwork.uhc.managers.world.UHCWarn;
import com.goldornetwork.uhc.managers.world.WorldFactory;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.listeners.DeathEvent;
import com.goldornetwork.uhc.managers.world.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.world.listeners.LeaveEvent;
import com.goldornetwork.uhc.managers.world.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.world.listeners.WeatherChange;
import com.goldornetwork.uhc.managers.world.listeners.team.ChatManager;
import com.goldornetwork.uhc.managers.world.listeners.team.TeamInteraction;
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
	private TeamInteraction teamI;
	private BoardManager boardM;
	private ChunkGenerator chunkG;
	private ScatterManager scatterM;
	private CommandHandler cmd;
	private MoveEvent moveE;
	private VoteManager voteM;
	private Medic medic;
	private WorldManager worldM;
	private ChatManager chatM;
	private WorldFactory worldF;
	private UHCBan uhcB;
	private UHCWarn uhcWarn;
	private UHCServer uhcServer;
	private SpectatorRegionManager spectM;
	private Runtime rt = Runtime.getRuntime();
	private OperatingSystemMXBean compHandler = ManagementFactory.getOperatingSystemMXBean();
	
	private final static int fillMemoryTolerance = 500;
	
	public void instances(){
		
		//instances
		worldF= new WorldFactory(plugin);
		
		
		gameModeM= new GameModeManager(plugin);
		
		boardM = new BoardManager(plugin);
		
		chunkG= new ChunkGenerator(plugin);
		
		teamM= new TeamManager(plugin, boardM);
		
		chatM = new ChatManager(plugin, teamM);
		
		moveE= new MoveEvent(plugin, teamM);
		
		worldM = new WorldManager(plugin, teamM, worldF, chunkG);
		
		scatterM= new ScatterManager(plugin, teamM, moveE, chatM, worldF, chunkG, worldM);
		
		voteM = new VoteManager(plugin, gameModeM, teamM);
		
		timerM = new TimerManager(plugin, scatterM, teamM, voteM, chatM, worldM);
		
		spectM = new SpectatorRegionManager(plugin, teamM, worldM);
		
		uhcServer=new UHCServer(plugin, teamM);
		
		medic= new Medic(plugin, teamM);
		
		teamI = new TeamInteraction(teamM);
		
		uhcWarn= new UHCWarn();
		
		uhcB = new UHCBan(teamM);
		//cmds
		cmd = new CommandHandler(plugin);
		
		cmd.registerCommands(teamM, timerM, gameModeM, chunkG, voteM, teamI, uhcB, uhcWarn, chatM);
		
		//listeners
		
		new TeamInteraction(teamM);
		new DeathEvent(plugin, teamM, scatterM, worldM);
		new JoinEvent(plugin, teamM, worldM);
		new LeaveEvent(plugin,teamM, scatterM);
		new WeatherChange(plugin);
		new AntiXray(plugin);
		
	}

	private void setup(){
		teamM.setup();
		timerM.setup();
		moveE.setup();
		voteM.setup();
		worldM.setup();
		gameModeM.setupGamemodes(teamM, worldM);
		scatterM.setup();
		spectM.setup();
		boardM.setup(teamM, worldM, timerM);
		uhcServer.setup();
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
		worldF.setup();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				setup();
				
			}
		}.runTaskLater(plugin, 20L);
		
	}
	public int AvailableMemory(){
		return (int)((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576);  // 1024*1024 = 1048576 (bytes in 1 MB)
	}
	
	
	public boolean availableMemoryTooLow(){

		return AvailableMemory() < fillMemoryTolerance;
	}


	@Override
	public void onDisable(){
		for(World world: Bukkit.getWorlds()){
			world.setAutoSave(false);
			Bukkit.unloadWorld(world, false);
		}
		Bukkit.getServer().getScheduler().cancelAllTasks();
		plugin=null;
	}


}
