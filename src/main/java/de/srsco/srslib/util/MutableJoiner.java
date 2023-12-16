/*
 * Project      : srsLib
 * File         : MutableJoiner.java
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

package de.srsco.srslib.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class MutableJoiner extends AbstractCollection<CharSequence> implements CharSequence
{
    private static final String EMPTY_STRING = "";

    private final String delimiter;
    private final List<CharSequence> elements;

    public MutableJoiner()
    {
        this("");
    }

    public MutableJoiner(final CharSequence delimiter)
    {
        Objects.requireNonNull(delimiter, "MutableJoiner requires a delimiter");
        this.delimiter = delimiter.toString();
        this.elements  = new ArrayList<>();
    }

    public MutableJoiner(final MutableJoiner other)
    {
        Objects.requireNonNull(other, "MutableJoiner requires a delimiter");
        this.delimiter = other.delimiter();
        this.elements  = new ArrayList<>(other.elements());
    }

    public String delimiter()
    {
        return delimiter;
    }

    public List<CharSequence> elements()
    {
        // only return a copy
        return new ArrayList<>(elements);
    }

    public MutableJoiner push(@Nullable final CharSequence addition)
    {
        if (addition != null && !addition.toString().isBlank()) {
            elements.add(addition);
        }
        return this;
    }

    public MutableJoiner pushAll(final CharSequence... additions)
    {
        if (additions.length > 0) {
            Arrays.stream(additions).forEachOrdered(this::push);
        }
        return this;
    }

    public MutableJoiner pop()
    {
        final int size;
        if ((size = elements.size()) > 0) {
            elements.remove(size - 1);
        }
        return this;
    }

    public MutableJoiner merge(@Nullable final MutableJoiner other)
    {
        if (other == null || other.elements.size() == 0) {
            return this;
        }
        other.forEach(this::push);
        return this;
    }

    public MutableJoiner copy()
    {
        return new MutableJoiner(this);
    }

    public Optional<String> getOptional()
    {
        return elements.size() == 0 ? Optional.empty() : Optional.of(String.join(delimiter, elements));
    }

    public Stream<CharSequence> mergeStreams(final MutableJoiner... others)
    {
        return Stream.concat(stream(), Arrays.stream(others).flatMap(MutableJoiner::stream));
    }


    /* CharSequence @Overrides */

    @Nonnull
    @Override
    public String toString()
    {
        return elements.size() == 0 ? EMPTY_STRING : String.join(delimiter, elements);
    }

    @Override
    public int length()
    {
        return elements.size() == 0 ? 0 : toString().length();
    }

    @Nonnull
    @Override
    public CharSequence subSequence(final int start, final int end)
    {
        return toString().subSequence(start, end);
    }

    @Override
    public char charAt(final int index)
    {
        return toString().charAt(index);
    }


    /* Object @Overrides */

    @Override
    public boolean equals(final Object obj)
    {
        return this == obj
            || obj instanceof MutableJoiner other
            && delimiter.equals(other.delimiter)
            // Same ordering required for equality of ArrayList
            && elements.equals(other.elements);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(delimiter, elements);
    }


    /* AbstractCollection @Overrides */

    @Override
    public boolean add(final CharSequence charSequence)
    {
        final var size = size();
        return push(charSequence).size() > size;
    }

    @Override
    public boolean addAll(@Nonnull final Collection<? extends CharSequence> other)
    {
        final var size = size();
        return pushAll(other.toArray(CharSequence[]::new)).size() > size;
    }

    @Override
    public boolean remove(final Object o)
    {
        return false;
    }

    @Override
    public boolean removeAll(@Nonnull final Collection<?> c)
    {
        return false;
    }

    @Override
    public void clear()
    {
        elements.clear();
    }

    @Override
    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(final Object o)
    {
        return elements.contains(o);
    }

    @Nonnull
    @Override
    public Object[] toArray()
    {
        return elements.toArray();
    }

    @Nonnull
    @Override
    public <T> T[] toArray(@Nonnull final T[] a)
    {
        return elements.toArray(a);
    }

    @Override
    public boolean containsAll(@Nonnull final Collection<?> c)
    {
        return elements.containsAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull final Collection<?> c)
    {
        return elements.retainAll(c);
    }

    @Nonnull
    @Override
    public Iterator<CharSequence> iterator()
    {
        return elements.iterator();
    }

    @Override
    public int size()
    {
        return elements.size();
    }

    @Override
    public void forEach(final Consumer<? super CharSequence> action)
    {
        elements.forEach(action);
    }

    @Override
    public Spliterator<CharSequence> spliterator()
    {
        return elements.spliterator();
    }


    /* Collection @Overrides */

    @Override
    public boolean removeIf(final Predicate<? super CharSequence> filter)
    {
        return elements.removeIf(filter);
    }

    @Override
    public Stream<CharSequence> stream()
    {
        return elements.stream();
    }

    @Override
    public Stream<CharSequence> parallelStream()
    {
        // should always be sequential to preserve order.
        return stream();
    }


    /* Instance providers */

    public static MutableJoiner newDotJoiner()
    {
        return new MutableJoiner(".");
    }

    public static MutableJoiner newSpaceJoiner()
    {
        return new MutableJoiner(" ");
    }

    public static MutableJoiner newCommaJoiner()
    {
        return new MutableJoiner(", ");
    }
}
