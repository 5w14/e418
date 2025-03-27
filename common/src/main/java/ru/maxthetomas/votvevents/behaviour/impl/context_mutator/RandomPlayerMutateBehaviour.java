package ru.maxthetomas.votvevents.behaviour.impl.context_mutator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.PreActiveBehaviour;
import ru.maxthetomas.votvevents.condition.Conditions;
import ru.maxthetomas.votvevents.condition.ICondition;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.List;

public class RandomPlayerMutateBehaviour extends ContextMutatorBehaviour {
    public static final MapCodec<RandomPlayerMutateBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PreActiveBehaviour.CODEC.listOf().fieldOf("behaviours").forGetter(ContextMutatorBehaviour::getBehaviours),
            Conditions.DISPATCH_CODEC.listOf().fieldOf("run_conditions").forGetter(ContextMutatorBehaviour::getRunConditions)
    ).apply(instance, RandomPlayerMutateBehaviour::new));

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "context_select_random_player");

    protected RandomPlayerMutateBehaviour(List<PreActiveBehaviour> behaviours, List<ICondition> runConditions) {
        super(behaviours, runConditions);
    }


    @Override
    public EventContext getMutatedContext(EventContext context) {
        var mutatedContext = context.clone();
        var playerList = context.getServer().getPlayerList().getPlayers();

        if (playerList.isEmpty())
            return null;

        var random = context.getServer().overworld().getRandom();

        Player target = playerList.get(random.nextIntBetweenInclusive(0, playerList.size() - 1));

        mutatedContext.withPlayer(target);

        return mutatedContext;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
