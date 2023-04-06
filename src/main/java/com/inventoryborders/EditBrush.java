package com.inventoryborders;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EditBrush {
    SINGLE("Single"),
    ROW("Row"),
    COLUMN("Column"),
    ALL("All");

    private final String name;

    @Override
    public String toString()
    {
        return getName();
    }
}
