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
import com.goldornetwork.uhc.commands.console.Console;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TabManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.chat.ChatManager;
import com.goldornetwork.uhc.managers.chat.TeamInteraction;
import com.goldornetwork.uhc.managers.world.Announcer;
import com.goldornetwork.uhc.managers.world.ChunkGenerator;
import com.goldornetwork.uhc.managers.world.Disguiser;
import com.goldornetwork.uhc.managers.world.InventoryView;
import com.goldornetwork.uhc.managers.world.SpectatorRegion;
import com.goldornetwork.uhc.managers.world.UHCBan;
import com.goldornetwork.uhc.managers.world.UHCServer;
import com.goldornetwork.uhc.managers.world.UHCWarn;
import com.goldornetwork.uhc.managers.world.UHCWhitelist;
import com.goldornetwork.uhc.managers.world.WorldFactory;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.listeners.CraftingListener;
import com.goldornetwork.uhc.managers.world.listeners.DeathListener;
import com.goldornetwork.uhc.managers.world.listeners.JoinListener;
import com.goldornetwork.uhc.managers.world.listeners.MoveListener;
import com.goldornetwork.uhc.managers.world.listeners.WeatherChangeListener;
import com.goldornetwork.uhc.managers.world.ubl.UBL;
import com.goldornetwork.uhc.utils.AntiXray;
import com.goldornetwork.uhc.utils.Medic;

public class UHC extends JavaPlugin {

	
	private File configf;
	private File teamf;
	private FileConfiguration config;
	private FileConfiguration team;

	private static UHC plugin;
	private GameModeManager gameModeM;
	private TeamManager teamM;
	private TimerManager timerM;
	private TeamInteraction teamI;
	private BoardManager boardM;
	private ChunkGenerator chunkG;
	private ScatterManager scatterM;
	private CommandHandler cmd;
	private MoveListener moveL;
	private VoteManager voteM;
	private WorldManager worldM;
	private ChatManager chatM;
	private WorldFactory worldF;
	private UHCBan uhcB;
	private UHCWarn uhcWarn;
	private UHCServer uhcServer;
	private UHCWhitelist uhcWhitelist;
	private SpectatorRegion spectM;
	private Announcer annnouncer;
	private UBL ubl;
	
	public void instances(){		
		
		worldF = new WorldFactory(plugin);
		
		ubl = new UBL(plugin);
		
		gameModeM = new GameModeManager(plugin);

		boardM = new BoardManager(plugin);

		chunkG = new ChunkGenerator(plugin);

		teamM = new TeamManager(plugin, boardM);

		chatM = new ChatManager(plugin, teamM);

		moveL = new MoveListener(plugin, teamM);

		worldM = new WorldManager(plugin, teamM, worldF, chunkG);

		scatterM = new ScatterManager(plugin, teamM, moveL, chatM, chunkG, worldM);

		voteM = new VoteManager(plugin, gameModeM);

		uhcWhitelist = new UHCWhitelist(plugin, teamM);
		
		timerM = new TimerManager(plugin, scatterM, teamM, voteM, chatM, worldM);

		spectM = new SpectatorRegion(plugin, teamM, worldM);
		
		uhcB = new UHCBan(plugin, teamM);

		uhcServer = new UHCServer(plugin, teamM, ubl, uhcB);

		teamI = new TeamInteraction(teamM);

		uhcWarn = new UHCWarn();		
		
		annnouncer= new Announcer(plugin);
		
		cmd = new CommandHandler(plugin);

		cmd.registerCommands(teamM, timerM, gameModeM, chunkG, voteM, teamI, uhcB, uhcWarn, chatM, uhcWhitelist, moveL);


		new Medic(plugin, teamM);
		new TeamInteraction(teamM);
		new DeathListener(plugin, teamM);
		new JoinListener(plugin, teamM, worldM);
		new WeatherChangeListener(plugin);
		new AntiXray(plugin);
		new Disguiser(plugin, teamM);
		new TabManager(plugin, gameModeM);
		new CraftingListener(plugin);
		new InventoryView(plugin, teamM);
		
		new Console(plugin, uhcB);
	}

	private void setup(){
		ubl.reload();
		teamM.setup();
		timerM.setup();
		moveL.setup();
		voteM.setup();
		worldM.setup();
		gameModeM.setupGamemodes(teamM, worldM, boardM);
		scatterM.setup();
		spectM.setup();
		boardM.setup(teamM, worldM, timerM);
		uhcServer.setup();
		uhcB.setup();
		annnouncer.setup();
	}

	private void createConfig() {
		teamf = new File(getDataFolder(), "teams.yml");
		configf= new File(getDataFolder(), "config.yml");

		if(!configf.exists()) {
			try {
				configf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			saveResource("config.yml", false);
		} 
		if(!teamf.exists()) {
			try {
				teamf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
