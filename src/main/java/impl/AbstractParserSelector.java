package impl;

import java.util.Optional;

import impl.AbstractParser;

public abstract class AbstractParserSelector<T> {
    public abstract void register(T from, AbstractParser to);
    public abstract Optional<AbstractParser> select(T from);
}
