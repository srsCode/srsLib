package de.srsco.srslib.function;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;


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
                                                       @Nonnull final BiPredicate<? super A, ? super B> predicate)
    {
        return of(first, second, third, (a, b, contingency) -> contingency && predicate.test(a, b));
    }

    static <A, B, C> Condition<A, B, C> of(final Supplier<A> first,
                                           final Supplier<B> second,
                                           final Supplier<C> third,
                                           @Nonnull final TriPredicate<? super A, ? super B, ? super C> predicate)
    {
        return new ConditionImpl<>(first, second, third, predicate);
    }

    static <A, B, C> Runnable runOnCondition(@Nonnull final Condition<A, B, C> condition,
                                             @Nonnull final Consumer<Condition<A, B, C>> consumer)
    {
        return runOnCondition(condition.first(), condition.second(), condition.third(), condition.predicate(), consumer);
    }

    static <A, B, C> Runnable runOnCondition(final Supplier<A> first,
                                             final Supplier<B> second,
                                             final Supplier<C> third,
                                             @Nonnull final TriPredicate<? super A, ? super B, ? super C> predicate,
                                             @Nonnull final Consumer<Condition<A, B, C>> consumer)
    {
        return new ConditionalRunnable<>(first, second, third, predicate, consumer);
    }

    record ConditionImpl<A, B, C>(@Override Supplier<A> first,
                                  @Override Supplier<B> second,
                                  @Override Supplier<C> third,
                                  @Override @Nonnull TriPredicate<? super A, ? super B, ? super C> predicate)
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
                                        @Override @Nonnull TriPredicate<? super A, ? super B, ? super C> predicate,
                                        @Nonnull Consumer<Condition<A, B, C>> consumer)
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
