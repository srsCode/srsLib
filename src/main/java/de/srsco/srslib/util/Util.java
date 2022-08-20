package de.srsco.srslib.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
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
import net.minecraft.world.level.material.Material;

import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;


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
     * Maps a ResourceKey to a ResourceLocation as {@link ForgeRegistry#getKey} returns the registry default key instead of null.
     *
     * @param block The Block to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Block block)
    {
        return ForgeRegistries.BLOCKS.getResourceKey(block).map(ResourceKey::location);
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Fluid}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link ForgeRegistry#getKey} returns the registry default key instead of null.
     *
     * @param fluid The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Fluid fluid)
    {
        return ForgeRegistries.FLUIDS.getResourceKey(fluid).map(ResourceKey::location);
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Item}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link ForgeRegistry#getKey} returns the registry default key instead of null.
     *
     * @param item The Item to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Item item)
    {
        return ForgeRegistries.ITEMS.getResourceKey(item).map(ResourceKey::location);
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Biome}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link ForgeRegistry#getKey} returns the registry default key instead of null.
     *
     * @param biome The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Biome biome)
    {
        return ForgeRegistries.BIOMES.getResourceKey(biome).map(ResourceKey::location);
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link Feature}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link ForgeRegistry#getKey} returns the registry default key instead of null.
     *
     * @param feature The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final Feature<?> feature)
    {
        return ForgeRegistries.FEATURES.getResourceKey(feature).map(ResourceKey::location);
    }

    /**
     * <h3>A helper method to get a {@link ResourceLocation} for a {@link EntityType}.</h3>
     * Maps a ResourceKey to a ResourceLocation as {@link ForgeRegistry#getKey} returns the registry default key instead of null.
     *
     * @param entityType The Biome to get a ResourceLocation for.
     * @return The ResourceLocation.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Optional<ResourceLocation> getResLoc(final EntityType<?> entityType)
    {
        return ForgeRegistries.ENTITY_TYPES.getResourceKey(entityType).map(ResourceKey::location);
    }

    /**
     * <h3>A helper to get the associated {@link IForgeRegistry} for an object like a Block or Item.</h3>
     *
     * @param obj An object to look up a IForgeRegistry for.
     * @param <T> The type of object.
     * @return    A registry instance associated with the object if it exist.
     */
    public static <T> Optional<IForgeRegistry<T>> getRegistryFor(final T obj)
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
     * Uses {@link ForgeI18n}
     *
     * @param key  A langkey
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final CharSequence key)
    {
        return getTranslation(key, CommonComponents.EMPTY);
    }

    /**
     * <h3>Creates a translatable Component for a langkey.</h3>
     * Uses {@link ForgeI18n}
     *
     * @param key  A langkey
     * @param objs Objects to be used in a formatted text string.
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final CharSequence key, final Object... objs)
    {
        return getTranslation(key.toString(), CommonComponents.EMPTY, null, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey using a possibly supplied Component.</h3>
     * Uses {@link ForgeI18n}
     *
     * @param component A possible Component to be used for translation (i.e. a {@link Player} display name).
     * @param key       A langkey
     * @param objs      Objects to be used in a formatted text string.
     * @return A translatable Component, or a literal Component if the langkey does not exist.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static Component getTranslation(final CharSequence key, @Nullable final Component component, final Object... objs)
    {
        return getTranslation(key.toString(), component, null, objs);
    }

    /**
     * <h3>Creates a translatable Component for a langkey using a possible supplied Component or uses a fallback string if the langkey does not exist.</h3>
     * Uses {@link ForgeI18n}
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
        return !ForgeI18n.getPattern(key).equals(key)
            ? component != null ? Component.translatable(key, component, objs) : Component.translatable(key, objs)
            : fallback  != null ? fallback : INVALID_LANGKEY.apply(key, Arrays.toString(objs));
    }


    /* Misc */

    /**
     * <h3>A factory method for creating a new DamageSource using a {@link LangKeyBuilder}.</h3>
     *
     * <p>The language key will be: &lt;root&gt;.dmgsrc.&lt;name&gt;[.killer]
     * Where 'root' will generally be a mod ID, 'name' will be the ID of the DamageSource,
     * and '.killer' will be added if the dead entity had an attacker with the attacker
     * being passed as a token object.</p>
     *
     * @param name     The ID of the DamageSource.
     * @param lkb      The LangKey builder used to generate a langkey from.
     * @param fallback A fallback message if the langkey does not exist.
     * @return         A new DamageSource.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static DamageSource createDamageSource(final String name, final LangKeyBuilder lkb, final String fallback)
    {
        return new DamageSource(name)
        {
            @Override
            @Nonnull
            public Component getLocalizedDeathMessage(@Nonnull final LivingEntity killed)
            {
                final var killer = killed.getKillCredit();
                return killer == null
                    ? getTranslation(lkb.append(name),             killed.getDisplayName(), fallback != null ? killed.getDisplayName().copy().append(" " + fallback) : null)
                    : getTranslation(lkb.append(name, "attacked"), killed.getDisplayName(), fallback != null ? killed.getDisplayName().copy().append(" " + fallback) : null, killer);
            }
        };
    }

    /**
     * <h3>Gets a String name for a vanilla {@link Material} from the {@link Materials} helper enum.</h3>
     * This will obviously only work for vanilla Materials.
     *
     * @param material The Material to get a name for.
     * @return A String name of the Material being looked up.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08
     */
    public static String getMaterialName(final Material material)
    {
        return Materials.getName(material);
    }

    /**
     * <h3>A helper enum to get String names for vanilla {@link Material}s.</h3>
     * (Getting field names through reflection is useless due to obfuscation.)
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08 - 49 Materials
     */
    public enum Materials
    {
        AIR(Material.AIR),
        STRUCTURAL_AIR(Material.STRUCTURAL_AIR),
        PORTAL(Material.PORTAL),
        CLOTH_DECORATION(Material.CLOTH_DECORATION),
        PLANT(Material.PLANT),
        WATER_PLANT(Material.WATER_PLANT),
        REPLACEABLE_PLANT(Material.REPLACEABLE_PLANT),
        REPLACEABLE_FIREPROOF_PLANT(Material.REPLACEABLE_FIREPROOF_PLANT),
        REPLACEABLE_WATER_PLANT(Material.REPLACEABLE_WATER_PLANT),
        WATER(Material.WATER),
        BUBBLE_COLUMN(Material.BUBBLE_COLUMN),
        LAVA(Material.LAVA),
        TOP_SNOW(Material.TOP_SNOW),
        FIRE(Material.FIRE),
        DECORATION(Material.DECORATION),
        WEB(Material.WEB),
        SCULK(Material.SCULK),
        BUILDABLE_GLASS(Material.BUILDABLE_GLASS),
        CLAY(Material.CLAY),
        DIRT(Material.DIRT),
        GRASS(Material.GRASS),
        ICE_SOLID(Material.ICE_SOLID),
        SAND(Material.SAND),
        SPONGE(Material.SPONGE),
        SHULKER_SHELL(Material.SHULKER_SHELL),
        WOOD(Material.WOOD),
        NETHER_WOOD(Material.NETHER_WOOD),
        BAMBOO_SAPLING(Material.BAMBOO_SAPLING),
        BAMBOO(Material.BAMBOO),
        WOOL(Material.WOOL),
        EXPLOSIVE(Material.EXPLOSIVE),
        LEAVES(Material.LEAVES),
        GLASS(Material.GLASS),
        ICE(Material.ICE),
        CACTUS(Material.CACTUS),
        STONE(Material.STONE),
        METAL(Material.METAL),
        SNOW(Material.SNOW),
        HEAVY_METAL(Material.HEAVY_METAL),
        BARRIER(Material.BARRIER),
        PISTON(Material.PISTON),
        MOSS(Material.MOSS),
        VEGETABLE(Material.VEGETABLE),
        EGG(Material.EGG),
        CAKE(Material.CAKE),
        AMETHYST(Material.AMETHYST),
        POWDER_SNOW(Material.POWDER_SNOW),
        FROGSPAWN(Material.FROGSPAWN),
        FROGLIGHT(Material.FROGLIGHT);

        private final Material material;

        Materials(final Material material)
        {
            this.material = material;
        }

        public static Optional<Materials> get(final Material mat)
        {
            return Arrays.stream(values()).filter(m -> Objects.equals(m.material, mat)).findFirst();
        }

        public static String getName(final Material mat)
        {
            return get(mat).map(Enum::name).orElse("[NULL]");
        }
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
     * <h3>A helper class to look up the associated {@link ResourceKey} or {@link IForgeRegistry} for an object like a Block or Item.</h3>
     *
     * @param key      The Resource key for the registry.
     * @param registry A Supplier for the IForgeRegistry instance.
     * @param <T>      The object type of the registry.
     *
     * @since 0.1.0, MC 1.19.1, 2022.08.08 - INTERNAL
     */
    private record RegistryHelper<T>(ResourceKey<Registry<T>> key, Supplier<IForgeRegistry<T>> registry)
    {
        private static final Map<Class<?>, RegistryHelper<?>> HELPERS = new HashMap<>();

        static
        {
            HELPERS.put(Block.class,                  make(Keys.BLOCKS,                     () -> ForgeRegistries.BLOCKS));
            HELPERS.put(Fluid.class,                  make(Keys.FLUIDS,                     () -> ForgeRegistries.FLUIDS));
            HELPERS.put(Item.class,                   make(Keys.ITEMS,                      () -> ForgeRegistries.ITEMS));
            HELPERS.put(MobEffect.class,              make(Keys.MOB_EFFECTS,                () -> ForgeRegistries.MOB_EFFECTS));
            HELPERS.put(Potion.class,                 make(Keys.POTIONS,                    () -> ForgeRegistries.POTIONS));
            HELPERS.put(Attribute.class,              make(Keys.ATTRIBUTES,                 () -> ForgeRegistries.ATTRIBUTES));
            HELPERS.put(StatType.class,               make(Keys.STAT_TYPES,                 () -> ForgeRegistries.STAT_TYPES));
            HELPERS.put(SoundEvent.class,             make(Keys.SOUND_EVENTS,               () -> ForgeRegistries.SOUND_EVENTS));
            HELPERS.put(Enchantment.class,            make(Keys.ENCHANTMENTS,               () -> ForgeRegistries.ENCHANTMENTS));
            HELPERS.put(EntityType.class,             make(Keys.ENTITY_TYPES,               () -> ForgeRegistries.ENTITY_TYPES));
            HELPERS.put(PaintingVariant.class,        make(Keys.PAINTING_VARIANTS,          () -> ForgeRegistries.PAINTING_VARIANTS));
            HELPERS.put(ParticleType.class,           make(Keys.PARTICLE_TYPES,             () -> ForgeRegistries.PARTICLE_TYPES));
            HELPERS.put(MenuType.class,               make(Keys.MENU_TYPES,                 () -> ForgeRegistries.MENU_TYPES));
            HELPERS.put(BlockEntityType.class,        make(Keys.BLOCK_ENTITY_TYPES,         () -> ForgeRegistries.BLOCK_ENTITY_TYPES));
            HELPERS.put(RecipeType.class,             make(Keys.RECIPE_TYPES,               () -> ForgeRegistries.RECIPE_TYPES));
            HELPERS.put(RecipeSerializer.class,       make(Keys.RECIPE_SERIALIZERS,         () -> ForgeRegistries.RECIPE_SERIALIZERS));
            HELPERS.put(VillagerProfession.class,     make(Keys.VILLAGER_PROFESSIONS,       () -> ForgeRegistries.VILLAGER_PROFESSIONS));
            HELPERS.put(PoiType.class,                make(Keys.POI_TYPES,                  () -> ForgeRegistries.POI_TYPES));
            HELPERS.put(MemoryModuleType.class,       make(Keys.MEMORY_MODULE_TYPES,        () -> ForgeRegistries.MEMORY_MODULE_TYPES));
            HELPERS.put(SensorType.class,             make(Keys.SENSOR_TYPES,               () -> ForgeRegistries.SENSOR_TYPES));
            HELPERS.put(Schedule.class,               make(Keys.SCHEDULES,                  () -> ForgeRegistries.SCHEDULES));
            HELPERS.put(Activity.class,               make(Keys.ACTIVITIES,                 () -> ForgeRegistries.ACTIVITIES));
            HELPERS.put(WorldCarver.class,            make(Keys.WORLD_CARVERS,              () -> ForgeRegistries.WORLD_CARVERS));
            HELPERS.put(Feature.class,                make(Keys.FEATURES,                   () -> ForgeRegistries.FEATURES));
            HELPERS.put(ChunkStatus.class,            make(Keys.CHUNK_STATUS,               () -> ForgeRegistries.CHUNK_STATUS));
            HELPERS.put(BlockStateProviderType.class, make(Keys.BLOCK_STATE_PROVIDER_TYPES, () -> ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES));
            HELPERS.put(FoliagePlacerType.class,      make(Keys.FOLIAGE_PLACER_TYPES,       () -> ForgeRegistries.FOLIAGE_PLACER_TYPES));
            HELPERS.put(TreeDecoratorType.class,      make(Keys.TREE_DECORATOR_TYPES,       () -> ForgeRegistries.TREE_DECORATOR_TYPES));
            HELPERS.put(Biome.class,                  make(Keys.BIOMES,                     () -> ForgeRegistries.BIOMES));
            // below Suppliers return new instances.
            HELPERS.put(FluidType.class,              make(Keys.FLUID_TYPES,                      ForgeRegistries.FLUID_TYPES));
            HELPERS.put(BiomeModifier.class,          make(Keys.BIOME_MODIFIERS,                  ForgeRegistries.BIOME_MODIFIERS_BUILTIN));
            HELPERS.put(StructureModifier.class,      make(Keys.STRUCTURE_MODIFIERS,              ForgeRegistries.STRUCTURE_MODIFIERS_BUILTIN));
        }

        private static <T> RegistryHelper<T> make(final ResourceKey<Registry<T>> key, final Supplier<IForgeRegistry<T>> registry)
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
