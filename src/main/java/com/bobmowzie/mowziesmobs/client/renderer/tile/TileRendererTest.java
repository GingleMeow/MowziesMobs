package com.bobmowzie.mowziesmobs.client.renderer.tile;

import com.bobmowzie.mowziesmobs.client.model.block.ModelBlockTest;
import com.bobmowzie.mowziesmobs.tile.TileTest;
import com.bobmowzie.mowziesmobs.MowziesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TileRendererTest extends TileEntitySpecialRenderer
{

	private static final ResourceLocation texture = new ResourceLocation(MowziesMobs.getModID() + "textures/blocks/test.png");
	private ModelBlockTest model = new ModelBlockTest();

	public TileRendererTest()
	{

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
	{
		if (tileEntity instanceof TileTest)
		{
			TileTest tileEntityModel = (TileTest) tileEntity;
			GL11.glPushMatrix();
			GL11.glScalef(1.0F, 1.0F, 1.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
	}
}