package org.fluentjava.volundr.smithy;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.fluentjava.volundr.LineVisitor;
import org.fluentjava.volundr.asexpected.AsExpected;
import org.fluentjava.volundr.bag.StronglyTypedSortedBag;
import org.fluentjava.volundr.concurrent.ThreadEngineApi;
import org.fluentjava.volundr.io.AsynchronousStreamReader;
import org.fluentjava.volundr.io.InputStreamReaderFactory;
import org.fluentjava.volundr.io.StreamReader;
import org.fluentjava.volundr.io.VisitingInputStreamsHandler;
import org.junit.Before;
import org.junit.Test;

public class VolundrSmithyTest {

    private VolundrSmithy volundrSmithy;

    @Before
    public void before() {
        this.volundrSmithy = new VolundrSmithy(StandardCharsets.UTF_8);
    }

    @Test
    public void stringToBytes() {
        assertArrayEquals(new byte[] { 118, -61, -74, 108, 117, 110, 100, 114 },
                smithy().stringToBytes().convert("völundr"));
    }

    @Test
    public void bytesToString() {
        assertEquals("völundr", smithy().bytesToString().convert(
                new byte[] { 118, -61, -74, 108, 117, 110, 100, 114 }));
    }

    @Test
    public void asyncTreeBag() {
        final StronglyTypedSortedBag<String> synchronizedTreeBag = smithy()
                .synchronizedTreeBag();
        ThreadEngineApi.Builder<Runnable> builder = ThreadEngineApi.builder();
        ThreadEngineApi<Runnable> api = builder.threadNamePrefix("asynctreebag")
                .runnables(() -> synchronizedTreeBag.add("100"),
                        () -> synchronizedTreeBag.add("101"))
                .build();
        api.run();
        assertFalse(synchronizedTreeBag.isEmpty());
        assertEquals(2, synchronizedTreeBag.size());
        assertEquals(1, synchronizedTreeBag.count("100"));
        assertEquals(1, synchronizedTreeBag.count("101"));
    }

    @Test
    public void treebag() {
        final StronglyTypedSortedBag<String> treebag = smithy().treeBag();
        treebag.add("100");
        treebag.add("101");
        assertEquals(2, treebag.size());
        assertEquals(1, treebag.count("100"));
        assertEquals(1, treebag.count("101"));
    }

    @Test
    public void lineReader() throws IOException {
        final StringBuilder result = new StringBuilder();
        smithy().lineReader().read(resourceAsStream("file-with-lines"),
                new LineVisitor() {

                    @Override
                    public void visit(final String line) {
                        result.append(line).append('\n');
                    }

                    @Override
                    public void emptyLine() {
                        throw new RuntimeException(
                                "No empty lines should have been there!");
                    }
                });

        final AsExpected<Void> expected = expected(result.toString());
        expected.line("line1");
        expected.line("line2");
        expected.line("line3");
        expected.end();
    }

    @Test
    public void inputStreamToString() throws IOException {
        final AsExpected<Void> expected = expected(
                smithy().inputStreamToString()
                        .toString(resourceAsStream("file-with-lines")));
        expected.line("line1");
        expected.line("line2");
        expected.line("line3");
        expected.end();
    }

    @Test
    public void stringToOutputStream() throws IOException {
        final ByteArrayOutputStream streamToWrite = new ByteArrayOutputStream();
        smithy().stringToOutputStream(streamToWrite).write("völundr");
        final AsExpected<Void> expected = expected(
                streamToWrite.toString("UTF-8"));
        expected.string("völundr").end();
    }

    @Test
    public void inputStreamToLines() throws IOException {
        final StringBuilder result = new StringBuilder();
        smithy().inputStreamToLines(new LineVisitor() {

            @Override
            public void visit(final String line) {
                result.append(line).append('\n');
            }

            @Override
            public void emptyLine() {
                throw new RuntimeException(
                        "No empty lines should have been there!");
            }
        }).readFrom(resourceAsStream("file-with-lines"));
        final AsExpected<Void> expected = expected(result.toString());
        expected.line("line1");
        expected.line("line2");
        expected.line("line3");
        expected.end();
    }

    @Test
    public void visitingInputStreams() {
        final StringBuilder result = new StringBuilder();
        smithy().visitingInputStreams(new VisitingInputStreamsHandler() {

            @Override
            public void interruptedWhileWaitingUntilDone(
                    final InterruptedException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void closeStreamFailed(final IOException e) {
                throw new RuntimeException(e);
            }
        }, (stream, t) -> {
            throw new RuntimeException(t);
        }).readStreams(new LineVisitor() {

            @Override
            public void visit(final String line) {
                result.append(line).append('\n');
            }

            @Override
            public void emptyLine() {
                throw new RuntimeException(
                        "No empty lines should have been there!");
            }
        }, resourceAsStream("file-with-lines"));
        final AsExpected<Void> expected = expected(result.toString());
        expected.line("line1");
        expected.line("line2");
        expected.line("line3");
        expected.end();
    }

    @Test
    public void visitingGzipInputStreams() {
        final StringBuilder result = new StringBuilder();
        smithy().visitingGzipInputStreams(new VisitingInputStreamsHandler() {

            @Override
            public void interruptedWhileWaitingUntilDone(
                    final InterruptedException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void closeStreamFailed(final IOException e) {
                throw new RuntimeException(e);
            }
        }, (stream, t) -> {
            throw new RuntimeException(t);
        }).readStreams(new LineVisitor() {

            @Override
            public void visit(final String line) {
                result.append(line).append('\n');
            }

            @Override
            public void emptyLine() {
                throw new RuntimeException(
                        "No empty lines should have been there!");
            }
        }, resourceAsStream("file-with-lines.gz"));
        final AsExpected<Void> expected = expected(result.toString());
        expected.line("line1");
        expected.line("line2");
        expected.line("line3");
        expected.end();
    }

    @Test
    public void asynchronousStreamReader() throws InterruptedException {
        final List<String> result = new ArrayList<>();
        final AsynchronousStreamReader reader = smithy()
                .asynchronousStreamReader(new LineVisitor() {

                    @Override
                    public void visit(final String line) {
                        synchronized (result) {
                            result.add(line);
                        }
                    }

                    @Override
                    public void emptyLine() {
                        throw new RuntimeException(
                                "No empty lines should have been there!");

                    }
                }, (stream, t) -> {
                    throw new RuntimeException(t);
                }, new InputStreamReaderFactory(StandardCharsets.UTF_8));
        reader.readFrom(resourceAsStream("file-with-lines"),
                resourceAsStream("second-file-with-lines"));
        reader.waitUntilDone();
        assertEquals(6, result.size());
        assertTrue(result.contains("line1"));
        assertTrue(result.contains("line2"));
        assertTrue(result.contains("line3"));
        assertTrue(result.contains("line4"));
        assertTrue(result.contains("line5"));
        assertTrue(result.contains("line6"));
    }

    @Test
    public void gzipInputStreamToLines() throws IOException {
        final StringBuilder result = new StringBuilder();
        smithy().gzipInputStreamToLines(new LineVisitor() {

            @Override
            public void visit(final String line) {
                result.append(line).append('\n');
            }

            @Override
            public void emptyLine() {
                throw new RuntimeException(
                        "No empty lines should have been there!");
            }
        }).readFrom(resourceAsStream("file-with-lines.gz"));
        final AsExpected<Void> expected = expected(result.toString());
        expected.line("line1");
        expected.line("line2");
        expected.line("line3");
        expected.end();
    }

    @Test
    public void readStreamsWith() {
        final StronglyTypedSortedBag<String> values = smithy()
                .synchronizedTreeBag();
        final StreamReader reader = smithy()
                .inputStreamToLines(new LineVisitor() {

                    @Override
                    public void visit(final String line) {
                        values.add(line);
                    }

                    @Override
                    public void emptyLine() {
                        throw new RuntimeException(
                                "No empty lines should have been there!");
                    }
                });
        smithy().readStreamsWith(2, 5, TimeUnit.SECONDS, reader,
                resourceAsStream("big-file-with-lines"),
                resourceAsStream("big-file-with-lines"));
        assertEquals(3182764, values.size());
        final StringBuilder result = new StringBuilder();
        for (final String sample : values.uniqueSamples()) {
            result.append(values.count(sample)).append(',').append(sample)
                    .append('\n');
        }
        final AsExpected<Void> expected = expected(result.toString());
        expected.line("1060928,line1");
        expected.line("1060918,line2");
        expected.line("1060918,line3");
        expected.end();
    }

    private VolundrSmithy smithy() {
        return this.volundrSmithy;
    }

    private static AsExpected<Void> expected(final String actual) {
        return AsExpected.expected(actual);
    }

    private static InputStream resourceAsStream(final String resource) {
        return VolundrSmithyTest.class.getResourceAsStream("/" + resource);
    }
}
