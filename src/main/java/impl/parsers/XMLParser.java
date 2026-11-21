package impl.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import impl.AbstractParser;
import impl.Record;

public class XMLParser extends AbstractParser {
    public static final String extension = ".xml";

    XMLStreamReader reader;
    BufferedReader bufReader;

    @Override
    protected void close() throws IOException {
        try {
            if (reader != null) {
                try { reader.close(); } catch (XMLStreamException e) {
                    throw new IOException(e);
                }
            }
        } finally {
            if (bufReader != null) bufReader.close();
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
