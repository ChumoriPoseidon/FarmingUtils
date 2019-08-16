package cmpsd.farmingutils.entity.ai;

import cmpsd.farmingutils.entity.Entity_Kakashi;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAI_Kakashi extends EntityAIBase {

	private final Entity_Kakashi kakashi;
	private final double followSpeed;
	private final double minDist;
	private final double maxDist;
	private final PathNavigate pathNavigate;

	private World world;
	private EntityLivingBase owner;
	private int timeToRecalcPath;

	public EntityAI_Kakashi(Entity_Kakashi entityIn, double followSpeedIn, float minDistIn, float maxDistIn) {
		this.kakashi = entityIn;
		this.followSpeed = followSpeedIn;
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.pathNavigate = entityIn.getNavigator();
		this.world = kakashi.world;
		this.setMutexBits(3);
		if (!(entityIn.getNavigator() instanceof PathNavigateGround) && !(entityIn.getNavigator() instanceof PathNavigateFlying)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase owner = this.kakashi.getOwner();
		if(owner == null) {
			return false;
		}
		else if(owner instanceof EntityPlayer && ((EntityPlayer)owner).isSpectator()) {
			return false;
		}
		else if(this.kakashi.getDistanceSq(owner) < (double)(this.minDist * this.maxDist)) {
			return false;
		}
		else {
			this.owner = owner;
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.pathNavigate.noPath() && this.kakashi.getDistanceSq(this.owner) > (double)(this.minDist * this.maxDist);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
	}

	@Override
	public void resetTask() {
		this.owner = null;
		this.pathNavigate.clearPath();
	}

	@Override
	public void updateTask() {
		this.kakashi.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.kakashi.getVerticalFaceSpeed());
		if(--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;
			if(!this.pathNavigate.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
				if(!this.kakashi.getLeashed() && !this.kakashi.isRiding()) {
					if(this.kakashi.getDistanceSq(this.owner) >= 144.0D) {
						int x = MathHelper.floor(this.owner.posX) - 2;
						int y = MathHelper.floor(this.owner.getEntityBoundingBox().minY);
						int z = MathHelper.floor(this.owner.posZ) - 2;
						for(int dx = 0; dx <= 4; dx++) {
							for(int dz = 0; dz <= 4; dz++) {
								if((dx < 1 || dz < 1 || dx > 3 || dz > 3) && this.isTeleporNearOwner(x, y, z, dx, dz)) {
									this.kakashi.setLocationAndAngles((double)((float)(x + dx) + 0.5F), (double)y, (double)((float)(z + dz) + 0.5F), this.kakashi.rotationYaw, this.kakashi.rotationPitch);
									this.pathNavigate.clearPath();
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isTeleporNearOwner(int x, int y, int z, int dx, int dz) {
		BlockPos pos = new BlockPos(x + dx, y, z + dz);
		IBlockState state = this.world.getBlockState(pos);
		return state.getBlockFaceShape(this.world, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID && state.canEntitySpawn(this.kakashi) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
	}
}
