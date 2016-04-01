package com.goldornetwork.uhc;

import org.bukkit.plugin.java.JavaPlugin;

import com.goldornetwork.uhc.commands.CommandHandler;
import com.goldornetwork.uhc.listeners.ChatEvent;
import com.goldornetwork.uhc.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.SpectatorRegionManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;

public class UHC extends JavaPlugin {

	/*
	 * TODO add a is this enabled method for all modifiers instead of having inefficient booleans 
	 * TODO create instances for managers and pass them through constructors
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
		scatterM= new ScatterManager(teamM, moveE);
		
		timerM = new TimerManager(scatterM, teamM);
		
		spectatorM= new SpectatorRegionManager(teamM, timerM, scatterM);
		
		gameModeM= new GameModeManager(plugin);
		
		boardM = new BoardManager(teamM);
		
		chunkG= new ChunkGenerator();
		cmd = new CommandHandler(plugin, teamM);
		
	}


	@Override
	public void onEnable(){
		plugin = this;
		instances();
		registerListeners();
		registerCommands();
		setup();
		registerTimers();
		gameModeM.setupGamemodes(timerM, teamM);
	}
	@Override
	public void onDisable(){

	}

	private void registerTimers() {
		/*Bukkit.getServer().getScheduler().runTaskTimer(this, ScatterManager.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, TimerManager.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, SpectatorRegionManager.getInstance(), 0L, 40L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, LocationListener.getInstance(), 0L, 20L);
		Bukkit.getServer().getScheduler().runTaskTimer(this, ChunkGenerator.getInstance(), 0L, 20L);
		 */
	}

	private void setup() {
		/*TeamManager.getInstance().setup();
		ScatterManager.getInstance().setup();
		ModifierManager.getInstance().setup(plugin);
		//DeathEvent.getInstance().setup();
		//BowListener.getInstance().setup();
		TimerManager.getInstance().setup();
		JoinEvent.getInstance().setup();
		BreakEvent.getInstance().setup();
		LocationListener.getInstance().setup();
		MoveEvent.getInstace().setup();
		 */
	}

	private void registerCommands() {
		/*getCommand("start").setExecutor(new StartCommand());
		getCommand("cancel").setExecutor(new CancelCommand());
		getCommand("create").setExecutor(new CreateCommand());
		getCommand("join").setExecutor(new JoinCommand());
		getCommand("invite").setExecutor(new InvitePlayerCommand());
		getCommand("uninvite").setExecutor(new UnInvitePlayerCommand());
		getCommand("render").setExecutor(new ChunkGenerateCommand());
		 */
	}

	private void registerListeners() {
		new ChatEvent(this);
		/*Bukkit.getServer().getPluginManager().registerEvents(JoinEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(BreakEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(BowListener.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(DeathEvent.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(TheHobbitManager.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(DisabledCrafting.getInstance(), this);
		Bukkit.getServer().getPluginManager().registerEvents(LeaveEvent.getInstace(), this);
		Bukkit.getServer().getPluginManager().registerEvents(MoveEvent.getInstace(), this);
		 */
	}









}
