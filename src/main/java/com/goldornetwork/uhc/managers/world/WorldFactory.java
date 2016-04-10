package com.goldornetwork.uhc.managers.world;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.FileUtil;

import com.goldornetwork.uhc.UHC;

public class WorldFactory {

	private UHC plugin;
	private File dir;
	
	public WorldFactory(UHC plugin) {
		this.plugin=plugin;
	}
	
	public void setup(){
		dir = new File(plugin.getDataFolder(), "matches");
		if(!dir.exists()){
			dir.getParentFile().mkdirs();
		}
	}
	private File getContainer(){
		return dir;
	}
	public void create(){
		String fileName = UUID.randomUUID().toString();
		File map = new File(getContainer(), fileName);
		WorldCreator creator = new WorldCreator(fileName);
		creator.generator(new ChunkGenerator() {
			public byte[] generate(World world, Random random, int x, int z, BiomeGrid biomeGrid){
				return new byte[65536];
			}
		});
		World world = creator.createWorld();
		for(File files: world.getWorldFolder().listFiles()){
			FileUtil.copy(files, map);
		}
		
	}
}
