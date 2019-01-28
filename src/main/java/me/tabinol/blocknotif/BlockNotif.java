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
package me.tabinol.blocknotif;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import me.tabinol.blocknotif.blockactions.ActionCleanUp;
import me.tabinol.blocknotif.blockactions.BlockActionList;
import me.tabinol.blocknotif.confdata.BlockData;
import me.tabinol.blocknotif.confdata.BlockData.BlockDataType;
import me.tabinol.blocknotif.confdata.TreeSetAll;
import me.tabinol.blocknotif.tnt.TntList;
import me.tabinol.blocknotif.utils.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the BlockNotif plugin.
 * @author Tabinol & Bhasher
 */
public class BlockNotif extends JavaPlugin implements Listener {

	private static BlockActionList blockActionList;
	private static TntList tntList;
	private static List<String> inActionList;
	private static MessagesTxt messagesTxt;
	private static LogTask logTask;

	private TreeSetAll<BlockData> blockBreakList;
	private TreeSetAll<BlockData> blockPlaceList;
	private TreeSetAll<BlockData> blockIgniteList;
	private TreeSetAll<BlockData> bucketUseList;
	private TreeSetAll<BlockData> entityKillList;
	private TreeSetAll<BlockData> blockBreakPreventList;
	private TreeSetAll<BlockData> blockPlacePreventList;
	private TreeSetAll<BlockData> blockIgnitePreventList;
	private TreeSetAll<BlockData> bucketUsePreventList;
	private TreeSetAll<BlockData> entityKillPreventList;
	private boolean watchTntExplode;
	private static BlockNotif thisPlugin;

	private static boolean debug ;

	@Override
	public void onEnable() {

		init(this) ;
	}

	/**
	 * Init BlockNotif
	 * @param blockNotif BlockNotif instance.
	 */
	private static void init(final BlockNotif blockNotif){

		blockNotif.saveDefaultConfig();
		thisPlugin = blockNotif;
		blockActionList = new BlockActionList();

		tntList = new TntList();
		inActionList = new ArrayList<>();
		logTask = new LogTask();
		blockNotif.getServer().getPluginManager().registerEvents(blockNotif, blockNotif);
		messagesTxt = new MessagesTxt();
		blockNotif.loadBlockNotifConfig();
		new ActionCleanUp().scheduleAction();
		debug = blockNotif.getConfig().getBoolean("Debug") ;
		if(debug){
			logInfo("Debug mod enabled. This mod disables bypass permissions.");
		}

	}

	public static BlockNotif getThisPlugin() {

		return thisPlugin;
	}

	public static BlockActionList getBlockActionList() {

		return blockActionList;
	}

	public static TntList getTntList() {

		return tntList;
	}

	public static List<String> getInActionList() {

		return inActionList;
	}

	public static MessagesTxt getMessagesTxt() {

		return messagesTxt ;
	}

	public static LogTask getLogTask() {

		return logTask ;
	}

	public static boolean getDebugState(){

		return debug ;
	}

	/**
	 * Logging a text as a warning.
	 * @param text Text to display
	 */
	public static void logWarn(final String text) {
		Bukkit.getLogger().warning(() -> "[BlockNotif] {}" + text);
	}

	/**
	 * Logging a text as an information.
	 * @param text Text to display
	 */

	public static void logInfo(final String text) {
		Bukkit.getLogger().info(() -> "[BlockNotif] " + text);
	}

	private void loadBlockNotifConfig() {

		this.reloadConfig();
		messagesTxt.loadMessages();
		blockBreakList = getBlockDataList("ActionListen.BlockBreak", BlockDataType.BLOCK);
		blockPlaceList = getBlockDataList("ActionListen.BlockPlace", BlockDataType.BLOCK);
		blockIgniteList = getBlockDataList("ActionListen.BlockIgnite", BlockDataType.BLOCK);
		bucketUseList = getBlockDataList("ActionListen.BucketUse", BlockDataType.BLOCK);
		entityKillList = getBlockDataList("ActionListen.EntityKill", BlockDataType.ENTITY);
		watchTntExplode = this.getConfig().getBoolean("ActionListen.TntExplode", true);
		blockBreakPreventList = getBlockDataList("ActionPrevent.BlockBreak", BlockDataType.BLOCK);
		blockPlacePreventList = getBlockDataList("ActionPrevent.BlockPlace", BlockDataType.BLOCK);
		blockIgnitePreventList = getBlockDataList("ActionPrevent.BlockIgnite", BlockDataType.BLOCK);
		bucketUsePreventList = getBlockDataList("ActionPrevent.BucketUse", BlockDataType.BLOCK);
		entityKillPreventList = getBlockDataList("ActionPrevent.EntityKill", BlockDataType.ENTITY);


		logTask.setLogEnable(this.getConfig().getBoolean("LogFile"));
	}

	private TreeSetAll<BlockData> getBlockDataList(final String strPath, final BlockDataType blockDataType) {

		final TreeSetAll<BlockData> bd = new TreeSetAll<>();
		final List<String> str = this.getConfig().getStringList(strPath);

		for (final String value : str) {
			if ("0".equals(value) || "*".equals(value)) {
				bd.setIsAll(true);
			} else {
				try {
					bd.add(new BlockData(blockDataType, value));
				} catch (NullPointerException ex) {
					this.getLogger().log(Level.WARNING, "In config.yml, {0}: {1} is invalid!",
							new Object[]{strPath, value});
					this.getLogger().log(Level.FINE, ex.getMessage(), ex);
				}
			}
		}

		return bd;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {

		int pageNumber = 1;

		if ("blocknotif".equalsIgnoreCase(cmd.getName()) || "bn".equalsIgnoreCase(cmd.getName())) {
			if (args.length != 0) {
				if (args.length >= 2) {

					try {
						pageNumber = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex) {
						this.getLogger().log(Level.FINE, ex.getMessage(), ex);
					}
				}
				new ShowActionList(sender, args[0], pageNumber).show();
			} else {
				sender.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_SPECIFYPLAYER,
						null, null));
			}

			return true;

		} else if ("blocknotifreload".equalsIgnoreCase(cmd.getName()) || "bnreload".equalsIgnoreCase(cmd.getName())) {

			loadBlockNotifConfig();
			sender.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_RELOAD, null, null));

			return true;

		} else {
			return false;
		}

	}

	// === For action MONITOR ===
	/**
	 * BlockBreakEvent event.
	 * @param event BlockBreakEvent event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {

		// A user breaks a block
		final BlockData bd = new BlockData(event.getBlock());

		if (blockBreakList.contains(bd)
				&& (debug || !Permission.playerHasPermission(event.getPlayer(), "blocknotif.ignore.break." + event.getBlock().getType().name()))) {

			blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
					MessagesTxt.DESTROY, event.getBlock().getLocation(), bd);
		}

		if (blockBreakPreventList.contains(bd)
				&& (debug || !Permission.playerHasPermission(event.getPlayer(),"blocknotif.allow.break." + event.getBlock().getType().name()))) {

			event.setCancelled(true);
			event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
		}
	}

	/**
	 * BlockPlaceEvent event.
	 * @param event BlockPlaceEvent event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {

		// A user places a block
		final BlockData bd = new BlockData(event.getBlock());

		if (blockPlaceList.contains(bd)
				&& (debug || !Permission.playerHasPermission(event.getPlayer(), "blocknotif.ignore.place." + event.getBlock().getType().name()))) {

			blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
					MessagesTxt.PLACE, event.getBlock().getLocation(), bd);
		}

		// A user places TNT for TNT list
		if (watchTntExplode && event.getBlock().getType().equals(Material.TNT)) {

			tntList.addAction(Calendar.getInstance(), event.getPlayer(),
					event.getBlock().getLocation());
		}

		if (blockPlacePreventList.contains(bd)
				&& (debug || !Permission.playerHasPermission(event.getPlayer(),"blocknotif.allow.place." + event.getBlock().getType().name()))) {

			event.setCancelled(true);
			event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
		}
	}

	/**
	 * BlockIgniteEvent event.
	 * @param event BlockIgniteEvent event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event) {

		if(event.getPlayer() != null){

			//A user ignites a block
			final BlockData bd = new BlockData(event.getPlayer().getTargetBlock(null, 10));

			if ((event.getCause() == IgniteCause.FLINT_AND_STEEL || event.getCause() == IgniteCause.FIREBALL)
					&& blockIgniteList.contains(bd)
					&& (debug || !Permission.playerHasPermission(event.getPlayer(), "blocknotif.ignore.ignite." + event.getBlock().getType().name()))) {

				blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
						MessagesTxt.IGNITE,
						event.getPlayer().getTargetBlock(null, 10).getLocation(), bd);
			}

			if (event.getCause() == IgniteCause.FLINT_AND_STEEL
					&& blockIgnitePreventList.contains(bd)
					&& (debug || !Permission.playerHasPermission(event.getPlayer(),"blocknotif.allow.ignite." + event.getBlock().getType().name()))) {

				event.setCancelled(true);
				event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
			}

		}
	}

	/**
	 * PlayerBucketEmptyEvent event.
	 * @param event PlayerBucketEmptyEvent event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		// a user empty a bucket
		if (bucketUseList.contains(new BlockData(event.getBucket()))
				&& (debug || !Permission.playerHasPermission(event.getPlayer(),"blocknotif.ignore.bucket." + event.getBucket().name()))) {

			blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
					MessagesTxt.USEBUCKET, event.getBlockClicked().getLocation(),
					new BlockData(event.getBucket()));
		}

		// a user empty a bucket
		if (bucketUsePreventList.contains(new BlockData(event.getBucket()))
				&& (debug || !Permission.playerHasPermission(event.getPlayer(),"blocknotif.allow.bucket." + event.getBucket().name()))) {

			event.setCancelled(true);
			event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
		}
	}


	/**
	 * EntityExplodeEvent event.
	 * @param event EntityExplodeEvent event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplodeEvent(final EntityExplodeEvent event) {

		// TNT explode
		if (watchTntExplode && event.getEntity() != null
				&& (event.getEntityType().equals(EntityType.PRIMED_TNT) || event.getEntityType().equals(EntityType.MINECART_TNT))) {

			blockActionList.addAction(Calendar.getInstance(), tntList.getPlayer(event.getEntity().getLocation()),
					MessagesTxt.TNTEXPLODE, event.getEntity().getLocation(), new BlockData(Material.TNT));
		}

	}

	/**
	 * EntityDeathEvent event.
	 * @param event EntityDeathEvent event.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(final EntityDeathEvent event) {

		// A user kill an entity
		if (event.getEntity().getKiller() != null && entityKillList.contains(new BlockData(event.getEntityType()))
				&& (debug || !(event.getEntity().getKiller().hasPermission("blocknotif.ignore.kill." + event.getEntityType().name())
				|| event.getEntity().getKiller().hasPermission("blocknotif.ignore.kill.*")))) {
			blockActionList.addAction(Calendar.getInstance(), event.getEntity().getKiller(),
					MessagesTxt.ENTITYKILL, event.getEntity().getLocation(),
					new BlockData(event.getEntityType()));
		}
	}

	/**
	 * EntityDamageByEntityEvent event.
	 * @param event EntityDamageByEntityEvent event.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {

		Player damager = null;
		final Arrow damagerArrow;

		// A user kill an entity
		if (event.getDamager() instanceof Player) {

			damager = (Player) event.getDamager();

		} else if (event.getDamager().getType() == EntityType.ARROW) {

			damagerArrow = (Arrow) event.getDamager();

			if (damagerArrow.getShooter() instanceof Player) {
				damager = (Player) damagerArrow.getShooter();
			}

		}

		if (damager != null && entityKillPreventList.contains(new BlockData(event.getEntityType()))
				&& (debug || !Permission.playerHasPermission(damager,"blocknotif.allow.kill." + event.getEntityType().name()))) {

			event.setCancelled(true);
			damager.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
		}
	}

	/**
	 * InventoryDragEvent event.
	 * @param event InventoryDragEvent event.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onInventoryDragEvent(final InventoryDragEvent event) {

		// Bypass bug exploitation, dispensers
		final int maxInv = checkMaxInv(event.getInventory().getType());

		if (maxInv != 0) {

			for (final int slot : event.getNewItems().keySet()) {
				if (slot < maxInv) {

					final Player player = (Player) event.getWhoClicked();
					final Block bl = player.getTargetBlock(null, 10);
					final ItemStack item = event.getNewItems().get(slot);
					if (itemCheck(player, bl.getLocation(), new BlockData(item.getType()))) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	/**
	 * InventoryClickEvent event.
	 * @param event InventoryClickEvent event.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onInventoryClickEvent(final InventoryClickEvent event) {

		// Bypass bug exploitation, dispensers
		final int maxInv = checkMaxInv(event.getInventory().getType());

		if (maxInv != 0
				&& ((event.getRawSlot() < maxInv && (event.getAction() == InventoryAction.PLACE_ALL
				|| event.getAction() == InventoryAction.PLACE_ONE
				|| event.getAction() == InventoryAction.PLACE_SOME
				|| event.getAction() == InventoryAction.SWAP_WITH_CURSOR))
				|| (event.getRawSlot() >= maxInv && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY))) {

			final Player player = (Player) event.getWhoClicked();
			final Block bl = player.getTargetBlock(null, 10);
			final ItemStack item;

			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				item = event.getCurrentItem();
			} else {
				item = event.getCursor();
			}
			if (itemCheck(player, bl.getLocation(), new BlockData(item.getType()))) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Check the number of slot for inventory types
	 * @param it Inventory type.
 	 */
	private static int checkMaxInv(final InventoryType it) {

		if (it == InventoryType.DISPENSER) {
			return 9;
		}
		if (it == InventoryType.HOPPER) {
			return 5;
		}

		return 0;
	}

	private boolean itemCheck(final Player player, final Location location, final BlockData blockData) {

		// For prevent
		if (blockPlacePreventList.contains(blockData)
				&& ( debug || !Permission.playerHasPermission(player,"blocknotif.allow.place." + blockData.getName()))) {

			player.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));

			return true;

			// For notify
		} else if (blockPlaceList.contains(blockData)
				&& (debug || !Permission.playerHasPermission(player,"blocknotif.ignore.place." + blockData.getName()))) {

			blockActionList.addAction(Calendar.getInstance(), player,
					MessagesTxt.PLACE, location, blockData);
		}

		return false;
	}
}
