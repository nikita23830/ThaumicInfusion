package drunkmafia.thaumicinfusion.common.util.annotation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.util.EffectGUI;
import net.minecraft.block.Block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by DrunkMafia on 12/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@SuppressWarnings("ALL")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Effect {
    /**
     * Separate Aspects by using Comma (,)
     */
    String aspect();

    boolean hasTileEntity() default false;

    Class<?> tileentity() default Object.class;

    /**
     * Gui Must extend GuiContainer
     */
    @SideOnly(Side.CLIENT)
    Class<?> gui() default Object.class;

    String infusedBlock() default "default";
}
