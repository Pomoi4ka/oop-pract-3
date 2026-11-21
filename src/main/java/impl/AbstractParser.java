package impl;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.Duration;

import impl.Record;
import impl.Report;

public abstract class AbstractParser {
    protected abstract void close() throws IOException;
    protected abstract void initParser(Path file) throws Exception;
    protected abstract Record parseRecord() throws Exception;

    public Report parseFile(Path file) throws Exception {
        Instant timeStart = Instant.now();
        Report report = new Report();
        Set<Record> records = new HashSet<>();

        try {
            initParser(file);
            for (;;) {
                Record rec = parseRecord();
                if (rec == null) break;
                if (!records.add(rec))
                    report.duplicates.compute(rec, (k, v) -> v == null ? 1 : v + 1);
                else
                    report.floors.compute(rec.floor, (k, v) -> v == null ? 1 : v + 1);
            }
        } finally {
            close();
        }

        report.timeSpent = Duration.between(timeStart, Instant.now());
        return report;
    }
}
