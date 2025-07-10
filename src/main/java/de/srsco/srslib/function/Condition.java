/*
 * Project      : srsLib
 * File         : Condition.java
 *
 * Copyright (c) 2023 srs_bsns (forfrdm [at] gmail.com)
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

package de.srsco.srslib.function;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public sealed interface Condition<A, B, C> extends Evaluator
{
    Supplier<A> first();

    Supplier<B> second();

    Supplier<C> third();

    TriPredicate<? super A, ? super B, ? super C> predicate();

    @Override
    default boolean evaluate()
    {
        return predicate().test(first().get(), second().get(), third().get());
    }

    default void conditionalRun(final Consumer<Condition<A, B, C>> consumer)
    {
        if (evaluate()) {
            consumer.accept(this);
        }
    }

    static Condition<Object, Object, Object> alwaysTrue()
    {
        return of(Dummies.dummySupplier(), Dummies.dummySupplier(), Dummies.dummySupplier(), (a, b, c) -> true);
    }

    static <A, B> Condition<A, B, Object> equality(final Supplier<A> first,
                                                   final Supplier<B> second)
    {
        return of(first, second, Dummies.dummySupplier(), (a, b, c) -> Objects.equals(a, b));
    }

    static <A, B, C> Condition<A, B, C> equality(final Supplier<A> first,
                                                 final Supplier<B> second,
                                                 final Supplier<C> third)
    {
        return of(first, second, third, (a, b, c) -> Objects.equals(a, b) && Objects.equals(a, c));
    }

    static <A, B> Condition<A, B, Boolean> contingency(final Supplier<Boolean> third,
                                                       final Supplier<A> first,
                                                       final Supplier<B> second,
                                                       @NotNull final BiPredicate<? super A, ? super B> predicate)
    {
        return of(first, second, third, (a, b, contingency) -> contingency && predicate.test(a, b));
    }

    static <A, B, C> Condition<A, B, C> of(final Supplier<A> first,
                                           final Supplier<B> second,
                                           final Supplier<C> third,
                                           @NotNull final TriPredicate<? super A, ? super B, ? super C> predicate)
    {
        return new ConditionImpl<>(first, second, third, predicate);
    }

    static <A, B, C> Runnable runOnCondition(@NotNull final Condition<A, B, C> condition,
                                             @NotNull final Consumer<Condition<A, B, C>> consumer)
    {
        return runOnCondition(condition.first(), condition.second(), condition.third(), condition.predicate(), consumer);
    }

    static <A, B, C> Runnable runOnCondition(final Supplier<A> first,
                                             final Supplier<B> second,
                                             final Supplier<C> third,
                                             @NotNull final TriPredicate<? super A, ? super B, ? super C> predicate,
                                             @NotNull final Consumer<Condition<A, B, C>> consumer)
    {
        return new ConditionalRunnable<>(first, second, third, predicate, consumer);
    }

    record ConditionImpl<A, B, C>(@Override Supplier<A> first,
                                  @Override Supplier<B> second,
                                  @Override Supplier<C> third,
                                  @Override @NotNull TriPredicate<? super A, ? super B, ? super C> predicate)
        implements Condition<A, B, C>
    {
        public ConditionImpl
        {
            Objects.requireNonNull(predicate, "Must have a predicate for condition");
        }
    }

    record ConditionalRunnable<A, B, C>(@Override Supplier<A> first,
                                        @Override Supplier<B> second,
                                        @Override Supplier<C> third,
                                        @Override @NotNull TriPredicate<? super A, ? super B, ? super C> predicate,
                                        @NotNull Consumer<Condition<A, B, C>> consumer)
        implements Condition<A, B, C>, Runnable
    {
        public ConditionalRunnable
        {
            Objects.requireNonNull(predicate, "Must have a predicate for the condition.");
            Objects.requireNonNull(consumer, "Must have an action to be performed.");
        }

        @Override
        public void run()
        {
            if (evaluate()) {
                consumer().accept(this);
            }
        }
    }
}
