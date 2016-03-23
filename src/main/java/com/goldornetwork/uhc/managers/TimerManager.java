package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.ModifierManager.actions.KingsManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager implements Runnable {
	
	private static TimerManager instance = new TimerManager();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private KingsManager kingM = KingsManager.getInstance();
	
	
	private int timeTillMatchStart;
	
	private int timeTillPVPStart;
	private boolean startMatch;
	
	private boolean hasMatchBegun;
	
	private boolean matchStart;
	
	private boolean startPVPTimer;
	
	private boolean isPVPEnabled;
	
	//
	private boolean enableTheHobbit;
	private boolean enableSkyHigh;
	private boolean enableGoneFishing;
	private boolean enableKings;
	
	private List<UUID> lateHobbits = new ArrayList<UUID>();
	private List<UUID> lateSkyHigh = new ArrayList<UUID>();
	private List <UUID> lateGoneFishing = new ArrayList<UUID>();
	
	
	//
	public static TimerManager getInstance(){
		return instance;
	}
	public void setup(){
		timeTillMatchStart=-2;
		timeTillPVPStart=-2;
		startMatch=false;
		hasMatchBegun=false;
		matchStart=false;
		startPVPTimer=false;
		isPVPEnabled=false;
		enableTheHobbit=false;
		enableSkyHigh=false;
		enableKings=false;
	}
	
	public void startMatch(boolean start, int timeTillMatchStarts, int timeTillPVPStarts){
		timeTillMatchStart = timeTillMatchStarts;
		this.timeTillPVPStart= timeTillPVPStarts;
		isPVPEnabled= false;
		matchStart = true;
		startMatch =true;
	}
	
	public void cancelMatch(){
		timeTillMatchStart=-1;
		
	}
	
	public boolean hasMatchStarted(){
		return matchStart;
	}
	
	public boolean hasCountDownEnded(){
		return hasMatchBegun;
	}
	
	public boolean isPVPEnabled(){
		return isPVPEnabled;
	}
	
	public void enableKings(boolean val){
		this.enableKings=val;
	}
	
	public void enableTheHobbit(boolean val){
		this.enableTheHobbit = val;
	}
	public void enableSkyHigh(boolean val){
		this.enableSkyHigh=val;
	}
	public void enableGoneFishing(boolean val){
		this.enableGoneFishing=val;
	}
	public List<UUID> getLateHobbits(){
		return lateHobbits;
	}
	public void removePlayerFromLateHobbits(Player p){
		lateHobbits.remove(p.getUniqueId());
	}
	public void lateGiveAPlayerHobbitItems(Player p){
		ItemStack given = new ItemStack(Material.GOLD_NUGGET,1);
		ItemMeta im = given.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "The Magic Ring of Invisibility");
		given.setItemMeta(im);
		p.getInventory().addItem(given);
	}
	
	public List<UUID> getLateSkyHigh(){
		return lateSkyHigh;
	}
	
	public void removePlayerFromLateSkyHigh(Player p){
		lateSkyHigh.remove(p.getUniqueId());
	}
	public void lateGiveAPlayerSkyHighItems(Player p){
		p.getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE, 1));
		p.getInventory().addItem(new ItemStack(Material.PUMPKIN, 10));
		p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 13)); //green
		p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 11));	//blue
		p.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 64));
		p.getInventory().addItem(new ItemStack(Material.STRING, 2));
	}
	
	public List<UUID> getLateGoneFishing(){
		return lateGoneFishing;
	}
	public void removeAPlayerFromLateGoneFishing(Player p){
		lateGoneFishing.remove(p);
	}
	
	public void lateGiveAPlayerGoneFishingItems(Player p){
		ItemStack given = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta im = given.getItemMeta();
		im.addEnchant(Enchantment.LUCK, 250, true);
		im.addEnchant(Enchantment.LURE, 250, true);
		im.addEnchant(Enchantment.DURABILITY, 150, true);
		given.setItemMeta(im);
		p.getInventory().addItem(new ItemStack(Material.ANVIL, 20));
		p.getInventory().addItem(given);
		p.setExp(Integer.MAX_VALUE);
	}
	
	@Override
	public void run() {
		if(startMatch){
			if(timeTillMatchStart >0){
				if(timeTillMatchStart>60 && timeTillMatchStart%60 ==0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart/60 + ChatColor.RED + " minutes");
				}
					
				else if(timeTillMatchStart <= 60 && timeTillMatchStart >=30 &&timeTillMatchStart%10==0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");
				}
				else if(timeTillMatchStart <=30 && timeTillMatchStart >=5 && timeTillMatchStart %5==0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");
				}
				else if(timeTillMatchStart <= 5 && timeTillMatchStart >0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");		
				}
				timeTillMatchStart--;
				
			}
			else if(timeTillMatchStart == 0){
				ms.broadcast("Match has started!");
				
				if(enableTheHobbit){
					ItemStack given = new ItemStack(Material.GOLD_NUGGET,1);
					ItemMeta im = given.getItemMeta();
					im.setDisplayName(ChatColor.AQUA + "The Magic Ring of Invisibility");
					given.setItemMeta(im);
					for(UUID u : teamM.getPlayersInGame()){
						if(Bukkit.getServer().getPlayer(u).isOnline()){
							Player p = Bukkit.getServer().getPlayer(u);
							p.getInventory().addItem(given);
						}
						else{
							lateHobbits.add(u);
						}
					}
				}
				if(enableSkyHigh){
					for(UUID u : teamM.getPlayersInGame()){
						if(Bukkit.getServer().getPlayer(u).isOnline()){
							Player p = Bukkit.getServer().getPlayer(u);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE, 1));
							p.getInventory().addItem(new ItemStack(Material.PUMPKIN, 10));
							p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 13)); //green
							p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 11));	//blue
							p.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 64));
							p.getInventory().addItem(new ItemStack(Material.STRING, 2));
						}
						else{
							lateSkyHigh.add(u);
						}
					}
				}
				if(enableGoneFishing){
					ItemStack given = new ItemStack(Material.FISHING_ROD, 1);
					ItemMeta im = given.getItemMeta();
					im.addEnchant(Enchantment.LUCK, 250, true);
					im.addEnchant(Enchantment.LURE, 250, true);
					im.addEnchant(Enchantment.DURABILITY, 150, true);
					given.setItemMeta(im);
					for(UUID u : teamM.getPlayersInGame()){//INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
						if(Bukkit.getServer().getPlayer(u).isOnline()){
							Player p = Bukkit.getServer().getPlayer(u);
							p.getInventory().addItem(new ItemStack(Material.ANVIL, 20));
							p.getInventory().addItem(given);
							p.setExp(Integer.MAX_VALUE);
							
						}
						else{
							lateGoneFishing.add(u);
						}
					}
				}
				if(enableKings){
					kingM.distibruteItemsToTeams();
				}
				
				hasMatchBegun = true;
				startPVPTimer= true;
				startMatch =false;
			}
		
		}
		
		else if(timeTillMatchStart == -1){
			ms.broadcast("Match canceled");
			timeTillMatchStart =-2; //-2 will act as a null value
		}
		
		else if(startPVPTimer = true){
			if(timeTillPVPStart>0){
				if(timeTillPVPStart >= (5*60) && timeTillPVPStart % (10*60) == 0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.RED + " minutes");
				}
				else if(timeTillPVPStart<= (4*60) && timeTillPVPStart >= (1*60) && timeTillPVPStart % (1*60) ==0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.RED + " minutes");
				}
				else if(timeTillPVPStart < 60 && timeTillPVPStart >=30 &&timeTillPVPStart%10==0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");
				}
				else if(timeTillPVPStart <=30 && timeTillPVPStart >=5 && timeTillPVPStart %5==0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");
				}
				else if(timeTillPVPStart <= 5 && timeTillPVPStart >0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");		
				}
				timeTillPVPStart--;
			}
			else if(timeTillPVPStart==0){
				ms.broadcast("PVP has been enabled!");
				isPVPEnabled = true;
				startPVPTimer= false;
			}
			
		}
		
	}

}
