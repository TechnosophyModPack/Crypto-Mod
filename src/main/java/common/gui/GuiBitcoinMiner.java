package common.gui;

import common.CryptoMod;
import common.container.ContainerBitcoinMiner;
import common.storage.WorldSaveDataHandler;
import common.tileentity.TileEntityBitcoinMiner;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiBitcoinMiner extends GuiContainer {

	private static final ResourceLocation TEXTURES = new ResourceLocation(CryptoMod.MODID + ":textures/gui/bitcoin_miner.png");
	private final InventoryPlayer player;
	private final TileEntityBitcoinMiner tileentity;
			
	public GuiBitcoinMiner(InventoryPlayer player, TileEntityBitcoinMiner tileentity) {
		super(new ContainerBitcoinMiner(player, tileentity));
		this.player = player;
		this.tileentity = tileentity;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		WorldSaveDataHandler worldSaveHandler = WorldSaveDataHandler.get(this.tileentity.getWorld());
		String tileName = this.tileentity.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2) - 5, 6, 4210752);
		this.fontRenderer.drawString("Energy Buffer : ", 7, this.ySize - 96 + 2, 4210752);
		this.fontRenderer.drawString(Integer.toString(this.tileentity.getEnergyStored()), 115, 72, 4210752);
		this.fontRenderer.drawString("Address: " + this.tileentity.getAddress(), 15, 18, 4210752);
		this.fontRenderer.drawString("Bitcoins: " + worldSaveHandler.getBalance(this.tileentity.getAddress()), 15, 58, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		int k = this.getEnergyStoredScaled(75);
		this.drawTexturedModalRect(this.guiLeft + 152, this.guiTop + 7, 176, 32, 16, 76 - k);

	}
	
	private int getEnergyStoredScaled(int pixels)
	{
		int i = this.tileentity.getEnergyStored();
		int j = this.tileentity.getMaxEnergyStored();
		return i != 0 && j != 0 ? i * pixels / j : 0;
	}
	
}
