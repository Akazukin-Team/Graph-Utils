package org.akazukin.graph.vec;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class Vec2d {
    public static final Vec2d[] EMPTY = new Vec2d[0];

    double x;
    double y;
}
