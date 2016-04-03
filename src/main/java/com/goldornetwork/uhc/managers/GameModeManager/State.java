package com.goldornetwork.uhc.managers.GameModeManager;

public enum State {
	NOT_RUNNING, OPEN, CLOSED, SCATTER, INGAME, ENDING;

	private static State currentState;
	
	/**
	 * Used to set the state of the server
	 * @param state - the state to set to
	 */
	public static void setState(State state){
		currentState = state;
	}
	
	/**
	 * Used to get the state of the server
	 * @return <code> State </code> of the server
	 */
	public static State getState(){
		if(currentState==null){
			currentState=State.NOT_RUNNING;
		}
		return currentState;
	}
	
}
