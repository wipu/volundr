package org.fluentjava.volundr.graph;

public interface GraphData<DATA, RANGE, PAINT> {

    PAINT paint();

    String title();

    RANGE range();

    DATA range(final RANGE range);

}
