package drunkmafia.thaumicinfusion.common.command;

import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.util.HashMap;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class AddInfusionCommand extends CommandBase {

    static HashMap<String, ItemStack> stacks = new HashMap<String, ItemStack>();

    @Override
    public String getCommandName() {
        return "add.infusion";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "add.infusion.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command) {
        EntityPlayer player = sender.getEntityWorld().getPlayerEntityByName(sender.getCommandSenderName());
        ItemStack currentStack = player.inventory.getCurrentItem();

        if(currentStack != null && !(currentStack.getItem() instanceof ItemBlock)){
            System.out.println(stacks.containsKey(sender.getCommandSenderName()));
            if(stacks.containsKey(sender.getCommandSenderName())) {
                ItemStack currentItem = stacks.get(sender.getCommandSenderName());
                Item item = currentItem.getItem();
                Block block = player.worldObj.getBlock((int)player.posX, (int)player.posY, (int)player.posZ);

                if(block == null)
                    sender.addChatMessage(new ChatComponentText("Please stand on block, otherwise the linking will not work"));

                sender.addChatMessage(new ChatComponentText("Block: " + block + " Has been marked as a block, with the item: " + currentItem.getDisplayName()));
                BlockHandler.addInfusion(item, block);
                stacks.remove(sender.getCommandSenderName());
            }else{
                if(currentStack.getItem() instanceof ItemBlock)
                    return;

                sender.addChatMessage(new ChatComponentText("Saved Item: " + currentStack.getDisplayName() + ", run this command again while standing ontop of the block you want this item to link to"));
                stacks.put(sender.getCommandSenderName(), currentStack);
            }
        }else
            sender.addChatMessage(new ChatComponentText("Please Equip over a item or block"));
    }
}
