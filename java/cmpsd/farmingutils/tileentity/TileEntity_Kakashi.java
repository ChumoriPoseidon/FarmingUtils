package cmpsd.farmingutils.tileentity;

import cmpsd.farmingutils.block.Kakashi;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntity_Kakashi extends TileEntity implements ITickable {

	private int timeLeft;
	private boolean isTarget;
	private BlockPos targetPos;

	@Override
	public void update() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("TimeLeft", this.timeLeft);
		compound.setBoolean("IsTarget", this.isTarget);
		compound.setTag("TargetPos", NBTUtil.createPosTag(this.targetPos));
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.timeLeft = compound.getInteger("TimeLeft");
		this.isTarget = compound.getBoolean("IsTarget");
		if(compound.getBoolean("IsTarget")) {
			this.targetPos = NBTUtil.getPosFromTag(compound.getCompoundTag("TargetPos"));
		}
		else {
			this.targetPos = this.getTargetPos();
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 99, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(super.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.handleUpdateTag(pkt.getNbtCompound());
	}

	private void sync() {
		this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
		this.world.notifyBlockUpdate(pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
		this.world.scheduleBlockUpdate(this.pos, this.blockType, 0, 0);
		this.markDirty();
	}

	public boolean setTargetPos(BlockPos pos) {
		double distance = pos.distanceSq(this.pos);
		System.out.print("Distance: " + distance);
		if(distance <= 10.0D) {
			System.out.println(": Yes");
			this.targetPos = pos;
			this.isTarget = true;
			this.sync();
			return true;
		}
		System.out.println(": No");
		this.sync();
		return false;
	}

	public BlockPos getTargetPos() {
		if(this.isTarget) {
			return this.targetPos;
		}
		IBlockState blockState = this.world.getBlockState(this.pos);
		return this.pos.offset((EnumFacing)blockState.getValue(Kakashi.FACING));
	}
}
