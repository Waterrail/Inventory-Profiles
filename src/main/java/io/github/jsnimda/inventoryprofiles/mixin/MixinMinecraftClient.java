package io.github.jsnimda.inventoryprofiles.mixin;

import io.github.jsnimda.inventoryprofiles.config.Tweaks;
import io.github.jsnimda.inventoryprofiles.event.GameEventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinMinecraftClient
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

  @Shadow
  private int itemUseCooldown;

  @Inject(at = @At("HEAD"), method = "tick()V")
  public void tick(CallbackInfo info) {
    if (this.itemUseCooldown > 0 && Tweaks.INSTANCE.getDISABLE_ITEM_USE_COOLDOWN().getBooleanValue()) {
      this.itemUseCooldown = 0;
    }
  }

  @Inject(at = @At("RETURN"), method = "tick()V")
  public void tick2(CallbackInfo info) {
    GameEventHandler.INSTANCE.onTick();
  }

  @Inject(at = @At("RETURN"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
  public void joinWorld(ClientWorld clientWorld, CallbackInfo info) {
    GameEventHandler.INSTANCE.onJoinWorld();
  }
}
