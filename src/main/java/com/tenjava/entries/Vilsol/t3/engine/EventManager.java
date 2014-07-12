package com.tenjava.entries.Vilsol.t3.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.tenjava.entries.Vilsol.t3.Config;
import com.tenjava.entries.Vilsol.t3.Reference;
import com.tenjava.entries.Vilsol.t3.TenJava;
import com.tenjava.entries.Vilsol.t3.engine.events.RandomEvent;

public class EventManager {
	
	private static List<RandomEvent> currentEvents = new ArrayList<RandomEvent>();
	private static BukkitRunnable eventCaller;
	
	public EventManager() {
		eventCaller = new BukkitRunnable() {
			@Override
			public void run() {
				Random r = new Random();
				if(r.nextInt(101) <= Config.eventChance) {
					int theChosenEventNumber = r.nextInt(Reference.availableEvents.size());
					Class<? extends RandomEvent> theChosenEvent = Reference.availableEvents.get(theChosenEventNumber);
					callEvent(theChosenEvent);
				}
				
				Iterator<RandomEvent> i = currentEvents.iterator();
				while(i.hasNext()) {
					RandomEvent randomEvent = i.next();
					if(randomEvent.isFinished()) {
						i.remove();
					}
				}
			}
		};
		
		eventCaller.runTaskTimer(TenJava.plugin, 0, Config.eventRepeatDelay * 20L);
	}
	
	private void callEvent(Class<? extends RandomEvent> event) {
		try {
			RandomEvent eventObject = event.newInstance();
			if(eventObject == null) return;
			currentEvents.add(eventObject);
			Location l = eventObject.getLocationType().generateLocation();
			l.setY(l.getWorld().getHighestBlockYAt(l));
			eventObject.onEvent(l);
		} catch(InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
