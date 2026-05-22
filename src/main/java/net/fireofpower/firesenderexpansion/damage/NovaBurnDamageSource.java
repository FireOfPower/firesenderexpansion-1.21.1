package net.fireofpower.firesenderexpansion.damage;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class NovaBurnDamageSource extends DamageSource {

    public static final ResourceKey<DamageType> NOVA_BURN_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "nova_burn"));

    public NovaBurnDamageSource(Entity pEntity) {
        super(getHolderFromResource(pEntity), pEntity);
    }

    private static Holder<DamageType> getHolderFromResource(Entity entity) {
        var option = entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(NOVA_BURN_DAMAGE);
        if (option.isPresent()) {
            return option.get();
        } else {
            return entity.level().damageSources().genericKill().typeHolder();
        }
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity pLivingEntity) {
        return Component.translatable("death.firesenderexpansion.nova_burn", pLivingEntity.getDisplayName());
    }
}
