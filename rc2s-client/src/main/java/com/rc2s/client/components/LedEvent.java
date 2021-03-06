package com.rc2s.client.components;

/**
 * LedEvent
 * 
 * Abstract runnable class for led event handling
 * 
 * @author RC2S
 */
public abstract class LedEvent implements Runnable
{
	private Led led;
	
	@Override
	public abstract void run();

	public Led getLed()
	{
		return led;
	}

	public void setLed(final Led led)
	{
		this.led = led;
	}
}
