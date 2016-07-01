package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCDeathEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCEnterMapEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCJoinEvent;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class KingsManager extends Gamemode implements Listener{


	private TeamManager teamM;

	private Map<String, UUID> listOfKings = new HashMap<String, UUID>();
	private Set<UUID> lateKings = new HashSet<UUID>();
	private Set<UUID> lateDebuffs = new HashSet<UUID>();
	private Set<UUID> currentlyDebuffed = new HashSet<UUID>();


	public KingsManager(TeamManager teamM) {
		super("Kings", "Kings", "A random player on a team will receive special powers, and when that player dies, all his teammates shall suffer.");
		this.teamM=teamM;
	}

	@Override
	public void onEnable() {
		listOfKings.clear();
		lateKings.clear();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		distibruteItemsToTeams();
	}

	@EventHandler
	public void on(UHCJoinEvent e){
		Player p =e.getPlayer();

		if(lateDebuffs.contains(p.getUniqueId())){
			MessageSender.alertMessage(p, ChatColor.RED + "Your king has died. You shall now suffer!");
			p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0));
			currentlyDebuffed.add(p.getUniqueId());
			lateDebuffs.remove(p.getUniqueId());
		}
	}

	@EventHandler(priority= EventPriority.HIGHEST)
	public void on(UHCEnterMapEvent e){
		Player p =e.getPlayer();

		if(lateKings.contains(p.getUniqueId())){
			giveAPlayerKingItems(p);
			removePlayerFromLateKings(p);
		}
	}

	@EventHandler(priority =EventPriority.MONITOR)
	public void on(UHCDeathEvent e){
		if(listOfKings.containsValue(e.getOfflinePlayer().getUniqueId())){
			debuffTeamMates(teamM.getTeamOfPlayer(e.getOfflinePlayer().getUniqueId()));
		}
	}

	@EventHandler
	public void on(PlayerItemConsumeEvent e){
		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(e.getPlayer().getUniqueId())){
				if(teamM.isTeamsEnabled()){
					if(currentlyDebuffed.contains(e.getPlayer().getUniqueId())){
						if(e.getItem().getType().equals(Material.MILK_BUCKET)){
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	private void debuffTeamMates(String team){

		for(UUID u : teamM.getPlayersOnATeam(team)){
			if(Bukkit.getServer().getPlayer(u).isOnline()){
				if(teamM.isPlayerOwner(team, u)==false){
					Bukkit.getServer().getPlayer(u).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0));
					currentlyDebuffed.add(u);
					MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.RED + "Your king has died. You shall now suffer!");
				}
			}
			else{
				lateDebuffs.add(u);
			}
		}
	}

	private void giveAPlayerKingItems(Player King){
		King.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD,1));
		King.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		King.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		King.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		King.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		King.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
		King.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		King.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1));
		King.setMaxHealth(30);
		King.setHealth(30);
	}

	private void removePlayerFromLateKings(Player King){
		lateKings.remove(King.getUniqueId());
	}

	private void distibruteItemsToTeams() {

		for(String team : teamM.getActiveTeams()){
			Random random = new Random();
			OfflinePlayer king = Bukkit.getServer().getOfflinePlayer(teamM.getPlayersOnATeam(team).get(random.nextInt(teamM.getPlayersOnATeam(team).size())));
			listOfKings.put(team, king.getUniqueId());

			for(UUID u : teamM.getPlayersOnATeam(team)){
				if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
					MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GOLD + "Your king is " + PlayerUtils.getPrefix(king)
					+ ChatColor.GREEN + king.getName());
				}
			}

			if(king.isOnline()){
				Player King = (Player) king;
				giveAPlayerKingItems(King);
			}
			else if(king.isOnline()==false){
				lateKings.add(king.getUniqueId());
			}
		}
	}	
}
