package com.inventoryborders;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EditTool {

    DRAW_PRIMARY("Primary color"),
    DRAW_SECONDARY("Secondary color"),
    DRAW_TERTIARY("Secondary color"),
    ERASE("Erase"),
    SHIFT_UP("Shift row up"),
    SHIFT_DOWN("Shift row down");


    private final String name;

    @Override
    public String toString()
    {
        return getName();
    }
}
