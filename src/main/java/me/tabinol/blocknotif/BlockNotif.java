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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import me.tabinol.blocknotif.blockactions.ActionCleanUp;
import me.tabinol.blocknotif.blockactions.BlockActionList;
import me.tabinol.blocknotif.confdata.BlockData;
import me.tabinol.blocknotif.confdata.BlockData.BlockDataType;
import me.tabinol.blocknotif.confdata.TreeSetAll;
import me.tabinol.blocknotif.tnt.TntList;
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
import org.bukkit.scheduler.BukkitRunnable;

public class BlockNotif extends JavaPlugin implements Listener {

    public static BlockActionList blockActionList;
    public TntList tntList;
    // List for action who are in progress
    // Stored "player:action:item"
    public List<String> inActionList;
    public MessagesTxt messagesTxt;
    public LogTask logTask;
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
    public LandAccess landAccess = null;
    private static BlockNotif thisPlugin;
    // for config
    public static int History_TimeBeforeNotify;
    public static boolean ActionListen_Creative;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        thisPlugin = this;
        blockActionList = new BlockActionList();
        tntList = new TntList();
        inActionList = new ArrayList<String>();
        logTask = new LogTask();
        getServer().getPluginManager().registerEvents(this, this);
        messagesTxt = new MessagesTxt();
        loadBlockNotifConfig();
        new ActionCleanUp().scheduleAction();
    }

    public static BlockNotif getThisPlugin() {

        return thisPlugin;
    }

    public static BlockActionList getBlockActionList() {

        return blockActionList;
    }

    public void loadBlockNotifConfig() {

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

        History_TimeBeforeNotify = getConfig().getInt("History.TimeBeforeNotify");
        ActionListen_Creative = getConfig().getBoolean("ActionListen.Creative");

        logTask.setLogEnable(this.getConfig().getBoolean("LogFile"));

        // Check for Flags plugin
        if (this.getConfig().getBoolean("ActionListen.ShowCuboidName")) {

            if (getServer().getPluginManager().getPlugin("Flags") != null) {

                landAccess = new LandAccess();
                this.getLogger().log(Level.INFO, "Flags Detected");

            } else {

                this.getLogger().log(Level.WARNING, "Flags is not loaded!");
            }
        }
    }

    private TreeSetAll<BlockData> getBlockDataList(String strPath, BlockDataType blockDataType) {

        TreeSetAll<BlockData> bd = new TreeSetAll<BlockData>();
        List<String> str = this.getConfig().getStringList(strPath);

        for (String value : str) {
            if (value.equals("0") || value.equals("*")) {
                bd.setIsAll(true);
            } else {
                try {
                    bd.add(new BlockData(blockDataType, value));
                } catch (Exception ex) {
                    this.getLogger().log(Level.WARNING, "In config.yml, {0}: {1} is invalid!",
                            new Object[]{strPath, value});
                }
            }
        }

        return bd;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        int pageNumber = 1;

        if (cmd.getName().equalsIgnoreCase("blocknotif") || cmd.getName().equalsIgnoreCase("bn")) {
            if (args.length != 0) {
                if (args.length >= 2) {

                    try {
                        pageNumber = Integer.parseInt(args[1]);
                    } catch (NumberFormatException E) {
                        pageNumber = 1;
                    }
                }
                new ShowActionList(sender, args[0], pageNumber).show();
            } else {
                sender.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_SPECIFYPLAYER,
                        null, null));
            }

            return true;

        } else if (cmd.getName().equalsIgnoreCase("blocknotifreload") || cmd.getName().equalsIgnoreCase("bnreload")) {

            loadBlockNotifConfig();
            sender.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_RELOAD, null, null));

            return true;

        } else {
            return false;
        }

    }

    // === For action MONITOR ===
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        // A user breaks a block
        BlockData bd;

        if (blockBreakList.contains(bd = new BlockData(event.getBlock()))
                && !(event.getPlayer().hasPermission("blocknotif.ignore.break." + event.getBlock().getTypeId())
                || event.getPlayer().hasPermission("blocknotif.ignore.break." + bd.toString())
                || event.getPlayer().hasPermission("blocknotif.ignore.break.*"))) {

            blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
                    MessagesTxt.DESTROY, event.getBlock().getLocation(), bd);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        // A user places a block
        BlockData bd;

        if (event.getBlock().getType() == Material.SKULL) {

            // If it is skull, delay task (to resolve a bug)
            new DelayBlockPlace(Calendar.getInstance(), event.getPlayer(),
                    MessagesTxt.PLACE, event.getBlock().getLocation());

        } else {
            if (blockPlaceList.contains(bd = new BlockData(event.getBlock()))
                    && !(event.getPlayer().hasPermission("blocknotif.ignore.place." + event.getBlock().getTypeId())
                    || event.getPlayer().hasPermission("blocknotif.ignore.place." + bd.toString())
                    || event.getPlayer().hasPermission("blocknotif.ignore.place.*"))) {

                blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
                        MessagesTxt.PLACE, event.getBlock().getLocation(), bd);
            }
        }

        // A user places TNT for TNT list
        if (watchTntExplode && event.getBlock().getTypeId() == 46) {

            tntList.addAction(Calendar.getInstance(), event.getPlayer(),
                    event.getBlock().getLocation());
        }
    }

    // For Skull, create a separate class for delay
    private class DelayBlockPlace extends BukkitRunnable {

        Calendar calendar;
        Player player;
        int action;
        Location location;

        public DelayBlockPlace(Calendar calendar, Player player, int action,
                Location location) {

            this.calendar = calendar;
            this.player = player;
            this.action = action;
            this.location = location;

            this.runTaskLater(BlockNotif.getThisPlugin(), 10);
        }

        public void run() {

            BlockData bd;

            if (blockPlaceList.contains(bd = new BlockData(location.getBlock()))
                    && !(player.hasPermission("blocknotif.ignore.place." + bd.getItemID())
                    || player.hasPermission("blocknotif.ignore.place." + bd.toString())
                    || player.hasPermission("blocknotif.ignore.place.*"))) {

                BlockNotif.getBlockActionList().addAction(calendar, player, action, location, bd);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        //A user ignites a block
        BlockData bd;
        if (event.getCause() == IgniteCause.FLINT_AND_STEEL
                && blockIgniteList.contains(bd = new BlockData(event.getPlayer().getTargetBlock((HashSet<Byte>) null, 10)))
                && event.getPlayer() != null
                && !(event.getPlayer().hasPermission("blocknotif.ignore.ignite." + event.getPlayer().getTargetBlock((HashSet<Byte>) null, 10).getTypeId())
                || event.getPlayer().hasPermission("blocknotif.ignore.ignite." + bd.toString())
                || event.getPlayer().hasPermission("blocknotif.ignore.ignite.*"))) {

            blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
                    MessagesTxt.IGNITE,
                    event.getPlayer().getTargetBlock((HashSet<Byte>) null, 10).getLocation(), bd);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        // a user empty a bucket
        if (bucketUseList.contains(new BlockData(BlockDataType.BLOCK, event.getBucket().getId()))
                && !(event.getPlayer().hasPermission("blocknotif.ignore.bucket." + event.getBucket().getId())
                || event.getPlayer().hasPermission("blocknotif.ignore.bucket.*"))) {

            blockActionList.addAction(Calendar.getInstance(), event.getPlayer(),
                    MessagesTxt.USEBUCKET, event.getBlockClicked().getLocation(),
                    new BlockData(event.getBucket()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplodeEvent(EntityExplodeEvent event) {

        // TNT explode
        if (watchTntExplode && event.getEntity() != null
                && (event.getEntityType().getTypeId() == 20 || event.getEntityType().getTypeId() == 45)) {

            blockActionList.addAction(Calendar.getInstance(), tntList.getPlayer(event.getEntity().getLocation()),
                    MessagesTxt.TNTEXPLODE, event.getEntity().getLocation(), new BlockData(Material.TNT));
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {

        // A user kill an entity
        if (event.getEntity().getKiller() != null && entityKillList.contains(new BlockData(event.getEntityType()))
                && !(event.getEntity().getKiller().hasPermission("blocknotif.ignore.kill." + event.getEntityType().getTypeId())
                || event.getEntity().getKiller().hasPermission("blocknotif.ignore.kill.*"))) {
            blockActionList.addAction(Calendar.getInstance(), event.getEntity().getKiller(),
                    MessagesTxt.ENTITYKILL, event.getEntity().getLocation(),
                    new BlockData(event.getEntityType()));
        }
    }

    // === For actions CANCEL ===
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak2(BlockBreakEvent event) {

        // A user breaks a block
        BlockData bd;
        if (blockBreakPreventList.contains(bd = new BlockData(event.getBlock()))
                && !(event.getPlayer().hasPermission("blocknotif.allow.break." + event.getBlock().getTypeId())
                || event.getPlayer().hasPermission("blocknotif.allow.break." + bd.toString())
                || event.getPlayer().hasPermission("blocknotif.allow.break.*")
                || event.getPlayer().hasPermission("blocknotif.allowall"))) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace2(BlockPlaceEvent event) {

        // A user places a block
        BlockData bd;
        if (blockPlacePreventList.contains(bd = new BlockData(event.getBlock()))
                && !(event.getPlayer().hasPermission("blocknotif.allow.place." + event.getBlock().getTypeId())
                || event.getPlayer().hasPermission("blocknotif.allow.place." + bd.toString())
                || event.getPlayer().hasPermission("blocknotif.allow.place.*")
                || event.getPlayer().hasPermission("blocknotif.allowall"))) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite2(BlockIgniteEvent event) {

        //A user ignites a block
        BlockData bd;
        if (event.getCause() == IgniteCause.FLINT_AND_STEEL
                && blockIgnitePreventList.contains(bd = new BlockData(event.getPlayer().getTargetBlock((HashSet<Byte>) null, 10)))
                && !(event.getPlayer().hasPermission("blocknotif.allow.ignite." + event.getPlayer().getTargetBlock((HashSet<Byte>) null, 10).getTypeId())
                || event.getPlayer().hasPermission("blocknotif.allow.ignite." + bd.toString())
                || event.getPlayer().hasPermission("blocknotif.allow.ignite.*")
                || event.getPlayer().hasPermission("blocknotif.allowall"))) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty2(PlayerBucketEmptyEvent event) {

        // a user empty a bucket
        if (bucketUsePreventList.contains(new BlockData(event.getBucket()))
                && !(event.getPlayer().hasPermission("blocknotif.allow.bucket." + event.getBucket().getId())
                || event.getPlayer().hasPermission("blocknotif.allow.bucket.*")
                || event.getPlayer().hasPermission("blocknotif.allowall"))) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {

        Player damager = null;
        Arrow damagerArrow;

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
                && !(damager.hasPermission("blocknotif.allow.kill." + event.getEntityType().getTypeId())
                || damager.hasPermission("blocknotif.allow.kill.*")
                || damager.hasPermission("blocknotif.allowall"))) {

            event.setCancelled(true);
            damager.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDragEvent(InventoryDragEvent event) {

        // Bypass bug exploitation, dispensers
        int maxInv = checkMaxInv(event.getInventory().getType());

        if (maxInv != 0) {

            for (int slot : event.getNewItems().keySet()) {
                if (slot < maxInv) {

                    Player player = (Player) event.getWhoClicked();
                    Block bl = player.getTargetBlock((HashSet<Byte>) null, 10);
                    ItemStack item = event.getNewItems().get(slot);
                    if (itemCheck(player, bl.getLocation(), new BlockData(item.getType()))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {

        // Bypass bug exploitation, dispensers
        int maxInv = checkMaxInv(event.getInventory().getType());

        if (maxInv != 0
                && ((event.getRawSlot() < maxInv && (event.getAction() == InventoryAction.PLACE_ALL
                || event.getAction() == InventoryAction.PLACE_ONE
                || event.getAction() == InventoryAction.PLACE_SOME
                || event.getAction() == InventoryAction.SWAP_WITH_CURSOR))
                || (event.getRawSlot() >= maxInv && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY))) {

            Player player = (Player) event.getWhoClicked();
            Block bl = player.getTargetBlock((HashSet<Byte>) null, 10);
            ItemStack item;

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

    // Check the number of slot for inventory types
    private int checkMaxInv(InventoryType it) {

        if (it == InventoryType.DISPENSER) {
            return 9;
        }
        if (it == InventoryType.HOPPER) {
            return 5;
        }

        return 0;
    }

    private boolean itemCheck(Player player, Location location, BlockData blockData) {

        // For prevent
        if (blockPlacePreventList.contains(blockData)
                && !(player.hasPermission("blocknotif.allow.place." + blockData.getItemID())
                || player.hasPermission("blocknotif.allow.place." + blockData.toString())
                || player.hasPermission("blocknotif.allow.place.*")
                || player.hasPermission("blocknotif.allowall"))) {

            player.sendMessage(messagesTxt.getMessage(MessagesTxt.MESSAGE_NOPERMISSION, null, null));

            return true;

            // For notify
        } else if (blockPlaceList.contains(blockData)
                && !(player.hasPermission("blocknotif.ignore.place." + blockData.getItemID())
                || player.hasPermission("blocknotif.ignore.place." + blockData.toString())
                || player.hasPermission("blocknotif.ignore.place.*"))) {

            blockActionList.addAction(Calendar.getInstance(), player,
                    MessagesTxt.PLACE, location, blockData);
        }

        return false;
    }
}
