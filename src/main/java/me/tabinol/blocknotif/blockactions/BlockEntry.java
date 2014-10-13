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
// Class for Block Action information
package me.tabinol.blocknotif.blockactions;

import java.util.Calendar;
import me.tabinol.blocknotif.BlockNotif;
import me.tabinol.blocknotif.confdata.BlockData;
import org.bukkit.Location;

public class BlockEntry {

    private final BlockNotif blockNotif;
    private final Calendar calendar;
    private final String playerName;
    private final int action; // type of action
    private final String cuboid;
    private final Location location;
    private final BlockData blockData;

    public BlockEntry(Calendar calendar, String playerName, int action,
            String cuboid, Location location, BlockData blockData) {

        blockNotif = BlockNotif.getThisPlugin();
        this.calendar = calendar;
        this.playerName = playerName;
        this.action = action;
        this.cuboid = cuboid;
        this.location = location;
        this.blockData = blockData;
    }

    public String getMessage() {

        String cuboidWorld;

        if (cuboid != null && !cuboid.equals("")) {
            cuboidWorld = cuboid + "/" + location.getWorld().getName();
        } else {
            cuboidWorld = location.getWorld().getName();
        }

        return blockNotif.messagesTxt.getMessage(action,
                new String[]{"<time>", "<player>", "<block>", "<world>", "<posx>", "<posy>", "<posz>"},
                new String[]{getTime(), playerName, blockData.getDisplay(), cuboidWorld,
            Integer.toString(location.getBlockX()), Integer.toString(location.getBlockY()), Integer.toString(location.getBlockZ())});
    }

    public String getTime() {

        return String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public boolean equals(BlockEntry blockEntryB) {

        return playerName.equals(blockEntryB.playerName)
                && this.action == blockEntryB.action
                && location.equals(blockEntryB.location)
                && this.blockData.equals(blockEntryB.blockData);
    }

    public Calendar getCalendar() {

        return calendar;
    }

    public String getPlayerName() {

        return playerName;
    }

    public String toActionInList() {

        return playerName + ":" + action + ":" + blockData;
    }
}
