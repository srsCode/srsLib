/*
 * Project      : srsLib
 * File         : ComponentUtil.java
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


import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;


@SuppressWarnings({"unused", "WeakerAccess"})
public final class ComponentUtil
{
    private static final Object[] EMPTY_ARGS = new Object[0];

    private ComponentUtil() {}


    /* Text & Component Helpers */

    /**
     * <h3>Creates a translatable Component for a langkey.</h3>
     *
     * @param key A langkey
     * @return    A translatable Component.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key)
    {
        return getTranslation(key, null, null, EMPTY_ARGS);
    }

    /**
     * <h3>Creates a translatable Component for a langkey or uses a fallback string if the langkey does not exist.</h3>
     *
     * @param key      A langkey
     * @param fallback An optional fallback string.
     * @return         A translatable Component.
     *
     * @since 4.0.0, MC 1.21, 2024.07.10
     */
    public static Component getTranslation(final String key, @Nullable final String fallback)
    {
        return getTranslation(key, fallback, null, EMPTY_ARGS);
    }

    /**
     * <h3>Creates a translatable Component for a langkey.</h3>
     *
     * @param key  A langkey
     * @param objs Objects to be used in a formatted text string
     * @return     A translatable Component, or a literal Component if the langkey does not exist
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key, final Object... objs)
    {
        return getTranslation(key, null, null, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey or uses a fallback string if the langkey does not exist.</h3>
     *
     * @param key      A langkey
     * @param fallback An optional fallback string
     * @param objs     Objects to be used in a formatted text string
     * @return         A translatable Component, or a literal Component if the langkey does not exist
     *
     * @since 4.0.0, MC 1.21, 2024.07.10
     */
    public static Component getTranslation(final String key, @Nullable final String fallback, final Object... objs)
    {
        return getTranslation(key, fallback, null, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey using an optional supplied Component.</h3>
     *
     * @param key       A langkey
     * @param component A possible Component to be used for translation (i.e. a {@link Player} display name)
     * @param objs      Objects to be used in a formatted text string
     * @return          A translatable Component, or a literal Component if the langkey does not exist
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key, @Nullable final Component component, final Object... objs)
    {
        return getTranslation(key, null, component, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey using an optional supplied Component or uses a fallback string if the langkey does not exist.</h3>
     *
     * @param key       A langkey
     * @param fallback  An optional fallback string
     * @param component A possible Component to be used for translation (i.e. a {@link Player} display name)
     * @param objs      Objects to be used in a formatted text string
     * @return          A translatable Component
     *
     * @since 4.0.0, MC 1.21, 2024.07.10
     */
    public static Component getTranslation(final String key, @Nullable final String fallback, @Nullable final Component component, final Object... objs)
    {
        final var fb = (fallback != null && fallback.isBlank()) ? null : fallback;
        return component == null ? Component.translatableWithFallback(key, fb, objs) : Component.translatableWithFallback(key, fb, component, objs);
    }
}
