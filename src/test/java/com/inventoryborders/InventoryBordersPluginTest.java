package com.inventoryborders;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class InventoryBordersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(InventoryBordersPlugin.class);
		RuneLite.main(args);
	}
}