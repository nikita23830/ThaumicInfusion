package drunkmafia.thaumicinfusion.common.commands;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.InfusionHelper;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;

public class SpawnInfusedBlockCommand extends CommandBase {

    @Override
    public String getCommandName(){
        return "infuse";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/infuse Will infuse the block in hand";
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {

    }
/**
    public int getSlotIDOfStack(ItemStack stack, EntityPlayer player){
        for(int i = 0; i < player.inventory.mainInventory.length; i++)
            if(player.inventory.mainInventory[i] == stack) return i;
        return -1;
    }

    public static ArrayList<ArrayList<Aspect>> getAspectsFromTags(String[] commands){
        ArrayList<ArrayList<Aspect>> aspects = new ArrayList<ArrayList<Aspect>>();
        for(int i = 0; i < commands.length; i++){
            String str = commands[i];
            if(str != null){
                if(str.matches("\\+") && ((i + 1) < commands.length) && (i > 0) && (Aspect.getAspect(commands[i + 1]) != null)){
                    aspects.get(i - 1).add(Aspect.getAspect(commands[i + 1]));
                    i++;
                }
                if(Aspect.getAspect(str) != null){
                    ArrayList<Aspect> aspects1 = new ArrayList<Aspect>();
                    aspects1.add(Aspect.getAspect(str));
                    aspects.add(aspects1);
                }
            }
        }
        return  aspects;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        EntityPlayer player = sender.getEntityWorld().getPlayerEntityByName(sender.getCommandSenderName());
        ItemStack currentItem = player.getHeldItem();
        if(currentItem.getItem() instanceof ItemBlock) {
            ArrayList<ArrayList<Aspect>> aspects = getAspectsFromTags(command);
            if (aspects != null && aspects.size() > 0) {
                ItemStack infusedStack = InfusionHelper.getInfusedItemStack(aspects, Block.getIdFromBlock(Block.getBlockFromItem(currentItem.getItem())), currentItem.stackSize, currentItem.getItemDamage());
                if(infusedStack != null){
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                    player.inventory.mainInventory[player.inventory.currentItem] = infusedStack;
                }
            }
        }
    } **/
}
