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
// List contains Tnt Entry
package me.tabinol.blocknotif.tnt;

import java.util.Calendar;
import java.util.LinkedList;
import me.tabinol.blocknotif.BlockNotif;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TntList extends LinkedList<TntEntry> {

    private static final long serialVersionUID = 4025050512327193640L;
    BlockNotif blockNotif;

    public TntList() {

        this.blockNotif = BlockNotif.getThisPlugin();
    }

    public void addAction(Calendar calendar, Player player, Location location) {

        final TntEntry tntEntry = new TntEntry(calendar, player, location);
        // Anti duplication
        if (this.isEmpty() || !tntEntry.equals(this.getLast())) {
            addLast(tntEntry);
        }
    }

    public Player getPlayer(Location location) {

        Player player = null;
        int t = 0;
        boolean foundIt = false;

        while (!isEmpty() && t != size() && !foundIt) {
            if (compareTnt((int) location.getX(), get(t).getLocation().getBlockX(), 15)
                    && compareTnt((int) location.getY(), get(t).getLocation().getBlockY(), 50)
                    && compareTnt((int) location.getZ(), get(t).getLocation().getBlockZ(), 15)) {
                foundIt = true;
                player = get(t).getPlayer();

            }
            t++;
        }

        return player;
    }

    public static boolean compareTnt(int a, int b, int distance) {

        return a >= b - distance && a <= b + distance;
    }
}
