/*
 * Project      : srsLib
 * File         : Util.java
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.material.Fluid;


@SuppressWarnings({"unused", "WeakerAccess"})
public final class Util
{
    private static final Logger LOGGER = getLogger(Util.class);

    private Util() {}


    /* Logging */

    /**
     * <h3>Gets a new {@link Logger} instance for a Class.</h3>
     * If the passed object is a String or a Class, create a logger with it,
     * otherwise a Logger is created using a class lookup of the Object.
     *
     * TODO: Migrate to pattern matching when it is no longer a preview feature. (Java19?)
     *
     * @param obj The Class/Object to derive a Logger from.
     * @return    A logger instance for a class.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Logger getLogger(@Nonnull final Object obj)
    {
        return switch (obj.getClass().getName()) {
            case    "java.lang.String" -> LoggerFactory.getLogger((String) obj);
            case    "java.lang.Class"  -> LoggerFactory.getLogger((Class<?>) obj);
            default                    -> LoggerFactory.getLogger(obj.getClass());
        };
    }


    /* Common Registry Helpers */

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Block}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link DefaultedRegistry#getKey} returns the registry default key instead of null.
     *
     * @param block The Block to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Block block)
    {
        return getRegistryFor(Block.class).flatMap(rh -> rh.getResourceKey(block).map(ResourceKey::location));
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Fluid}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link DefaultedRegistry#getKey} returns the registry default key instead of null.
     *
     * @param fluid The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Fluid fluid)
    {
        return getRegistryFor(Fluid.class).flatMap(rh -> rh.getResourceKey(fluid).map(ResourceKey::location));
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Item}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link DefaultedRegistry#getKey} returns the registry default key instead of null.
     *
     * @param item The Item to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Item item)
    {
        return getRegistryFor(Item.class).flatMap(rh -> rh.getResourceKey(item).map(ResourceKey::location));
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Feature}.</h3>
     * Maps a ResourceKey to a ResourceLocation.
     *
     * @param feature The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Feature<?> feature)
    {
        return getRegistryFor(Feature.class).flatMap(rh -> rh.getResourceKey(feature).map(ResourceKey::location));
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link EntityType}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link DefaultedRegistry#getKey} returns the registry default key instead of null.
     *
     * @param entityType The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final EntityType<?> entityType)
    {
        return getRegistryFor(EntityType.class).flatMap(rh -> rh.getResourceKey(entityType).map(ResourceKey::location));
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Biome} from a {@link Level}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link DefaultedRegistry#getKey} returns the registry default key instead of null.
     *
     * @param level The Level to get the Biome registry from.
     * @param biome The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Level level, final Biome biome)
    {
        return level.registryAccess().registry(Registries.BIOME).flatMap(registry -> registry.getResourceKey(biome).map(ResourceKey::location));
    }

    /**
     * <h3>A helper to get the associated {@link ResourceKey} of a {@link Registry} for an object like a Block or Item.</h3>
     *
     * @param obj An object to look up a ResourceKey for.
     * @param <T> The type of object.
     * @return    A ResourceKey of a registry associated with the object if it exist.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<ResourceKey<Registry<T>>> getKeyFor(final Class<T> obj)
    {
        return getRegistryFor(obj).map(Registry::key).map(key -> (ResourceKey<Registry<T>>) key);
    }

    /**
     * <h3>A helper to get the associated {@link Registry} for an object like a Block or Item.</h3>
     *
     * @param type An object to look up a IForgeRegistry for.
     * @param <T> The type of object.
     * @return    A registry instance associated with the object if it exist.
     */
    public static <T> Optional<Registry<T>> getRegistryFor(final Class<T> type)
    {
        return RegistryHelper.getFromType(type);
    }

    public static final class Identity
    {
        private Identity() {}

        /**
         * <h3>A Helper to check if two {@link ResourceKey}s are equal base on their internal data.</h3>
         *
         * @param first  The first ResourceKey
         * @param second The second ResourceKey
         * @return       true if the ResourceKeys are equal
         */
        public static boolean isEqual(final ResourceKey<?> first, final ResourceKey<?> second)
        {
            return first.registry().equals(second.registry()) && first.location().equals(second.location());
        }

        /**
         * <h3>A Helper to generate a hashCode for a {@link ResourceKey} from its internal data.</h3>
         *
         * @param key The ResourceKey
         * @return    The hashCode
         */
        public static int getResKeyHashCode(final ResourceKey<?> key)
        {
            return 31 * key.registry().hashCode() + key.location().hashCode();
        }
    }

    /**
     * <h3>A helper class to get the associated {@link Registry} for an object like a Block or Item.</h3>
     *
     * @since 2.0.0, MC 1.20.4, 2023.12.18 - INTERNAL
     */
    private static final class RegistryHelper
    {

        /**
         * Use a List instead of a HashSet because we don't want RegistryEntry#hashCode being called from a static context, which will cause
         * the associated Registry to be supplied to get it's RegistryKey. This could cause a ClassNotFoundException in a dev environment
         * since this will likely load before Minecraft classes.
         */
        private static final List<RegistryEntry<?>> ENTRIES;

        static
        {
            ENTRIES = ImmutableList.<RegistryEntry<?>>builder().add(
                RegistryEntry.make(Block.class,                  () -> BuiltInRegistries.BLOCK),
                RegistryEntry.make(Fluid.class,                  () -> BuiltInRegistries.FLUID),
                RegistryEntry.make(Item.class,                   () -> BuiltInRegistries.ITEM),
                RegistryEntry.make(MobEffect.class,              () -> BuiltInRegistries.MOB_EFFECT),
                RegistryEntry.make(Potion.class,                 () -> BuiltInRegistries.POTION),
                RegistryEntry.make(Attribute.class,              () -> BuiltInRegistries.ATTRIBUTE),
                RegistryEntry.make(StatType.class,               () -> BuiltInRegistries.STAT_TYPE),
                RegistryEntry.make(SoundEvent.class,             () -> BuiltInRegistries.SOUND_EVENT),
                RegistryEntry.make(Enchantment.class,            () -> BuiltInRegistries.ENCHANTMENT),
                RegistryEntry.make(EntityType.class,             () -> BuiltInRegistries.ENTITY_TYPE),
                RegistryEntry.make(PaintingVariant.class,        () -> BuiltInRegistries.PAINTING_VARIANT),
                RegistryEntry.make(ParticleType.class,           () -> BuiltInRegistries.PARTICLE_TYPE),
                RegistryEntry.make(MenuType.class,               () -> BuiltInRegistries.MENU),
                RegistryEntry.make(BlockEntityType.class,        () -> BuiltInRegistries.BLOCK_ENTITY_TYPE),
                RegistryEntry.make(RecipeType.class,             () -> BuiltInRegistries.RECIPE_TYPE),
                RegistryEntry.make(RecipeSerializer.class,       () -> BuiltInRegistries.RECIPE_SERIALIZER),
                RegistryEntry.make(VillagerProfession.class,     () -> BuiltInRegistries.VILLAGER_PROFESSION),
                RegistryEntry.make(PoiType.class,                () -> BuiltInRegistries.POINT_OF_INTEREST_TYPE),
                RegistryEntry.make(MemoryModuleType.class,       () -> BuiltInRegistries.MEMORY_MODULE_TYPE),
                RegistryEntry.make(SensorType.class,             () -> BuiltInRegistries.SENSOR_TYPE),
                RegistryEntry.make(Schedule.class,               () -> BuiltInRegistries.SCHEDULE),
                RegistryEntry.make(Activity.class,               () -> BuiltInRegistries.ACTIVITY),
                RegistryEntry.make(WorldCarver.class,            () -> BuiltInRegistries.CARVER),
                RegistryEntry.make(Feature.class,                () -> BuiltInRegistries.FEATURE),
                RegistryEntry.make(ChunkStatus.class,            () -> BuiltInRegistries.CHUNK_STATUS),
                RegistryEntry.make(BlockStateProviderType.class, () -> BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE),
                RegistryEntry.make(FoliagePlacerType.class,      () -> BuiltInRegistries.FOLIAGE_PLACER_TYPE),
                RegistryEntry.make(TreeDecoratorType.class,      () -> BuiltInRegistries.TREE_DECORATOR_TYPE)
            ).build();
        }

        @SuppressWarnings("unchecked")
        public static <T> Optional<Registry<T>> getFromType(final Class<T> type)
        {
            return ENTRIES.stream()
                .filter(entry -> entry.type().isAssignableFrom(type))
                .findFirst()
                .map(RegistryEntry::registry)
                .map(Supplier::get)
                .map(registry -> (Registry<T>) registry);
        }

        /**
         * <h3>An entry class linking a Class to an associated {@link Registry} for an object like a Block or Item.</h3>
         *
         * @param registry A Supplier for the Registry instance.
         * @param <T>      The object type of the registry.
         */
        public record RegistryEntry<T>(Class<? super T> type, Supplier<Registry<T>> registry)
        {
            /**
             * The {@link ResourceKey} of the {@link Registry} is sufficient in determining equality.
             */
            @Override
            public boolean equals(final Object obj)
            {
                return this == obj || obj instanceof RegistryEntry<?> other && Identity.isEqual(registry().get().key(), other.registry().get().key());
            }

            /**
             * The {@link ResourceKey} of the {@link Registry} is sufficient in calculating the hashCode.
             */
            @Override
            public int hashCode()
            {
                return Identity.getResKeyHashCode(registry().get().key());
            }

            private static <T> RegistryEntry<T> make(final Class<? super T> type, final Supplier<Registry<T>> registry)
            {
                return new RegistryEntry<>(type, registry);
            }
        }
    }


    /* Language keys */

    /**
     * <h3>A helper class used for generating language keys for localisation.</h3>
     *
     * @param root    The root element of a key. (Generally a mod Id)
     * @param context A context for the key. (command, block, item, biome, etc.)
     * @param stack   A stack of additional key elements that have been {@link #push}ed.
     * @param buffer  A buffer of key elements that have not been push to the stack that is cleared if a key is built.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
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
            return this == obj || obj instanceof LangKeyBuilder other
                && Objects.equals(root.get(), other.root.get())
                && Objects.equals(context.get(), other.context.get())
                && Objects.equals(stack, other.stack)
                && Objects.equals(buffer, other.buffer);
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
}
