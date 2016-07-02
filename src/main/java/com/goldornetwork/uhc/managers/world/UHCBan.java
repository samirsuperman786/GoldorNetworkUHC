package com.goldornetwork.uhc.managers.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class UHCBan {

	
	private UHC plugin;
	private TeamManager teamM;
	
	private File file;
	private FileConfiguration ipConfig;

	public UHCBan(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		this.teamM=teamM;
	}
	
	public void setup(){
		config();
	}
	
	private void config(){
		file = new File(plugin.getDataFolder(), "ips.yml");
		if(!(file.exists())){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			plugin.saveResource(file.getPath(), false);
		}
		ipConfig= new YamlConfiguration();
		try {
			ipConfig.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		ipConfig.options().copyDefaults(true);
		save();
	}
	
	private void save(){
		try {
			ipConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void banPlayer(Player banner, Player target, String reason){
		MessageSender.broadcast(PlayerUtils.getPrefix(banner) + teamM.getColorOfPlayer(banner.getUniqueId()) + banner.getName()
		+ ChatColor.GOLD + " \u27A0 Banned \u27A0 " + PlayerUtils.getPrefix(target) + teamM.getColorOfPlayer(target.getUniqueId())
		+ target.getName() + ChatColor.GOLD + " \u27A0 " + reason);

		Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), reason, null, banner.getName());
		target.kickPlayer(ChatColor.GOLD + reason);
	}
	
	public void banPlayer(String target, String reason){
		MessageSender.broadcast(ChatColor.GOLD + "Console \u27A0 Banned \u27A0 " + target + ChatColor.GOLD + " \u27A0 " + reason);
		plugin.getServer().getBanList(Type.NAME).addBan(target, reason, null, "Console");
	}
	
	public void banIPAndAliases(String ip, String reason){
		plugin.getServer().getBanList(Type.IP).addBan(ip, reason, null, "Console");
		
		if(getAliases(ip)!=null){
			for(UUID u : getAliases(ip)){
				banPlayer(plugin.getServer().getOfflinePlayer(u).getName(), reason);
				if(PlayerUtils.getOfflinePlayer(u).isOnline()){
					Player target = PlayerUtils.getPlayer(u);
					target.kickPlayer(ChatColor.GOLD + reason);
				}
			}
		}
	}
	
	public void logIP(String ip, UUID target){
		if(ipConfig.contains("IPS." + ip.replace(".", "/"))){
			Set<String> aliases = new HashSet<String>();
			aliases.addAll(ipConfig.getStringList("IPS." + ip.replace(".", "/")));
			aliases.add(target.toString());
			List<String> toAdd = new ArrayList<String>();
			toAdd.addAll(aliases);
			ipConfig.set("IPS." + ip.replace(".", "/"), toAdd);
		}
		else{
			List<String> toAdd = new ArrayList<String>();
			toAdd.add(target.toString());
			ipConfig.addDefault("IPS." + ip.replace(".", "/"), toAdd);
		}
		save();
	}
	
	public Set<UUID> getAliases(String ip){
		Set<UUID> toReturn = new HashSet<UUID>();
		if(ipConfig.contains("IPS." + ip.replace(".", "/"))){
			for(String toLoop : ipConfig.getStringList("IPS." + ip.replace(".", "/"))){
				toReturn.add(UUID.fromString(toLoop));
			}
		}
		return toReturn;
	}
}
