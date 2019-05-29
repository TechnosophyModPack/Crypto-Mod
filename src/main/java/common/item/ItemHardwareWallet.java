package common.item;

import java.util.List;

import javax.annotation.Nullable;

import common.CryptoMod;
import common.storage.WorldSaveDataHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHardwareWallet extends Item  {
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		
		if(!worldIn.isRemote) 
		{
			WorldSaveDataHandler walletData = WorldSaveDataHandler.get(worldIn);
			ItemStack stack = playerIn.getHeldItem(handIn);
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null) { nbt = new NBTTagCompound(); }
			
			if(!nbt.hasKey("account"))
			{
				Long walletID = walletData.createNewWallet();
				CryptoMod.logger.info("walletID: " + walletID);
				nbt.setLong("account", walletID);
				if (stack.getCount() > 1 ) 
				{
					stack.shrink(1);
					ItemStack copiedStack = stack.copy();
					copiedStack.shrink(copiedStack.getCount() - 1);
					copiedStack.setTagCompound(nbt);
					if(!playerIn.inventory.addItemStackToInventory(copiedStack)) 
					{
						playerIn.dropItem(copiedStack, false);
					}
				}
				else {
					stack.setTagCompound(nbt);
				}
			}
			
			double balance = walletData.getBalance(nbt.getLong("account"));
			
			playerIn.sendMessage(new TextComponentString("Bitcoins: " + balance));
			CryptoMod.logger.info("Bitcoins: " + balance);
		}
		
		return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		final NBTTagCompound compound = stack.getTagCompound();
		
		/* Debugging NBT Data
		 * if(stack.getTagCompound() == null) { CryptoMod.logger.info("NO TAGCOMPOUND FOUND"); }
		 *
		 *	else { CryptoMod.logger.info(stack.getTagCompound().toString());}
		 */
		
		if (stack.getTagCompound() != null && compound.hasKey("account")) { tooltip.add("Address: " + stack.getTagCompound().getLong("account")); }
		else { tooltip.add("Address: "); }
    }
	
	@Override
	public int getItemStackLimit(ItemStack stack) { return (stack.hasTagCompound()) ? 1 : 16;}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		
		//CryptoMod.logger.info("Ticking Item");
		
	}
	
}
