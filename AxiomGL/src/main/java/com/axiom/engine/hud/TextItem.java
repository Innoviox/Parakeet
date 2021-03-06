/**
 * A line of text
 * <p><br>
 * A TextItem holds a single String of text
 * to render to the screen based on a font
 * and size. It contains the total FontTexture
 * for the font and renders it based on that.
 * </p><p>
 * @author Antonio Hernández Bejarano (@lwjglgamedev)
 * @author The Axiom Corp, 2017.
 * </p>
 */
package com.axiom.engine.hud;

import java.util.ArrayList;
import java.util.List;

import com.axiom.engine.Utils;
import com.axiom.engine.item.Item;
import com.axiom.engine.item.model.Material;
import com.axiom.engine.item.model.Mesh;

public class TextItem extends Item {

    private static final float ZPOS = 0.0f;

    private static final int VERTICES_PER_QUAD = 4;

    private final FontTexture fontTexture;
    
    private String text;

    /**
     * Construct a TextItem based on the given text and font
     * @param text the string to render
     * @param fontTexture the type of font to render
     * @throws Exception if the font file isn't found
     */
    public TextItem(String text, FontTexture fontTexture) throws Exception {
        super();
        this.text = text;
        this.fontTexture = fontTexture;
        setMesh(buildMesh());
    }
    
    /**
     * Build the mesh out of triangles
     * @return mesh out of triangles
     */
    private Mesh buildMesh() {
        List<Float>   positions  = new ArrayList<Float>();
        List<Float>   textCoords = new ArrayList<Float>();
        List<Integer> indices    = new ArrayList<Integer>();
        float[] normals   = new float[0];
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        float startx = 0;
        for(int i=0; i<numChars; i++) {
            FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

            // Build a character tile composed by two triangles
            
            // Left Top vertex
            positions.add(startx); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add( (float)charInfo.getStartX() / (float)fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD);
                        
            // Left Bottom vertex
            positions.add(startx); // x
            positions.add((float)fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float)charInfo.getStartX() / (float)fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add((float)fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD + 3);
            
            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
            
            startx += charInfo.getWidth();
        }

        float[] posArr = Utils.listToArray(positions);
        float[] textCoordsArr = Utils.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();

        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(fontTexture.getTexture()));
        return mesh;
    }

    /**
     * Text of the item
     * @return the text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Set new text
     * @param text new text
     */
    public void setText(String text) {
        this.text = text;
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh());
    }
}
