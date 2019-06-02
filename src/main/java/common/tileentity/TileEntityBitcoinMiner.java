package common.tileentity;

import common.CryptoMod;
import common.energy.CryptoEnergyStorage;
import common.storage.WorldSaveDataHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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
	public boolean isCurrentlyMining = false;
	public int localTicksSinceLastBlock = 0;

	
	public void update()
	{
		WorldSaveDataHandler worldSaveHandler = WorldSaveDataHandler.get(this.world);
		if(localTicksSinceLastBlock < worldSaveHandler.getTicksSinceLastBlock())
		{
			if(worldSaveHandler.getTicksSinceLastBlock() - localTicksSinceLastBlock > 1)
			{
				localTicksSinceLastBlock = worldSaveHandler.getTicksSinceLastBlock();
			}
		}
		if(localTicksSinceLastBlock > worldSaveHandler.getTicksSinceLastBlock())
		{
			localTicksSinceLastBlock = worldSaveHandler.getTicksSinceLastBlock();
		}
		if(world.isBlockPowered(pos)) energy += 100;
		
		if(!handler.getStackInSlot(0).isEmpty()) 
		{
			//if (handler.getStackInSlot(0) == ItemHardwareWallet)
		}
		
		if(energy > 200 /* && !world.isBlockPowered(pos)*/) {
			isCurrentlyMining = true;
			if(localTicksSinceLastBlock == worldSaveHandler.getTicksSinceLastBlock())
			{
				localTicksSinceLastBlock++;
				worldSaveHandler.setTicksSinceLastBlock(worldSaveHandler.getTicksSinceLastBlock() + 1);
			}
			CryptoMod.logger.info("Ticks Since Last Bitcoin Block: " + worldSaveHandler.getTicksSinceLastBlock());
			energy -= 200;
			worldSaveHandler.setTimeMined(this.getAddress(), worldSaveHandler.getTimeMined(this.getAddress()) + 1);
			if(worldSaveHandler.getTicksSinceLastBlock() >= 200) {
				worldSaveHandler.payoutRewards();
			}
		}
		else
		{
			isCurrentlyMining = false;
		}
		
		//this.storage.receiveEnergy(100, false);
		//this.storage.extractEnergy(0, false);
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
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("container.bitcoin_miner");
	}
	
	public long getAddress()
	{
		return this.walletAddress;
	}
	
	public void setAddress(long newAddr)
	{
		this.walletAddress = newAddr;
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
		}
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
	    return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
	    return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
	    readFromNBT(pkt.getNbtCompound());
	}
	
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return this.world.getTileEntity(this.pos)!= this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D; 	
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setTag("Inventory", this.handler.serializeNBT());
		compound.setInteger("GuiEnergy", this.energy);
		compound.setString("Name", getDisplayName().toString());
		compound.setLong("account", this.walletAddress);
		this.storage.writeToNBT(compound);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) 
	{
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		this.energy = compound.getInteger("GuiEnergy");
		this.customName = compound.getString("Name");
		this.walletAddress = compound.getLong("account");
		this.storage.readFromNBT(compound);
		CryptoMod.logger.info("Energy: " + compound.getInteger("GuiEnergy") + " Wallet Address: " + compound.getLong("account"));
	}
	
	/* 
	 *	@Override
	 *	protected void onContentsChanged(int slot) {
	 *		if(!world.isRemote) {
	 *			//Put logic for filling wallet placed in machine with available/mined Bitcoins
	 *		}
	 *	}
	 */	
	
}
