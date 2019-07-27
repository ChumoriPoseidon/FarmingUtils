package cmpsd.farmingutils.item;

import java.util.List;

import cmpsd.farmingutils.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FarmersSeedBag extends Item {

	public FarmersSeedBag() {
		this.setRegistryName("item_farmers_seed_bag");
		this.setUnlocalizedName("farmersSeedBag");
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);

		this.setMaxDamage(1024);

		ModItem.ITEMS.add(this);
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		this.setDamage(stack, this.getMaxDamage(stack));
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemEnchantability() {
		return 15;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if(enchantment == Enchantments.EFFICIENCY) return true;
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(playerIn.isSneaking()) {
			ItemStack itemStack = playerIn.getHeldItem(handIn);
			ItemStack seed = this.getSeed(itemStack);
			if(seed.isEmpty()) {
				if(this.setSeed(worldIn, playerIn, handIn, itemStack)) {
					return new ActionResult(EnumActionResult.SUCCESS, itemStack);
				}
			}
			else {
				if(this.refillSeed(playerIn, seed)) {
					return new ActionResult(EnumActionResult.SUCCESS, itemStack);
				}
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	private boolean setSeed(World world, EntityPlayer player, EnumHand hand, ItemStack stack) {
		if(hand == EnumHand.MAIN_HAND) {
			ItemStack subStack = player.getHeldItemOffhand();
			if(!subStack.isEmpty()) {
				Item item = subStack.getItem();
				if(!(item instanceof IPlantable)) {
					return false;
				}
				IPlantable seed = (IPlantable)item;
				if(seed.getPlantType(world, player.getPosition()) == EnumPlantType.Crop) {
					ItemStack copy = subStack.copy();
					int value = subStack.getCount();
//					stack.damageItem(-value, player);
					stack.setItemDamage(this.getMaxDamage(stack) - value);
					subStack.shrink(value);
					NBTTagCompound nbt = stack.getTagCompound();
					if(nbt == null) {
						nbt = new NBTTagCompound();
					}
					NBTTagCompound nbtData = new NBTTagCompound();
					copy.setCount(1);
					copy.writeToNBT(nbtData);
					nbt.setTag("Seed", nbtData);
					stack.setTagCompound(nbt);

//					if(world.isRemote) {
//						System.out.println("Set Seed: " + value);
//						System.out.print("Stock: " + this.getDamage(stack));
//					}
					return true;
				}
			}
		}
		return false;
	}

	private boolean refillSeed(EntityPlayer player, ItemStack stack) {
		InventoryPlayer inventory = player.inventory;
		if(inventory.hasItemStack(stack) || this.getDamage(stack) > 0) {
			do {
				stack.damageItem(-1, player);
				int slot = inventory.getSlotFor(stack);
				if(slot > 0) {
					inventory.decrStackSize(slot, 1);
				}
				else if(player.getHeldItemOffhand().isItemEqual(stack)) {
					player.getHeldItemOffhand().shrink(1);
				}
			}while(inventory.hasItemStack(stack) || this.getDamage(stack) > 0);

//			if(player.world.isRemote) {
//				System.out.println("Refill Seed");
//				System.out.print("Stock: " + this.getDamage(stack));
//			}
			return true;
		}
		return false;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemStack = player.getHeldItem(hand);
		int range = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack) * 2 + 1;
		if(this.plantRangeSeed(worldIn, player, pos, itemStack, range)) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	private boolean plantRangeSeed(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {
		int result = 0;
		label: for(int dx = 0; dx < range; dx++) {
			for(int dz = 0; dz < range; dz++) {
				BlockPos posTarget = pos.add(-range / 2 + dx, 0, -range / 2 + dz);
				IBlockState stateTarget = world.getBlockState(posTarget);
				result += this.plantSeed(world, posTarget, stateTarget, stateTarget.getBlock(), this.getSeed(stack).getItem()) ? 1 : 0;
				if(stack.getItemDamage() + result >= this.getMaxDamage(stack)) break label;
			}
		}
		if(result > 0) {
			if(!world.isRemote) {
				stack.damageItem(result, player);
				if(stack.getItemDamage() == this.getMaxDamage(stack)) {
					NBTTagCompound nbt = stack.getTagCompound();
					nbt.removeTag("Seed");
					stack.setTagCompound(nbt);
					stack.setItemDamage(0);
				}
			}
		}
		return result > 0;
	}

	private boolean plantSeed(World world, BlockPos pos, IBlockState state, Block block, Item item) {
		if(item instanceof IPlantable) {
			IPlantable seed = (IPlantable)item;
			if(seed.getPlantType(world, pos) == EnumPlantType.Crop) {
				return this.plant(world, pos, state, seed);
			}
		}
		return false;
	}

	private boolean plant(World world, BlockPos pos, IBlockState state, IPlantable item) {
		if(state.getBlock().canSustainPlant(state, world, pos, EnumFacing.UP, item) && world.isAirBlock(pos.up())) {
			world.setBlockState(pos.up(), item.getPlant(world, pos));
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		ItemStack seed = this.getSeed(stack);
		if(!seed.isEmpty()) {
			tooltip.add(String.format("%s: %d / %d", seed.getDisplayName(), (this.getMaxDamage(stack) - this.getDamage(stack)), stack.getMaxDamage()));
		}
		else {
			tooltip.add("No Stock");
		}
	}

	private ItemStack getSeed(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			ItemStack seed = new ItemStack(nbt.getCompoundTag("Seed"));
			return seed;
		}
		return ItemStack.EMPTY;
	}
}
