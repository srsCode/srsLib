package de.srsco.srslib.function;

import java.util.function.Supplier;


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
