package utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class QueryTimingLogger {

    private static final Path LOG_FILE =
            Paths.get("post-optimization-query-times.log");

    private static final AsynchronousFileChannel CHANNEL;
    private static final AtomicLong FILE_POSITION = new AtomicLong(0);

    static {
        try {
            CHANNEL = AsynchronousFileChannel.open(
                    LOG_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE
            );
            FILE_POSITION.set(CHANNEL.size());
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void log(
            String queryName,
            Instant start,
            Instant end
    ) {

        long durationMs = Duration.between(start, end).toMillis();

        String entry = String.format(
                "[%s] START=%s END=%s DURATION=%dms%n",
                queryName,
                start,
                end,
                durationMs
        );

        byte[] bytes = entry.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        long position = FILE_POSITION.getAndAdd(bytes.length);

        CHANNEL.write(buffer, position);
    }

    public static void shutdown() throws IOException {
        CHANNEL.close();
    }
}
