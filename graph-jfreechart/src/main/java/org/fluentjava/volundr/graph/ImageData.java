package org.fluentjava.volundr.graph;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class ImageData {
    private final String title;
    private final String xAxisTitle;
    private double range;
    private final GraphStatisticsProvider statistics;
    private final DatasetAdapter<?, ?> adapter;

    private ImageData(final String title, final String xAxisTitle,
            final double range, final GraphStatisticsProvider statistics,
            final DatasetAdapter<?, ?> adapter) {
        this.title = title;
        this.xAxisTitle = xAxisTitle;
        this.range = range;
        this.statistics = statistics;
        this.adapter = adapter;
    }

    public static ImageData statistics(final String title,
            final String xAxisTitle, final double range,
            final GraphStatisticsProvider statistics,
            final DatasetAdapter<?, ?> adapter) {
        return new ImageData(title, xAxisTitle, range, statistics, adapter);
    }

    public static ImageData noStatistics(final String title,
            final String xAxisTitle, final double range,
            final DatasetAdapter<?, ?> adapter) {
        return new ImageData(title, xAxisTitle, range, null, adapter);
    }

    public static ImageData noStatistics(final String title,
            final String xAxisTitle, final DatasetAdapter<?, ?> adapter) {
        return new ImageData(title, xAxisTitle, 0, null, adapter);
    }

    public void add(final Number x, final Number y) {
        adapter().add(x, y);
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public DatasetAdapter<?, ?> adapter() {
        return this.adapter;
    }

    public String title() {
        return this.title;
    }

    public String xAxisLabel() {
        return this.xAxisTitle;
    }

    public double range() {
        return this.range;
    }

    public GraphStatisticsProvider statistics() {
        return this.statistics;
    }

    public boolean hasStatistics() {
        return this.statistics != null;
    }

    public ImageData range(final double range) {
        this.range = range;
        return this;
    }

}
