package cmpsd.farmingutils.item;

import cmpsd.farmingutils.Main;
import cmpsd.farmingutils.ModItem;
import cmpsd.farmingutils.entity.Entity_Kakashi;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Debugger extends Item {

	public Debugger() {
		this.setRegistryName("item_debugger");
		this.setUnlocalizedName("debugger");
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);

		ModItem.ITEMS.add(this);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote) {
			if(player.isSneaking()) {
				Entity_Kakashi entityKakashi = new Entity_Kakashi(worldIn);
				entityKakashi.setOwnerId(player.getUniqueID());
				entityKakashi.setLocationAndAngles(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, player.cameraYaw, player.cameraPitch);
				worldIn.spawnEntity(entityKakashi);
				Main.LOGGER.info("Spawn at : " + hitX + "," + hitY + "," + hitZ);
				return EnumActionResult.SUCCESS;
			}
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
