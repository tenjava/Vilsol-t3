package com.tenjava.entries.Vilsol.t3.engine;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.tenjava.entries.Vilsol.t3.Config;
import com.tenjava.entries.Vilsol.t3.TenJava;
import com.tenjava.entries.Vilsol.t3.engine.events.RandomEvent;

public class EventManager {
	
	private static HashMap<Class<? extends RandomEvent>, RandomEvent> loadedEvents = new HashMap<Class<? extends RandomEvent>, RandomEvent>();
	private static BukkitRunnable eventCaller;
	
	public EventManager() {
		eventCaller = new BukkitRunnable() {
			@Override
			public void run() {
				if(loadedEvents.size() == 0) return;
				Random r = new Random();
				if(r.nextInt(101) <= Config.eventChance) {
					int theChosenEventNumber = r.nextInt(Config.availableEvents.size());
					Class<? extends RandomEvent> theChosenEvent = Config.availableEvents.get(theChosenEventNumber);
					callEvent(theChosenEvent);
				}
			}
		};
		
		eventCaller.runTaskTimer(TenJava.plugin, Config.eventRepeatDelay * 20, Config.eventRepeatDelay * 20L);
		
		loadEvents();
	}
	
	/**
	 * Loads all events that are enabled
	 */
	private void loadEvents() {
		for(Class<? extends RandomEvent> e : Config.availableEvents) {
			try {
				RandomEvent event = e.newInstance();
				loadedEvents.put(e, event);
			} catch(InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Generates a random location and calls the event
	 * @param event The event to be called
	 */
	public static void callEvent(Class<? extends RandomEvent> event) {
		RandomEvent eventObject = loadedEvents.get(event);
		if(eventObject == null) return;
		Location l = eventObject.getLocationType().generateLocation();
		l.setY(l.getWorld().getHighestBlockYAt(l));
		eventObject.callEvent(l);
	}
	
}
