/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spout.player.SimplePlayerManager;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutPlayerListener implements Listener {
	public final PlayerManager manager = new PlayerManager();

	public SpoutPlayerListener(Spout plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (!event.getPlayer().getClass().equals(SpoutCraftPlayer.class)) {
			SpoutCraftPlayer.updateNetServerHandler(event.getPlayer());
			SpoutCraftPlayer.updateBukkitEntity(event.getPlayer());
			updatePlayerEvent(event);
			Spout.getInstance().authenticate(event.getPlayer());
			SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(event.getPlayer());

			//This forces EXISTING players to see the new player's skin, cape, and title
			player.setSkin(player.getSkin());
			player.setCape(player.getCape());
			player.setTitle(player.getTitle());
		}
		((SimplePlayerManager)SpoutManager.getPlayerManager()).onPlayerJoin(event.getPlayer());
		manager.onPlayerJoin(event.getPlayer());
		synchronized(Spout.getInstance().playersOnline) {
			Spout.getInstance().playersOnline.add((SpoutPlayer) event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getPlayer() instanceof SpoutCraftPlayer) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)event.getPlayer();
			if (event.getReason().equals("You moved too quickly :( (Hacking?)")) {
				if (player.canFly()) {
					event.setCancelled(true);
				}
				if (System.currentTimeMillis() < player.velocityAdjustmentTime) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (!(event.getPlayer() instanceof SpoutPlayer)) {
			updatePlayerEvent(event);
		}
		if (event.isCancelled()) {
			return;
		}

		Runnable update = null;
		final SpoutCraftPlayer scp = (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(event.getPlayer());

		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			update = new PostTeleport(scp);
		}
		else {
			update = new PostWorldTeleport(scp);
		}
		if (update != null) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Spout.getInstance(), update, 2);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!(event.getPlayer() instanceof SpoutPlayer)) {
			updatePlayerEvent(event);
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		SpoutCraftPlayer player = (SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(event.getPlayer());

		if (event.getClickedBlock() != null) {
			boolean action = false;

			switch (event.getClickedBlock().getType()) {
				case BREWING_STAND:
				case CHEST:
				case DISPENSER:
				case ENCHANTMENT_TABLE:
				case FURNACE:
				case WORKBENCH:
				case BED_BLOCK:
				case CAKE_BLOCK:
				case CAULDRON:
				case DIODE_BLOCK_OFF:
				case DIODE_BLOCK_ON:
				case FENCE_GATE:
				case IRON_DOOR_BLOCK:
				case LEVER:
				case NOTE_BLOCK:
				case STONE_BUTTON:
				case TRAP_DOOR:
				case WOODEN_DOOR:
				action = true;
				break;
			}

			if (event.hasItem() && !action) {
				SpoutBlock block = (SpoutBlock)event.getClickedBlock().getRelative(event.getBlockFace());

				if (event.getClickedBlock().getType() == Material.SNOW) {
					block = block.getRelative(0, -1, 0);
				}

				ItemStack item = event.getItem();
				int damage = item.getDurability();
				if (item.getType() == Material.FLINT && damage != 0) {

				SimpleMaterialManager mm = (SimpleMaterialManager)SpoutManager.getMaterialManager();

					if (!player.getEyeLocation().getBlock().equals(block) && !player.getLocation().getBlock().equals(block)) {

						CustomBlock cb = MaterialData.getCustomBlock(damage);


						if (cb != null && isReplaceable(block.getType())) {
							BlockState oldState = block.getState();
							block.setTypeIdAndData(cb.getBlockId(), (byte)(cb.getBlockData()), true);
							cb.onBlockPlace(block.getWorld(), block.getX(), block.getY(), block.getZ(), player);

							int rot = Math.round(player.getLocation().getYaw() + 45 % 360);
							if(rot < 0 ) rot += 360;
							rot = (2 - (rot/90)) % 4;
							if(rot < 0) rot += 4;
							byte rotation = cb.canRotate() ? (byte) rot : 0;
							mm.overrideBlock(block, cb, rotation);

							if (canPlaceAt(block, oldState, (SpoutBlock)event.getClickedBlock(), item, player)) {
								// Yay, take the item from inventory
								if (player.getGameMode() == GameMode.SURVIVAL) {
									if (item.getAmount() == 1) {
										event.getPlayer().setItemInHand(null);
									} else {
										item.setAmount(item.getAmount() - 1);
									}
								}
								player.updateInventory();
							} else {
								// Event cancelled or can't build
								mm.removeBlockOverride(block);
								block.setTypeIdAndData(oldState.getTypeId(), oldState.getRawData(), true);
							}
						}
					}
				}
			}
		}
	}

	private boolean isReplaceable(Material type) {
		return type == Material.FIRE || type == Material.SNOW || type == Material.AIR || type == Material.VINE || type == Material.DEAD_BUSH || type == Material.LONG_GRASS;
	}

	//TODO: canBuild should be set properly, CraftEventFactory.canBuild() would do this...
	//       but it's private so... here it is >.>
	private boolean canPlaceAt(SpoutBlock result, BlockState oldState, SpoutBlock clicked, ItemStack item, SpoutPlayer player) {
		int spawnRadius = Bukkit.getServer().getSpawnRadius();
		boolean canBuild = spawnRadius <= 0 || player.isOp();
		if (!canBuild) {
			Location spawn = clicked.getWorld().getSpawnLocation();
			if (Math.max(Math.abs(result.getX()-spawn.getBlockX()), Math.abs(result.getZ()-spawn.getBlockZ())) > spawnRadius) { // Slower check
				canBuild = true;
			}
		}

		BlockPlaceEvent placeEvent = new BlockPlaceEvent(result, oldState, clicked, item, player, canBuild);
		Bukkit.getPluginManager().callEvent(placeEvent);

		return !placeEvent.isCancelled() && placeEvent.canBuild();
	}

	private void updatePlayerEvent(PlayerEvent event) {
		try {
			Field player = PlayerEvent.class.getDeclaredField("player");
			player.setAccessible(true);
			player.set(event, (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(event.getPlayer()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		synchronized(Spout.getInstance().playersOnline) {
			Iterator<SpoutPlayer> i = Spout.getInstance().playersOnline.iterator();
			while (i.hasNext()) {
				String name = i.next().getName();
				if (name.equals(player.getName())) {
					i.remove();
				}
			}
		}
	}
}

class PostTeleport implements Runnable {
	SpoutCraftPlayer player;
	public PostTeleport(SpoutCraftPlayer player) {
		this.player = player;
	}

	@Override
	public void run() {
		player.updateAppearance();
	}
}

class PostWorldTeleport implements Runnable {
	SpoutCraftPlayer player;
	public PostWorldTeleport(SpoutCraftPlayer player) {
		this.player = player;
	}

	@Override
	public void run() {
		player.updateAppearance();
		player.updateWaypoints();
	}
}

