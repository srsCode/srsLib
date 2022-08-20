package de.srsco.srslib.function;

import java.util.Objects;


/**
 * A predicate that takes 3 objects and returns a boolean.
 *
 * @param <A> the Type of the first object.
 * @param <B> the Type of the second object.
 * @param <C> the Type of the third object.
 */
@FunctionalInterface
public interface TriPredicate<A, B, C>
{
    boolean test(A a, B b, C c);

    default TriPredicate<A, B, C> and(TriPredicate<? super A, ? super B, ? super C> other)
    {
        Objects.requireNonNull(other);
        return (A a, B b, C c) ->
            test(a, b, c) && other.test(a, b, c);
    }

    default TriPredicate<A, B, C> negate()
    {
        return (A a, B b, C c) ->
            !test(a, b, c);
    }

    default TriPredicate<A, B, C> or(TriPredicate<? super A, ? super B, ? super C> other)
    {
        Objects.requireNonNull(other);
        return (A a, B b, C c) ->
            test(a, b, c) || other.test(a, b, c);
    }
}
