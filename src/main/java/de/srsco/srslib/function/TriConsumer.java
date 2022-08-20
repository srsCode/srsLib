package de.srsco.srslib.function;

import java.util.Objects;


/**
 * A Consumer that takes 3 objects.
 *
 * @param <A> the Type of the first object.
 * @param <B> the Type of the second object.
 * @param <C> the Type of the third object.
 */
@FunctionalInterface
public interface TriConsumer<A, B, C>
{
    void accept(A a, B b, C c);

    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after)
    {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
