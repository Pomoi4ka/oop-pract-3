package impl;

import impl.AbstractParser;
import impl.parsers.CSVParser;
import impl.parsers.XMLParser;
import impl.parserSelectors.ExtensionParserSelector;

import java.util.List;
import java.lang.NoSuchFieldException;
import java.lang.reflect.InvocationTargetException;

class ParserSelectorFactory {
    private static final List<Class<? extends AbstractParser>> parsers =
        List.of(CSVParser.class,
                XMLParser.class);

    public static ParserSelector buildExtensionParserSelector() {
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
