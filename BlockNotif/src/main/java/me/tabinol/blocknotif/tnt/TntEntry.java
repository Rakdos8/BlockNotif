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

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.entity.Player;
import org.bukkit.Location;

/**
 * TntEntry
 * @author Tabinol
 */
public final class TntEntry {

	private Calendar calendar;
	private Player player;
	private Location location;

	/**
	 * Initialise TntEntry
	 * @param calendar Calendar
	 * @param player Player
	 * @param location Location
	 */
	TntEntry(final Calendar calendar, final Player player, final Location location) {

		this.calendar = calendar;
		this.player = player;
		this.location = location;
	}

	@Override
	public final int hashCode(){
		return new HashCodeBuilder()
				.append(player)
				.append(location)
				.hashCode();
	}

	/**
	 * Check if two TntEntry are similar
	 * @param object TntEntry object
	 * @return same TntEntry
	 */
	@Override
	public final boolean equals(final Object object) {

		if(object instanceof TntEntry){

			final TntEntry tntEntryB = (TntEntry) object ;

			return player.getName().equals(tntEntryB.player.getName())
					&& location.equals(tntEntryB.location);
		}

		return false ;

	}

	Location getLocation() {

		return location;
	}

	public Calendar getCalendar() {

		return calendar;
	}

	public Player getPlayer() {

		return player;
	}
}
