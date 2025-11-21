package mygame;

import static org.lwjgl.opengl.GL11.*;

public class GUI {
    private int textureId;
        private int[] slotTextures;
    private int selectedSlot = 0;
    float slotSize = 64f;    // how big each slot looks (try 48f, 64f, 80f…)
float padding = 6f;      // spacing between slots

    public GUI(int textureId,int[] slotTextures) {
        this.textureId = textureId;
        this.slotTextures = slotTextures;
    }
public void setSelectedSlot(int index) {
        if (index >= 0 && index < slotTextures.length) {
            selectedSlot = index;
        }
    }
    public int getSelectedSlot() {
        return selectedSlot;
    }
    


    /**
     * Renders a simple 3D-style GUI bar (like a hotbar preview)
     */
    public void render3DGUI() {
        // Disable depth test so GUI appears on top of the world
        glDisable(GL_DEPTH_TEST);

        // Setup for 2D overlay drawing
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(-1, 1, -1, 1, -1, 1); // 2D coordinate space
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        // Draw a row of block previews
        float blockSize = 0.25f;
        float startX = -0.6f;
        float startY = -0.8f;
        int numBlocks = 5;

        glBindTexture(GL_TEXTURE_2D, textureId);
        glBegin(GL_QUADS);

        for (int i = 0; i < numBlocks; i++) {
            float x = startX + i * (blockSize + 0.05f);
            float y = startY;
            float s = blockSize;

            glTexCoord2f(0, 0); glVertex2f(x, y);
            glTexCoord2f(1, 0); glVertex2f(x + s, y);
            glTexCoord2f(1, 1); glVertex2f(x + s, y + s);
            glTexCoord2f(0, 1); glVertex2f(x, y + s);
        }

        glEnd();

        // Restore projection and model matrices
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);

        // Re-enable depth test
        glEnable(GL_DEPTH_TEST);

        
    }
    public void renderHotbar(float scale) {
    glDisable(GL_DEPTH_TEST);

    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    glOrtho(-1, 1, -1, 1, -1, 1);  // Normalized 2D space
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();

    // === 🔧 Adjustable values ===
    float baseBlockSize = 0.25f;
    float blockSize = baseBlockSize * scale; // scale size
    float spacing = 0.05f * scale;
    int numSlots = slotTextures.length;

    // Center hotbar horizontally
    float totalWidth = numSlots * (blockSize + spacing) - spacing;
    float startX = -totalWidth / 2f;
    float startY = -0.9f + (1 - scale) * 0.1f; // move slightly up when smaller

    for (int i = 0; i < numSlots; i++) {
        float x = startX + i * (blockSize + spacing);
        float y = startY;

        // Draw slot background
        glColor3f(1f, 1f, 1f);
        glBegin(GL_QUADS);
        glVertex2f(x - 0.02f * scale, y - 0.02f * scale);
        glVertex2f(x + blockSize + 0.02f * scale, y - 0.02f * scale);
        glVertex2f(x + blockSize + 0.02f * scale, y + blockSize + 0.02f * scale);
        glVertex2f(x - 0.02f * scale, y + blockSize + 0.02f * scale);
        glEnd();

        // Draw either texture or placeholder
        if (slotTextures[i] != 0) {
            glBindTexture(GL_TEXTURE_2D, slotTextures[i]);
            glColor3f(1f, 1f, 1f);
        } else {
            glColor3f(0.3f, 0.3f, 0.3f);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(x, y);
        glTexCoord2f(1, 0); glVertex2f(x + blockSize, y);
        glTexCoord2f(1, 1); glVertex2f(x + blockSize, y + blockSize);
        glTexCoord2f(0, 1); glVertex2f(x, y + blockSize);
        glEnd();

        // Highlight selected slot
        if (i == selectedSlot) {
            glColor3f(1f, 1f, 0f); // yellow
            glLineWidth(3);
            glBegin(GL_LINE_LOOP);
            glVertex2f(x - 0.025f * scale, y - 0.025f * scale);
            glVertex2f(x + blockSize + 0.025f * scale, y - 0.025f * scale);
            glVertex2f(x + blockSize + 0.025f * scale, y + blockSize + 0.025f * scale);
            glVertex2f(x - 0.025f * scale, y + blockSize + 0.025f * scale);
            glEnd();
        }
    }
    // Restore GL state
glPopMatrix();
glMatrixMode(GL_PROJECTION);
glPopMatrix();
glMatrixMode(GL_MODELVIEW);

glColor3f(1f, 1f, 1f); // reset color to white
glBindTexture(GL_TEXTURE_2D, 0); // unbind GUI textures
glEnable(GL_DEPTH_TEST); // re-enable depth test
}
}
    
