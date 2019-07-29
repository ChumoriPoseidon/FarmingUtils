package cmpsd.farmingutils.item;

import cmpsd.farmingutils.ModItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class GrowthSoil extends Item {

	public GrowthSoil() {
		this.setRegistryName("item_growth_soil");
		this.setUnlocalizedName("growthSoil");
		this.setCreativeTab(CreativeTabs.MATERIALS);

		ModItem.ITEMS.add(this);
	}
}
