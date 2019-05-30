package common;

import org.apache.logging.log4j.Logger;

import common.blocks.BitcoinMinerBlock;
import common.item.ItemHardwareWallet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CryptoMod.MODID, name=CryptoMod.NAME)
@Mod.EventBusSubscriber
public class CryptoMod
{
    public static final String NAME="CryptoMod";
    public static final String MODID="cryptomod";
    
    public static final int GUI_BITCOIN_MINER = 1;
    
    @Instance
    public static CryptoMod instance;
    
    public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("Initializing Cryptocurrency Mod");
    }

    public static CreativeTabs TAB = new CreativeTabs("cryptomodtab")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModItems.HARDWARE_WALLET);
        }
    };

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {

        event.getRegistry().registerAll(
                new ItemHardwareWallet().setTranslationKey("hardware_wallet").setRegistryName("hardware_wallet").setCreativeTab(TAB)
        		);

    }
    
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
    	
    	event.getRegistry().registerAll(
    			new BitcoinMinerBlock().setTranslationKey("bitcoin_miner").setRegistryName("bitcoin_miner").setCreativeTab(TAB)
    			);
    	
    }

    @ObjectHolder(value = "")
    public static class ModBlocks {
    	public static final Block BITCOIN_MINER_BLOCK = null;
    } 

    
    @ObjectHolder(MODID)
    public static class ModItems {
        public static final Item HARDWARE_WALLET = null;
    }

    @Mod.EventBusSubscriber(value= Side.CLIENT, modid=MODID)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void onRegisterModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(ModItems.HARDWARE_WALLET, 0, new ModelResourceLocation(ModItems.HARDWARE_WALLET.getRegistryName(), "inventory"));
        }
    }
}