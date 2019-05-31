package common;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import common.blocks.BitcoinMinerBlock;
import common.handlers.GuiHandler;
import common.item.ItemHardwareWallet;
import common.tileentity.TileEntityBitcoinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = CryptoMod.MODID, name=CryptoMod.NAME)
@Mod.EventBusSubscriber
public class CryptoMod
{
    public static final String NAME="CryptoMod";
    public static final String MODID="cryptomod";
    
    public static final int GUI_BITCOIN_MINER = 1;
    
    public static final List<Item> MOD_ITEMS = new ArrayList<>();
    public static final List<Block> MOD_BLOCKS = new ArrayList<>();
    
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
        
    	NetworkRegistry.INSTANCE.registerGuiHandler(CryptoMod.instance, new GuiHandler());
    	
        logger.info("Initializing Cryptocurrency Mod");
    }

    public static CreativeTabs TAB = new CreativeTabs("cryptomod")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModItems.HARDWARE_WALLET);
        }
    };

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {

    	IForgeRegistry<Item> registry = event.getRegistry();
    	
    	helper(new ItemHardwareWallet(), "hardware_wallet", registry);
    	helper2(ModBlocks.BITCOIN_MINER_BLOCK, registry);
    	
       /* event.getRegistry().registerAll(
                new ItemHardwareWallet().setTranslationKey("hardware_wallet").setRegistryName("hardware_wallet").setCreativeTab(TAB),
        		new ItemBlock(ModBlocks.BITCOIN_MINER_BLOCK).setTranslationKey("bitcoin_miner").setRegistryName("bitcoin_miner").setCreativeTab(TAB)
        		);
        */
    }
    
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
    	
    	IForgeRegistry<Block> registry = event.getRegistry();
    	helper(new BitcoinMinerBlock(), "bitcoin_miner_block", registry, 100.0F, 5.0F);
    	
    	/*
    	 * event.getRegistry().registerAll(
    	 *
    	 *		new BitcoinMinerBlock().setTranslationKey("bitcoin_miner").setRegistryName("bitcoin_miner").setCreativeTab(TAB)
    	 *		);
    	*/
    	registerTileEntities();    	
    }

    public static void registerTileEntities()
    {
    	GameRegistry.registerTileEntity(TileEntityBitcoinMiner.class, new ResourceLocation(MODID + ":bitcoin_miner"));
    }
    
    @ObjectHolder(MODID)
    public static class ModBlocks {
    	public static final Block BITCOIN_MINER_BLOCK = null;
    } 

    
    @ObjectHolder(MODID)
    public static class ModItems {
        public static final Item HARDWARE_WALLET = null;
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
      for (Item item : MOD_ITEMS)
        helper3(item);
      for (Block block : MOD_BLOCKS)
        helper3(Item.getItemFromBlock(block));
    }
    
/*    @Mod.EventBusSubscriber(value= Side.CLIENT, modid=MODID)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void onRegisterModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(ModItems.HARDWARE_WALLET, 0, new ModelResourceLocation(ModItems.HARDWARE_WALLET.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.BITCOIN_MINER_BLOCK), 0, new ModelResourceLocation(ModBlocks.BITCOIN_MINER_BLOCK.getRegistryName(), "inventory"));
        }
    }
*/
    
    private static void helper(Block block, String name, IForgeRegistry<Block> registry, float blastResistance, float hardness) {
        block.setRegistryName(name);
        block.setTranslationKey(block.getRegistryName().toString());
        block.setCreativeTab(TAB);
        block.setResistance(blastResistance);
        block.setHardness(hardness);
        MOD_BLOCKS.add(block);
        registry.register(block);
      }
    
    private static void helper(Item item, String name, IForgeRegistry<Item> registry) {
        item.setRegistryName(name);
        item.setTranslationKey(item.getRegistryName().toString());
        item.setCreativeTab(TAB);
        MOD_ITEMS.add(item);
        registry.register(item);
      }

      private static void helper2(Block block, IForgeRegistry<Item> registry) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        registry.register(itemBlock);
      }
      
      private static void helper3(Item item) {
    	    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    	  }
      
}