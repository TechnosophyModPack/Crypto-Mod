package common.storage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import common.CryptoMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class WorldSaveDataHandler extends WorldSavedData
{
	private static final String StorageKey = "bitcoinStorageManager";
	private Map<Long, Double> walletInfo = new HashMap<>();
	private Map<Long, Integer> timeMinedInfo = new HashMap<>();
	private int lastID;
	private long nextFreeID = 0;
	private int ticksSinceLastBlock = 0;
	
	public WorldSaveDataHandler() 
	{
		super(StorageKey);
	}

	public WorldSaveDataHandler(String s)
	{
		super(s);
	}
	
	public static WorldSaveDataHandler get(World world)
	{
		MapStorage storage = world.getMapStorage();
		WorldSaveDataHandler instance = (WorldSaveDataHandler) storage.getOrLoadData(WorldSaveDataHandler.class, StorageKey);
		if (instance == null)
		{
			instance = new WorldSaveDataHandler();
			storage.setData(StorageKey, instance);
		}
		return instance;
	}
	
	public int getTicksSinceLastBlock()
	{
		return ticksSinceLastBlock;
	}
	
	public void setTicksSinceLastBlock(int amount)
	{
		ticksSinceLastBlock = amount;
		this.markDirty();
	}

	public Double getBalance(Long account)
	{
		if (account == null || !walletInfo.containsKey(account))
		{
			return null;
		}
		return walletInfo.get(account);
	}
	
	public void setBalance(Long account, Double newBalance)
	{
		if (account != null && walletInfo.containsKey(account))
		{
			walletInfo.put(account, newBalance);
			this.markDirty();
		}
		CryptoMod.logger.info("Invalid or Null account called for setBalance!");
	}
	
	public int getTimeMined(Long account)
	{
		if (account == null || !timeMinedInfo.containsKey(account))
		{
			return 0;
		}
		return timeMinedInfo.get(account);
	}
	
	public void setTimeMined(Long account, int amount)
	{
		if (account != null)
		{
			timeMinedInfo.put(account, amount);
			this.markDirty();
		}
		CryptoMod.logger.info("Invalid or Null account called for set balance");
	}
	
	public void payoutRewards() 
	{
		for (Map.Entry<Long, Integer> entry : timeMinedInfo.entrySet()) 
		{
			long address = entry.getKey();
			int ticksMinedFor = entry.getValue();
			
			double reward = 10 * (ticksMinedFor / 1200);
			
			setBalance(address, getBalance(address) + reward);
		}
		timeMinedInfo.clear();
	}
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
	{
		NBTTagCompound bitcoinData = new NBTTagCompound();		
		NBTTagList balancesCompoundList = new NBTTagList();
		NBTTagList miningTimeCompoundList = new NBTTagList();
		
		walletInfo.forEach((account, balance) -> {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("account", account);
			nbt.setDouble("balance", balance);
			balancesCompoundList.appendTag(nbt);
		});
		timeMinedInfo.forEach((account, ticksMinedFor) -> {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("account", account);
			nbt.setInteger("ticksMinedFor", ticksMinedFor);
			miningTimeCompoundList.appendTag(nbt);
		}); 

		bitcoinData.setTag("balancesCompoundList", balancesCompoundList);
		bitcoinData.setTag("miningTimeCompoundList", miningTimeCompoundList);
		nbtTagCompound.setTag("bitcoinData", bitcoinData);
		nbtTagCompound.setLong("nextFreeID", nextFreeID);
		return nbtTagCompound;
	}	
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) 
	{
		NBTTagCompound bitcoinData = nbtTagCompound.getCompoundTag("bitcoinData");
		NBTTagList balancesCompoundList = bitcoinData.getTagList("balancesCompoundList", Constants.NBT.TAG_COMPOUND);
		NBTTagList miningTimeCompoundList = bitcoinData.getTagList("miningTimeCompoundList", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < balancesCompoundList.tagCount(); ++i)
		{
			NBTTagCompound balancesNBT = balancesCompoundList.getCompoundTagAt(i);
			Long account = balancesNBT.getLong("account");
			double balance = balancesNBT.getDouble("balance");
			walletInfo.put(account, balance);
		}
		for(int i = 0; i < miningTimeCompoundList.tagCount(); ++i)
		{
			NBTTagCompound miningTimeNBT = miningTimeCompoundList.getCompoundTagAt(i);
			Long account = miningTimeNBT.getLong("account");
			Integer balance = miningTimeNBT.getInteger("ticksMinedFor");
			timeMinedInfo.put(account, balance);
		}
		nextFreeID = nbtTagCompound.getLong("nextFreeID");
	}

	public long createNewWallet() 
	{ 
		markDirty();
		walletInfo.put(nextFreeID + 1, 0.0);
		return nextFreeID++; 
	}


	
	
	
}
