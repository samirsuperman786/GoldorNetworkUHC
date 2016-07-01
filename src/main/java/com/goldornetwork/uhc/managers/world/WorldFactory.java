package com.goldornetwork.uhc.managers.world;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import com.goldornetwork.uhc.UHC;

public class WorldFactory implements Listener{

	
	private UHC plugin;
	private File dir;
	private World Lobby;
	private Random random = new Random();

	
	public WorldFactory(UHC plugin) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setup(){
		File root = new File("./");
		dir = new File(root, "/matches/");

		if(!dir.exists()){
			dir.getParentFile().mkdirs();
			Bukkit.getLogger().info("Creating matches folder");
		}

		for(World world : Bukkit.getWorlds()){
			for(Chunk chunk : world.getLoadedChunks()){
				chunk.unload();
			}
			for(Entity entity: world.getEntities()){
				entity.remove();
			}
			Bukkit.unloadWorld(world, false);
		}

		for(File cleanup : dir.listFiles()){
			try {
				FileUtils.deleteDirectory(cleanup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File lobby = new File(dir, "/Lobby/");
		if(!lobby.exists()){
			lobby.getParentFile().mkdirs();
		}

		for(File rootFiles : root.listFiles()){
			if(rootFiles.getName().equalsIgnoreCase("Lobby")){
				try {
					FileUtils.copyDirectory(rootFiles, lobby);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public World getLobby(){
		if(!(plugin.getServer().getWorlds().contains(Lobby))){
			Lobby = plugin.getServer().createWorld(new WorldCreator("Lobby"));
			Lobby.setSpawnLocation(0, 32, 0);
			Lobby.setGameRuleValue("doMobSpawning", "false");
			Lobby.setGameRuleValue("doDaylightCycle", "false");
			Lobby.setTime(60);
			Lobby.setThundering(false);
			Lobby.setStorm(false);
			Lobby.setSpawnFlags(false, false);
			
			for(Entity entity : Lobby.getEntities()){
				if(!(entity instanceof Player)){
					entity.remove();
				}
			}
		}

		return Lobby;
	}

	public World create(){
		String fileName = UUID.randomUUID().toString();
		WorldCreator creator = new WorldCreator(fileName);
		creator.environment(World.Environment.NORMAL);
		creator.type(WorldType.NORMAL);
		creator.seed(random.nextLong());
		creator.generateStructures(true);
		World world = Bukkit.getServer().createWorld(creator);
		world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);
		world.setAutoSave(false);
		return world;
	}

	@EventHandler
	public void on(WorldInitEvent e){
		e.getWorld().setAutoSave(false);
		e.getWorld().setKeepSpawnInMemory(false);	
	}
}
