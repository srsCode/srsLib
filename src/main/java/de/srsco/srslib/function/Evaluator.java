package de.srsco.srslib.function;

import java.util.Objects;

import javax.annotation.Nonnull;


@FunctionalInterface
public interface Evaluator
{
    boolean evaluate();

    default Evaluator negate()
    {
        return () -> !evaluate();
    }

    default Evaluator and(@Nonnull final Evaluator other)
    {
        Objects.requireNonNull(other);
        return () -> evaluate() && other.evaluate();
    }

    default Evaluator or(@Nonnull final Evaluator other)
    {
        Objects.requireNonNull(other);
        return () -> evaluate() || other.evaluate();
    }
}
