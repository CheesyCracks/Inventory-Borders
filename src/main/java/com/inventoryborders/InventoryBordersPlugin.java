package com.inventoryborders;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

//TODO Fix for other inventory modes

@Slf4j
@PluginDescriptor(
	name = "Inventory Borders"
)
public class InventoryBordersPlugin extends Plugin
{
	@Inject
	private Client client;

	ArrayList<Widget> invBorderWidgets = new ArrayList<>();
	ArrayList<BorderState> invBorderState = new ArrayList<>();
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
							invBorderState.add(BorderState.ERASED);
						}
						if (curry < 7){
							//do down widget
							invBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER), currx * 42, (curry * 36) + 1, 2, 36));
							invSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_CONTAINER), currx * 42, (curry * 36) + 1, 4, 36,invBorderWidgets.size()-1));
							invBorderState.add(BorderState.ERASED);
						}
						currx++;
					}
					currx = 0;
					curry++;
				}
				loadStatesFromConfig();
				loadAllWidgetStates();
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
			loadAllWidgetStates();
		}
	}

	public void loadWidgetState(int widgetIndex){
		invBorderWidgets.get(widgetIndex).setHidden(false);
		if (invBorderState.get(widgetIndex) == BorderState.PRIMARY){
			invBorderWidgets.get(widgetIndex).setTextColor(config.BorderColorPrimary().getRGB());
			invBorderWidgets.get(widgetIndex).setOpacity(255-config.BorderColorPrimary().getAlpha());
		}
		else if (invBorderState.get(widgetIndex) == BorderState.SECONDARY){
			invBorderWidgets.get(widgetIndex).setTextColor(config.BorderColorSecondary().getRGB());
			invBorderWidgets.get(widgetIndex).setOpacity(255-config.BorderColorSecondary().getAlpha());
		}
		else if (invBorderState.get(widgetIndex) == BorderState.TERTIARY){
			invBorderWidgets.get(widgetIndex).setTextColor(config.BorderColorTertiary().getRGB());
			invBorderWidgets.get(widgetIndex).setOpacity(255-config.BorderColorTertiary().getAlpha());
		}
		else if (invBorderState.get(widgetIndex) == BorderState.ERASED){
			invBorderWidgets.get(widgetIndex).setHidden(true);
		}
	}

	public void loadAllWidgetStates(){
		for (int i = 0; i < invBorderWidgets.size(); i ++){
			loadWidgetState(i);
		}
	}


	public Widget makeBorderWidget (Widget parent, int x, int y, int width, int height){
		Widget tempWidget = parent.createChild(-1, WidgetType.RECTANGLE);
		tempWidget.setOriginalX(x+12);
		tempWidget.setOriginalY(y+5);
		tempWidget.setOriginalWidth(width);
		tempWidget.setOriginalHeight(height);
		tempWidget.setHidden(false);
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
		if (config.EditTool() == EditTool.DRAW_PRIMARY){
			invBorderState.set(index,BorderState.PRIMARY);
			loadWidgetState(index);
		}else if (config.EditTool() == EditTool.DRAW_SECONDARY){
			invBorderState.set(index,BorderState.SECONDARY);
			loadWidgetState(index);
		}else if (config.EditTool() == EditTool.DRAW_TERTIARY){
			invBorderState.set(index,BorderState.TERTIARY);
			loadWidgetState(index);
		}else if (config.EditTool() == EditTool.ERASE){
			invBorderState.set(index,BorderState.ERASED);
			loadWidgetState(index);
		}else if (config.EditTool() == EditTool.SHIFT_UP){
			//TODO use modulus to get the start of the row,add the amount of elements in a row to get copy index, add row length to get paste index, copy values from copy index (or blank if copy index is out of range) to paste index
			loadAllWidgetStates();
		}else if (config.EditTool() == EditTool.SHIFT_DOWN){
			//TODO above
			loadAllWidgetStates();
		}
		saveStatesToConfig();
	}

	public void saveStatesToConfig(){
		String temp = "";
		for (BorderState bs : invBorderState){
			if (bs == BorderState.PRIMARY) temp += "0";
			if (bs == BorderState.SECONDARY) temp += "1";
			if (bs == BorderState.TERTIARY) temp += "2";
			if (bs == BorderState.ERASED) temp += "3";
		}
		config.setInventoryBorderStates(StringUtils.stripEnd(temp,"3"));
	}

	public void loadStatesFromConfig(){
		if (config.InventoryBorderStates() != null){
			char[] stateIn = config.InventoryBorderStates().toCharArray();

			for (int i = 0; i < stateIn.length; i++){
				if (stateIn[i] == '0') invBorderState.set(i, BorderState.PRIMARY);
				else if (stateIn[i] == '1') invBorderState.set(i, BorderState.SECONDARY);
				else if (stateIn[i] == '2') invBorderState.set(i, BorderState.TERTIARY);
				else invBorderState.set(i, BorderState.ERASED);
			}
		}
	}

}
