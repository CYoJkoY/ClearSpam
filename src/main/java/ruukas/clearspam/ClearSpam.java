// ClearSpam.java

package ruukas.clearspam;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(name = "ClearSpam", modid = ClearSpam.MODID, version = ClearSpam.VERSION, acceptedMinecraftVersions="[1.11,1.12.2]")
public class ClearSpam {
    public static final String MODID = "clearspam";
    public static final String VERSION = "0.4";

    //TODO remove caps
    //TODO add a [x] on a filtered message, which will show the violation on mouseover�
    //TODO add config to save settings
    //TODO add command to change timer
    //TODO simplify links

    @EventHandler
    public void init(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(SpamEventHandler.class);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static List<ChatLine> getChatLines() {
        List<ChatLine> chatLine = null;

        try {
            GuiNewChat chatGui = getChatGui();
            Class<?> clazz = chatGui.getClass();
            Field chatLinesField = null;

            // 查找 "chatLines" 字段，兼容继承
            while (clazz != null) {
                try {
                    chatLinesField = clazz.getDeclaredField("chatLines");
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            // 若没找到，再查 SRG 名字
            if (chatLinesField == null) {
                clazz = chatGui.getClass();
                while (clazz != null) {
                    try {
                        chatLinesField = clazz.getDeclaredField("field_146252_h");
                        break;
                    } catch (NoSuchFieldException e) {
                        clazz = clazz.getSuperclass();
                    }
                }
            }
            if (chatLinesField == null) {
                throw new NoSuchFieldException("chatLines field not found in chat GUI class hierarchy.");
            }
            chatLinesField.setAccessible(true);
            chatLine = (List<ChatLine>) chatLinesField.get(chatGui);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chatLine;
    }

    public static GuiNewChat getChatGui() {
        return Minecraft.getMinecraft().ingameGUI.getChatGUI();
    }
}