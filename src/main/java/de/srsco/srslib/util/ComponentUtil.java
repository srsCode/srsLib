/*
 * Project      : srsLib
 * File         : ComponentUtil.java
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
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.common.I18nExtension;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class ComponentUtil
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentUtil.class);
    private static final String EMPTY_STRING = "";

    private ComponentUtil() {}


    /* Text & Component Helpers */

    /**
     * <h3>A BiFunction to generate an error message for a missing langkey.</h3>
     * (If the langkey was not found in the lang file and a fallback was not provided.)
     * Logs an error as well as returns a Component for in-game feedback.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static final BiFunction<CharSequence, String, Component> INVALID_LANGKEY = (key, objs) -> {
        LOGGER.debug("Bad langkey: {}, Using Objects: {}", key, objs);
        return Component.literal("Bad langkey: " + key + ", Using Objects: " + objs);
    };

    /**
     * <h3>Creates a translatable Component for a langkey.</h3>
     * Uses {@link I18nExtension}
     *
     * @param key  A langkey
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key)
    {
        return getTranslation(key, CommonComponents.EMPTY);
    }

    /**
     * <h3>Creates a translatable Component for a langkey.</h3>
     * Uses {@link I18nExtension}
     *
     * @param key  A langkey
     * @param objs Objects to be used in a formatted text string.
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key, final Object... objs)
    {
        return getTranslation(key, CommonComponents.EMPTY, null, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey using a possibly supplied Component.</h3>
     * Uses {@link I18nExtension}
     *
     * @param component A possible Component to be used for translation (i.e. a {@link Player} display name).
     * @param key       A langkey
     * @param objs      Objects to be used in a formatted text string.
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key, @Nullable final Component component, final Object... objs)
    {
        return getTranslation(key, component, null, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey using a possible supplied Component or uses a fallback string if the langkey does not exist.</h3>
     * Uses {@link I18nExtension}
     *
     * @param component A possible Component to be used for translation (i.e. a {@link Player} display name).
     * @param key       A langkey
     * @param objs      Objects to be used in a formatted text string.
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final String key, @Nullable final Component component, @Nullable final Component fallback, final Object... objs)
    {
        return I18nExtension.getPattern(key, () -> EMPTY_STRING).equals(EMPTY_STRING)
            ? fallback  != null ? fallback : INVALID_LANGKEY.apply(key, Arrays.toString(objs))
            : component != null ? Component.translatable(key, component, objs) : Component.translatable(key, objs);
    }
}
