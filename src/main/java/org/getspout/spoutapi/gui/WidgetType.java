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

import java.util.HashMap;

/**
 * This defines the built-in Spout widgets.
 */
public class WidgetType {
	private static HashMap<WidgetType, Integer> lookupClass = new HashMap<WidgetType, Integer>();
	private static HashMap<Integer, WidgetType> lookupId = new HashMap<Integer, WidgetType>();
	private static int lastId = 0;
	public static WidgetType Label = new WidgetType(Label.class, 0);
	public static WidgetType HealthBar = new WidgetType(HealthBar.class, 1);
	public static WidgetType BubbleBar = new WidgetType(BubbleBar.class, 2);
	public static WidgetType ChatBar = new WidgetType(ChatBar.class, 3);
	public static WidgetType ChatTextBox = new WidgetType(ChatTextBox.class, 4);
	public static WidgetType ArmorBar = new WidgetType(ArmorBar.class, 5);
	public static WidgetType Texture = new WidgetType(Texture.class, 6);
	public static WidgetType PopupScreen = new WidgetType(PopupScreen.class, 7);
	public static WidgetType InGameScreen = new WidgetType(null, 8);
	public static WidgetType ItemWidget = new WidgetType(ItemWidget.class, 9);
	public static WidgetType Button = new WidgetType(Button.class, 10);
	public static WidgetType Slider = new WidgetType(Slider.class, 11);
	public static WidgetType TextField = new WidgetType(TextField.class, 12);
	public static WidgetType Gradient = new WidgetType(Gradient.class, 13);
	public static WidgetType Container = new WidgetType(Container.class, 14, true);
	public static WidgetType EntityWidget = new WidgetType(EntityWidget.class, 15);
	public static WidgetType OverlayScreen = new WidgetType(OverlayScreen.class, 16);
	public static WidgetType HungerBar = new WidgetType(HungerBar.class, 17);
	public static WidgetType ExpBar = new WidgetType(ExpBar.class, 18);
	public static WidgetType CheckBox = new WidgetType(CheckBox.class, 19);
	public static WidgetType RadioButton = new WidgetType(RadioButton.class, 20);
	public static WidgetType ListWidget = new WidgetType(ListWidget.class, 21);
	//public static WidgetType DirtBackground = new WidgetType(DirtBackground.class, 22); Only on the client :P
	//public static WidgetType ScrollArea = new WidgetType(GenericScrollArea.class, 23); Not yet :P
	public static WidgetType ComboBox = new WidgetType(ComboBox.class, 25);
	public static WidgetType Slot = new WidgetType(Slot.class, 27);
	private final int id;
	private final boolean server;
	private final Class<? extends Widget> widgetClass;

	public WidgetType(Class<? extends Widget> widget) {
		widgetClass = widget;
		id = lastId;
		lastId++;
		lookupClass.put(this, id);
		lookupId.put(id, this);
		server = false;
	}

	private WidgetType(Class<? extends Widget> widget, int id) {
		widgetClass = widget;
		this.id = id;
		if (id > lastId) {
			lastId = id;
		}
		lookupClass.put(this, id);
		lookupId.put(id, this);
		server = false;
	}

	private WidgetType(Class<? extends Widget> widget, int id, boolean server) {
		widgetClass = widget;
		this.id = id;
		if (id > lastId) {
			lastId = id;
		}
		lookupClass.put(this, id);
		lookupId.put(id, this);
		this.server = server;
	}

	public int getId() {
		return id;
	}

	public Class<? extends Widget> getWidgetClass() {
		return widgetClass;
	}

	public static Integer getWidgetId(Class<? extends Widget> widget) {
		return lookupClass.get(widget);
	}

	public static WidgetType getWidgetFromId(int id) {
		return lookupId.get(id);
	}

	public static int getNumWidgetTypes() {
		return lastId;
	}

	public boolean isServerOnly() {
		return server;
	}
}
