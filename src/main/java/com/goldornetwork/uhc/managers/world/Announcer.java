package com.goldornetwork.uhc.managers.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.utils.MessageSender;

import net.md_5.bungee.api.ChatColor;

public class Announcer {


	private UHC plugin;
	private Random random = new Random();
	
	private File file;
	private FileConfiguration annFile;
	
	private String lastMessage;
	
	
	public Announcer(UHC plugin) {
		this.plugin=plugin;	
	}
	
	public void setup(){
		config();
		runTask();
	}
	
	private void config(){
		file = new File(plugin.getDataFolder(), "announcer.yml");
		if(!(file.exists())){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			plugin.saveResource(file.getPath(), false);
		}
		annFile= new YamlConfiguration();
		try {
			annFile.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		annFile.options().copyDefaults(true);
		
		List<String> toAdd = new ArrayList<String>();
		toAdd.add("Remember to use the /help menu!");
		toAdd.add("Remeber to use /helpop if you need help!");
		annFile.addDefault("text", toAdd);
		save();
	}
	
	private void save(){
		try {
			annFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getMsg(){
		String text = randomMsg();
		while(validate(text)==false){
			text = randomMsg();
		}
		return text;
	}
	
	private String randomMsg(){
		return annFile.getStringList("text").get(random.nextInt(annFile.getStringList("text").size()));
	}
	
	private boolean validate(String text){
		boolean valid = true;
		
		if(lastMessage!=null){
			if(lastMessage==text){
				valid = false;
			}
			else{
				lastMessage=text;
			}
		}
		else{
			lastMessage=text;
		}
		
		return valid;
	}
	
	private void runTask(){
		
		new BukkitRunnable() {
			@Override
			public void run() {
				String prefix = ChatColor.GRAY + "[" + ChatColor.AQUA + ChatColor.ITALIC + "Tip" + ChatColor.GRAY + "] ";
				String msg = ChatColor.DARK_AQUA + getMsg();
				MessageSender.broadcastNoPrefix(prefix + msg);
			}
		}.runTaskTimer(plugin, 6000L, 6000L);
	}

}
