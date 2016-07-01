package com.goldornetwork.uhc.managers.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.world.customevents.GameOpenEvent;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class UHCWhitelist implements Listener{

	
	private UHC plugin;
	private TeamManager teamM;
	
	
	private List<String> cooldownForWhitelist = new ArrayList<String>();
	private Map<String, String> requests = new HashMap<String, String>();
	
	public UHCWhitelist(UHC plugin, TeamManager teamM) {
		this.teamM=teamM;
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void requestWhitelist(Player sender, String team, String target){
			MessageSender.send(sender, ChatColor.GREEN + "Requested " + target + " to be whitelisted.");
			TextComponent message = new TextComponent(ChatColor.GOLD + "[REQUEST] " + teamM.getColorOfTeam(team) + teamM.getTeamNameProper(team) + ChatColor.GRAY + " requests to whitelist " + target);
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist add " + target));
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + "Click to whitelist.").create()));
			MessageSender.sendToOPS(message);
			
			cooldownForWhitelist.add(team);
			requests.put(target, team);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					cooldownForWhitelist.remove(team);
				}
			}.runTaskLater(plugin, 300L);
	}
	
	public boolean isOnCooldown(String team){
		return cooldownForWhitelist.contains(team);
	}
	
	public void addWhitelist(String target){
		OfflinePlayer p = PlayerUtils.getOfflinePlayer(target);
		p.setWhitelisted(true);
		
		if(requests.containsKey(target)){
			String team = requests.remove(target);
			
			for(UUID u : teamM.getPlayersOnATeam(team)){
				if(plugin.getServer().getOfflinePlayer(u).isOnline()){
					Player teammate = plugin.getServer().getPlayer(u);
					MessageSender.send(teammate, ChatColor.GREEN + "Your team has whitelisted " + target);
				}
			}
		}
	}
	
	public boolean isWhitelisted(String target){
		OfflinePlayer p = PlayerUtils.getOfflinePlayer(target);
		return p.isWhitelisted();
	}
	
	public void setWhitelist(boolean val){
		plugin.getServer().setWhitelist(val);
		String toBroadcast = ChatColor.GOLD + "Whitelist turned ";
		String suffix = val ? "on." : "off.";
		MessageSender.broadcast(toBroadcast + suffix);
	}
	
	@EventHandler
	public void on(GameOpenEvent e){
		setWhitelist(true);
	}
}
