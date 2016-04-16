package com.goldornetwork.uhc.managers;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.utils.MessageSender;

public class ChunkGenerator {


	//instances
	private UHC plugin;

	public ChunkGenerator(UHC plugin) {
		this.plugin=plugin;
	}

	//storage
	private int radius;
	private World world;

	//lengths to manipulate by
	int di =-16;
	int dj = 0;
	// length of current segment
	int segment_length;

	// current position (i, j) and how much of current segment we passed
	int i;
	int j;
	int segment_passed;
	private int k;


	//storage
	private boolean pause;
	private boolean generating;
	private boolean cancel;
	private final int CHUNKS_PER_RUN =5;
	private int duration;	
	private Chunk[] loaded;

	public void generate(World world, Location center, int radius){
		this.world = world;
		this.loaded=world.getLoadedChunks();
		this.radius = 16*(Math.round(radius/16));
		this.i=center.getBlockX();
		this.j=center.getBlockZ();
		this.segment_length=1;
		this.segment_passed=0;
		this.k =0;
		this.duration=0;
		this.generating=true;
		this.pause=false;
		this.cancel=false;
		generateMachine();
	}
	public boolean isGenerating(){
		return generating;
	}

	public double getTimeTillCompleteInMinutes(){
		return (((radius/16) * (radius/16) *4)/CHUNKS_PER_RUN)/60;
	}

	public void cancelGeneration(){
		MessageSender.broadcast("Generation cancelled.");
		cancel=true;
	}
	public void pauseGeneration(){
		pause=true;
		MessageSender.broadcast("Generation paused.");
	}
	public void resumeGeneration(){
		pause = false;
		MessageSender.broadcast("Generation resumed.");
	}

	private void generateMachine(){

		new BukkitRunnable() {

			@Override
			public void run() {
				duration++;
				if(duration%10 ==0){
					world.save();
					return;
				}
				

				if(pause){
					return;
				}
				if(cancel){
					cancel();
					cancel=false;
				}
				for(int c = 0; c< CHUNKS_PER_RUN; c++){
					if(k <((radius/16) * (radius/16) *4)){
						++k;
						i += di;
						j += dj;
						++segment_passed;
						while(Arrays.asList(loaded).contains(world.getChunkAt(i, j))){
							++k;
							i += di;
							j += dj;
							++segment_passed;
						}
						if (segment_passed == segment_length) {
							// done with current segment
							segment_passed = 0;

							// 'rotate' directions
							int buffer = di;
							di = -dj;
							dj = buffer;

							// increase segment length if necessary
							if (dj == 0) {
								++segment_length;
							}
						}
						world.loadChunk(i, j);
					}
					else if(k==((radius/16) * (radius/16) *4)){
						MessageSender.broadcast("Generating complete.");
						generating=false;
						cancel();
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}




}
