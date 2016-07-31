package com.goldornetwork.uhc.managers.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFirework;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameEndEvent;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.MeetupEvent;
import com.goldornetwork.uhc.managers.world.customevents.PVPEnableEvent;
import com.goldornetwork.uhc.utils.CoordXZ;
import com.goldornetwork.uhc.utils.LocationUtils;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;
import com.google.common.collect.ImmutableSet;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class WorldManager implements Listener{


	private UHC plugin;
	private TeamManager teamM;
	private WorldFactory worldF;
	private ChunkGenerator chunkG;
	private Random random = new Random();

	private int timer;
	private int radius;
	private World uhcWorld;
	private static final Set<FireworkEffect> effects = ImmutableSet.of(
			
			FireworkEffect.builder()
			.with(FireworkEffect.Type.BURST)
			.withColor(Color.BLUE)
			.build(),
			
			FireworkEffect.builder()
			.with(FireworkEffect.Type.BURST)
			.withColor(Color.RED)
			.build(),
			
			FireworkEffect.builder()
			.with(FireworkEffect.Type.BALL_LARGE)
			.withColor(Color.BLUE)
			.build(),
			
			FireworkEffect.builder()
			.with(FireworkEffect.Type.BALL_LARGE)
			.withColor(Color.RED)
			.build()
			
			
			);

	public WorldManager(UHC plugin, TeamManager teamM, WorldFactory worldF, ChunkGenerator chunkG) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		this.teamM=teamM;
		this.worldF=worldF;
		this.chunkG=chunkG;
	}

	public void setup(){
		newUHCWorld();
		plugin.getServer().setSpawnRadius(0);
		getUHCWorld().setPVP(false);
		getUHCWorld().setGameRuleValue("doMobSpawning", "false");
		getUHCWorld().setGameRuleValue("naturalRegeneration", "false");
		getUHCWorld().setDifficulty(Difficulty.HARD);
		radius = plugin.getConfig().getInt("radius");
		WorldBorder wb = getUHCWorld().getWorldBorder();
		wb.setCenter(getUHCWorld().getSpawnLocation());
		wb.setSize(radius*2);
		wb.setDamageBuffer(0);
		wb.setDamageAmount(.5);
		wb.setWarningTime(15);
		wb.setWarningDistance(20);

		for(Entity e : getUHCWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		chunkG.generate(getUHCWorld(), getCenter(), radius);
		plugin.getConfig().addDefault("ENDGAME-GRACE-PERIOD", 2);
		this.timer=((plugin.getConfig().getInt("ENDGAME-GRACE-PERIOD")) *60);

		for(Player all : Bukkit.getOnlinePlayers()){
			all.setGameMode(GameMode.ADVENTURE);
			all.setMaxHealth(20);
			all.setLevel(0);
			all.setExp(0L);
			Medic.heal(all);
			for(PotionEffect effect : all.getActivePotionEffects()){
				all.removePotionEffect(effect.getType());
			}
			all.getInventory().clear();
			all.getInventory().setArmorContents(null);
			all.teleport(worldF.getLobby().getSpawnLocation());
		}

		plugin.getServer().setIdleTimeout(60);
		MinecraftServer.getServer().getPlayerList().getWhitelist().getValues().clear();
		plugin.getServer().setWhitelist(true);
	}
	@EventHandler
	public void on(GameStartEvent e){
		for(UUID u : teamM.getPlayersInGame()){
			OfflinePlayer target = Bukkit.getOfflinePlayer(u);
			target.setWhitelisted(true);
		}
		getUHCWorld().setGameRuleValue("doMobSpawning", "true");
		getUHCWorld().setGameRuleValue("dodaylightcycle", "true");
		getUHCWorld().setTime(60);
		getUHCWorld().setThundering(false);
		getUHCWorld().setStorm(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.getServer().setIdleTimeout(5);
			}
		}.runTaskLater(plugin, 1200L);
	}

	@EventHandler
	public void on(MeetupEvent e){
		shrinkBorder();
	}

	@EventHandler
	public void on(PVPEnableEvent e){
		getUHCWorld().setPVP(true);
	}

	@EventHandler
	public void on(PlayerJoinEvent e){
		Player target = e.getPlayer();
		e.setJoinMessage(ChatColor.GREEN + "\u2713" + PlayerUtils.getPrefix(target) + teamM.getColorOfPlayer(target.getUniqueId()) +  target.getName());
		target.setHealth(target.getHealth());
		for(Player online : Bukkit.getOnlinePlayers()){
			online.hidePlayer(target);
			online.showPlayer(target);
		}
	}

	@EventHandler
	public void on(PlayerQuitEvent e){
		Player target = e.getPlayer();
		e.setQuitMessage(ChatColor.RED + "\u2717" + PlayerUtils.getPrefix(target) + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName());

		if(teamM.isPlayerAnObserver(target.getUniqueId())){
			teamM.removePlayerFromObservers(target);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(FoodLevelChangeEvent e){
		if(!(State.getState().equals(State.INGAME))){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void on(PlayerDropItemEvent e){
		if(State.getState().equals(State.OPEN) || State.getState().equals(State.NOT_RUNNING)){
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(EntityDamageEvent e){
		if(!(State.getState().equals(State.INGAME))){
			if(e.getEntity() instanceof Player){
				e.setCancelled(true);	
			}
		}
	}

	@EventHandler
	public void on(GameEndEvent e){
		endGame(e.getWinners());
	}

	public void endGame(List<UUID> winners){
		List<String> toBroadcast = new LinkedList<String>();

		toBroadcast.add("Game has ended, thanks for playing.");
		MessageSender.broadcast(toBroadcast);
		fireworks(uhcWorld);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(winners!=null){
					List<String> toReturn = new ArrayList<String>();
					int comma = 0;
					StringBuilder str = new StringBuilder();

					for(UUID u : winners){
						comma++;
						String message = teamM.getColorOfPlayer(u) + Bukkit.getOfflinePlayer(u).getName();
						String properMessage;
						if(comma<winners.size()){
							properMessage = message + ChatColor.GRAY + " + ";
						}
						else{
							properMessage=message;
						}
						str.append(properMessage);

						if(Bukkit.getOfflinePlayer(u).isOnline()){
							Player target = Bukkit.getServer().getPlayer(u);
							target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
						}
					}
					String msg = ChatColor.GOLD + "Winners: " + str.toString();
					toReturn.add(msg);
					MessageSender.broadcast(toReturn);
					MessageSender.broadcastTitle(ChatColor.GOLD + "Game Over!", msg);
				}
				else{
					MessageSender.broadcastBigTitle(ChatColor.GOLD + "Game Over!");
				}
			}
		}.runTaskLater(plugin, 10L);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(timer>60 && timer%60==0){
					MessageSender.broadcast(ChatColor.DARK_AQUA + "Server closing in " + ChatColor.DARK_RED + (timer/60) + ChatColor.DARK_AQUA + " minutes.");
				}
				else if(timer<=60 && timer>=15 && timer%15==0){
					MessageSender.broadcast(ChatColor.DARK_AQUA + "Server closing in " + ChatColor.DARK_RED + timer + ChatColor.DARK_AQUA + " seconds.");
				}
				else if(timer <=5 && timer>1){
					MessageSender.broadcast(ChatColor.DARK_AQUA + "Server closing in " + ChatColor.DARK_RED + timer + ChatColor.DARK_AQUA + " seconds.");
				}
				else if(timer==1){
					MessageSender.broadcast(ChatColor.DARK_AQUA + "Server closing in " + ChatColor.DARK_RED + timer + ChatColor.DARK_AQUA + " second.");
				}
				else if(timer==0){
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
				}

				--timer;
			}
		}.runTaskTimer(plugin, 60L, 20L);
	}

	private void fireworks(World world){

		new BukkitRunnable() {
			private int timer = 0;
			
			@Override
			public void run() {
				
				if(timer==16){
					cancel();
				}
				for(Player online : Bukkit.getServer().getOnlinePlayers()){
					Location pLoc = online.getLocation();
					CoordXZ coord = LocationUtils.locationInRadius(4);
					int variationX = coord.x;
					int variationZ = coord.z;
					Location fireLoc = new Location(world, pLoc.getBlockX() + variationX, pLoc.getBlockY(), pLoc.getBlockZ() + variationZ);

					FireworkEffect effect = getRandomFirework();
					playFirework(fireLoc, effect);
				}
				timer++;
			}
		}.runTaskTimer(plugin, 0L, 5L);
	}

	private FireworkEffect getRandomFirework(){
		List<FireworkEffect> toReturn = new ArrayList<FireworkEffect>();
		toReturn.addAll(effects);
		int index = random.nextInt(toReturn.size());
		return toReturn.get(index);
	}
	
	private void playFirework(Location location, FireworkEffect effect){
		final Firework firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
		NBTTagCompound nbtData = new NBTTagCompound();
		nbtData.setInt("Life", 1);
		nbtData.setInt("LifeTime", 2);
		((CraftFirework) firework).getHandle().a(nbtData);
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent e){
		if(e.getFrom().getPlayers().isEmpty()){
			Bukkit.unloadWorld(e.getFrom(), false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerMoveEvent e){
		if(e.getPlayer().getWorld().equals(getLobby())){
			Location pLoc = e.getTo();
			if(pLoc.getBlockY()<=0){
				Location lobby = getLobby().getSpawnLocation();
				CoordXZ variation = LocationUtils.locationInRadius(2);
				Location toTeleport = lobby.clone().add(variation.x, 0, variation.z);
				e.getPlayer().teleport(toTeleport);
			}
		}
	}

	public World getLobby(){
		return worldF.getLobby();
	}

	public void shrinkBorder(){

		new BukkitRunnable() {
			@Override
			public void run() {
				getUHCWorld().getWorldBorder().setSize(500, 15*60);
				MessageSender.broadcast("The worldborder will now slowly shrink to a radius of 250.");

				new BukkitRunnable() {
					@Override
					public void run() {
						getUHCWorld().getWorldBorder().setSize(300, 10*60);
						MessageSender.broadcast("The worldborder will now slowly shrink to a radius of 150.");

						new BukkitRunnable() {
							@Override
							public void run() {
								getUHCWorld().getWorldBorder().setSize(100, 5*60);
								MessageSender.broadcast("The worldborder will now slowly shrink to a radius of 50.");
							}
						}.runTaskLater(plugin, 36000L);
					}
				}.runTaskLater(plugin, 36000L);
			}
		}.runTaskLater(plugin, 100L);
	}

	public void newUHCWorld(){
		uhcWorld = worldF.create();
	}

	public World getUHCWorld(){
		return this.uhcWorld;
	}

	public Location getCenter(){
		return uhcWorld.getSpawnLocation();
	}

	@EventHandler
	public void on(BlockFromToEvent e){

		if(e.getBlock().getLocation().getBlockY()>100){
			if(e.getBlock().getType().equals(Material.STATIONARY_WATER)|| e.getBlock().getType().equals(Material.STATIONARY_LAVA)){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent e){
		if(State.getState().equals(State.INGAME)==false){
			if(e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityDamageByEntityEvent e){
		if(State.getState().equals(State.INGAME)){
			if(e.getEntity() instanceof Player){
				if(e.getDamager() instanceof Arrow){
					Arrow arrow = (Arrow) e.getDamager();

					if(arrow.getShooter() instanceof Player){
						Player target = (Player) e.getEntity();
						Player shooter = (Player) arrow.getShooter();

						if(teamM.isPlayerInGame(target.getUniqueId()) && teamM.isPlayerInGame(shooter.getUniqueId())){
							send(shooter, target);
						}
					}
				}
			}
		}
	}

	private void send(Player shooter, Player target){
		Location shooterLocation = shooter.getLocation();
		Location targetLocation = target.getLocation();
		int distance = (int) shooterLocation.distance(targetLocation);

		MessageSender.send(shooter, ChatColor.GREEN + "You hit " + teamM.getColorOfPlayer(target.getUniqueId())
		+ target.getName() + ChatColor.GREEN +  " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks.");

		MessageSender.send(target, ChatColor.RED + "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks.");
	}
}
