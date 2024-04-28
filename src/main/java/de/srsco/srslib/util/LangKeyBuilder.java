/*
 * Project      : srsLib
 * File         : LangKeyBuilder.java
 *
 * Copyright (c) 2024 srs_bsns (forfrdm [at] gmail.com)
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

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <h3>A helper class used for generating language keys for localisation.</h3>
 *
 * @param root    The root element of a key. (Generally a mod Id)
 * @param context A context for the key. (command, block, item, biome, etc.)
 * @param stack   A stack of additional key elements that have been {@link #push}ed.
 * @param buffer  A buffer of key elements that have not been push to the stack that is cleared if a key is built.
 * @since 0.1.0, MC 1.19.1, 2022.08.08
 */
@SuppressWarnings("unused")
public record LangKeyBuilder(@Nonnull Supplier<String> root, @Nonnull Supplier<String> context, MutableJoiner stack, MutableJoiner buffer) implements CharSequence
{

    private static final Collector<CharSequence, MutableJoiner, Optional<String>> OPTIONAL_COLLECTOR = Collector.of(
        MutableJoiner::newDotJoiner, MutableJoiner::push, MutableJoiner::merge, MutableJoiner::getOptional);

    public LangKeyBuilder
    {
        Objects.requireNonNull(root, "LangKey requires a root element");
        Objects.requireNonNull(context, "LangKey requires a context element");
    }

    private LangKeyBuilder(final Supplier<String> root, final Supplier<String> context)
    {
        this(root, context, MutableJoiner.newDotJoiner(), MutableJoiner.newDotJoiner());
    }

    private LangKeyBuilder(final LangKeyBuilder other)
    {
        this(other.root(), other.context(), other.stack().copy(), other.buffer().copy());
    }

    @Override
    public int length()
    {
        return toString().length();
    }

    @Override
    public char charAt(final int index)
    {
        return toString().charAt(index);
    }

    @Nonnull
    @Override
    public CharSequence subSequence(final int start, final int end)
    {
        return toString().subSequence(start, end);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return this == obj || obj instanceof LangKeyBuilder(var rootOther, var contextOther, var stackOther, var bufferOther)
            && Objects.equals(root.get(), rootOther.get())
            && Objects.equals(context.get(), contextOther.get())
            && Objects.equals(stack, stackOther)
            && Objects.equals(buffer, bufferOther);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(toString());
    }

    @Nonnull
    @Override
    public String toString()
    {
        return path()
            .map(path -> String.join(".", root.get(), context.get(), path))
            .orElseGet(() -> String.join(".", root.get(), context.get()));
    }

    public String getKey()
    {
        final var ret = toString();
        buffer.clear();
        return ret;
    }

    public LangKeyBuilder append(final @Nullable CharSequence path)
    {
        if (path != null && !path.isEmpty()) {
            buffer.push(path);
        }
        return this;
    }

    public LangKeyBuilder append(final CharSequence... path)
    {
        if (path.length > 0) {
            Arrays.stream(path).forEachOrdered(this::append);
        }
        return this;
    }

    public Optional<String> path()
    {
        return stack.mergeStreams(buffer).collect(OPTIONAL_COLLECTOR);
    }

    public LangKeyBuilder push()
    {
        stack.merge(buffer);
        buffer.clear();
        return this;
    }

    public LangKeyBuilder pop()
    {
        stack.pop();
        buffer.clear();
        return this;
    }

    public LangKeyBuilder reset()
    {
        stack.clear();
        buffer.clear();
        return this;
    }

    public LangKeyBuilder copy()
    {
        return new LangKeyBuilder(this);
    }

    public static Supplier<LangKeyBuilder> from(final Supplier<String> root, final LangKeyContext context)
    {
        return () -> new LangKeyBuilder(root, context);
    }

    public enum LangKeyContext implements Supplier<String>
    {
        DAMAGE_SOURCE("dmgsrc"),
        COMMAND,
        BLOCK,
        FLUID,
        ITEM,
        BIOME,
        FEATURE,
        ENTITY_TYPE("entitytype");

        private final String context;

        LangKeyContext()
        {
            this.context = name().toLowerCase(Locale.ROOT);
        }

        LangKeyContext(final String context)
        {
            this.context = context;
        }

        @Override
        public String get()
        {
            return context;
        }
    }
}
