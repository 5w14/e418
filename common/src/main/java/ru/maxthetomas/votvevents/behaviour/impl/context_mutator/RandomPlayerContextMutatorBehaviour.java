package ru.maxthetomas.votvevents.behaviour.impl.context_mutator;

//import com.google.gson.JsonElement;
//import net.minecraft.world.entity.player.Player;
//import ru.maxthetomas.votvevents.event.EventContext;
//
//public class RandomPlayerContextMutatorBehaviour extends ContextMutatorBehaviour {
//
//    public RandomPlayerContextMutatorBehaviour(JsonElement jsonElement) {
//        super(jsonElement);
//    }
//
//    @Override
//    public EventContext getMutatedContext(EventContext context) {
//        var mutatedContext = context.clone();
//        var playerList = context.getServer().getPlayerList().getPlayers();
//
//        if (playerList.isEmpty())
//            return null;
//
//        var random = context.getServer().overworld().getRandom();
//
//        Player target = playerList.get(random.nextIntBetweenInclusive(0, playerList.size() - 1));
//
//        mutatedContext.withPlayer(target);
//
//        return mutatedContext;
//    }
//}
