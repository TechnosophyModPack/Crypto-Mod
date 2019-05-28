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
	
	private Map<UUID, BigDecimal> walletInfo = new HashMap<>();
	private int lastID;
	
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
	

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		
		NBTTagList nbtTagList = nbtTagCompound.getTagList("bitcoins", Constants.NBT.TAG_COMPOUND);
		
		for (int i = 0; i < nbtTagList.tagCount(); ++i)
		{
			NBTTagCompound nbtTagCompound1 = nbtTagList.getCompoundTagAt(i);
			int j = nbtTagCompound1.getByte("bitcoins");
			
		}
		lastID = nbtTagCompound.getInteger("lastID");
	}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList nbtTagList = new NBTTagList();

        for (Map.Entry<UUID, BigDecimal> entry : walletInfo.entrySet())
        {
/*            
 			BitcoinInventory inventory = entry.getValue();

            NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
            nbtTagCompound1.setInteger("bitcoins", entry.getKey());
            inventory.writeToNBT(nbtTagCompound1);
            nbtTagList.appendTag(nbtTagCompound1);
*/
        }

        nbtTagCompound.setTag("bitcoins", nbtTagList);
        nbtTagCompound.setInteger("LastID", lastID);

        return nbtTagCompound;
    }
	
	
	
}
