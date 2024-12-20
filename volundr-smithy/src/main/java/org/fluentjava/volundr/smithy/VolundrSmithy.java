package org.fluentjava.volundr.smithy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.fluentjava.volundr.LineReader;
import org.fluentjava.volundr.LineVisitor;
import org.fluentjava.volundr.StringToOutputStream;
import org.fluentjava.volundr.bag.StronglyTypedSortedBag;
import org.fluentjava.volundr.concurrent.NamedThreadFactory;
import org.fluentjava.volundr.io.AsynchronousStreamReader;
import org.fluentjava.volundr.io.BytesToString;
import org.fluentjava.volundr.io.GZipStreamReaderFactory;
import org.fluentjava.volundr.io.GZipStreamToLines;
import org.fluentjava.volundr.io.InputStreamReaderFactory;
import org.fluentjava.volundr.io.InputStreamToLines;
import org.fluentjava.volundr.io.InputStreamToString;
import org.fluentjava.volundr.io.StreamReadFailedNotifier;
import org.fluentjava.volundr.io.StreamReader;
import org.fluentjava.volundr.io.StreamReaderFactory;
import org.fluentjava.volundr.io.StringToBytes;
import org.fluentjava.volundr.io.VisitingInputStreams;
import org.fluentjava.volundr.io.VisitingInputStreamsHandler;

public final class VolundrSmithy {

    private final Charset charset;

    public VolundrSmithy(final Charset charset) {
        this.charset = charset;
    }

    public LineReader lineReader() {
        return new LineReader(charset());
    }

    public StreamReader inputStreamToLines(final LineVisitor visitor) {
        return newInputStreamToLines(visitor);
    }

    public StreamReader gzipInputStreamToLines(final LineVisitor visitor) {
        return new GZipStreamToLines(newInputStreamToLines(visitor));
    }

    @SuppressWarnings("static-method")
    public AsynchronousStreamReader asynchronousStreamReader(
            final LineVisitor visitor,
            final StreamReadFailedNotifier failNotifier,
            final StreamReaderFactory streamReaderFactory) {
        return new AsynchronousStreamReader(visitor, streamReaderFactory,
                failNotifier);
    }

    public StringToOutputStream stringToOutputStream(
            final OutputStream streamToWrite) {
        return StringToOutputStream.forCharset(streamToWrite, charset());
    }

    public InputStreamToString inputStreamToString() {
        return InputStreamToString.forCharset(charset());
    }

    public VisitingInputStreams visitingInputStreams(
            final VisitingInputStreamsHandler handler,
            final StreamReadFailedNotifier readFailedNotifier) {
        return new VisitingInputStreams(handler,
                new InputStreamReaderFactory(charset()), readFailedNotifier);
    }

    public VisitingInputStreams visitingGzipInputStreams(
            final VisitingInputStreamsHandler handler,
            final StreamReadFailedNotifier readFailedNotifier) {
        return new VisitingInputStreams(handler,
                new GZipStreamReaderFactory(charset()), readFailedNotifier);
    }

    public StringToBytes stringToBytes() {
        return StringToBytes.forCharset(charset());
    }

    public BytesToString bytesToString() {
        return BytesToString.forCharset(charset());
    }

    @SuppressWarnings("static-method")
    public <TYPE> StronglyTypedSortedBag<TYPE> synchronizedTreeBag() {
        return StronglyTypedSortedBag.synchronizedTreeBag();
    }

    @SuppressWarnings("static-method")
    public <TYPE> StronglyTypedSortedBag<TYPE> treeBag() {
        return StronglyTypedSortedBag.treeBag();
    }

    @SuppressWarnings({ "static-method", "PMD.CloseResource" })
    public void readStreamsWith(final int threads, final int awaitTermination,
            final TimeUnit timeUnitForAwaitTermination,
            final StreamReader reader, final InputStream... streams) {
        final ExecutorService executor = Executors.newFixedThreadPool(threads,
                NamedThreadFactory.forNamePrefix("stream-reader-"));
        final CountDownLatch latch = new CountDownLatch(streams.length);
        for (final InputStream stream : streams) {
            executor.execute(() -> {
                try {
                    reader.readFrom(stream);
                } catch (IOException e) {
                    throw new VolundrSmithyException(
                            "Reading the stats stream failed!", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        shutdown(awaitTermination, timeUnitForAwaitTermination, executor);
    }

    private static void shutdown(final int awaitTermination,
            final TimeUnit timeUnitForAwaitTermination,
            final ExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(awaitTermination,
                    timeUnitForAwaitTermination);
        } catch (InterruptedException e) {
            throw new VolundrSmithyException(e);
        }
    }

    private Charset charset() {
        return this.charset;
    }

    private InputStreamToLines newInputStreamToLines(
            final LineVisitor visitor) {
        return new InputStreamToLines(visitor, charset());
    }
}
