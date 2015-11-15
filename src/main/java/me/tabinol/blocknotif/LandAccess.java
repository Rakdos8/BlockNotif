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

import me.tabinol.secuboidapi.ApiSecuboidSta;
import me.tabinol.secuboidapi.lands.ApiLand;
import org.bukkit.Location;

public class LandAccess {

    public String getLandName(Location location) {

        ApiLand land = ApiSecuboidSta.getLands().getLand(location);

        if (land != null) {
            return land.getName();
        } else {
            return null;
        }
    }
}
