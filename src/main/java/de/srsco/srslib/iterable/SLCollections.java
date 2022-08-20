/*
 * Project      : srsLib
 * File         : SLCollections.java
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

package de.srsco.srslib.iterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;


public final class SLCollections
{
    private SLCollections() {}

    /**
     * <h3>Creates a new ArrayList from the provided elements.</h3>
     *
     * @param elems Elements to be added to the list.
     * @param <T>   The type of elements.
     * @return      A new ArrayList with the provided elements.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(final T... elems)
    {
        return new ArrayList<>(Arrays.asList(elems));
    }

    /**
     * <h3>Merges the input Iterables into a new ArrayList.</h3>
     *
     * @param inputs    Inputs to be merged into a new Collection.
     * @param <T>       The type of collection elements
     * @return          A new Collection with the contents of the inputs merged into it.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    @SafeVarargs
    public static <T> ArrayList<T> toArrayList(@Nonnull final Iterable<? extends T>... inputs)
    {
        return merge(ArrayList::new, inputs);
    }

    /**
     * <h3>Merges the input Iterables into the supplied Collection.</h3>
     *
     * @param collector A Supplier of a Collection that accepts the merger of the input Collections.
     * @param inputs    Input Collections to be merged into the supplied Collection.
     * @param <T>       The type of collection elements
     * @param <C>       The output collection type
     * @return          The supplied Collection with the contents of the inputs merged into it.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    @SafeVarargs
    public static <T, C extends Collection<T>> C merge(@Nonnull final Supplier<? extends C> collector,
                                                       @Nonnull final Iterable<? extends T>... inputs)
    {
        Objects.requireNonNull(collector, "A Collection must be provided for the merger.");
        Objects.requireNonNull(inputs, "Input Iterables can not be null.");
        final var output = collector.get();
        Arrays.stream(inputs).forEach(input -> input.forEach(output::add));
        return output;
    }

    /**
     * <h3>A merging function that returns a Collection that is a merger of two input Collections.</h3>
     * Useful for when the preservation of the input Collections is required, or when the input Collections are unmodifiable.
     * The ordering of objects in the new Collection is dependant on the Collection used in the merger.
     *
     * @param input1   The first input Iterable to merge.
     * @param input2   The second input Iterable to merge.
     * @param merger   A transforming {@link BiFunction} that performs the merging of two inputs.
     * @param finisher A finisher {@link Function} that transforms the output Collection before returning.
     * @param <T>      The type of Iterable elements.
     * @param <C>      The input/output Collection type.
     * @param <R>      The return Collection type produced by the finisher function.
     * @return         A new Collection that is the merger of the input Collections.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <T, C extends Collection<T>, R> R merge(@Nonnull final C input1,
                                                          @Nonnull final C input2,
                                                          @Nonnull final BinaryOperator<C> merger,
                                                          @Nonnull final Function<C, R> finisher)
    {
        Objects.requireNonNull(input1, "Input collection can not be null.");
        Objects.requireNonNull(input2, "Input collection can not be null.");
        Objects.requireNonNull(merger, "A merger function must be provided.");
        Objects.requireNonNull(finisher, "A finisher function must be provided.");
        return finisher.apply(merger.apply(input1, input2));
    }

    /**
     * <h3>An overload of {@link #merge(Collection, Collection, BinaryOperator, Function)} without a finisher Function.</h3>
     *
     * @see #merge(Collection, Collection, BinaryOperator, Function)
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <T, C extends Collection<T>> C merge(@Nonnull final C input1,
                                                       @Nonnull final C input2,
                                                       @Nonnull final BinaryOperator<C> merger)
    {
        return merge(input1, input2, merger, Function.identity());
    }

    /**
     * <h3>Returns an unmodifiable merger of the input Collections into the supplied Collection.</h3>
     *
     * @param collector A Supplier of a Collection that accepts the merger of the input Collections.
     * @param inputs    Input Collections to be merged into the supplied Collection.
     * @param <T>       The type of collection elements
     * @param <C>       The input/output collection type
     * @return          The supplied Collection with the contents of the inputs merged into it and made unmodifiable.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    @SafeVarargs
    public static <T, C extends Collection<T>> Collection<T> mergeToUnmodifiable(@Nonnull final Supplier<? extends C> collector,
                                                                                 @Nonnull final C... inputs)
    {
        return Collections.unmodifiableCollection(merge(collector, inputs));
    }

    /**
     * <h3>Creates a BinaryOperator for merging the contents of the second input Collection into the first.</h3>
     *
     * @param <T> The type of collection elements
     * @param <C> The input/output collection type
     * @return    A BinaryOperator for merging.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <T, C extends Collection<T>> BinaryOperator<C> mergeBintoA()
    {
        return (a, b) -> {
            a.addAll(b);
            return a;
        };
    }

    /**
     * <h3>Creates a BinaryOperator for merging the contents of two input Collection into a supplied Collection.</h3>
     *
     * @param collector A Supplier of an output Collection
     * @param <T>       The type of collection elements
     * @param <C>       The input/output collection type
     * @return          A Collection that accepts the the elements of the inputs.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static <T, C extends Collection<T>> BinaryOperator<C> mergeNew(@Nonnull final Supplier<? extends C> collector)
    {
        Objects.requireNonNull(collector, "A Collection must be provided for the merger function.");
        return (input1, input2) -> {
            final var output = collector.get();
            output.addAll(input1);
            output.addAll(input2);
            return output;
        };
    }
}
