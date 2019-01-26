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
// Represent an item value (Entity or Block)
package me.tabinol.blocknotif.confdata;

import me.tabinol.blocknotif.BlockNotif;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;

public class BlockData implements Comparable<BlockData> {

    public static enum BlockDataType {

        BLOCK,
        ENTITY;
    }
    private final BlockDataType blockDataType;
    private boolean hasData = false;
    private org.bukkit.block.data.BlockData itemData = null;
    private boolean hasName = false;
    private String name = null;
    private Material material = null ;
    private EntityType entity = null ;

    public BlockData(BlockDataType blockDataType, String dataInf) throws Exception {

        this.blockDataType = blockDataType;
        final String[] dataVal = dataInf.split(":");
        Material mat;
        EntityType ent;

        if (blockDataType == BlockDataType.BLOCK && (mat = Material.matchMaterial(dataVal[0])) != null) {
            this.material = mat ;
            this.name = this.material.name();
        } else if (blockDataType == BlockDataType.ENTITY && (ent = EntityType.valueOf(dataVal[0])) != null) {
            this.entity = ent ;
            this.name = this.entity.name();
        } else {
        	BlockNotif.logWarn(dataInf + " is not a recognized block or entity.");
        }
        
        if (dataVal.length >= 2) {
            // If this is a skull
            if (this.material == Material.PLAYER_HEAD || this.material == Material.WITHER_SKELETON_SKULL
            		|| this.material == Material.CREEPER_HEAD || this.material == Material.SKELETON_SKULL) {
                hasName = true;
                name = dataVal[1];
            }
        }
    }

    public BlockData(Block bl) {

        this.blockDataType = BlockDataType.BLOCK;
        this.material = bl.getType() ;
        this.name = this.material.name() ;

        // If this is a head, get the name
        if (bl.getType() == Material.PLAYER_HEAD) {
        	 final Skull skull = (Skull) bl.getState();
        	 if (skull.hasOwner()) {
                 hasName = true;
                 name = skull.getOwningPlayer().getName();
             }
        }else if(bl.getType() == Material.CREEPER_HEAD || bl.getType() == Material.DRAGON_HEAD || bl.getType() == Material.ZOMBIE_HEAD) {
       	 	final Skull skull = (Skull) bl.getState();
        	hasName = true;
            name = skull.getType().name();
        } else {
            // For a non skull
            hasData = true;
            itemData = bl.getBlockData();
        }
    }

    public BlockData(Material material) {
    	
        blockDataType = BlockDataType.BLOCK;
        this.material = material ;
        this.name = material.name() ;
    }

    public BlockData(EntityType entityType) {

        blockDataType = BlockDataType.ENTITY;
        this.entity = entityType ;
        this.name = entityType.name() ;
    }

    @Override
    public String toString() {

        if (hasData) {
            return name + ":" + itemData;
        } else if (hasName) {
            return name + ":" + name;
        } else {
            return name + "";
        }
    }

    public BlockDataType getBlockDataType() {

        return blockDataType;
    }

    public boolean hasData() {

        return hasData;
    }

    public org.bukkit.block.data.BlockData getItemData() {

        return itemData;
    }

    public boolean hasName() {

        return hasName;
    }

    public String getName() {

        return name;
    }

    public String getDisplay() {

        String colSrc;
        final StringBuilder iName = new StringBuilder();

        // Get color
        if ((colSrc = BlockNotif.getThisPlugin().getConfig().getString("Color." + blockDataType + "." + name)) != null) {
            iName.append(colSrc.replaceAll("&", "§"));
        }


        iName.append(this.name);
        if (hasName) {
            // Get the name if it is a skull
            iName.append(":").append(name);
        }
        
        return iName.toString();
    }

	@Override
    public int compareTo(BlockData bd2) {

        if (this.blockDataType != bd2.blockDataType) {
            return this.blockDataType.compareTo(bd2.blockDataType);
        }
        
        if(this.blockDataType == BlockDataType.BLOCK) {

            if(this.material == bd2.material) {
            	return 0 ;
            }
            
            return this.material.name().compareTo(bd2.material.name()) ;
            
        }else if(this.blockDataType == BlockDataType.ENTITY) {

            if(this.entity == bd2.entity) {
            	return 0 ;
            }
            
            return this.entity.name().compareTo(bd2.entity.name()) ;
        	
        }else {
        	BlockNotif.logWarn("Comparison of two BlockData that are not blocks or entities. (" + this.getDisplay() + "," + bd2.getDisplay() + ")");
        	return 0 ;
        }
    }
}
