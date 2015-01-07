package drunkmafia.thaumicinfusion.common.command;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BanInfusionCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return ThaumicInfusion.translate("ban.infusion");
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "ban.infusion.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command) {
        EntityPlayer player = sender.getEntityWorld().getPlayerEntityByName(sender.getCommandSenderName());
        ItemStack currentStack = player.inventory.getCurrentItem();

        if(currentStack != null && currentStack.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(currentStack.getItem());
            BlockHandler.banBlock(block);
            sender.addChatMessage(new ChatComponentText(currentStack.getDisplayName() + (BlockHandler.isBlockBlacklisted(block) ? " is now banned" : " is not banned")));
        }else
            sender.addChatMessage(new ChatComponentText("Please hold a valid block"));
    }
}
