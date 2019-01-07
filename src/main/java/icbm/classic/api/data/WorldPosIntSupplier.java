package icbm.classic.api.data;

import net.minecraft.world.World;

/**
 *
 * Created by Dark(DarkGuardsman, Robert) on 1/7/19.
 */
@FunctionalInterface
public interface WorldPosIntSupplier
{
    int get(World world, double x, double y, double z);
}