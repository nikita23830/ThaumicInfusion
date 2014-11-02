package drunkmafia.thaumicinfusion.client.cape;

import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Created by DrunkMafia on 27/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class CapeHandler {
    /**
    public static void init(){
        try {
            URL xmlURL = new URL(ModInfo.EXTRA_INFO_XML);
            InputStream xml = xmlURL.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xml);
            xml.close();
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("cape");
            for(int n = 0; n < nodes.getLength(); n++){
                Node node = nodes.item(n);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element)node;
                    registerCape(element.getElementsByTagName("Player").item(0).getTextContent(), element.getElementsByTagName("URL").item(0).getTextContent());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
     **/
}
