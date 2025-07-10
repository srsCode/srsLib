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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;


@SuppressWarnings({"unused", "WeakerAccess"})
public final class Util
{
    private Util() {}


    /* Logging */

    /**
     * <h3>Gets a new {@link Logger} instance for a Class.</h3>
     * If the passed object is a String or a Class, create a logger with it,
     * otherwise a Logger is created using a class lookup of the Object.
     *
     * @param obj The Class/Object to derive a Logger from.
     * @return    A logger instance for a class.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     * @deprecated @ 4.3.0 2025.07.10 - This seems pointless to have since Minecraft already has a useful util method for this
     * Keep this as-is until removal as defering to Minecraft LogUtils will produce an erroneous Logger as it is caller-sensitive
     */
    @Deprecated
    public static Logger getLogger(@NotNull final Object obj)
    {
        return switch (obj) {
            case String s   -> LoggerFactory.getLogger(s);
            case Class<?> c -> LoggerFactory.getLogger(c);
            default         -> LoggerFactory.getLogger(obj.getClass());
        };
    }

    /**
     * <h3>Gets a new {@link Logger} instance for a Class.</h3>
     * This is an alias of {@link com.mojang.logging.LogUtils#getLogger}
     * This must return a Supplier as #getLogger is caller-sensitive via a StackWalker
     *
     * @return A Supplier for a Logger instance.
     *
     * @since 4.3.0, MC 1.21.1, 2025.07.10
     */
    public static Supplier<Logger> getLogger()
    {
        return LogUtils::getLogger;
    }


    /* Common Registry Helpers */

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a registered object of nonambiguous type.</h3>
     *
     * @param obj The object to get a ResourceLocation for
     * @return    The ResourceKey
     *
     * @since 4.0.0, MC 1.21, 2024.07.10
     */
    public static <T> Optional<ResourceLocation> getResLoc(@NotNull final T obj)
    {
        return getResKey(obj).map(ResourceKey::location);
    }

    /**
     * <h3>An alias for {@link ResourceKey#create(ResourceKey, ResourceLocation)}</h3>
     *
     * @param registry The Registry to use for a new ResourceKey
     * @param resLoc   The ResourceLocation to create a new ResourceKey with
     * @param <T>      The type of object for the registry
     * @return         A new ResourceKey
     *
     * @since 4.3.0, MC 1.21, 2025.07.10
     */
    public static <T> ResourceKey<T> makeResKey(@NotNull final Registry<T> registry, @NotNull final ResourceLocation resLoc)
    {
        return makeResKey(registry.key(), resLoc);
    }

    /**
     * <h3>An alias for {@link ResourceKey#create(ResourceKey, ResourceLocation)}</h3>
     *
     * @param registryKey The ResourceKey for a Registry
     * @param resLoc      The ResourceLocation to create a new ResourceKey with
     * @param <T>         The type of object for the registry
     * @return            A new ResourceKey
     *
     * @since 4.3.0, MC 1.21, 2025.07.10
     */
    public static <T> ResourceKey<T> makeResKey(@NotNull final ResourceKey<? extends Registry<T>> registryKey, @NotNull final ResourceLocation resLoc)
    {
        return ResourceKey.create(registryKey, resLoc);
    }

    /**
     * <h3>A helper method to get a {@link ResourceKey} for a registered object of nonambiguous type.</h3>
     *
     * @param obj The object to get a ResourceKey for.
     * @return The ResourceKey.
     *
     * @since 4.0.0, MC 1.21, 2024.07.04
     */
    public static <T> Optional<ResourceKey<T>> getResKey(@NotNull final T obj)
    {
        return getRegistryFor(obj).flatMap(rh -> rh.getResourceKey(obj));
    }

    /**
     * <h3>A helper method to get a {@link ResourceKey} for a registered MapCodec.</h3>
     *
     * @param obj The MapCodec to get a ResourceKey for.
     * @return The ResourceKey.
     *
     * @since 4.0.0, MC 1.21, 2024.07.04
     */
    public static <T extends MapCodec<?>> Optional<ResourceKey<?>> getResKey(final T obj)
    {
        return Stream.of(
                // MapCodec registries as of 1.21
                BuiltInRegistries.BIOME_SOURCE,
                BuiltInRegistries.CHUNK_GENERATOR,
                BuiltInRegistries.MATERIAL_CONDITION,
                BuiltInRegistries.MATERIAL_RULE,
                BuiltInRegistries.DENSITY_FUNCTION_TYPE,
                BuiltInRegistries.BLOCK_TYPE,
                BuiltInRegistries.POOL_ALIAS_BINDING_TYPE,
                BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE,
                BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE,
                BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
                BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE,
                BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE,
                BuiltInRegistries.ENCHANTMENT_PROVIDER_TYPE,
                BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE
            )
            .flatMap(Registry::holders)
            .filter(h -> h.value().equals(obj))
            .findFirst()
            .map(Holder.Reference::key);
    }

    /**
     * <h3>A helper method to get a {@link ResourceKey} for a registered DataComponentType.</h3>
     *
     * @param obj The DataComponentType to get a ResourceKey for.
     * @return The ResourceKey.
     *
     * @since 4.0.0, MC 1.21, 2024.07.04
     */
    public static <T extends DataComponentType<?>> Optional<ResourceKey<?>> getResKey(final T obj)
    {
        return Stream.of(
                // DataComponentType registries as of 1.21
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE
            )
            .flatMap(Registry::holders)
            .filter(h -> h.value().equals(obj))
            .findFirst()
            .map(Holder.Reference::key);
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
    public static Optional<ResourceKey<Biome>> getResKey(final Level level, final Biome biome)
    {
        return level.registryAccess().registry(Registries.BIOME).flatMap(registry -> registry.getResourceKey(biome));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Registry<T>> getRegistryFor(final T obj)
    {
        return Optional.ofNullable(
            (Registry<T>) switch (obj) {
                case GameEvent                      ignored -> BuiltInRegistries.GAME_EVENT;
                case SoundEvent                     ignored -> BuiltInRegistries.SOUND_EVENT;
                case Fluid                          ignored -> BuiltInRegistries.FLUID;
                case MobEffect                      ignored -> BuiltInRegistries.MOB_EFFECT;
                case Block                          ignored -> BuiltInRegistries.BLOCK;
                case EntityType<?>                  ignored -> BuiltInRegistries.ENTITY_TYPE;
                case Item                           ignored -> BuiltInRegistries.ITEM;
                case Potion                         ignored -> BuiltInRegistries.POTION;
                case ParticleType<?>                ignored -> BuiltInRegistries.PARTICLE_TYPE;
                case BlockEntityType<?>             ignored -> BuiltInRegistries.BLOCK_ENTITY_TYPE;
                case ResourceLocation               ignored -> BuiltInRegistries.CUSTOM_STAT;
                case ChunkStatus                    ignored -> BuiltInRegistries.CHUNK_STATUS;
                case RuleTestType<?>                ignored -> BuiltInRegistries.RULE_TEST;
                case RuleBlockEntityModifierType<?> ignored -> BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER;
                case PosRuleTestType<?>             ignored -> BuiltInRegistries.POS_RULE_TEST;
                case MenuType<?>                    ignored -> BuiltInRegistries.MENU;
                case RecipeType<?>                  ignored -> BuiltInRegistries.RECIPE_TYPE;
                case RecipeSerializer<?>            ignored -> BuiltInRegistries.RECIPE_SERIALIZER;
                case Attribute                      ignored -> BuiltInRegistries.ATTRIBUTE;
                case PositionSourceType<?>          ignored -> BuiltInRegistries.POSITION_SOURCE_TYPE;
                case ArgumentTypeInfo<?, ?>         ignored -> BuiltInRegistries.COMMAND_ARGUMENT_TYPE;
                case StatType<?>                    ignored -> BuiltInRegistries.STAT_TYPE;
                case VillagerType                   ignored -> BuiltInRegistries.VILLAGER_TYPE;
                case VillagerProfession             ignored -> BuiltInRegistries.VILLAGER_PROFESSION;
                case PoiType                        ignored -> BuiltInRegistries.POINT_OF_INTEREST_TYPE;
                case MemoryModuleType<?>            ignored -> BuiltInRegistries.MEMORY_MODULE_TYPE;
                case SensorType<?>                  ignored -> BuiltInRegistries.SENSOR_TYPE;
                case Schedule                       ignored -> BuiltInRegistries.SCHEDULE;
                case Activity                       ignored -> BuiltInRegistries.ACTIVITY;
                case LootPoolEntryType              ignored -> BuiltInRegistries.LOOT_POOL_ENTRY_TYPE;
                case LootItemFunctionType<?>        ignored -> BuiltInRegistries.LOOT_FUNCTION_TYPE;
                case LootItemConditionType          ignored -> BuiltInRegistries.LOOT_CONDITION_TYPE;
                case LootNumberProviderType         ignored -> BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE;
                case LootNbtProviderType            ignored -> BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE;
                case LootScoreProviderType          ignored -> BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE;
                case FloatProviderType<?>           ignored -> BuiltInRegistries.FLOAT_PROVIDER_TYPE;
                case IntProviderType<?>             ignored -> BuiltInRegistries.INT_PROVIDER_TYPE;
                case HeightProviderType<?>          ignored -> BuiltInRegistries.HEIGHT_PROVIDER_TYPE;
                case BlockPredicateType<?>          ignored -> BuiltInRegistries.BLOCK_PREDICATE_TYPE;
                case WorldCarver<?>                 ignored -> BuiltInRegistries.CARVER;
                case Feature<?>                     ignored -> BuiltInRegistries.FEATURE;
                case StructurePlacementType<?>      ignored -> BuiltInRegistries.STRUCTURE_PLACEMENT;
                case StructurePieceType             ignored -> BuiltInRegistries.STRUCTURE_PIECE;
                case StructureType<?>               ignored -> BuiltInRegistries.STRUCTURE_TYPE;
                case PlacementModifierType<?>       ignored -> BuiltInRegistries.PLACEMENT_MODIFIER_TYPE;
                case BlockStateProviderType<?>      ignored -> BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE;
                case FoliagePlacerType<?>           ignored -> BuiltInRegistries.FOLIAGE_PLACER_TYPE;
                case TrunkPlacerType<?>             ignored -> BuiltInRegistries.TRUNK_PLACER_TYPE;
                case RootPlacerType<?>              ignored -> BuiltInRegistries.ROOT_PLACER_TYPE;
                case TreeDecoratorType<?>           ignored -> BuiltInRegistries.TREE_DECORATOR_TYPE;
                case FeatureSizeType<?>             ignored -> BuiltInRegistries.FEATURE_SIZE_TYPE;
                case StructureProcessorType<?>      ignored -> BuiltInRegistries.STRUCTURE_PROCESSOR;
                case StructurePoolElementType<?>    ignored -> BuiltInRegistries.STRUCTURE_POOL_ELEMENT;
                case CatVariant                     ignored -> BuiltInRegistries.CAT_VARIANT;
                case FrogVariant                    ignored -> BuiltInRegistries.FROG_VARIANT;
                case Instrument                     ignored -> BuiltInRegistries.INSTRUMENT;
                case DecoratedPotPattern            ignored -> BuiltInRegistries.DECORATED_POT_PATTERN;
                case CreativeModeTab                ignored -> BuiltInRegistries.CREATIVE_MODE_TAB;
                case CriterionTrigger<?>            ignored -> BuiltInRegistries.TRIGGER_TYPES;
                case NumberFormatType<?>            ignored -> BuiltInRegistries.NUMBER_FORMAT_TYPE;
                case ArmorMaterial                  ignored -> BuiltInRegistries.ARMOR_MATERIAL;
                case ItemSubPredicate.Type<?>       ignored -> BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE;
                case MapDecorationType              ignored -> BuiltInRegistries.MAP_DECORATION_TYPE;
                default                                     -> null;
            }
        );
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<Registry<T>> getRegistryFor(final ResourceKey<T> key)
    {
        if (key.registry().equals(Registries.ROOT_REGISTRY_NAME)) {
            return BuiltInRegistries.REGISTRY.getHolder(key.location()).map(holder -> (Registry<T>) holder.value());
        } else {
            return BuiltInRegistries.REGISTRY.getHolder(key.registry()).map(holder -> (Registry<T>) holder.value());
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
    public record LangKeyBuilder(@NotNull Supplier<String> root, @NotNull Supplier<String> context, MutableJoiner stack, MutableJoiner buffer) implements CharSequence
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

        @NotNull
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

        @NotNull
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
