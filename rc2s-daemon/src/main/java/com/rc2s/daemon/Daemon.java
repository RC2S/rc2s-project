package com.rc2s.daemon;

import com.rc2s.daemon.hardware.Hardware;
import com.rc2s.daemon.network.Listener;
import com.rc2s.daemon.network.Packet;
import com.rc2s.daemon.network.Stage;

/**
 * Daemon
 * 
 * Main daemon runnable class
 * 
 * Listen on 1337, uses Hardware class for GPIO manipulation
 * and StatesProcessor for sound algorithm management
 * 
 * @author RC2S
 */
public class Daemon implements Runnable
{
    private final Hardware hardware;
    private final StatesProcessor processor;
	private final Listener listener;

    private boolean running;

    public static void main(String args[])
    {
        Daemon d = new Daemon();

        d.run();
        d.shutdown();
    }

    public Daemon()
    {
        this.hardware   = new Hardware();
        this.processor  = new StatesProcessor(this);
		this.listener   = new Listener(this, 1337);
    }

    @Override
    public void run()
    {
        this.running = true;
		listener.start();
        
        Stage s1 = new Stage(new boolean[][] {{true, false, false, false}, {false, false, false, false}, {false, false, false, false}, {false, false, false, false}});
        Stage s2 = new Stage(new boolean[][] {{false, false, false, false}, {true, false, false, false}, {false, false, false, false}, {false, false, false, false}});
        Stage s3 = new Stage(new boolean[][] {{false, false, false, false}, {false, false, false, false}, {true, false, false, false}, {false, false, false, false}});
        Stage s4 = new Stage(new boolean[][] {{false, false, false, false}, {false, false, false, false}, {false, false, false, false}, {true, false, false, false}});
        
        processor.add(new Packet(10000l, new Stage[] {s1, s2, s3, s4}));

        processor.run();
    }

    public boolean isRunning()
    {
        return running;
    }

    public Hardware getHardware()
    {
        return hardware;
    }

	public StatesProcessor getProcessor()
	{
		return processor;
	}	

    public void shutdown()
    {
        this.running = false;

        processor.shutdown();
        hardware.shutdown();
    }
}
