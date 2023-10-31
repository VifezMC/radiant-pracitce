package xyz.kiradev.utils.assemble;

import lombok.Getter;

@Getter
public enum AssembleStyle {
    MODERN(false, 1);

    private final boolean descending;
    private final int startNumber;

    AssembleStyle(boolean descending, int startNumber) {
        this.descending = descending;
        this.startNumber = startNumber;
    }
}
