package common.tileentity;

import common.energy.CryptoEnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityBitcoinMiner extends TileEntity implements ITickable {

	private CryptoEnergyStorage storage = new CryptoEnergyStorage(100000);
	private String customName;
	public int energy = storage.getEnergyStored();
	public ItemStackHandler handler = new ItemStackHandler(1);
	public long walletAddress = 0L;
	
	/* 
	 *{
	 *	@Override
	 *	protected void onContentsChanged(int slot) {
	 *		if(!world.isRemote) {
	 *			//Put logic for filling wallet placed in machine with available/mined Bitcoins
	 *		}
	 *	}
	 *}
	 */	
	
	@Override
	public void update()
	{
		if(!handler.getStackInSlot(0).isEmpty()) 
		{
			//if (handler.getStackInSlot(0) == ItemHardwareWallet)
		}
		
		this.storage.receiveEnergy(100, false);
		this.storage.extractEnergy(0, false);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
	{
		if(capability == CapabilityEnergy.ENERGY) return (T)this.storage;
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T)this.handler;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
	{
		if(capability == CapabilityEnergy.ENERGY) return true;
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setTag("Inventory", this.handler.serializeNBT());
		//compound.setIntegeter("CookTime", this.cookTime);
		compound.setInteger("GuiEnergy", this.energy);
		compound.setString("Name", getDisplayName().toString());
		this.storage.writeToNBT(compound);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) 
	{
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		//this.cooktime = compound.getInteger("CookTime");
		this.energy = compound.getInteger("GuiEnergy");
		this.customName = compound.getString("Name");
		this.storage.readFromNBT(compound);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("container.bitcoin_miner");
	}
	
	public int getEnergyStored()
	{
		return this.energy;
	}
	
	public int getMaxEnergyStored()
	{
		return this.storage.getMaxEnergyStored();
	}
	
	public int getField(int id)
	{
		switch(id)
		{
		case 0:
			return this.energy;
		//case 1:
			//return this.cookTime;
		default:
			return 0;
		}
	}
	
	public void setField(int id, int value)
	{
		switch(id)
		{
		case 0:
			this.energy = value;
		//case 1:
			//this.cookTime = value;
		}
	}
	
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return this.world.getTileEntity(this.pos)!= this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D; 	
	}

	
}
