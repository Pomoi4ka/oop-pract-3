package impl;

import java.util.Optional;

import impl.AbstractParser;

public interface ParserSelector<T> {
    public void register(T from, AbstractParser to);
    public Optional<AbstractParser> select(T from);
}
