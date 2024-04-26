/*
 * Project      : srsLib
 * File         : SLFunctions.java
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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


@SuppressWarnings("unused")
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
