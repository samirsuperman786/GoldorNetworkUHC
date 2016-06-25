package com.goldornetwork.uhc.utils;

public class CoordXZ {

	
	public int x;
	public int z;

	
	public CoordXZ(int x, int z){
		this.x = x;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}

		else if (obj == null || obj.getClass() != this.getClass()){
			return false;
		}

		CoordXZ test = (CoordXZ)obj;
		return test.x == this.x && test.z == this.z;
	}
}
