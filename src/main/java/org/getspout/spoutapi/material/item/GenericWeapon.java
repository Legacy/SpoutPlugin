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
package org.getspout.spoutapi.material.item;

import org.getspout.spoutapi.material.Weapon;

public class GenericWeapon extends GenericItem implements Weapon {
	public GenericWeapon(String name, int id) {
		super(name, id);
	}
	
	public Weapon setMaxDurability(short durability) {
		return this;
	}
	
	public short getMaxDurability() {
		return 0;
	}
	
	public int getDamage() {
		return 0;
	}
	
	public Weapon setDamage(int damage) {
		return this;
	}
	
	public int getAttackSpeed() {
		return 0;
	}
	
	public Weapon setAttackSpeed(int speed) {
		return this;
	}
	
	public boolean isBlockFlag() {
		return false;
	}
	
	public Weapon setBlockFlag(boolean canBlock) {
		return this;
	}
}
