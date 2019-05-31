package common.blocks;

import java.util.List;
import java.util.Random;

import common.CryptoMod;
import common.storage.WorldSaveDataHandler;
import common.tileentity.TileEntityBitcoinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBitcoinMiner extends Block 
{

	public BlockBitcoinMiner() 
	{
		super(Material.WOOD);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) 
	{
		if(worldIn.isRemote) {
			return true;
		}
		else
		{
			playerIn.openGui(CryptoMod.instance, CryptoMod.GUI_BITCOIN_MINER, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	public TileEntity createTileEntity(World worldIn, IBlockState state)
	{
		return new TileEntityBitcoinMiner();
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) 
	{	
		TileEntity te = worldIn.getTileEntity(pos);
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("account"))
		{
			if(te instanceof TileEntityBitcoinMiner) {
				((TileEntityBitcoinMiner) te).walletAddress = stack.getTagCompound().getLong("account");
			}
		}
		else 
		{
			if(te instanceof TileEntityBitcoinMiner) 
			{
				WorldSaveDataHandler walletData = WorldSaveDataHandler.get(worldIn);
				((TileEntityBitcoinMiner) te).walletAddress = walletData.createNewWallet();
			}
		}
	}
	

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) 
	{
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots)
		{
			ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
			
			if(te instanceof TileEntityBitcoinMiner) 
			{
				TileEntityBitcoinMiner bitcoinminer = (TileEntityBitcoinMiner) te;
				//Need to get the data from TE..
				CryptoMod.getOrCreateTag(drop).setLong("account", ((TileEntityBitcoinMiner) te).walletAddress);
			}
			spawnAsEntity(worldIn, pos, drop);
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.getTagCompound() == null)
		{
			tooltip.add("TAG COMPOUND IS NULL");
		}
		else if (stack.getTagCompound() != null && !stack.getTagCompound().hasKey("account"))
		{
			tooltip.add(stack.getTagCompound().toString());
		}
		else if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("account")) {
			tooltip.add("Tag" + stack.getTagCompound().getLong("account"));
		}
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) 
	{
		TileEntityBitcoinMiner tileentity = (TileEntityBitcoinMiner)worldIn.getTileEntity(pos);
		worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileentity.handler.getStackInSlot(0)));
		super.breakBlock(worldIn, pos, state);
	}
	
	
	
}
