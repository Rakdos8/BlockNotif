/*
    BlockNotif: Minecraft plugin player action on blocks notification
    Copyright (C) 2013  Michel Blanchet

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.tabinol.blocknotif.blockactions;

import java.util.Calendar;
import me.tabinol.blocknotif.BlockNotif;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Clean Up Action in memory
 * @author Tabinol
 */
public class ActionCleanUp extends BukkitRunnable {

	private Calendar timeBefore;
	private BlockNotif blockNotif;

	/**
	 * Initialise memory
	 */
	public ActionCleanUp() { 
		
		super();
		timeBefore = Calendar.getInstance();
		blockNotif = BlockNotif.getThisPlugin();
	}
	
	@Override
	public void run() {
		int t = 0 ;
		int u = 0 ;
		
		// Clean up Block Action List
		while(!BlockNotif.getBlockActionList().isEmpty() &&
				(BlockNotif.getBlockActionList().getFirst().getCalendar().before(timeBefore) ||
						BlockNotif.getBlockActionList().size() >= blockNotif.getConfig().getInt("History.MaxEntryKeep"))) {
			BlockNotif.getBlockActionList().removeFirst();
			t ++;
		}
		
		// Clean up TNT list
		while(!BlockNotif.getTntList().isEmpty() && (BlockNotif.getTntList().getFirst().getCalendar().before(timeBefore) ||
				BlockNotif.getTntList().size() >= blockNotif.getConfig().getInt("History.MaxEntryKeep"))) {
			BlockNotif.getTntList().removeFirst();
			u ++;
		}
		
		// Notify cleanup in the console
		BlockNotif.getLogTask().writeLog("Clean up: " + t + " action(s), " + u + " TNT(s)");
		
		// Reschedule Action
		new ActionCleanUp().scheduleAction();
		
	}

	/**
	 * Schedule
	 */
	public void scheduleAction() {

		runTaskLater(blockNotif,
				20 * blockNotif.getConfig().getLong("History.MaxTimeKeep"));
	}
}
