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
// Contains Tnt information Entry
package me.tabinol.blocknotif.tnt;

import java.util.Calendar;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class TntEntry {

    private Calendar calendar;
    private Player player;
    private Location location;

    public TntEntry(Calendar calendar, Player player, Location location) {

        this.calendar = calendar;
        this.player = player;
        this.location = location;
    }

    public boolean equals(TntEntry tntEntryB) {

        return player.getName().equals(tntEntryB.player.getName())
                && location.equals(tntEntryB.location);
    }

    public Location getLocation() {

        return location;
    }

    public Calendar getCalendar() {

        return calendar;
    }

    public Player getPlayer() {

        return player;
    }
}
