package common.handlers;

import common.CryptoMod;
import common.container.ContainerBitcoinMiner;
import common.gui.GuiBitcoinMiner;
import common.tileentity.TileEntityBitcoinMiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == CryptoMod.GUI_BITCOIN_MINER) return new ContainerBitcoinMiner(player.inventory, (TileEntityBitcoinMiner)world.getTileEntity(new BlockPos(x,y,z)));
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == CryptoMod.GUI_BITCOIN_MINER) return new GuiBitcoinMiner(player.inventory, (TileEntityBitcoinMiner)world.getTileEntity(new BlockPos(x,y,z)));
		return null;
	}
	
}
