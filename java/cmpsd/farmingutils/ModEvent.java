package cmpsd.farmingutils;

public class ModEvent {

	// グローブを持ったときに対象のカカシが見ているブロックを強調
//	@SubscribeEvent
//	@SideOnly(Side.CLIENT)
//	public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
//		EntityPlayer player = event.getPlayer();
//		RayTraceResult rayTraceResult = event.getTarget();
//		if(rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
//			return;
//		}
//		ItemStack heldItem = player.getHeldItemMainhand();
//		if(heldItem.isEmpty()) {
//			heldItem = player.getHeldItemOffhand();
//			if(heldItem.isEmpty()) {
//				return;
//			}
//		}
//		World world = player.world;
//		BlockPos pos = rayTraceResult.getBlockPos();
//	}
//
//
}
