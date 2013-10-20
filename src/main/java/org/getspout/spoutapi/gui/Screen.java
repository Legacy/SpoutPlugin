/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.getspout.spoutapi.gui;

import java.io.IOException;
import java.lang.NullPointerException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.io.SpoutInputStream;
import org.getspout.spoutapi.io.SpoutOutputStream;
import org.getspout.spoutapi.packet.PacketWidget;
import org.getspout.spoutapi.packet.PacketWidgetRemove;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * This defines the basic Screen, but should not be used directly.
 */
public abstract class Screen extends Widget {
	protected Map<Widget, Plugin> widgets = new ConcurrentHashMap<Widget, Plugin>();
	protected int playerId;
	protected boolean bg = true;

	public Screen() {
	}

	public Screen(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + 0;
	}

	/**
	 * Gets an array of all the attached widgets to this screen. Modifying this array will not affect the screen.
	 * @return array of all widgets
	 */
	public Widget[] getAttachedWidgets() {
		Widget[] list = new Widget[widgets.size()];
		widgets.keySet().toArray(list);
		return list;
	}

	/**
	 * Attaches a widget to this screen
	 * @param plugin that owns this widget
	 * @param widget to attach
	 * @return screen
	 */
	public Screen attachWidget(Plugin plugin, Widget widget) {
		if (plugin == null) throw new NullPointerException("Plugin can not be null!");
		if (widget == null) throw new NullPointerException("Widget can not be null!");
		widgets.put(widget, plugin);
		widget.setPlugin(plugin);
		widget.setDirty(true);
		widget.setScreen(plugin, this);
		return this;
	}

	/**
	 * Attaches a series of widgets to this screen in one call.
	 * @param plugin  that owns these widgets
	 * @param widgets to attach
	 * @return screen
	 */
	public Screen attachWidgets(Plugin plugin, Widget... widgets) {
		for (Widget widget : widgets) {
			attachWidget(plugin, widget);
		}
		return this;
	}

	/**
	 * Removes a widget from this screen
	 * @param widget to remove
	 * @return screen
	 */
	public Screen removeWidget(Widget widget) {
		SpoutPlayer player = SpoutManager.getPlayerFromId(playerId);
		if (player != null) {
			if (widgets.containsKey(widget)) {
				widgets.remove(widget);
				if (!widget.getType().isServerOnly()) {
					SpoutManager.getPlayerFromId(playerId).sendPacket(new PacketWidgetRemove(widget, getId()));
				}
				widget.setScreen(null, null);
			}
		}
		return this;
	}

	/**
	 * Removes all of a plugin's widgets from this screen
	 * @param widget to remove
	 * @return screen
	 */
	public Screen removeWidgets(Plugin p) {
		if (p != Bukkit.getServer().getPluginManager().getPlugin("Spout")) {
			for (Widget i : getAttachedWidgets()) {
				if (widgets.get(i) != null && widgets.get(i).equals(p)) {
					removeWidget(i);
				}
			}
		}
		return this;
	}

	/**
	 * Is true if the screen has the given widget attached to it. Uses a linear search, takes O(n) time to complete.
	 * @param widget to search for
	 * @return true if the widget was found
	 */
	public boolean containsWidget(Widget widget) {
		return containsWidget(widget.getId());
	}

	/**
	 * Is true if the screen has a widget with the given id attached to it. Uses a linear search, takes O(n) time to complete.
	 * @param id to search for
	 * @return true if the widget was found
	 */
	public boolean containsWidget(UUID id) {
		return getWidget(id) != null;
	}

	/**
	 * Gets the widget that is associated with the given id, or null if none was found
	 * @param id to search for
	 * @return widget, or null if none found.
	 */
	public Widget getWidget(UUID id) {
		for (Widget w : widgets.keySet()) {
			if (w.getId().equals(id)) {
				return w;
			}
		}
		return null;
	}

	/**
	 * Replaces any attached widget with the given widget's id with the new widget
	 * @param widget to replace with
	 * @return true if a widget was replaced
	 */
	public boolean updateWidget(Widget widget) {
		if (widgets.containsKey(widget)) {
			Plugin plugin = widgets.get(widget);
			widgets.remove(widget);
			widgets.put(widget, plugin);
			widget.setScreen(plugin, this);
			return true;
		}
		return false;
	}

	@Override
	public void onTick() {
		SpoutPlayer player = SpoutManager.getPlayerFromId(playerId);
		if (player != null) {
			// Create a copy because onTick may remove the widget
			Set<Widget> widgetCopy = new HashSet<Widget>(widgets.keySet());
			for (Widget widget : widgetCopy) {
				try {
					widget.onTick();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (Widget widget : widgets.keySet()) {
				try {
					widget.onAnimate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (Widget widget : widgets.keySet()) {
				if (widget.isDirty()) {
					if (!widget.hasSize()/* || !widget.hasPosition()*/) {
						String type = "Unknown";
						try {
							type = widget.getType().getWidgetClass().getSimpleName();
						} catch (Exception e) {
						}
						Logger.getLogger("Minecraft").log(Level.WARNING,
								type
										+ " belonging to " + widget.getPlugin().getDescription().getName()
										+ " does not have a default "
										+ (!widget.hasSize() ? "size" : "") + (!widget.hasSize() && !widget.hasPosition() ? " or " : "") + (!widget.hasPosition() ? "position" : "")
										+ "!");
						widget.setX(widget.getX());
						widget.setHeight(widget.getHeight());
					}
					if (!widget.getType().isServerOnly()) {
						player.sendPacket(new PacketWidget(widget, getId()));
					}
					widget.setDirty(false);
				}
			}
		}
	}

	/**
	 * Sets the visibility of the grey background. If true, it will render normally. If false, it will not appear on the screen.
	 * @param enable the visibility
	 * @return the screen
	 */
	public Screen setBgVisible(boolean enable) {
		bg = enable;
		return this;
	}

	/**
	 * Is true if this grey background is visible and rendering on the screen
	 * @return visible
	 */
	public boolean isBgVisible() {
		return bg;
	}

	/**
	 * Get the player the screen is attached to
	 * @return spout player
	 */
	public SpoutPlayer getPlayer() {
		return SpoutManager.getPlayerFromId(playerId);
	}

	@Override
	public void readData(SpoutInputStream input) throws IOException {
		super.readData(input);
		setBgVisible(input.readBoolean());
	}

	@Override
	public void writeData(SpoutOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isBgVisible());
	}

	@Override
	public void setDirty(boolean dirty) {
		super.setDirty(dirty);
		if (dirty) {
			for (Widget widget : getAttachedWidgets()) {
				widget.setDirty(true);
			}
		}
	}

	@Override
	public Widget copy() {
		throw new UnsupportedOperationException("You can not create a copy of a screen");
	}

	/**
	 * Gets the screen type of this screen
	 * @return the screen type
	 */
	abstract public ScreenType getScreenType();

	/**
	 * Get a list of all widgets that are attached to this screen
	 * @param recursive Also get popup screen widgets
	 * @return all widgets
	 */
	public Set<Widget> getAttachedWidgetsAsSet(boolean recursive) {
		Set<Widget> set = new HashSet<Widget>();
		for (Widget w : widgets.keySet()) {
			set.add(w);
			if (w instanceof Screen && recursive) {
				set.addAll(((Screen) w).getAttachedWidgetsAsSet(true));
			}
		}
		return set;
	}

	/**
	 * Called when the screen is closed.
	 *
	 * @param event
	 */
	public void onScreenClose(ScreenCloseEvent e) {
	}
}
