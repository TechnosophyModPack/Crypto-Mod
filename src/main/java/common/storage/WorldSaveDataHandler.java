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
	
	public void setBalance(long account, double newBalance)
	{
		if (account >= 0L && walletInfo.containsKey(account))
		{
			walletInfo.put(account, newBalance);
			this.markDirty();
		}
		else
		{
			CryptoMod.logger.info("Invalid or Null account called for setBalance!");
			CryptoMod.logger.info(account);
			CryptoMod.logger.info(newBalance);
		}
	}
	
	public int getTimeMined(Long account)
	{
		if (account == null || !timeMinedInfo.containsKey(account))
		{
			return 0;
		}
		return timeMinedInfo.get(account);
	}
	
	public void setTimeMined(long account, int amount)
	{
		if (account >= 0L)
		{
			timeMinedInfo.put(account, amount);
			this.markDirty();
		}
		else 
		{
			CryptoMod.logger.info("Invalid or Null account called for set Time Mined");
			CryptoMod.logger.info(account);	
		}
	}
	
	public void payoutRewards() 
	{
		int totalTicksMined = 0;
		for (Map.Entry<Long, Integer> entry : timeMinedInfo.entrySet()) 
		{
			int ticksMinedFor = entry.getValue();
			totalTicksMined += ticksMinedFor;
		}
		for (Map.Entry<Long, Integer> entry : timeMinedInfo.entrySet()) 
		{
			long address = entry.getKey();
			int ticksMinedFor = entry.getValue();
			double percentMined = (ticksMinedFor / totalTicksMined);
			setBalance(address, 10 * percentMined);
		}
		timeMinedInfo.clear();
		ticksSinceLastBlock = 0;
		this.markDirty();
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
			CryptoMod.logger.info("Account: " + account + " Balance: " + balance + " was written to disk");
		});
		timeMinedInfo.forEach((account, ticksMinedFor) -> {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("account", account);
			nbt.setInteger("ticksMinedFor", ticksMinedFor);
			miningTimeCompoundList.appendTag(nbt);
			CryptoMod.logger.info("Account: " + account + " time mined: " + ticksMinedFor + "was written to disk");
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
			CryptoMod.logger.info("Account: " + account + " Balance: " + balance + " was read from disk");
		}
		for(int i = 0; i < miningTimeCompoundList.tagCount(); ++i)
		{
			NBTTagCompound miningTimeNBT = miningTimeCompoundList.getCompoundTagAt(i);
			Long account = miningTimeNBT.getLong("account");
			Integer ticksMinedFor = miningTimeNBT.getInteger("ticksMinedFor");
			timeMinedInfo.put(account, ticksMinedFor);
			CryptoMod.logger.info("Account: " + account + " time mined: " + ticksMinedFor + "was read from disk");
		}
		nextFreeID = nbtTagCompound.getLong("nextFreeID");
	}

	public long createNewWallet() 
	{ 
		markDirty();
		walletInfo.put(nextFreeID, 0.0);
		return nextFreeID++; 
	}


	
	
	
}
