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
			description = "click a border to edit it!",
			position = 0
	)
	default boolean EditMode()
	{
		return false;
	}
	@ConfigItem(
			keyName = "EditTool",
			name = "Edit Tool",
			description = "Determines what edit mode will do when you click a border.",
			position = 1
	)
	default EditTool EditTool()
	{
		return EditTool.DRAW_PRIMARY;
	}
	@Alpha
	@ConfigItem(
			keyName = "BorderColorPrimary",
			name = "Primary Border Color",
			description = "The color borders will be.",
			position = 3
	)
	default Color BorderColorPrimary()
	{
		return new Color(0x80eeeeee, true);
	}
	@Alpha
	@ConfigItem(
			keyName = "BorderColorSecondary",
			name = "Secondary Border Color",
			description = "The color borders will be.",
			position = 4
	)
	default Color BorderColorSecondary()
	{
		return new Color(0x40eeeeee, true);
	}
	@Alpha
	@ConfigItem(
			keyName = "BorderColorTertiary",
			name = "Tertiary Border Color",
			description = "The color borders will be.",
			position = 5
	)
	default Color BorderColorTertiary()
	{
		return new Color(0x80ff0000, true);
	}
	@ConfigItem(
			keyName = "InventoryBorderStates",
			name = "",
			description = "",
			hidden = true

	)
	default String InventoryBorderStates()
	{
		return "111110000000000122";
	}
	@ConfigItem(
			keyName = "InventoryBorderStates",
			name = "",
			description = "",
			hidden = true

	)
	void setInventoryBorderStates(String borderStates);
}
