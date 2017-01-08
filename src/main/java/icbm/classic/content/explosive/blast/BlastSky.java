package icbm.classic.content.explosive.blast;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import icbm.classic.Reference;
import icbm.classic.content.potion.CustomPotionEffect;
import icbm.classic.content.potion.PoisonFrostBite;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class BlastSky extends BlastBeam
{
    public BlastSky(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.red = 0f;
        this.green = 0.3f;
        this.blue = 0.7f;
    }

    @Override
    public void doExplode()
    {
        super.doExplode();
        this.world().playSoundEffect(position.x(), position.y(), position.z(), Reference.PREFIX + "redmatter", 4.0F, 0.8F);
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!this.world().isRemote)
        {
            if (this.canFocusBeam(this.world(), position) && this.thread.isComplete)
            {
                /*
                 * Freeze all nearby entities.
                 */
                List<EntityLiving> livingEntities = world().getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(position.x() - getRadius(), position.y() - getRadius(), position.z() - getRadius(), position.x() + getRadius(), position.y() + getRadius(), position.z() + getRadius()));

                Iterator<EntityLiving> it = livingEntities.iterator();

                while (it.hasNext())
                {
                    EntityLiving entity = it.next();
                    entity.addPotionEffect(new CustomPotionEffect(PoisonFrostBite.INSTANCE.getId(), 60 * 20, 1, null));
                    entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 10 * 20, 2));
                    entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 120 * 20, 2));
                    entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120 * 20, 4));
                }

                for (Pos targetPosition : this.thread.results)
                {
                    double distance = targetPosition.distance(position);

                    double distanceFromCenter = position.distance(targetPosition);

                    if (distanceFromCenter > this.getRadius())
                        continue;

                    /*
                     * Reduce the chance of setting blocks on fire based on distance from center.
                     */
                    double chance = this.getRadius() - (Math.random() * distanceFromCenter);

                    if (chance > distanceFromCenter * 0.55)
                    {
                        /*
                         * Place down ice blocks.
                         */
                        Block blockID = this.world().getBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());

                        if (blockID == Blocks.fire || blockID == Blocks.flowing_lava || blockID == Blocks.lava)
                        {
                            this.world().setBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), Blocks.snow, 0, 2);
                        }
                        else if (blockID == Blocks.air && this.world().getBlock(targetPosition.xi(), targetPosition.yi() - 1, targetPosition.zi()) != Blocks.ice && world().getBlock(targetPosition.xi(), targetPosition.yi() - 1, targetPosition.zi()) != Blocks.air)
                        {
                            this.world().setBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), Blocks.ice, 0, 2);
                        }
                    }
                }

                this.world().playSoundEffect(position.x() + 0.5D, position.y() + 0.5D, position.z() + 0.5D, Reference.PREFIX + "redmatter", 6.0F, (1.0F + (world().rand.nextFloat() - world().rand.nextFloat()) * 0.2F) * 1F);
            }

            this.world().setWorldTime(1200);
        }
    }

    @Override
    public boolean canFocusBeam(World worldObj, Location position)
    {
        long worldTime = worldObj.getWorldTime();

        while (worldTime > 23999)
        {
            worldTime -= 23999;
        }

        return worldTime > 12000 && super.canFocusBeam(worldObj, position);
    }

}
