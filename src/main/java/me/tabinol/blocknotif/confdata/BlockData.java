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
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BlockData implements Comparable<BlockData> {

	public enum BlockDataType {

		BLOCK,
		ENTITY;
	}
	private final BlockDataType blockDataType;
	private int itemID = 0;
	private boolean hasData = false;
	private byte itemData = 0;
	private boolean hasName = false;
	private String name = null;

	public BlockData(BlockDataType blockDataType, String dataInf) throws Exception {

		this.blockDataType = blockDataType;
		String[] dataVal = dataInf.split(":");
		Material mat;
		EntityType ent;

		if (blockDataType == BlockDataType.BLOCK && (mat = Material.matchMaterial(dataVal[0])) != null) {
			itemID = mat.getId();
		} else if (blockDataType == BlockDataType.ENTITY && (ent = EntityType.fromName(dataVal[0])) != null) {
			itemID = ent.getTypeId();
		} else {
			itemID = Integer.parseInt(dataVal[0]);
		}
		if (dataVal.length >= 2) {
			// If this is a skull
			if (itemID == 144) {
				hasName = true;
				name = dataVal[1];
			} else {
				hasData = true;
				itemData = adjustData(Byte.parseByte(dataVal[1]));
			}
		}
	}

	public BlockData(BlockDataType blockDataType, int itemID) {

		this.blockDataType = blockDataType;
		this.itemID = itemID;
	}

	public BlockData(Block bl) {

		blockDataType = BlockDataType.BLOCK;
		itemID = bl.getTypeId();

		// If this is a head, get the name
		if (bl.getType() == Material.SKULL) {
			Skull skull = (Skull) bl.getState();
			if (skull.getSkullType() == SkullType.PLAYER) {
				if (skull.hasOwner()) {
					hasName = true;
					name = skull.getOwner();
				}
			} else {
				hasName = true;
				name = skull.getSkullType().name();
			}
		} else {
			// For a non skull
			hasData = true;
			itemData = adjustData(bl.getData());
		}
	}

	public BlockData(Material meterial) {

		blockDataType = BlockDataType.BLOCK;
		itemID = meterial.getId();
	}

	public BlockData(Entity entity) {

		blockDataType = BlockDataType.ENTITY;
		itemID = entity.getEntityId();
	}

	public BlockData(EntityType entityType) {

		blockDataType = BlockDataType.ENTITY;
		itemID = entityType.getTypeId();
	}

	public BlockData(BlockDataType blockDataType, int itemID, boolean hasData, byte itemData, boolean hasName, String name) {

		this.blockDataType = blockDataType;
		this.itemID = itemID;
		this.hasData = hasData;
		this.itemData = adjustData(itemData);
		this.hasName = hasName;
		this.name = name;
	}

	public boolean equals(BlockData bd2) {

		return blockDataType == bd2.blockDataType
				&& itemID == bd2.itemID
				&& (!bd2.hasData || !hasData
				|| itemData == bd2.itemData)
				&& (!bd2.hasName || !hasName
				|| name.equalsIgnoreCase(bd2.name));
	}

	public BlockData copyOf() {

		return new BlockData(blockDataType, itemID, hasData, itemData, hasName, name);
	}

	@Override
	public String toString() {

		if (hasData) {
			return itemID + ":" + itemData;
		} else if (hasName) {
			return itemID + ":" + name;
		} else {
			return itemID + "";
		}
	}

	public int compareTo(BlockData bd2) {

		if (blockDataType != bd2.blockDataType) {
			return blockDataType.compareTo(bd2.blockDataType);
		}
		if (itemID < bd2.itemID) {
			return -1;
		}
		if (itemID > bd2.itemID) {
			return 1;
		}
		if (bd2.hasData && this.hasData) {
			if (itemData < bd2.itemData) {
				return -1;
			}
			if (itemData > bd2.itemData) {
				return 1;
			}
		}
		if (bd2.hasName && this.hasName) {
			return name.compareToIgnoreCase(bd2.name);
		}
		return 0;
	}

	public BlockDataType getBlockDataType() {

		return blockDataType;
	}

	public int getItemID() {

		return itemID;
	}

	public boolean hasData() {

		return hasData;
	}

	public int getItemData() {

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
		StringBuilder iName = new StringBuilder();

		// Get color
		if ((colSrc = BlockNotif.getThisPlugin().getConfig().getString("Color." + blockDataType + "." + itemID)) != null) {
			iName.append(colSrc.replaceAll("&", "§"));
		}

		// Get the name of Entity or Item
		if (blockDataType == BlockDataType.BLOCK) {
			Material material = Material.getMaterial(itemID);
			if (material == null) {
				iName.append(itemID);
			} else {
				iName.append(material.name());
				if (hasName) {
					// Get the name if it is a skull
					iName.append(":").append(name);
				}
			}
		} else {
			EntityType entityType = EntityType.fromId(itemID);
			if (entityType == null) {
				iName.append(itemID);
			} else {
				iName.append(entityType.name());
			}

		}

		return iName.toString();
	}

	private byte adjustData(byte DataToAdj) {

		// wood
		if (itemID == 17) {
			return (byte) (DataToAdj & 3);
		}

		// Block of quartz
		if (itemID == 155 && DataToAdj >= 2) {
			return 2;
		}

		return DataToAdj;
	}
}
