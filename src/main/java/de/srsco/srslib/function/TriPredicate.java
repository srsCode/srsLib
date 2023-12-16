/*
 * Project      : srsLib
 * File         : TriPredicate.java
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
