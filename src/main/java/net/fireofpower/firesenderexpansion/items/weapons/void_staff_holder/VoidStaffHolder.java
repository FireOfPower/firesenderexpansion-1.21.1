package net.fireofpower.firesenderexpansion.items.weapons.void_staff_holder;

import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.fireofpower.firesenderexpansion.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class VoidStaffHolder extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public VoidStaffHolder() {
        super(ItemPropertiesHelper.equipment(1).rarity(Rarity.UNCOMMON).fireResistant());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.firesenderexpansion.void_staff_holder.description").
                withStyle(ChatFormatting.DARK_PURPLE));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        level.getEntitiesOfClass(BlackHole.class,
                        player.getBoundingBox()
                                .inflate(10))
                .forEach(e -> {
                    if(e.getOwner().equals(player) && player.getMainHandItem().is(net.fireofpower.firesenderexpansion.registries.ItemRegistry.VOID_STAFF_HOLDER) && Config.allowFillingVoidStaffWithBlackHole){
                        e.discard();
                        level.playSound(player, player.position().x, player.position().y, player.position().z, SoundRegistry.BLACK_HOLE_CAST, SoundSource.PLAYERS, 1.0F, 1.0F);
                        EarthquakeAoe aoe = new EarthquakeAoe(level);
                        aoe.moveTo(player.position());
                        aoe.setOwner(player);
                        aoe.setCircular();
                        aoe.setRadius(6);
                        aoe.setDuration(20);
                        aoe.setDamage(0);
                        aoe.setSlownessAmplifier(0);
                        level.addFreshEntity(aoe);
                        player.setItemInHand(usedHand,new ItemStack(net.fireofpower.firesenderexpansion.registries.ItemRegistry.VOID_STAFF));
                    }
                });
        return super.use(level, player, usedHand);
    }

    private final AnimationController<VoidStaffHolder> animationController = new AnimationController<>(this, "controller", 0, this::predicate);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private VoidStaffHolderRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new VoidStaffHolderRenderer();

                return this.renderer;
            }
        });
    }

    private PlayState predicate(AnimationState<VoidStaffHolder> event)
    {
        //event.getController().setAnimation(IDLE_ANIMATION);
        return PlayState.CONTINUE;
    }
}
