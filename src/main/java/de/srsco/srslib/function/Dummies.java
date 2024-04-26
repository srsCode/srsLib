/*
 * Project      : srsLib
 * File         : Dummies.java
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

import java.util.function.Supplier;


@SuppressWarnings("WeakerAccess")
public final class Dummies
{
    private Dummies() {}

    /**
     * <h3>A dummy Object.</h3>
     */
    private static final Object DUMMY_OBJECT = new Object();

    /**
     * <h3>Returns a single static Object used as a dummy.</h3>
     *
     * @return A dummy Object
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Object dummyObject()
    {
        return DUMMY_OBJECT;
    }

    /**
     * <h3>Returns a Supplier of a dummy Object.</h3>
     *
     * @return A dummy Supplier
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Supplier<Object> dummySupplier()
    {
        return Dummies::dummyObject;
    }
}
