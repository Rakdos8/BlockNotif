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

package me.tabinol.blocknotif;

import me.tabinol.blocknotif.blockactions.BlockActionList;
import me.tabinol.blocknotif.blockactions.BlockEntry;
import java.util.Calendar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

// Notify action task after the delay
public class NotifyActionTask extends BukkitRunnable {
	
	private BlockNotif blockNotif;
	private BlockActionList blockActionList;
	private String playerActionBlock;
	private Calendar calendar;
	
	public NotifyActionTask(BlockActionList blockActionList, 
			Calendar calendar, String playerActionBlock) {
		
		super();
		blockNotif = BlockNotif.getThisPlugin();
		this.blockActionList = blockActionList;
		this.calendar = calendar;
		this.playerActionBlock = playerActionBlock;
		
		blockNotif.inActionList.add(playerActionBlock);
	}

	@Override
	public void run() {
		
		int cmp = blockActionList.size();
		blockNotif.inActionList.remove(playerActionBlock);
		BlockEntry blockEntry;
		int nbOfValue = 0;
		BlockEntry blockEntryLast = null;
		
		do {
			cmp--;
			blockEntry = blockActionList.get(cmp);
			if(playerActionBlock.equals(blockEntry.toActionInList())) {
				if(blockEntryLast == null) {
					blockEntryLast = blockEntry;
				}
				nbOfValue++;
			}
			
			
		} while(blockEntry.getCalendar().after(calendar) && cmp != 0);
		
		sendNotificationMessage(blockNotif, blockEntryLast, nbOfValue);
	}
		
	public static void sendNotificationMessage(BlockNotif blockNotif, BlockEntry blockEntryLast, int nbOfValue) {
		
		String message;
		String finalMessage;
		
		message = blockEntryLast.getMessage();
		if(nbOfValue > 1) {
			finalMessage = message + blockNotif.messagesTxt.getMessage(MessagesTxt.MESSAGE_BEFORE, 
					new String[] { "<nb>" },
					new String[] { Integer.toString(nbOfValue - 1) } ); 
		} else {
			finalMessage = message;
		}
		
		for(Player players: blockNotif.getServer().getOnlinePlayers()) {
			if(players.hasPermission("blocknotif.notify") || players.isOp()) {
				players.sendMessage(finalMessage);
			}
		}
	}
}
