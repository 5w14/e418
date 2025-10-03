package ru.maxthetomas.e418.condition.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.numberprovider.NumberProvider;
import ru.maxthetomas.e418.codecs.numberprovider.NumberProviders;
import ru.maxthetomas.e418.codecs.numberprovider.impl.ConstantNumberProvider;
import ru.maxthetomas.e418.condition.ICondition;
import ru.maxthetomas.e418.event.EventContext;

/**
 * Returns true only when one or more conditions are true.
 * <ul>
 *   <li><code>conditions</code> - Conditions to check.</li>
 * </ul>
 */
public record OnSurfaceCondition(NumberProvider maxDistance) implements ICondition {
    public static final ResourceLocation ID = E418.resLoc("on_surface");
    public static final MapCodec<OnSurfaceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NumberProviders.CODEC.optionalFieldOf("max_distance", new ConstantNumberProvider(7f)).forGetter(OnSurfaceCondition::maxDistance)
    ).apply(instance, OnSurfaceCondition::new));


    @Override
    public boolean check(EventContext context) {
        if (!context.hasPlayer()) 
            return false;
        if (context.getPlayer() == null)
            return false;
        
        var player = context.getPlayer();

        var playerPos = player.position();
        var levelHeight = player.level().getHeight(Types.WORLD_SURFACE, (int)playerPos.x, (int)playerPos.y);

        var maxDist = maxDistance().get(context, this);
        return playerPos.y > levelHeight - maxDist.doubleValue();
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }
}
