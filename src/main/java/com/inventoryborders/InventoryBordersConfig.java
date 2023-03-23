package com.inventoryborders;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("inventoryborders")
public interface InventoryBordersConfig extends Config
{
	@ConfigItem(
			keyName = "EditMode",
			name = "Edit Mode",
			description = "click a border to toggle it!",
			position = 0
	)
	default boolean EditMode()
	{
		return false;
	}
	@Alpha
	@ConfigItem(
			keyName = "BorderColor",
			name = "Border Color",
			description = "The color borders will be.",
			position = 1
	)
	default Color BorderColor()
	{
		return new Color(0x80eeeeee, true);
	}
}
