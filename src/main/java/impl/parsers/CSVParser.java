package impl.parsers;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Iterator;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;

import impl.AbstractParser;
import impl.Record;

public class CSVParser extends AbstractParser {
    public static final String extension = ".csv";

    Iterator<CsvRecord> iter;
    CsvReader reader;

    @Override
    protected void close() throws IOException {
        if (reader != null) reader.close();
    }

    @Override
    protected void initParser(Path file) throws IOException {
        reader = CsvReader.builder()
            .fieldSeparator(';')
            .ofCsvRecord(file);
        iter = reader.iterator();
        iter.next(); // skip headers
    }

    @Override
    protected Record parseRecord() throws IOException {
        Record record;
        CsvRecord csvRecord;
        if (!iter.hasNext()) return null;
        csvRecord = iter.next();

        return new Record(csvRecord.getField(0),
                            csvRecord.getField(1),
                            Integer.parseInt(csvRecord.getField(2)),
                            Integer.parseInt(csvRecord.getField(3)));
    }
}
