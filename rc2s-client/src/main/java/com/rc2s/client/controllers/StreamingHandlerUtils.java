package com.rc2s.client.controllers;

import com.rc2s.client.Main;
import com.rc2s.ejb.streaming.StreamingFacadeRemote;

public class StreamingHandlerUtils
{
	private StreamingUtils streaming;

	public StreamingHandlerUtils(final StreamingFacadeRemote streamingEJB,
            final String id, final String media) throws Exception
	{
		this.streaming = new StreamingUtils(streamingEJB, id, media);

		// Register the streaming Thread in the RC2S-Client thread pool
		Main.registerThread(streaming);
	}

	public void start()
	{
		synchronized (streaming) {
			streaming.start();
		}
	}

	public void pauseStreaming()
	{
		synchronized (streaming) {
			streaming.setStreamingState(StreamingUtils.StreamingState.PAUSE);
			streaming.notifyAll();
		}
	}

	public void resumeStreaming()
	{
		synchronized (streaming) {
			streaming.setStreamingState(StreamingUtils.StreamingState.PLAY);
			streaming.notifyAll();
		}
	}

	public void stopStreaming()
	{
		synchronized (streaming) {
			streaming.setStreamingState(StreamingUtils.StreamingState.STOP);
			streaming.notifyAll();

			System.err.println("------- Waiting for the current StreamingUtils process to stop... ------");
			try {
				streaming.join();
				Main.releaseThread(streaming); // Release from the thread pool
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("------ StreamingUtils has ended! ------");
		}
	}

	public boolean isPlaying()
	{
		return streaming.isPlaying();
	}

	public StreamingUtils.StreamingState getStreamingState()
	{
		return streaming.getStreamingState();
	}

	public StreamingUtils getSlave()
	{
		return streaming;
	}
}