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
// Store All Block Actions
package me.tabinol.blocknotif.blockactions;

import java.util.Calendar;
import java.util.LinkedList;
import me.tabinol.blocknotif.BlockNotif;
import me.tabinol.blocknotif.MessagesTxt;
import me.tabinol.blocknotif.NotifyActionTask;
import me.tabinol.blocknotif.confdata.BlockData;
import me.tabinol.blocknotif.utils.Permission;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.GameMode ;

/**
 * List actions
 * @author Tabinol
 */
public class BlockActionList extends LinkedList<BlockEntry> {

	private static final long serialVersionUID = -2474988612847951992L;

	/**
	 *  Initialise list of actions
	 */
	public BlockActionList() {

		super();
	}

	/**
	 * Add Action (common)
	 * @param calendar Calendar
	 * @param player Player
	 * @param action Action
	 * @param location Location
	 * @param blockData BlockData
	 */
	public void addAction(final Calendar calendar, final Player player, final int action,
			final Location location, final BlockData blockData) {

		String playerName;
		
		// Verify permissions and GameMode
		if (action == MessagesTxt.TNTEXPLODE || (BlockNotif.getDebugState() || !Permission.playerHasPermission(player,"blocknotif.ignore")
				&& (player.getGameMode() == GameMode.CREATIVE
				&& BlockNotif.getThisPlugin().getConfig().getBoolean("ActionListen.Creative")
				|| player.getGameMode() != GameMode.CREATIVE))) {
			if (player == null) {
				playerName = "UNKNOWN";
			} else {
				playerName = player.getName();
			}

			// Add entry and Notify
			final BlockEntry blockEntry = new BlockEntry(calendar, playerName,
					action, location, blockData);
			// Anti duplication
			if (this.isEmpty() || !blockEntry.equals(this.getLast())) {
				addLast(blockEntry);
				BlockNotif.getLogTask().writeLog(blockEntry.getMessage().replaceAll("§.", ""));
				// Check if Entry exist, if not, add it
				final String actionInList = blockEntry.toActionInList();
				if (!BlockNotif.getInActionList().contains(actionInList)) {
					new NotifyActionTask(this, calendar, actionInList).runTaskLater(BlockNotif.getThisPlugin(),
							20 * BlockNotif.getThisPlugin().getConfig().getLong("History.TimeBeforeNotify"));

				}
			}

		}

	}
}
