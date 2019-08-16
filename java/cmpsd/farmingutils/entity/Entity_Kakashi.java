package cmpsd.farmingutils.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import cmpsd.farmingutils.entity.ai.EntityAI_Kakashi;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class Entity_Kakashi extends EntityGolem implements IEntityOwnable {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(Entity_Kakashi.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public Entity_Kakashi(World worldIn) {
		super(worldIn);
	}

	// Entityの初期設定？
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(1, new EntityAI_Kakashi(this, 1.0D, 2.0F, 5.0F));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (this.getOwnerId() == null) {
			compound.setString("OwnerUUID", "");
		}
		else {
			compound.setString("OwnerUUID", this.getOwnerId().toString());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	// IEntityOwnable

	public void setOwnerId(@Nullable UUID owner) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(owner));
	}

	@Nullable
	@Override
	public UUID getOwnerId() {
		return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	@Override
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		}
		catch(IllegalArgumentException error) {
			return null;
		}
	}

	public boolean isOwner(EntityLivingBase entity) {
		return entity == this.getOwner();
	}
}
