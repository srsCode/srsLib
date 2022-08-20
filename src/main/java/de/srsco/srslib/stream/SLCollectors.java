/*
 * Project      : srsLib
 * File         : SLCollectors.java
 *
 * Copyright (c) 2022 srs_bsns (forfrdm [at] gmail.com)
 *
 * The MIT License (MIT) 
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including 
 * without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to 
 * the following conditions: 
 *
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software. 
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 *
 */

package de.srsco.srslib.stream;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.annotation.Nonnull;


public final class SLCollectors
{
    private SLCollectors() {}

    /**
     * <h3>This will return a Collector with a wrapped finaliser Function that returns an {@link Optional} of its result.</h3>
     * The application of this functionality is useful when an empty Optional is more prefered than, say, and empty Collection.
     * <p>
     * This operation relies upon wrapping the result of the finisher with an Optional and is therefore incompatible
     * with the {@link Collector.Characteristics#IDENTITY_FINISH} directive, which returns an unchecked cast of the
     * accumulator instead of executing the finisher. As the #IDENTITY_FINISH contract will have been broken, this will
     * lead to a ClassCastException as the accumulator can not be cast to Optional.
     * {@link #removeIdFinish} will be called on the Characteristics to remove this directive if it has been used and
     * All overloads of this method should call this one to filter out this directive.
     *
     * @param supplier    {@link Collector#supplier}
     * @param accumulator {@link Collector#accumulator}
     * @param combiner    {@link Collector#combiner}
     * @param finisher    {@link Collector#finisher}
     * @param c13s        {@link Collector#characteristics}
     * @param <T>         The input element type
     * @param <A>         The type of accumulation result
     * @param <R>         The type of the final result that is returned
     * @return The Collector
     *
     * @see #wrapFinisher
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <T, A, R> Collector<T, A, Optional<R>> toOptional(@Nonnull final Supplier<A> supplier,
                                                                    @Nonnull final BiConsumer<A, T> accumulator,
                                                                    @Nonnull final BinaryOperator<A> combiner,
                                                                    @Nonnull final Function<A, R> finisher,
                                                                    @Nonnull final Collector.Characteristics... c13s)
    {
        return Collector.of(supplier, accumulator, combiner, wrapFinisher(finisher), removeIdFinish(c13s));
    }

    /**
     * <h3>This will return a Collector with a NOOP finisher that will just wrap the accumulator and return it.</h3>
     *
     * @param supplier    {@link Collector#supplier}
     * @param accumulator {@link Collector#accumulator}
     * @param combiner    {@link Collector#combiner}
     * @param c13s        {@link Collector#characteristics}
     * @param <T>         The input element type
     * @param <A>         The type of accumulation result, and final result
     * @return The Collector
     *
     * @see #toOptional(Supplier, BiConsumer, BinaryOperator, Function, Collector.Characteristics...)
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <T, A> Collector<T, A, Optional<A>> toOptional(@Nonnull final Supplier<A> supplier,
                                                                 @Nonnull final BiConsumer<A, T> accumulator,
                                                                 @Nonnull final BinaryOperator<A> combiner,
                                                                 @Nonnull final Collector.Characteristics... c13s)
    {
        return toOptional(supplier, accumulator, combiner, Function.identity(), c13s);
    }

    /**
     * <h3>A helper to wrap the finisher of an existing Collector.</h3>
     *
     * @see SLCollectors#toOptional(Supplier, BiConsumer, BinaryOperator, Function, Collector.Characteristics...)
     * @see SLCollectors#toOptional(Supplier, BiConsumer, BinaryOperator, Collector.Characteristics...)
     * @see SLCollectors#wrapFinisher
     */
    public static <T, A, R> Collector<T, A, Optional<R>> toOptional(@Nonnull final Collector<T, A, R> collector)
    {
        return toOptional(collector.supplier(), collector.accumulator(), collector.combiner(), collector.finisher(),
            collector.characteristics().toArray(Collector.Characteristics[]::new));
    }

    /**
     * <h3>This wraps a finisher Function of a Stream {@link Collector} to return an Optional of the return value.</h3>
     * <p>
     * Short-circuits and returns an empty Optional before executing the finisher if:
     * the resultant object of the combiner is an {@link Iterable} with 0 elements, or
     * a {@link CharSequence} with 0 length, or a {@link StringJoiner} with 0 length.
     * Otherwise, if the result of the finisher operation is null, then an empty Optional is returned.
     *
     * @param finisher The original finisher function from a Collector.
     * @param <A>      The input type of the intermediary operations.
     * @param <R>      The return element type of the finisher.
     * @return Returns a wrapped finisher that returns an Optional of it's result.
     *     <p>
     *     TODO: Candidate for case pattern matching with switch when no longer a preview feature (Java 19?)
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <A, R> Function<A, Optional<R>> wrapFinisher(@Nonnull final Function<A, R> finisher)
    {
        Objects.requireNonNull(finisher, "A finisher function is required for wrapping.");
        return obj -> obj instanceof Iterable<?> it && !it.iterator().hasNext() ||
            // Perhaps better would be cs.toString().isBlank() as it would also
            // ignore whitespace, however, that process would be more expensive.
            obj instanceof CharSequence cs && cs.isEmpty() ||
            obj instanceof StringJoiner sj && sj.length() == 0
            ? Optional.empty()
            : Optional.ofNullable(finisher.apply(obj));

        //return obj -> switch (obj) {
        //    case Iterable<?>  it && !it.iterator().hasNext() -> Optional.empty();
        //    case CharSequence cs && cs.isEmpty()             -> Optional.empty();
        //    case StringJoiner sj && sj.length() == 0         -> Optional.empty();
        //    default                                          -> Optional.ofNullable(finisher.apply(obj));
        //};
    }

    /**
     * <h3>A helper function to remove the Collector.Characteristics.IDENTITY_FINISH directive.</h3>
     *
     * @param c13s An varargs array of Characteristics that were passed to a Collector.
     * @return facepalm.jpg
     */
    private static Collector.Characteristics[] removeIdFinish(@Nonnull final Collector.Characteristics[] c13s)
    {
        return c13s.length == 0 ? c13s : Arrays.stream(c13s).filter(e -> e != Collector.Characteristics.IDENTITY_FINISH).toArray(Collector.Characteristics[]::new);
    }
}
