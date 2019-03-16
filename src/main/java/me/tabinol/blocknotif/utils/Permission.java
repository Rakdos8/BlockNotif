package me.tabinol.blocknotif.utils;

import org.bukkit.entity.Player;

import me.tabinol.blocknotif.BlockNotif;

/**
 * Gère les permissions
 * @author Bhasher
 */
public final class Permission {

	private Permission(){
		throw new IllegalStateException("Utility class") ;
	}

	/**
	 * Vérifie que le joueur a la permission demandée ou une permission supérieur.
	 * @param player Le joueur
	 * @param permission La permission
	 * @return Retourne si le joueur a la permission ou une permission supérieur
	 */
	public static boolean playerHasPermission(final Player player, final String permission){

		if(player.isOp() || player.hasPermission(permission)){
			return true ;
		}else if(player.hasPermission("-" + permission)){
			return false ;
		}

		final String[] permissionParts = permission.split("\\.");

		if(permissionParts.length == 0){
			return false ;
		}

		final StringBuilder partPerm = new StringBuilder() ;

		for(final String part : permissionParts){

			partPerm.append(part) ;
			partPerm.append(".") ;

			if(player.hasPermission(partPerm.toString() + "*")){
				return true ;
			}else if(player.hasPermission("-" + partPerm.toString() + "*")){
				return false ;
			}

		}

		return false ;
	}

}
