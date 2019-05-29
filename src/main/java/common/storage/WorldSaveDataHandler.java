package common.storage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	private int lastID;
	private long nextFreeID = 0;
	
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
	

	public Double getBalance(Long account)
	{
		if (account == null)
		{
			return null;
		}
		if (!walletInfo.containsKey(account))
		{
			walletInfo.put(account, 0.0);
			this.markDirty();
		}
		return walletInfo.get(account);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		
		NBTTagList nbtTagList = nbtTagCompound.getTagList("wallets", Constants.NBT.TAG_COMPOUND);
		
		for (int i = 0; i < nbtTagList.tagCount(); ++i)
		{
			NBTTagCompound walletNBT = nbtTagList.getCompoundTagAt(i);
			
			Long account = walletNBT.getLong("account");
			double balance = walletNBT.getDouble("balance");
			
		}
        nbtTagCompound.setLong("nextFreeID", nextFreeID);
	}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList nbtTagList = new NBTTagList();

        for (Map.Entry<Long, Double> entry : walletInfo.entrySet())
        {
            NBTTagCompound walletNBT = new NBTTagCompound();
            walletNBT.setLong("account", entry.getKey());
            walletNBT.setDouble("balance", entry.getValue());
            nbtTagList.appendTag(walletNBT);
        }

        nbtTagCompound.setTag("wallets", nbtTagList);
        nbtTagCompound.setLong("nextFreeID", nextFreeID);

        return nbtTagCompound;
    }

	public Long createNewWallet() 
	{ 
		markDirty();
		return nextFreeID++; 
	}
	
	
	
}
