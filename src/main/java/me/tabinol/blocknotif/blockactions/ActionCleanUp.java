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

// Clean Up Action in memory
public class ActionCleanUp extends BukkitRunnable {

	private Calendar timeBefore;
	private BlockNotif blockNotif;
	
	public ActionCleanUp() { 
		
		super();
		timeBefore = Calendar.getInstance();
		blockNotif = BlockNotif.getThisPlugin();
	}
	
	@Override
	public void run() {
		int t = 0, u = 0;
		
		// Clean up Block Action List
		while(!BlockNotif.blockActionList.isEmpty() && 
				(BlockNotif.blockActionList.getFirst().getCalendar().before(timeBefore) ||
						BlockNotif.blockActionList.size() >= blockNotif.getConfig().getInt("History.MaxEntryKeep"))) {
			BlockNotif.blockActionList.removeFirst();
			t ++;
		}
		
		// Clean up TNT list
		while(!blockNotif.tntList.isEmpty() && (blockNotif.tntList.getFirst().getCalendar().before(timeBefore) ||
				blockNotif.tntList.size() >= blockNotif.getConfig().getInt("History.MaxEntryKeep"))) {
			blockNotif.tntList.removeFirst();
			u ++;
		}
		
		// Notify cleanup in the console
		blockNotif.logTask.writeLog("Clean up: " + t + " action(s), " + u + " TNT(s)");
		
		// Reschedule Action
		new ActionCleanUp().scheduleAction();
		
	}

	public void scheduleAction() {

		runTaskLater(blockNotif,
				20 * blockNotif.getConfig().getInt("History.MaxTimeKeep"));
	}
}
