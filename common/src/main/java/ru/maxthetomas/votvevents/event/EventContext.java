package ru.maxthetomas.votvevents.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public record EventContext(MinecraftServer server, ResourceLocation world) {
}
