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
public class Vec2dVec1d {
    public static final Vec2dVec1d[] EMPTY = new Vec2dVec1d[0];

    @NotNull
    Vec2d pos;
    @Nullable
    Double vector;
}
