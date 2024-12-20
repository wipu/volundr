package org.fluentjava.volundr.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatisticsValueProviderTest {

    @Test
    public void empty() {
        final StatisticsValueProvider stat = new StatisticsValueProvider();
        assertEquals("Min doesn't match!", 0, stat.min(), 0);
        assertEquals("Max doesn't match!", 0, stat.max(), 0);
        assertEquals("Mean doesn't match!", 0, stat.mean(), 0);
        assertEquals("Median doesn't match!", 0, stat.median(), 0);
        stat.percentile(0);
        for (int i = 1; i < 101; i++) {
            assertEquals(i + " percentile doesn't match!", 0,
                    stat.percentile(i), 0);
        }
    }

    @Test
    public void statsOdd() {
        final StatisticsValueProvider stat = new StatisticsValueProvider();
        for (int i = 100; i > -1; i--) {
            stat.addSample(i);
        }
        assertEquals("Min doesn't match!", 0, stat.min(), 0);
        assertEquals("Max doesn't match!", 100, stat.max(), 0);
        assertEquals("Mean doesn't match!", 50, stat.mean(), 0);
        assertEquals("Median doesn't match!", 50, stat.median(), 0);
        assertEquals("50 percentile doesn't match!", 50, stat.percentile(50),
                0);
        assertEquals("90 percentile doesn't match!", 90, stat.percentile(90),
                0);
        assertEquals("95 percentile doesn't match!", 95, stat.percentile(95),
                0);
        assertEquals("96 percentile doesn't match!", 96, stat.percentile(96),
                0);
        assertEquals("97 percentile doesn't match!", 97, stat.percentile(97),
                0);
        assertEquals("98 percentile doesn't match!", 98, stat.percentile(98),
                0);
        assertEquals("99 percentile doesn't match!", 99, stat.percentile(99),
                0);
        assertEquals("100 percentile doesn't match!", 100, stat.percentile(100),
                0);
    }

    @Test
    public void statsEven() {
        final StatisticsValueProvider stat = new StatisticsValueProvider();
        for (int i = 100; i > 0; i--) {
            stat.addSample(i);
        }
        assertEquals("Min doesn't match!", 1, stat.min(), 0);
        assertEquals("Max doesn't match!", 100, stat.max(), 0);
        assertEquals("Mean doesn't match!", 50.5, stat.mean(), 0);
        assertEquals("Median doesn't match!", 50, stat.median(), 0);
        assertEquals("50 percentile doesn't match!", 51, stat.percentile(50),
                0);
        assertEquals("90 percentile doesn't match!", 91, stat.percentile(90),
                0);
        assertEquals("95 percentile doesn't match!", 96, stat.percentile(95),
                0);
        assertEquals("96 percentile doesn't match!", 97, stat.percentile(96),
                0);
        assertEquals("97 percentile doesn't match!", 98, stat.percentile(97),
                0);
        assertEquals("98 percentile doesn't match!", 99, stat.percentile(98),
                0);
        assertEquals("99 percentile doesn't match!", 100, stat.percentile(99),
                0);
        assertEquals("99 percentile doesn't match!", 100, stat.percentile(100),
                0);
    }

    @Test
    public void percentileTest() {
        final StatisticsValueProvider stat = new StatisticsValueProvider();
        stat.addSample(15);
        stat.addSample(20);
        stat.addSample(35);
        stat.addSample(40);
        stat.addSample(50);
        assertEquals(20, stat.percentile(30), 0);
        assertEquals(20, stat.percentile(35), 0);
        assertEquals(35, stat.percentile(40), 0);
        assertEquals(50, stat.percentile(100), 0);
    }

    @Test
    public void performance() {
        final StatisticsValueProvider stat = new StatisticsValueProvider();
        for (int i = 1000000; i > -1; i--) {
            stat.addSample(i);
        }
        assertEquals("Min doesn't match!", 0, stat.min(), 0);
        assertEquals("Max doesn't match!", 1000000, stat.max(), 0);
        assertEquals("Mean doesn't match!", 500000, stat.mean(), 0);
        assertEquals("Median doesn't match!", 500000, stat.median(), 0);
        assertEquals("50 percentile doesn't match!", 500000,
                stat.percentile(50), 0);
        assertEquals("90 percentile doesn't match!", 900000,
                stat.percentile(90), 0);
        assertEquals("95 percentile doesn't match!", 950000,
                stat.percentile(95), 0);
        assertEquals("96 percentile doesn't match!", 960000,
                stat.percentile(96), 0);
        assertEquals("97 percentile doesn't match!", 970000,
                stat.percentile(97), 0);
        assertEquals("98 percentile doesn't match!", 980000,
                stat.percentile(98), 0);
        assertEquals("99 percentile doesn't match!", 990000,
                stat.percentile(99), 0);
    }

}
