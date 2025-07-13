package org.akazukin.graph.animation.builder.algorithm;

import org.akazukin.graph.algorithm.graph.IGraph;
import org.akazukin.graph.animation.builder.IEasingBuilder;

public interface IAlgorithmEasingBuilder extends IEasingBuilder {
    <T extends IGraph & Cloneable> void setAlgorithm(T algorithm);
}
