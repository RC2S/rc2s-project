package com.rc2s.common.bo;

import java.io.Serializable;

/**
 * CubeState
 * 
 * Business Object representing the cube state
 * A cube state is a 3D reprensentation of its LEDs
 * 
 * @author RC2S
 */
public class CubeState implements Serializable
{
	private boolean[][][] states;

	public CubeState() {}

	public CubeState(final boolean[][][] states)
	{
		this.states = states;
	}

	public void set(final int x, final int y, final int z, final boolean state)
	{
		this.states[x][y][z] = state;
	}

	public boolean get(final int x, final int y, final int z)
	{
		return this.states[x][y][z];
	}

	public boolean[][][] getStates()
	{
		return states;
	}

	public void setStates(final boolean[][][] states)
	{
		this.states = states;
	}
}
