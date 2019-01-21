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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlockActionList extends LinkedList<BlockEntry> {

    /**
     *
     */
    private static final long serialVersionUID = -2474988612847951992L;
    private final BlockNotif blockNotif;

    public BlockActionList() {

        super();
        blockNotif = BlockNotif.getThisPlugin();
    }

    // Add Action (common)
    public void addAction(Calendar calendar, Player player, int action,
            Location location, BlockData blockData) {

        String playerName;
        String cuboid;
        
        // Verify permissions and GameMode
        if (action == MessagesTxt.TNTEXPLODE || (!player.hasPermission("blocknotif.ignore")
                && ((player.getGameMode() == GameMode.CREATIVE
                && BlockNotif.ActionListen_Creative
                || !(player.getGameMode() == GameMode.CREATIVE))))) {
            if (player == null) {
                playerName = "UNKNOWN";
            } else {
                playerName = player.getName();
            }
        }
    }
}
