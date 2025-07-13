package org.akazukin.graph.vec;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Vec2d2 {
    public static final Vec2d2[] EMPTY = new Vec2d2[0];

    @NotNull
    Vec2d pos;
    @Nullable
    Vec2d vector;
}
