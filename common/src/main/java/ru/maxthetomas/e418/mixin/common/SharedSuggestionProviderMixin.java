package ru.maxthetomas.e418.mixin.common;

import com.google.common.base.Strings;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.e418.config.Config;

import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(SharedSuggestionProvider.class)
public interface SharedSuggestionProviderMixin {
    @Inject(
            method = "filterResources(Ljava/lang/Iterable;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static <T> void patchFilterResources(
            Iterable<T> iterable,
            String string,
            Function<T, ResourceLocation> function,
            Consumer<T> consumer,
            CallbackInfo ci
    ) {
        if (!Config.isDebug()) return;

        boolean bl = string.indexOf(':') > -1;

        for (T object : iterable) {
            ResourceLocation resourceLocation = function.apply(object);

            if (e418$shouldSkip(resourceLocation.toString()))
                continue;

            if (bl) {
                String string2 = resourceLocation.toString();
                if (SharedSuggestionProvider.matchesSubStr(string, string2)) {
                    consumer.accept(object);
                }
            } else if (SharedSuggestionProvider.matchesSubStr(string, resourceLocation.getNamespace())
                    || (resourceLocation.getNamespace().equals("minecraft")
                    && SharedSuggestionProvider.matchesSubStr(string, resourceLocation.getPath()))) {
                consumer.accept(object);
            }
        }

        ci.cancel();
    }

    @Inject(
            method = "filterResources(Ljava/lang/Iterable;Ljava/lang/String;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static <T> void patchFilterResources2(
            Iterable<T> iterable,
            String string,
            String string2,
            Function<T, ResourceLocation> function,
            Consumer<T> consumer,
            CallbackInfo ci
    ) {
        if (!Config.isDebug()) return;

        if (string.isEmpty()) {
            iterable.forEach(object -> {
                ResourceLocation rl = function.apply(object);

                if (e418$shouldSkip(rl.toString()))
                    return;

                consumer.accept(object);
            });
        } else {
            String string3 = Strings.commonPrefix(string, string2);
            if (!string3.isEmpty()) {
                String string5 = string.substring(string3.length());
                SharedSuggestionProvider.filterResources(iterable, string5, function, consumer);
            }
        }

        ci.cancel();
    }


    @Unique
    private static boolean e418$shouldSkip(String rl) {
        return Config.hiddenNamespaces.get().stream().anyMatch(rl::startsWith);
    }
}
