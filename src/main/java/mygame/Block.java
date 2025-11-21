package mygame;

import static org.lwjgl.opengl.GL11.*;

public class Block {
    private String type;
    private float x, y, z, size;
    private int topTexture, sideTexture, bottomTexture;

    public Block(String type, int topTexture, int sideTexture, int bottomTexture,
                 float x, float y, float z, float size) {
        this.type = type;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public void setTextures(int top, int side, int bottom) {
        this.topTexture = top;
        this.sideTexture = side;
        this.bottomTexture = bottom;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public float getSize() { return size; }
    public void setPosition(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }

    /**
     * Renders block with separate top/side/bottom textures.
     */
   public void render(Block[][][] world, int i, int j, int k, int worldX, int worldY, int worldZ) {
    if (type.equals("Air")) return;

    // FRONT face (Z-1)
    if (k == 0 || world[i][j][k - 1].getType().equals("Air")) {
        glBindTexture(GL_TEXTURE_2D, sideTexture);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0); glVertex3f(x, y, z);
        glTexCoord2f(1,0); glVertex3f(x + size, y, z);
        glTexCoord2f(1,1); glVertex3f(x + size, y + size, z);
        glTexCoord2f(0,1); glVertex3f(x, y + size, z);
        glEnd();
    }

    // BACK face (Z+1)
    if (k == worldZ - 1 || world[i][j][k + 1].getType().equals("Air")) {
        glBindTexture(GL_TEXTURE_2D, sideTexture);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0); glVertex3f(x, y, z + size);
        glTexCoord2f(1,0); glVertex3f(x + size, y, z + size);
        glTexCoord2f(1,1); glVertex3f(x + size, y + size, z + size);
        glTexCoord2f(0,1); glVertex3f(x, y + size, z + size);
        glEnd();
    }

    // LEFT face (X-1)
    if (i == 0 || world[i - 1][j][k].getType().equals("Air")) {
        glBindTexture(GL_TEXTURE_2D, sideTexture);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0); glVertex3f(x, y, z);
        glTexCoord2f(1,0); glVertex3f(x, y, z + size);
        glTexCoord2f(1,1); glVertex3f(x, y + size, z + size);
        glTexCoord2f(0,1); glVertex3f(x, y + size, z);
        glEnd();
    }

    // RIGHT face (X+1)
    if (i == worldX - 1 || world[i + 1][j][k].getType().equals("Air")) {
        glBindTexture(GL_TEXTURE_2D, sideTexture);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0); glVertex3f(x + size, y, z);
        glTexCoord2f(1,0); glVertex3f(x + size, y, z + size);
        glTexCoord2f(1,1); glVertex3f(x + size, y + size, z + size);
        glTexCoord2f(0,1); glVertex3f(x + size, y + size, z);
        glEnd();
    }

    // TOP face (Y+1)
    if (j == worldY - 1 || world[i][j + 1][k].getType().equals("Air")) {
        glBindTexture(GL_TEXTURE_2D, topTexture);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0); glVertex3f(x, y + size, z);
        glTexCoord2f(1,0); glVertex3f(x + size, y + size, z);
        glTexCoord2f(1,1); glVertex3f(x + size, y + size, z + size);
        glTexCoord2f(0,1); glVertex3f(x, y + size, z + size);
        glEnd();
    }

    // BOTTOM face (Y-1)
    if (j == 0 || world[i][j - 1][k].getType().equals("Air")) {
        glBindTexture(GL_TEXTURE_2D, bottomTexture);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0); glVertex3f(x, y, z);
        glTexCoord2f(1,0); glVertex3f(x + size, y, z);
        glTexCoord2f(1,1); glVertex3f(x + size, y, z + size);
        glTexCoord2f(0,1); glVertex3f(x, y, z + size);
        glEnd();
    }
}

}
