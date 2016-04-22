package com.goldornetwork.uhc.managers.world;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;

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
	private int di =-16;
	private int dj = 0;
	// length of current segment
	private int segment_length;

	// current position (i, j) and how much of current segment we passed
	private int x;
	private int z;
	private int segment_passed;
	private int k;
	

	//storage
	private boolean generating;
	private boolean cancel;
	private final int CHUNKS_PER_RUN =10;
	private int duration;	
	private List<CoordXZ> storedChunks = new LinkedList<CoordXZ>();
	
	public void generate(World world, Location center, int radius){
		this.world = world;
		this.radius = 16*(Math.round(radius/16));
		this.x=center.getBlockX();
		this.z=center.getBlockZ();
		this.segment_length=1;
		this.segment_passed=0;
		this.k =0;
		this.duration=0;
		this.generating=true;
		this.cancel=false;
		generateMachine();
	}
	public boolean isGenerating(){
		return generating;
	}

	public void cancelGeneration(){
		cancel=true;
	}

	private void generateMachine(){

		new BukkitRunnable() {

			@Override
			public void run() {
				if(plugin.availableMemoryTooLow()){
					return;
				}
				if(duration%30 ==0){
					world.save();
				}
				if(cancel){
					cancel();
				}
				else{
					for(int c = 0; c< CHUNKS_PER_RUN; c++){
						if(k <((radius/16) * (radius/16) *4)){
							++k;
							x += di;
							z += dj;
							++segment_passed;
							
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
							world.loadChunk(x, z, true);
							storedChunks.add(new CoordXZ(x, z));
							
							while(storedChunks.size()>8){
								CoordXZ coord = storedChunks.remove(0);
								world.unloadChunkRequest(coord.x, coord.z);
							}
							
							duration++;
						}
						else if(k>=((radius/16) * (radius/16) *4)){
							world.save();
							generating=false;
							cancel();
						}
					}
				}
				
			}
		}.runTaskTimer(plugin, 200L, 30L);
	}




}
