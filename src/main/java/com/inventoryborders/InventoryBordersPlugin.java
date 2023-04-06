package com.inventoryborders;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.math.BigInteger;

@Slf4j
@PluginDescriptor(
	name = "Inventory Borders"
)
public class InventoryBordersPlugin extends Plugin
{
	@Inject
	private Client client;
	AllInventoryBorders data = new AllInventoryBorders();
	String currInvTab = "";
	@Inject
	private InventoryBordersConfig config;

	@Override
	protected void startUp() throws Exception
	{
		loadStatesFromConfig();
	}

	@Override
	public void resetConfiguration(){
		loadStatesFromConfig();
		updateAllWidgets();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.INVENTORY_GROUP_ID) {
			//widget constructor for inventory
			//coordinates represent the selection dots found at the corner of each item
			for (int curry = 0; curry <= 7; curry++) {
				for (int currx = 0; currx <= 4; currx++) {
					if (curry < 7) {
						//do down widget
						data.character.invBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.INVENTORY), currx * 42, (curry * 36) + 1, 2, 36));
						data.character.invSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.INVENTORY), currx * 42, (curry * 36) + 1, 4, 36, data.character.invBorderWidgets.size() - 1));
					}
					if (currx < 4) {
						//do right widget
						data.character.invBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.INVENTORY), (currx * 42) + 1, curry * 36, 42, 2));
						data.character.invSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.INVENTORY), (currx * 42) + 1, curry * 36, 42, 4, data.character.invBorderWidgets.size() - 1));
					}
				}
			}
			updateAllWidgets();
		}
		if (event.getGroupId() == WidgetID.BANK_INVENTORY_GROUP_ID)
		{
			//log.info("");
			//widget constructor for inventory
			//coordinates represent the corners of items, starting in the top left
			for (int curry = 0; curry <= 7; curry++) {
				for (int currx = 0; currx <= 4; currx++) {
					if (curry < 7){
						//do down widget
						data.character.bankInvBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER), currx * 42, (curry * 36) + 1, 2, 36));
						data.character.bankInvSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER), currx * 42, (curry * 36) + 1, 4, 36,data.character.bankInvBorderWidgets.size()-1));
					}
					if (currx < 4) {
						//do right widget
						data.character.bankInvBorderWidgets.add(makeBorderWidget(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER), (currx * 42) + 1, curry * 36, 42, 2));
						data.character.bankInvSelectionWidgets.add(makeSelectionWidget(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER), (currx * 42) + 1, curry * 36, 42, 4,data.character.bankInvBorderWidgets.size()-1));
					}
				}
			}
			updateAllWidgets();
		}
		if(event.getGroupId() == WidgetID.BANK_GROUP_ID){
			log.info("Title text is "+client.getWidget(WidgetInfo.BANK_TITLE_BAR).getText());
			//TODO for dynamic bank sizes, check the y position of the 1st bank slot and the y position of the varbit added to the starting index.  This will give you the bounds
		}

	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() == WidgetID.INVENTORY_GROUP_ID)
		{
			data.character.invBorderWidgets.clear();
			data.character.invSelectionWidgets.clear();
		}
		if (event.getGroupId() == WidgetID.BANK_INVENTORY_GROUP_ID)
		{
			data.character.bankInvBorderWidgets.clear();
			data.character.bankInvSelectionWidgets.clear();
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
			if (event.getKey().equals("EditMode")){
				for (Widget tempWidget : data.character.invSelectionWidgets) tempWidget.setHidden(!config.EditMode());
				for (Widget tempWidget : data.character.bankInvSelectionWidgets) tempWidget.setHidden(!config.EditMode());
				if (!config.EditMode()){
					saveStatesToConfig();
				}
			}
			else updateAllWidgets();
		}
	}
	public void updateWidget(int widgetIndex, int RGB, int opacity,boolean hidden){
		if (data.character.invBorderWidgets.size() > widgetIndex) {
			data.character.invBorderWidgets.get(widgetIndex).setHidden(hidden);
			data.character.invBorderWidgets.get(widgetIndex).setTextColor(RGB);
			data.character.invBorderWidgets.get(widgetIndex).setOpacity(opacity);
		}
		if (data.character.bankInvBorderWidgets.size() > widgetIndex) {
			data.character.bankInvBorderWidgets.get(widgetIndex).setHidden(hidden);
			data.character.bankInvBorderWidgets.get(widgetIndex).setTextColor(RGB);
			data.character.bankInvBorderWidgets.get(widgetIndex).setOpacity(opacity);
		}
	}
	public void updateWidget(int widgetIndex){
		if (data.character.invBorderState.get(widgetIndex) == BorderState.PRIMARY){
			updateWidget(widgetIndex, config.BorderColorPrimary().getRGB(), 255-config.BorderColorPrimary().getAlpha(), false);
		}
		else if (data.character.invBorderState.get(widgetIndex) == BorderState.SECONDARY){
			updateWidget(widgetIndex, config.BorderColorSecondary().getRGB(), 255-config.BorderColorSecondary().getAlpha(), false);
		}
		else if (data.character.invBorderState.get(widgetIndex) == BorderState.TERTIARY){
			updateWidget(widgetIndex, config.BorderColorTertiary().getRGB(), 255-config.BorderColorTertiary().getAlpha(), false);
		}
		else if (data.character.invBorderState.get(widgetIndex) == BorderState.ERASED){
			updateWidget(widgetIndex, 0x000000, 0, true);
			updateWidget(widgetIndex, 0x000000, 0, true);
		}
	}

	public void updateAllWidgets(){
		for (int i = 0; i < data.character.invBorderWidgets.size(); i ++){
			updateWidget(i);
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
		tempWidget.setOpacity(192);
		tempWidget.setFilled(true);
		tempWidget.setHidden(!config.EditMode());
		tempWidget.setAction(0, "Toggle");
		tempWidget.setHasListener(true);
		tempWidget.setOnOpListener((JavaScriptCallback)  e -> selectWidget(index));
		return tempWidget;

	}
	public void selectWidget(int index){
		client.playSoundEffect(SoundEffectID.UI_BOOP);
		BorderState originState = data.character.invBorderState.get(index);
		if (config.EditBrush() == EditBrush.SINGLE){
			modifyState(index,originState);
		}
		else if (config.EditBrush() == EditBrush.ROW){
			for (int i : getRow(index)){
				modifyState(i,originState);
			}
		}
		else if (config.EditBrush() == EditBrush.COLUMN){
			for (int i : getColumn(index)) {
				modifyState(i,originState);
			}
		}
		else if (config.EditBrush() == EditBrush.ALL){
			for (int i=0; i<67;i++){
				modifyState(i,originState);
			}
		}
	}
	int getRowStart(int val){
		if (val > 63) return 63;
		else return val-val%9;
	}
	int[] getRow(int val){
		if (val>=63) return (new int[]{63,64,65,66});
		if ((val-getRowStart(val))%2==0) return (new int[]{getRowStart(val),getRowStart(val)+2,getRowStart(val)+4,getRowStart(val)+6,getRowStart(val)+8});
		else return (new int[]{getRowStart(val)+1,getRowStart(val)+3,getRowStart(val)+5,getRowStart(val)+7});

	}
	int[] getColumn(int val){
		val = val%9;
		if (val%2==0) return (new int[]{val, val + 9, val + 18, val + 27, val + 36, val + 45, val + 54});
		else return (new int[]{val, val + 9, val + 18, val + 27, val + 36, val + 45, val + 54, (val/2)+63});
	}

	void modifyState(int index, BorderState originState){
		if (config.EditTool() == EditTool.DRAW_PRIMARY){
			if (originState == BorderState.PRIMARY) data.character.invBorderState.set(index,BorderState.ERASED);
			else data.character.invBorderState.set(index,BorderState.PRIMARY);
			updateWidget(index);
		}else if (config.EditTool() == EditTool.DRAW_SECONDARY){
			if (originState == BorderState.SECONDARY) data.character.invBorderState.set(index,BorderState.ERASED);
			else data.character.invBorderState.set(index,BorderState.SECONDARY);
			updateWidget(index);
		}else if (config.EditTool() == EditTool.DRAW_TERTIARY){
			if (originState == BorderState.TERTIARY) data.character.invBorderState.set(index,BorderState.ERASED);
			else data.character.invBorderState.set(index,BorderState.TERTIARY);
			updateWidget(index);
		}else if (config.EditTool() == EditTool.ERASE){
			data.character.invBorderState.set(index,BorderState.ERASED);
			updateWidget(index);
		}else if (config.EditTool() == EditTool.SHIFT_UP){
			//TODO use modulus to get the start of the row,add the amount of elements in a row to get copy index, add row length to get paste index, copy values from copy index (or blank if copy index is out of range) to paste index
			updateAllWidgets();
		}else if (config.EditTool() == EditTool.SHIFT_DOWN){
			//TODO above
			updateAllWidgets();
		}

	}

	public void saveStatesToConfig(){
		StringBuilder temp = new StringBuilder();
		for (BorderState bs : data.character.invBorderState){
			if (bs == BorderState.PRIMARY) temp.append("1");
			if (bs == BorderState.SECONDARY) temp.append("2");
			if (bs == BorderState.TERTIARY) temp.append("3");
			if (bs == BorderState.ERASED) temp.append("0");
		}
		String out = new BigInteger(temp.reverse().toString(), 4).toString(36);
		config.setInventoryBorderStates(out);
	}

	public void loadStatesFromConfig(){
		if (config.InventoryBorderStates() != null){
			char[] stateIn = new StringBuilder(new BigInteger(config.InventoryBorderStates(), 36).toString(4)).reverse().toString().toCharArray();
			data.character.invBorderState.clear();
			for (int i = 0; i < 67; i++) {//cap inv size
				if (i < stateIn.length){
					if (stateIn[i] == '1') data.character.invBorderState.add(BorderState.PRIMARY);
					else if (stateIn[i] == '2') data.character.invBorderState.add(BorderState.SECONDARY);
					else if (stateIn[i] == '3') data.character.invBorderState.add(BorderState.TERTIARY);
					else data.character.invBorderState.add(BorderState.ERASED);
				}
				else{
					data.character.invBorderState.add(BorderState.ERASED);
				}
			}

		}
	}

}
