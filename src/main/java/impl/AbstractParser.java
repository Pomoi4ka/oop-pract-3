package impl;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

import impl.Record;
import impl.Report;

public abstract class AbstractParser {
    protected abstract void close() throws IOException;
    protected abstract void initParser(Path file) throws Exception;
    protected abstract Record parseRecord() throws Exception;

    public Report parseFile(Path file) throws Exception {
        Instant timeStart = Instant.now();
        Set<Record> records = new HashSet<>();
        Map<Record, Integer> dups = new HashMap<>();
        Map<String, Map<Integer, Integer>> cityFloors = new HashMap<>();

        try {
            initParser(file);
            for (;;) {
                Record rec = parseRecord();
                if (rec == null) break;
                if (!records.add(rec))
                    dups.compute(rec, (k, v) -> v == null ? 1 : v + 1);
                else
                    cityFloors.computeIfAbsent(rec.city, k -> new HashMap<>())
                        .compute(rec.floor, (k, v) -> v == null ? 1 : v + 1);
            }
        } finally {
            close();
        }

        return new Report(Duration.between(timeStart, Instant.now()), dups, cityFloors);
    }
}
