package ru.maxthetomas.e418.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.e418.util.E418ClientVariables;

import java.util.Objects;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {
    @Inject(method = "renderSun", at = @At("HEAD"), cancellable = true)
    private void injectRenderSun(float f, MultiBufferSource multiBufferSource, PoseStack poseStack, CallbackInfo ci) {
        if (Objects.equals(E418ClientVariables.SunResource, ResourceLocation.withDefaultNamespace("empty"))) {
            return;
        }

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.celestial(E418ClientVariables.SunResource));
        int i = ARGB.white(f);
        Matrix4f matrix4f = poseStack.last().pose();

        if (matrix4f.get(1, 0) > 0.1f) {
            vertexConsumer.addVertex(matrix4f, -30.0F, 100.0F, -30.0F).setUv(0.0F, 1.0F).setColor(i);
            vertexConsumer.addVertex(matrix4f, 30.0F, 100.0F, -30.0F).setUv(1.0F, 1.0F).setColor(i);
            vertexConsumer.addVertex(matrix4f, 30.0F, 100.0F, 30.0F).setUv(1.0F, 0.0F).setColor(i);
            vertexConsumer.addVertex(matrix4f, -30.0F, 100.0F, 30.0F).setUv(0.0F, 0.0F).setColor(i);
        } else {
            vertexConsumer.addVertex(matrix4f, -30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F).setColor(i);
            vertexConsumer.addVertex(matrix4f, 30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F).setColor(i);
            vertexConsumer.addVertex(matrix4f, 30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F).setColor(i);
            vertexConsumer.addVertex(matrix4f, -30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F).setColor(i);
        }

        ci.cancel();
    }

    @Inject(method = "renderMoon", at = @At("HEAD"), cancellable = true)
    private void injectRenderMoon(int i, float f, MultiBufferSource multiBufferSource, PoseStack poseStack, CallbackInfo ci) {
        if (Objects.equals(E418ClientVariables.MoonResource, ResourceLocation.withDefaultNamespace("empty"))) {
            return;
        }

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.celestial(E418ClientVariables.MoonResource));
        int p = ARGB.white(f);
        Matrix4f matrix4f = poseStack.last().pose();

        if (matrix4f.get(1, 0) > 0.1f) {
            vertexConsumer.addVertex(matrix4f, -20.0F, -100.0F, 20.0F).setUv(1, 0).setColor(p);
            vertexConsumer.addVertex(matrix4f, 20.0F, -100.0F, 20.0F).setUv(0, 0).setColor(p);
            vertexConsumer.addVertex(matrix4f, 20.0F, -100.0F, -20.0F).setUv(0, 1).setColor(p);
            vertexConsumer.addVertex(matrix4f, -20.0F, -100.0F, -20.0F).setUv(1, 1).setColor(p);
        } else {
            vertexConsumer.addVertex(matrix4f, -20.0F, -100.0F, 20.0F).setUv(0, 1).setColor(p);
            vertexConsumer.addVertex(matrix4f, 20.0F, -100.0F, 20.0F).setUv(1, 1).setColor(p);
            vertexConsumer.addVertex(matrix4f, 20.0F, -100.0F, -20.0F).setUv(1, 0).setColor(p);
            vertexConsumer.addVertex(matrix4f, -20.0F, -100.0F, -20.0F).setUv(0, 0).setColor(p);
        }

        ci.cancel();
    }
}
