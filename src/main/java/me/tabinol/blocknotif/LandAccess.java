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
// Access the Name of the Land (Flags Plugin)
package me.tabinol.blocknotif;

import io.github.alshain01.flags.api.FlagsAPI;
import io.github.alshain01.flags.api.area.Area;
import io.github.alshain01.flags.api.area.Area.AreaRelationship;
import org.bukkit.Location;

public class LandAccess {

    public String getLandName(Location location) {

        Area area = FlagsAPI.getAbsoluteAreaAt(location);

        if (area != null
                && area.getRelationship(FlagsAPI.getWildernessArea(location.getWorld())) != AreaRelationship.EQUAL) {
            
            return area.getName();
        } else {
            return null;
        }
    }
}
