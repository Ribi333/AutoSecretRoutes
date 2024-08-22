/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.utils;

import cc.polyfrost.oneconfig.config.core.OneColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderUtils {
    private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
    public static void drawBoxAtBlock(double x, double y, double z, OneColor color, double width, double height, double alpha) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(3);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        GL11.glTranslated(x, y, z);

        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, (float) alpha);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(width, height, width);
        GL11.glVertex3d(width, height, 0);
        GL11.glVertex3d(0, height, 0);
        GL11.glVertex3d(0, height, width);
        GL11.glVertex3d(width, height, width);
        GL11.glVertex3d(width, 0, width);
        GL11.glVertex3d(width, 0, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, width);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, height, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(width, 0, 0);
        GL11.glVertex3d(width, height, 0);
        GL11.glVertex3d(width, 0, 0);
        GL11.glVertex3d(width, 0, width);
        GL11.glVertex3d(0, 0, width);
        GL11.glVertex3d(0, height, width);
        GL11.glVertex3d(width, height, width);
        GL11.glEnd();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void spawnParticleAtLocation(BlockPos loc, BlockPos offset, EnumParticleTypes particle) {
        World world = Minecraft.getMinecraft().theWorld;

        if (world != null) {
            double x = loc.getX() + 0.5;
            double y = loc.getY() + 0.5;
            double z = loc.getZ() + 0.5;

            double offsetX = offset.getX();
            double offsetY = offset.getY();
            double offsetZ = offset.getZ();

            world.spawnParticle(particle, x, y, z, offsetX, offsetY, offsetZ);
        }
    }

    public static void drawLineParticles(BlockPos loc1, BlockPos loc2, EnumParticleTypes particle) {
        double distanceX = loc2.getX() - loc1.getX();
        double distanceY = loc2.getY() - loc1.getY();
        double distanceZ = loc2.getZ() - loc1.getZ();

        double maxDistance = Math.max(Math.abs(distanceX), Math.abs(distanceZ));
        int maxPoints = (int) Math.ceil(maxDistance * 1);

        double deltaX = distanceX / (double) maxPoints;
        double deltaY = distanceY / (double) maxPoints;
        double deltaZ = distanceZ / (double) maxPoints;

        double x = loc1.getX();
        double y = loc1.getY();
        double z = loc1.getZ();

        for (int i = 0; i <= maxPoints; i++) {
            spawnParticleAtLocation(new BlockPos(x, y, z), new BlockPos(0, 0, 0), particle);

            x += deltaX;
            y += deltaY;
            z += deltaZ;
        }
    }

    public static void drawLineMultipleParticles(EnumParticleTypes particle, List<BlockPos> locations) {
        if(locations == null) {
            return;
        }
        if(locations.size() >= 2) {
            BlockPos lastLoc = null;
            for (BlockPos loc : locations) {
                if (lastLoc == null) {
                    lastLoc = loc;
                    continue;
                }

                drawLineParticles(lastLoc, loc, particle);
                lastLoc = loc;
            }
        }
    }

    public static void drawNormalLine(BlockPos pos1, BlockPos pos2, OneColor colour, float partialTicks, boolean depth, int width) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushAttrib();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(width);
        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue()/ 255f, colour.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        worldRenderer.pos(pos1.getX(), pos1.getY(), pos1.getZ()).endVertex();
        worldRenderer.pos(pos2.getX(), pos2.getY(), pos2.getZ()).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.popAttrib();
    }


    public static void drawMultipleNormalLines(List<BlockPos> locations, float partialTicks, OneColor color, int width) {
        if(locations == null) {
            return;
        }
        if(locations.size() >= 2) {
            BlockPos lastLoc = null;
            for (BlockPos loc : locations) {
                if (lastLoc == null) {
                    lastLoc = loc;
                    continue;
                }

                drawNormalLine(lastLoc, loc, color, partialTicks, true, width);
                lastLoc = loc;
            }
        }
    }

    //
    public static void drawNormalLine(float x1, float y1, float z1, float x2, float y2, float z2, OneColor colour, float partialTicks, boolean depth, int width) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushAttrib();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(width);
        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue()/ 255f, colour.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        worldRenderer.pos(x1, y1, z1).endVertex();
        worldRenderer.pos(x2, y2, z2).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.popAttrib();
    }
}