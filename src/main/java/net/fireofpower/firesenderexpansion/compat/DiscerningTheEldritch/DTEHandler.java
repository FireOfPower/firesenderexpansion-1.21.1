package net.fireofpower.firesenderexpansion.compat.DiscerningTheEldritch;

import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_edge.EsotericEdge;
import net.minecraft.world.entity.projectile.Projectile;

public class DTEHandler {
    public static boolean isEsotericEdge(Projectile proj){
        return proj instanceof EsotericEdge;
    }

    public static void init() {
    }
}
