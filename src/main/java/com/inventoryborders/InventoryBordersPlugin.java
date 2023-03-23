package com.inventoryborders;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.w3c.dom.css.RGBColor;

import java.util.ArrayList;
@Slf4j
@PluginDescriptor(
	name = "Inventory Borders"
)
public class InventoryBordersPlugin extends Plugin
{
	@Inject
	private Client client;

	ArrayList<Widget> invBorderWidgets = new ArrayList<>();
	ArrayList<Widget> invSelectionWidgets = new ArrayList<>();

	boolean drawInvBorders = false;

	@Inject
	private InventoryBordersConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}


	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (invBorderWidgets.size() == 0 && drawInvBorders && client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER) != null){
			if (invBorderWidgets.size() == 0) {

				//widget constructor for inventory
				//coordinates represent the selection dots found at the corner of each item
				int currx = 0;
				int curry = 0;
				while (curry <= 7){
					while (currx <= 4){//TODO set a nice data structure where the lines adjacent to a selector can be found easily in the list
						//TODO the data structure also has to be dynamic for bank tabs which can vary in size
						//TODO for dynamic sizes, check the y position of the 1st bank slot and the y position of the varbit added to the starting index.  This will give you the bounds
						if (currx < 4) {
							//do right widget
							invBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER), (currx * 42) + 1, curry * 36, 42, 2));
							invSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER), (currx * 42) + 1, curry * 36, 42, 4,invBorderWidgets.size()-1));
						}
						if (curry < 7){
							//do down widget
							invBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER), currx * 42, (curry * 36) + 1, 2, 36));
							invSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER), currx * 42, (curry * 36) + 1, 4, 36,invBorderWidgets.size()-1));
						}
						currx++;
					}
					currx = 0;
					curry++;
				}


				/*
				//coolWidget.setTextColor(0x00ff00);
				//coolWidget.setOpacity(128);
				//coolWidget.setBorderType(2);
				//coolWidget.setFilled(false);
				//firstInvSlot = client.getWidget(WidgetInfo.INVENTORY).getChild(0);
				//coolWidget.setOriginalX(firstInvSlot.getOriginalX()-1);
				//coolWidget.setOriginalY(firstInvSlot.getOriginalY()-1);
				//coolWidget.setOriginalWidth(firstInvSlot.getOriginalWidth()+2);
				//coolWidget.setOriginalHeight(firstInvSlot.getOriginalHeight()+2);

				 */
			}
		}


	}
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.INVENTORY_GROUP_ID)
		{
			drawInvBorders = true;
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() == WidgetID.INVENTORY_GROUP_ID)
		{
			drawInvBorders = false;
		}
	}
	@Provides
	InventoryBordersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryBordersConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("inventoryborders"))
		{
			for (Widget tempWidget : invSelectionWidgets){
				tempWidget.setHidden(!config.EditMode());
			}
			for (Widget tempWidget : invBorderWidgets){
				tempWidget.setTextColor(config.BorderColor().getRGB());
				tempWidget.setOpacity(255-config.BorderColor().getAlpha());
			}
		}
	}


	public Widget makeBorderWidget (Widget parent, int x, int y, int width, int height){
		Widget tempWidget = parent.createChild(-1, WidgetType.RECTANGLE);
		tempWidget.setOriginalX(x+12);
		tempWidget.setOriginalY(y+5);
		tempWidget.setOriginalWidth(width);
		tempWidget.setOriginalHeight(height);
		tempWidget.setTextColor(config.BorderColor().getRGB());
		tempWidget.setOpacity(255-config.BorderColor().getAlpha());
		tempWidget.setHidden(true);
		return tempWidget;

	}

	public Widget makeSelectionWidget (Widget parent, int x, int y, int width, int height, int index){
	Widget tempWidget = parent.createChild(-1, WidgetType.RECTANGLE);
	tempWidget.setOriginalX(x+11);
	tempWidget.setOriginalY(y+4);
	tempWidget.setOriginalWidth(width);
	tempWidget.setOriginalHeight(height);
		tempWidget.setTextColor(0x777777);
		tempWidget.setOpacity(128);
		tempWidget.setFilled(true);
	tempWidget.setHidden(!config.EditMode());
		tempWidget.setAction(0, "Toggle");
	tempWidget.setHasListener(true);
	tempWidget.setOnOpListener((JavaScriptCallback)  e -> selectWidget(index));
	return tempWidget;

	}
	public void selectWidget(int index){
		client.playSoundEffect(SoundEffectID.UI_BOOP);
		invBorderWidgets.get(index).setHidden(!invBorderWidgets.get(index).isHidden());

	}


}
