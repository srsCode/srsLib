package de.srsco.srslib.function;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


public final class SLFunctions
{
    private SLFunctions() {}

    /**
     * <h3>Provides a mapping Function for safe downcasting in a {@link Stream}/{@link Optional} that returns an Optional result.</h3>
     * <p>eg: Given a Stream&lt;LivingEntity&gt;: {@code stream.map(SLFunctions.safeCast(Player.class))}
     * would return a Stream&lt;Optional&lt;Player&gt;&gt; with empty Optionals representing casting failures
     * which can be filtered out in a postceding operation.</p>
     * <p>eg: Given an Optional&lt;LivingEntity&gt;: {@code optional.flatMap(SLFunctions.safeCast(Player.class))}
     * would result in a Optional&lt;Player&gt;</p>
     * <br><br>
     *
     * @param castClass The downstream class to be casted to.
     * @param <A>       The superclass or super interface of the input object.
     * @param <B>       The type of class to cast the input object to.
     * @return An Optional containing the casted object, or an empty Optional if the
     *     the casting class in not in the inheritance tree of the object.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <A, B extends A> Function<A, Optional<B>> safeCast(final Class<B> castClass)
    {
        return obj -> castClass.isAssignableFrom(obj.getClass()) ? Optional.of(castClass.cast(obj)) : Optional.empty();
    }

    /**
     * <h3>Provides a subclass filter Predicate for use in a {@link Stream}.</h3>
     * <p>eg: Given a Stream&lt;LivingEntity&gt;: {@code stream.filter(SLFunctions.subclassFilter(Player.class))}
     * would return a Stream&lt;Player&gt; with all objects that are not of the subclass Player filtered out.
     *
     * @param castClass The subclass to filter objects with.
     * @param <A>       The superclass type.
     * @param <B>       The subclass type to filter with.
     * @return A Predicate for object filtering in a Stream.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <A, B extends A> Predicate<A> subclassFilter(final Class<B> castClass)
    {
        return obj -> castClass.isAssignableFrom(obj.getClass());
    }
}
