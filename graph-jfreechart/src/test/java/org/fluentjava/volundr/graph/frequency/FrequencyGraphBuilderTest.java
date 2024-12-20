package org.fluentjava.volundr.graph.frequency;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.fluentjava.volundr.graph.jfreechart.DefaultDatasetAdapterFactory;
import org.fluentjava.volundr.graph.jfreechart.ImageFactoryUsingJFreeChart;
import org.fluentjava.volundr.graph.jfreechart.JFreeChartWriter;
import org.fluentjava.volundr.statistics.AbstractStatisticsValueProvider;
import org.fluentjava.volundr.statistics.StatisticsListProvider;
import org.fluentjava.volundr.statistics.StatisticsListProviderFactory;
import org.fluentjava.volundr.statistics.StatisticsValueProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("PMD.UseProperClassLoader")
public class FrequencyGraphBuilderTest {

    private static byte[] goldenMasterBytes;
    private static String targetPath;
    private static ImageFactoryUsingJFreeChart imageFactory;

    @BeforeClass
    public static void loadGoldenMaster() throws IOException, URISyntaxException {
        File goldenMaster = new File(
                FrequencyGraphBuilderTest.class
                        .getResource("/goldenMaster.png").toURI());
        goldenMasterBytes = Files
                .readAllBytes(Paths.get(goldenMaster.getPath()));
        targetPath = System.getProperty("user.dir") + "/target";

        imageFactory = new ImageFactoryUsingJFreeChart(
                new JFreeChartWriter(targetPath));
    }

    @Test
    public void writeGraph() throws IOException {
        FrequencyData frequencyData = new FrequencyData() {

            @Override
            public long max() {
                return 8;
            }

            @Override
            public boolean hasSamples() {
                return true;
            }

            @Override
            public long countFor(long value) {
                switch ((int) value) {
                case 2:
                case 3:
                case 7:
                case 8:
                    return 1;
                case 4:
                case 6:
                    return 2;
                case 5:
                    return 3;
                default:
                    return 0;
                }
            }
        };
        DefaultDatasetAdapterFactory lineChartAdapterProvider = new DefaultDatasetAdapterFactory();
        FrequencyGraphBuilder frequencyGraphBuilder = FrequencyGraphBuilder
                .newFrequencyGraph(frequencyData, "graphTitle", "xAxisTitle",
                        lineChartAdapterProvider);
        String graphName = "writeGraph";
        frequencyGraphBuilder.writeGraph(imageFactory, graphName);

        byte[] bytes = Files
                .readAllBytes(Paths.get(targetPath, graphName + ".png"));
        Assert.assertArrayEquals(goldenMasterBytes, bytes);
    }

    @Test
    public void writeGraphFromStatisticsValueProvider() throws IOException {
        AbstractStatisticsValueProvider<Long> stats = StatisticsValueProviderFactory
                .longValues();
        stats.addSample(2L);
        stats.addSample(3L);
        stats.addSample(4L);
        stats.addSample(4L);
        stats.addSample(5L);
        stats.addSample(5L);
        stats.addSample(5L);
        stats.addSample(6L);
        stats.addSample(6L);
        stats.addSample(7L);
        stats.addSample(8L);

        DefaultDatasetAdapterFactory lineChartAdapterProvider = new DefaultDatasetAdapterFactory();
        FrequencyData frequencyData = new FrequencyData() {

            @Override
            public long max() {
                return stats.max();
            }

            @Override
            public boolean hasSamples() {
                return stats.hasSamples();
            }

            @Override
            public long countFor(long value) {
                return stats.countFor(value);
            }
        };
        FrequencyGraphBuilder frequencyGraphBuilder = FrequencyGraphBuilder
                .newFrequencyGraph(frequencyData, "graphTitle", "xAxisTitle",
                        lineChartAdapterProvider);
        String graphName = "writeGraphFromStatisticsValueProvider";
        frequencyGraphBuilder.writeGraph(imageFactory, graphName);
        byte[] bytes = Files
                .readAllBytes(Paths.get(targetPath, graphName + ".png"));
        Assert.assertArrayEquals(goldenMasterBytes, bytes);
    }

    @Test
    public void writeGraphFromStatisticsListProviderLongValues()
            throws IOException {
        List<Long> values = new ArrayList<>();
        values.add(2L);
        values.add(3L);
        values.add(4L);
        values.add(4L);
        values.add(5L);
        values.add(5L);
        values.add(5L);
        values.add(6L);
        values.add(6L);
        values.add(7L);
        values.add(8L);

        StatisticsListProvider<Long> stats = StatisticsListProviderFactory
                .longValues(values);

        DefaultDatasetAdapterFactory lineChartAdapterProvider = new DefaultDatasetAdapterFactory();
        FrequencyData frequencyData = new FrequencyData() {

            @Override
            public long max() {
                return stats.max();
            }

            @Override
            public boolean hasSamples() {
                return stats.hasSamples();
            }

            @Override
            public long countFor(long value) {
                AtomicLong count = new AtomicLong(0);
                values.forEach(val -> {
                    if (val == value) {
                        count.getAndIncrement();
                    }
                });
                return count.get();
            }
        };
        FrequencyGraphBuilder frequencyGraphBuilder = FrequencyGraphBuilder
                .newFrequencyGraph(frequencyData, "graphTitle", "xAxisTitle",
                        lineChartAdapterProvider);
        String graphName = "writeGraphFromStatisticsListProviderLongValues";
        frequencyGraphBuilder.writeGraph(imageFactory, graphName);

        byte[] bytes = Files
                .readAllBytes(Paths.get(targetPath, graphName + ".png"));
        Assert.assertArrayEquals(goldenMasterBytes, bytes);
    }

    @Test
    public void writeGraphFromStatisticsListProviderIntValues()
            throws IOException {
        List<Integer> values = new ArrayList<>();
        values.add(2);
        values.add(3);
        values.add(4);
        values.add(4);
        values.add(5);
        values.add(5);
        values.add(5);
        values.add(6);
        values.add(6);
        values.add(7);
        values.add(8);

        StatisticsListProvider<Integer> stats = StatisticsListProviderFactory
                .integerValues(values);

        DefaultDatasetAdapterFactory lineChartAdapterProvider = new DefaultDatasetAdapterFactory();
        FrequencyData frequencyData = new FrequencyData() {

            @Override
            public long max() {
                return stats.max();
            }

            @Override
            public boolean hasSamples() {
                return stats.hasSamples();
            }

            @Override
            public long countFor(long value) {
                AtomicLong count = new AtomicLong(0);
                values.forEach(val -> {
                    if (val == value) {
                        count.getAndIncrement();
                    }
                });
                return count.get();
            }
        };
        FrequencyGraphBuilder frequencyGraphBuilder = FrequencyGraphBuilder
                .newFrequencyGraph(frequencyData, "graphTitle", "xAxisTitle",
                        lineChartAdapterProvider);
        String graphName = "writeGraphFromStatisticsListProviderIntValues";
        frequencyGraphBuilder.writeGraph(imageFactory, graphName);
        byte[] bytes = Files
                .readAllBytes(Paths.get(targetPath, graphName + ".png"));
        Assert.assertArrayEquals(goldenMasterBytes, bytes);
    }

}
