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
package me.tabinol.blocknotif.confdata;

import java.util.Collection;
import java.util.TreeSet;

/**
 * TreeSetAll
 * @author Tabinol
 * @param <E> E
 */
public class TreeSetAll<E> extends TreeSet<E> {

	/**
	 * Serial Number
	 */
	private static final long serialVersionUID = -3235246768443291689L;
	private boolean isAll = false;

	/**
	 * Initialise TreeSetAll
	 */
	public TreeSetAll() {
		
		super();
	}

	@Override
	public boolean contains(final Object o) {

		if (isAll) {
			return true;
		}
		return super.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> clctn) {

		if (isAll) {
			return true;
		}
		return super.containsAll(clctn);
	}

	public void setIsAll(final boolean isAll) {

		this.isAll = isAll;
	}

	@Override
	public final int hashCode() {
		return super.hashCode() ;
	}

	@Override
	public boolean equals(final Object obj){
		return super.equals(obj) ;
	}
}
