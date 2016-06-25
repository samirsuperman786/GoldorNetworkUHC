package com.goldornetwork.uhc;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.commands.CommandHandler;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.chat.ChatManager;
import com.goldornetwork.uhc.managers.chat.TeamInteraction;
import com.goldornetwork.uhc.managers.world.ChunkGenerator;
import com.goldornetwork.uhc.managers.world.SpectatorRegion;
import com.goldornetwork.uhc.managers.world.UHCBan;
import com.goldornetwork.uhc.managers.world.UHCServer;
import com.goldornetwork.uhc.managers.world.UHCWarn;
import com.goldornetwork.uhc.managers.world.WorldFactory;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.listeners.DeathListener;
import com.goldornetwork.uhc.managers.world.listeners.JoinListener;
import com.goldornetwork.uhc.managers.world.listeners.MoveListener;
import com.goldornetwork.uhc.managers.world.listeners.WeatherChangeListener;
import com.goldornetwork.uhc.utils.AntiXray;
import com.goldornetwork.uhc.utils.Medic;

public class UHC extends JavaPlugin {

	
	private File configf, teamf;
	private FileConfiguration config, team;

	private static UHC plugin;
	private GameModeManager gameModeM;
	private TeamManager teamM;
	private TimerManager timerM;
	private TeamInteraction teamI;
	private BoardManager boardM;
	private ChunkGenerator chunkG;
	private ScatterManager scatterM;
	private CommandHandler cmd;
	private MoveListener moveE;
	private VoteManager voteM;
	private WorldManager worldM;
	private ChatManager chatM;
	private WorldFactory worldF;
	private UHCBan uhcB;
	private UHCWarn uhcWarn;
	private UHCServer uhcServer;
	private SpectatorRegion spectM;

	
	public void instances(){

		worldF= new WorldFactory(plugin);

		gameModeM= new GameModeManager(plugin);

		boardM = new BoardManager(plugin);

		chunkG= new ChunkGenerator(plugin);

		teamM= new TeamManager(plugin, boardM);

		chatM = new ChatManager(plugin, teamM);

		moveE= new MoveListener(plugin, teamM);

		worldM = new WorldManager(plugin, teamM, worldF, chunkG);

		scatterM= new ScatterManager(plugin, teamM, moveE, chatM, chunkG, worldM);

		voteM = new VoteManager(plugin, gameModeM);

		timerM = new TimerManager(plugin, scatterM, teamM, voteM, chatM, worldM);

		spectM = new SpectatorRegion(plugin, teamM, worldM);

		uhcServer=new UHCServer(plugin, teamM);

		teamI = new TeamInteraction(teamM);

		uhcWarn= new UHCWarn();

		uhcB = new UHCBan(teamM);
		
		
		cmd = new CommandHandler(plugin);

		cmd.registerCommands(teamM, timerM, gameModeM, chunkG, voteM, teamI, uhcB, uhcWarn, chatM);


		new Medic(plugin, teamM);
		new TeamInteraction(teamM);
		new DeathListener(plugin, teamM);
		new JoinListener(plugin, teamM, worldM);
		new WeatherChangeListener(plugin);
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

		if(!configf.exists()) {
			configf.getParentFile().mkdirs();
			saveResource("config.yml", false);
		} 
		if(!teamf.exists()) {
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
		try{
			team.save(teamf);
		}catch (IOException e){
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
