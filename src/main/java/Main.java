import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Formatter;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Optional;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import java.lang.NoSuchFieldException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.Duration;
import java.util.Objects;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

class Report {
    public Duration timeSpent;
    public Map<Record, Integer> duplicates;
    public Map<Integer, Integer> floors;

    public Report() {
        duplicates = new HashMap<>();
        floors = new HashMap<>();
    }
}

class Record {
    final String city;
    final String street;
    final int house;
    final int floor;

    public Record(String city, String street, int house, int floor) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.floor = floor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Record)) return false;
        Record other = (Record)obj;
        if (!Objects.equals(city, other.city)) return false;
        if (!Objects.equals(street, other.street)) return false;
        if (house != other.house) return false;
        if (floor != other.floor) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, house, floor);
    }

    @Override
    public String toString() {
        return new Formatter()
            .format("{city: %s, street: %s, house: %d, floor: %d}",
                    city, street, house, floor)
            .toString();
    }
};

abstract class AbstractParser {
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

class CSVParser extends AbstractParser {
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

class XMLParser extends AbstractParser {
    public static final String extension = ".xml";

    XMLStreamReader reader;
    BufferedReader bufReader;

    @Override
    protected void close() throws IOException {
        bufReader.close();
        if (reader != null) {
            try { reader.close(); } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    protected void initParser(Path file) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        bufReader = Files.newBufferedReader(file);
        reader = factory.createXMLStreamReader(bufReader);
    }

    @Override
    protected Record parseRecord() throws XMLStreamException {
       while (reader.hasNext()) {
            int event = reader.next();
            if (event != XMLStreamReader.START_ELEMENT) continue;
            if (!"item".equals(reader.getLocalName())) continue;

            Record r = new Record(reader.getAttributeValue(null, "city"),
                                  reader.getAttributeValue(null, "street"),
                                  Integer.parseInt(reader.getAttributeValue(null, "house")),
                                  Integer.parseInt(reader.getAttributeValue(null, "floor")));

            // пропустить всё до </item>
            while (reader.hasNext()
                   && !(reader.next() == XMLStreamReader.END_ELEMENT
                        && "item".equals(reader.getLocalName()))) {
                // skip
            }
            return r;
        }
        return null;
    }
}

abstract class AbstractParserSelector<T> {
    public abstract void register(T from, AbstractParser to);
    public abstract Optional<AbstractParser> select(T from);
}

class ExtensionParserSelector extends AbstractParserSelector<String> {
    private Map<String, AbstractParser> parserDispatchExtensionTable;

    public ExtensionParserSelector() {
        super();
        parserDispatchExtensionTable = new HashMap<>();
    }

    @Override
    public void register(String from, AbstractParser to) {
        parserDispatchExtensionTable.put(from, to);
    }

    @Override
    public Optional<AbstractParser> select(String from) {
        return Optional.ofNullable(parserDispatchExtensionTable.get(from));
    }
}

class ParserSelectorFactory {
    private static final List<Class<? extends AbstractParser>> parsers =
        List.of(CSVParser.class,
                XMLParser.class);

    public static ExtensionParserSelector buildExtensionParserSelector() {
        ExtensionParserSelector selector = new ExtensionParserSelector();
        for (Class<? extends AbstractParser> clazz : parsers) {
            String extension;
            AbstractParser parser;
            try {
                extension = (String)clazz.getField("extension").get(null);
                parser = clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchFieldException
                     |NoSuchMethodException
                     |InvocationTargetException
                     |IllegalAccessException
                     |InstantiationException e) {
                continue;
            }
            selector.register(extension, parser);
        }
        return selector;
    }
}

public class Main {
    private void run(Path path) {
        ExtensionParserSelector selector = ParserSelectorFactory.buildExtensionParserSelector();
        String ext = path.toString()
            .toLowerCase()
            .chars()
            .boxed()
            .reduce("", (acc, e) -> {
                    if (e == '.') acc = "";
                    return acc += String.valueOf((char)e.intValue());
                }, (u, e) -> u += e);
        Optional<AbstractParser> parser = selector.select(ext);
        if (!parser.isPresent()) {
            System.err.printf("error: no parser found for extension %s\n", ext);
            System.exit(1);
        }

        try {
            Report report = parser.get().parseFile(path);
            System.out.println(report.timeSpent);
            System.out.println(report.duplicates);
            System.out.println(report.floors);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Path path = Path.of(scanner.nextLine());
        new Main().run(path);
    }
}
