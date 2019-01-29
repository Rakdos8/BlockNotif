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

import me.tabinol.blocknotif.BlockNotif;
import me.tabinol.blocknotif.utils.EnumUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

/**
 * Data of block
 * @author Tabinol & Bhasher
 */
public class BlockData implements Comparable<BlockData> {

	/**
	 * Type of BlockData
	 */
	public enum BlockDataType {

		BLOCK(),
		ENTITY()
	}
	private final BlockDataType blockDataType;
	private boolean hasData = false;
	private org.bukkit.block.data.BlockData itemData = null;
	private boolean hasName = false;
	private String name = null;
	private Material material = null ;
	private EntityType entity = null ;

	/**
	 * Data of block
	 * @param blockDataType type of block
	 * @param dataInf data
	 */
	public BlockData(final BlockDataType blockDataType, final String dataInf) {

		this.blockDataType = blockDataType;
		final String[] dataVal = dataInf.split(":");
		final Material mat;
		final EntityType ent;

		if (blockDataType == BlockDataType.BLOCK && (mat = EnumUtils.getEnumFromName(Material.class, dataVal[0])) != null) {
			this.material = mat ;
			this.name = this.material.name();
		} else if (blockDataType == BlockDataType.ENTITY && (ent = EnumUtils.getEnumFromName(EntityType.class, dataVal[0], EntityType.UNKNOWN)) != null) {
			this.entity = ent ;
			this.name = this.entity.name();
		} else {
			BlockNotif.logWarn(dataInf + " is not a recognized " + blockDataType.name().toLowerCase() + ".");
		}

		if (dataVal.length >= 2
			&& (this.material == Material.PLAYER_HEAD || this.material == Material.WITHER_SKELETON_SKULL
			|| this.material == Material.CREEPER_HEAD || this.material == Material.SKELETON_SKULL)) {

			hasName = true;
			name = dataVal[1];
		}
	}

	/**
	 * For a block
	 * @param bl Block
	 */
	public BlockData(final Block bl) {

		this.blockDataType = BlockDataType.BLOCK;
		this.material = bl.getType() ;
		this.name = this.material.name() ;

		hasData = true;
		itemData = bl.getBlockData();
	}

	/**
	 * For a material
	 * @param material Material
	 */
	public BlockData(final Material material) {
		
		blockDataType = BlockDataType.BLOCK;
		this.material = material ;
		this.name = material.name() ;
	}

	/**
	 * For a entity
	 * @param entityType entity
	 */
	public BlockData(final EntityType entityType) {

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

	public String getName() {

		return name;
	}

	public String getDisplay() {

		final String colSrc;
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
	public final int hashCode() {
		if (blockDataType == BlockDataType.BLOCK){
			return new HashCodeBuilder()
					.append(blockDataType)
					.append(material)
					.hashCode();
		}else if (blockDataType == BlockDataType.ENTITY){
			return new HashCodeBuilder()
					.append(blockDataType)
					.append(entity)
					.hashCode();
		}
		return new HashCodeBuilder()
				.append(blockDataType)
				.hashCode() ;
	}

	/**
	 * Return if two BlockData are similar
	 * @param obj BlockData object
	 * @return similar
	 */
	@Override
	public final boolean equals(final Object obj){
		if (obj instanceof BlockData){

			final BlockData bd2 = (BlockData) obj ;

			if (blockDataType != bd2.blockDataType) {
				return false ;
			}

			if (blockDataType == BlockDataType.BLOCK) {
				return new EqualsBuilder()
						.append(material, bd2.material)
						.isEquals() ;
			}else if (blockDataType == BlockDataType.ENTITY) {
				return new EqualsBuilder()
						.append(entity, bd2.entity)
						.isEquals() ;
			}

		}

		return false ;

	}

	@Override
	public int compareTo(final BlockData bd2) {

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
