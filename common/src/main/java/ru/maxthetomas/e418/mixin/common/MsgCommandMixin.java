package ru.maxthetomas.e418.mixin.common;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.e418.util.E418Variables;

import java.util.Collection;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {
    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    private static void sendMessage(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, PlayerChatMessage playerChatMessage, CallbackInfo ci) {
        if (E418Variables.PreventMsgUsage)
            ci.cancel();
    }
}
