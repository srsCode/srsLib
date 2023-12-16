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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        return BuiltInRegistries.BLOCK.getResourceKey(block).map(ResourceKey::location);
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
        return BuiltInRegistries.FLUID.getResourceKey(fluid).map(ResourceKey::location);
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
        return BuiltInRegistries.ITEM.getResourceKey(item).map(ResourceKey::location);
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
        return BuiltInRegistries.FEATURE.getResourceKey(feature).map(ResourceKey::location);
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
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(entityType).map(ResourceKey::location);
    }

    /**
     * <h3>A helper to get the associated {@link Registry} for an object like a Block or Item.</h3>
     *
     * @param obj An object to look up a IForgeRegistry for.
     * @param <T> The type of object.
     * @return    A registry instance associated with the object if it exist.
     */
    public static <T> Optional<Registry<T>> getRegistryFor(final T obj)
    {
        return RegistryHelper.getRegistryHelper(obj).map(RegistryHelper::registry).map(Supplier::get);
    }

    /**
     * <h3>A helper to get the associated {@link ResourceKey} of a {@link Registry} for an object like a Block or Item.</h3>
     *
     * @param obj An object to look up a ResourceKey for.
     * @param <T> The type of object.
     * @return    A ResourceKey of a registry associated with the object if it exist.
     */
    public static <T> Optional<ResourceKey<Registry<T>>> getKeyFor(final T obj)
    {
        return RegistryHelper.getRegistryHelper(obj).map(RegistryHelper::key);
    }


    /**
     * <h3>A helper class used for generating language keys for localisation.</h3>
     *
     * @param root    The root element of a key. (Generally a mod Id)
     * @param context A context foe the key. (command, block, item, biome, etc.)
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

    /**
     * <h3>A helper class to look up the associated {@link ResourceKey} or {@link Registry} for an object like a Block or Item.</h3>
     *
     * @param key      The Resource key for the registry.
     * @param registry A Supplier for the IForgeRegistry instance.
     * @param <T>      The object type of the registry.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08 - INTERNAL
     */
    private record RegistryHelper<T>(ResourceKey<Registry<T>> key, Supplier<Registry<T>> registry)
    {
        private static final Map<Class<?>, RegistryHelper<?>> HELPERS = new HashMap<>();

        static
        {
            HELPERS.put(Block.class,                  make(Registries.BLOCK,                     () -> BuiltInRegistries.BLOCK));
            HELPERS.put(Fluid.class,                  make(Registries.FLUID,                     () -> BuiltInRegistries.FLUID));
            HELPERS.put(Item.class,                   make(Registries.ITEM,                      () -> BuiltInRegistries.ITEM));
            HELPERS.put(MobEffect.class,              make(Registries.MOB_EFFECT,                () -> BuiltInRegistries.MOB_EFFECT));
            HELPERS.put(Potion.class,                 make(Registries.POTION,                    () -> BuiltInRegistries.POTION));
            HELPERS.put(Attribute.class,              make(Registries.ATTRIBUTE,                 () -> BuiltInRegistries.ATTRIBUTE));
            HELPERS.put(StatType.class,               make(Registries.STAT_TYPE,                 () -> BuiltInRegistries.STAT_TYPE));
            HELPERS.put(SoundEvent.class,             make(Registries.SOUND_EVENT,               () -> BuiltInRegistries.SOUND_EVENT));
            HELPERS.put(Enchantment.class,            make(Registries.ENCHANTMENT,               () -> BuiltInRegistries.ENCHANTMENT));
            HELPERS.put(EntityType.class,             make(Registries.ENTITY_TYPE,               () -> BuiltInRegistries.ENTITY_TYPE));
            HELPERS.put(PaintingVariant.class,        make(Registries.PAINTING_VARIANT,          () -> BuiltInRegistries.PAINTING_VARIANT));
            HELPERS.put(ParticleType.class,           make(Registries.PARTICLE_TYPE,             () -> BuiltInRegistries.PARTICLE_TYPE));
            HELPERS.put(MenuType.class,               make(Registries.MENU,                      () -> BuiltInRegistries.MENU));
            HELPERS.put(BlockEntityType.class,        make(Registries.BLOCK_ENTITY_TYPE,         () -> BuiltInRegistries.BLOCK_ENTITY_TYPE));
            HELPERS.put(RecipeType.class,             make(Registries.RECIPE_TYPE,               () -> BuiltInRegistries.RECIPE_TYPE));
            HELPERS.put(RecipeSerializer.class,       make(Registries.RECIPE_SERIALIZER,         () -> BuiltInRegistries.RECIPE_SERIALIZER));
            HELPERS.put(VillagerProfession.class,     make(Registries.VILLAGER_PROFESSION,       () -> BuiltInRegistries.VILLAGER_PROFESSION));
            HELPERS.put(PoiType.class,                make(Registries.POINT_OF_INTEREST_TYPE,    () -> BuiltInRegistries.POINT_OF_INTEREST_TYPE));
            HELPERS.put(MemoryModuleType.class,       make(Registries.MEMORY_MODULE_TYPE,        () -> BuiltInRegistries.MEMORY_MODULE_TYPE));
            HELPERS.put(SensorType.class,             make(Registries.SENSOR_TYPE,               () -> BuiltInRegistries.SENSOR_TYPE));
            HELPERS.put(Schedule.class,               make(Registries.SCHEDULE,                  () -> BuiltInRegistries.SCHEDULE));
            HELPERS.put(Activity.class,               make(Registries.ACTIVITY,                  () -> BuiltInRegistries.ACTIVITY));
            HELPERS.put(WorldCarver.class,            make(Registries.CARVER,                    () -> BuiltInRegistries.CARVER));
            HELPERS.put(Feature.class,                make(Registries.FEATURE,                   () -> BuiltInRegistries.FEATURE));
            HELPERS.put(ChunkStatus.class,            make(Registries.CHUNK_STATUS,              () -> BuiltInRegistries.CHUNK_STATUS));
            HELPERS.put(BlockStateProviderType.class, make(Registries.BLOCK_STATE_PROVIDER_TYPE, () -> BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE));
            HELPERS.put(FoliagePlacerType.class,      make(Registries.FOLIAGE_PLACER_TYPE,       () -> BuiltInRegistries.FOLIAGE_PLACER_TYPE));
            HELPERS.put(TreeDecoratorType.class,      make(Registries.TREE_DECORATOR_TYPE,       () -> BuiltInRegistries.TREE_DECORATOR_TYPE));
        }

        private static <T> RegistryHelper<T> make(final ResourceKey<Registry<T>> key, final Supplier<Registry<T>> registry)
        {
            return new RegistryHelper<>(key, registry);
        }

        @SuppressWarnings("unchecked")
        private static <T> Optional<RegistryHelper<T>> getRegistryHelper(final T obj)
        {
            return HELPERS.entrySet().stream().filter(e -> e.getKey().isAssignableFrom(obj.getClass())).findFirst().map(rh -> ((RegistryHelper<T>) rh.getValue()));
        }
    }
}
