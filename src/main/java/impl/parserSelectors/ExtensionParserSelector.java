package impl.parserSelectors;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import impl.AbstractParser;
import impl.AbstractParserSelector;

public class ExtensionParserSelector extends AbstractParserSelector<String> {
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
