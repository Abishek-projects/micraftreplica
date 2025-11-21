    package mygame;

    import org.lwjgl.BufferUtils;
    import org.lwjgl.glfw.GLFWVidMode;
    import org.lwjgl.opengl.GL;
    import org.joml.Matrix4f;

    import java.nio.FloatBuffer;

    import static org.lwjgl.glfw.GLFW.*;
    import static org.lwjgl.opengl.GL11.*;
    import static org.lwjgl.system.MemoryUtil.NULL;

    public class Main {

        private long window;

        // texture IDs (now explicit)
        private int grassTopTex, grassSideTex, dirtTex, stoneTex, bedrockTex, playerTex,woodTopTex,woodSideTex,woodBottomTex,leavesTex,plankTex;

        private double lastMouseX, lastMouseY;
        private boolean firstMouse = true;
        private float mouseSensitivity = 0.1f;

        private final int worldSizeX = 1000;
        private final int worldSizeY = 50;
        private final int worldSizeZ = 1000;
        private Block[][][] world = new Block[worldSizeX][worldSizeY][worldSizeZ];

        private float blockSize = 1.0f;
        private int renderDistance = 50; // set an explicit value

        // For click handling
        private boolean wasLeftDown = false;
        private double lastBreakTime = 0;
        private double breakCooldown = 0.15;


        private boolean fullscreen = false;
        private int windowedWidth = 1280, windowedHeight = 720;
        private long windowedMonitor = NULL;
        private boolean wasRightDown = false;
        private int selectedBlock = 1; 

        public void run() {
            init();
            loop();
            glfwDestroyWindow(window);
            glfwTerminate();
        }

        private void init() {
            if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

            window = glfwCreateWindow(1280, 720, "Minecraft FPS", NULL, NULL);
            
            
            if (window == NULL) throw new RuntimeException("Failed to create window");

            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            glfwShowWindow(window);
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }

        private void setup3D() {
        int[] width = new int[1], height = new int[1];
        glfwGetFramebufferSize(window, width, height);
        glViewport(0, 0, width[0], height[0]);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        Matrix4f projection = new Matrix4f().perspective(
            (float) Math.toRadians(70f),
            (float) width[0] / height[0],
            0.1f, 100.0f
        );
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        projection.get(fb);
        glLoadMatrixf(fb);
        glMatrixMode(GL_MODELVIEW);
    }


        private void loop() {
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glClearColor(0.5f, 0.7f, 1.0f, 0.0f);

        // Load textures
        grassTopTex = TextureLoader.loadTexture("resources/grass_top.jpg");
        grassSideTex = TextureLoader.loadTexture("resources/grass_side.jpg");
        dirtTex = TextureLoader.loadTexture("resources/dirt.jpg");
        stoneTex = TextureLoader.loadTexture("resources/stone.jpg");
        bedrockTex = TextureLoader.loadTexture("resources/bedrock.jpg");
        playerTex = TextureLoader.loadTexture("resources/player.png");
        woodTopTex = TextureLoader.loadTexture("resources/wood_top_bottom.jpg");
        woodSideTex = TextureLoader.loadTexture("resources/wood_side.jpg");
        woodBottomTex = TextureLoader.loadTexture("resources/wood_top_bottom.jpg");
        leavesTex=TextureLoader.loadTexture("resources/leaf.jpg");
        plankTex=TextureLoader.loadTexture("resources/plank.png");
        Player player = new Player(50, worldSizeY, 50, playerTex);
        int[] hotbarTextures = new int[10];
String[] hotbarBlocks = new String[10];
        
       hotbarTextures[0] = grassTopTex; hotbarBlocks[0] = "Grass";
hotbarTextures[1] = dirtTex;     hotbarBlocks[1] = "Dirt";
hotbarTextures[2] = stoneTex;    hotbarBlocks[2] = "Stone";
hotbarTextures[3] = plankTex;    hotbarBlocks[3] = "Plank";
hotbarTextures[4] = bedrockTex;  hotbarBlocks[4] = "Bedrock";

// The rest start empty
for (int i = 5; i < 10; i++) {
    hotbarTextures[i] = 0;
    hotbarBlocks[i] = "Empty";
}

// Initialize GUI
GUI gui = new GUI(0, hotbarTextures);
glfwSetScrollCallback(window, (win, xoffset, yoffset) -> {
    if (gui != null) {
        if (yoffset > 0) {
            gui.setSelectedSlot((gui.getSelectedSlot() - 1 + 10) % 10);
        } else if (yoffset < 0) {
            gui.setSelectedSlot((gui.getSelectedSlot() + 1) % 10);
        }
    }
});

        // Generate world after textures are loaded
        generateWorld();

        // Framebuffer resize callback
        glfwSetFramebufferSizeCallback(window, (win, w, h) -> setup3D());

        double lastPlaceTime = 0;   // For right-click placement cooldown
        double placeCooldown = 0.1;

        while (!glfwWindowShouldClose(window)) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        setup3D();
        // --- Hotbar selection ---
if (glfwGetKey(window, GLFW_KEY_1) == GLFW_PRESS) gui.setSelectedSlot(0);
if (glfwGetKey(window, GLFW_KEY_2) == GLFW_PRESS) gui.setSelectedSlot(1);
if (glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS) gui.setSelectedSlot(2);
if (glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS) gui.setSelectedSlot(3);
if (glfwGetKey(window, GLFW_KEY_5) == GLFW_PRESS) gui.setSelectedSlot(4);
if (glfwGetKey(window, GLFW_KEY_6) == GLFW_PRESS) gui.setSelectedSlot(5);
if (glfwGetKey(window, GLFW_KEY_7) == GLFW_PRESS) gui.setSelectedSlot(6);
if (glfwGetKey(window, GLFW_KEY_8) == GLFW_PRESS) gui.setSelectedSlot(7);
if (glfwGetKey(window, GLFW_KEY_9) == GLFW_PRESS) gui.setSelectedSlot(8);
if (glfwGetKey(window, GLFW_KEY_0) == GLFW_PRESS) gui.setSelectedSlot(9);


        player.update(window, world, worldSizeX, worldSizeY, worldSizeZ, blockSize);
        updateMouse(player);

        glLoadIdentity();
        glRotatef(-player.pitch, 1, 0, 0);
        glRotatef(-player.yaw, 0, 1, 0);
        glTranslatef(-player.x, -player.getEyeY(), -player.z);

        boolean isLeftDown = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
        
        // Break block (single left-click)
        if (isLeftDown && !wasLeftDown && glfwGetTime() - lastBreakTime >= breakCooldown) {
            breakBlock(player);
            lastBreakTime = glfwGetTime();
        }
        boolean isRightDown = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS;

if (isRightDown && !wasRightDown) {
    placeBlock(player, gui.getSelectedSlot(), hotbarBlocks);
}

        wasRightDown = isRightDown;

        wasLeftDown = isLeftDown;
        


        int px = (int) player.x;
        int pz = (int) player.z;
        for (int x = Math.max(0, px - renderDistance); x < Math.min(worldSizeX, px + renderDistance); x++) {
            for (int y = 0; y < worldSizeY; y++) {
                for (int z = Math.max(0, pz - renderDistance); z < Math.min(worldSizeZ, pz + renderDistance); z++) {
                    Block b = world[x][y][z];
                    if (b != null) b.render(world, x, y, z, worldSizeX, worldSizeY, worldSizeZ);
                }
            }
        }

        renderCrosshair();
        gui.renderHotbar(0.5f);

        glfwSwapBuffers(window);
        glfwPollEvents();
        for (int i = 0; i < 5; i++) {
        if (glfwGetKey(window, GLFW_KEY_1 + i) == GLFW_PRESS) {
            gui.setSelectedSlot(i);
    }
}

        if (glfwGetKey(window, GLFW_KEY_F11) == GLFW_PRESS) toggleFullscreen();
    }

    }


        

    private void toggleFullscreen() {
        fullscreen = !fullscreen;

        if (fullscreen) {
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            glfwGetWindowPos(window, new int[]{0}, new int[]{0}); // optional save
            glfwSetWindowMonitor(window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
        } else {
            glfwSetWindowMonitor(window, NULL, 100, 100, windowedWidth, windowedHeight, 0);
        }

        setup3D(); // immediately update projection for new size
    }


        private void breakBlock(Player player) {
            double currentTime = glfwGetTime();
            if (currentTime - lastBreakTime < breakCooldown) return;
            lastBreakTime = currentTime;

            // Start ray from player's eye slightly forward
            float eyeX = player.x;
            float eyeY = player.getEyeY();
            float eyeZ = player.z;

            // Convert angles to radians
            float yawRad = (float) Math.toRadians(player.yaw);
            float pitchRad = (float) Math.toRadians(player.pitch);

            // Correct look vector (forward direction)
            float dirX = (float) (-Math.sin(yawRad) * Math.cos(pitchRad));
            float dirY = (float) Math.sin(pitchRad);
            float dirZ = (float) (-Math.cos(yawRad) * Math.cos(pitchRad));

            // Normalize the direction
            float len = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
            dirX /= len;
            dirY /= len;
            dirZ /= len;

            float step = 0.05f;
            float maxDist = 5.0f;

            // Move the ray slightly forward from the player's eyes
            float startOffset = 0.3f;
            eyeX += dirX * startOffset;
            eyeY += dirY * startOffset;
            eyeZ += dirZ * startOffset;

            for (float t = 0f; t <= maxDist; t += step) {
                float testX = eyeX + dirX * t;
                float testY = eyeY + dirY * t;
                float testZ = eyeZ + dirZ * t;

                int bx = (int) Math.floor(testX);
                int by = (int) Math.floor(testY);
                int bz = (int) Math.floor(testZ);

                // Bound check
                if (bx < 0 || bx >= worldSizeX || by < 0 || by >= worldSizeY || bz < 0 || bz >= worldSizeZ)
                    continue;

                Block target = world[bx][by][bz];
                if (target == null) continue;

                // Break non-air, non-bedrock blocks
                if (!target.getType().equals("Air") && !target.getType().equals("Bedrock")) {
                    System.out.println("Breaking " + target.getType() + " at " + bx + "," + by + "," + bz);
                    world[bx][by][bz] = new Block("Air", 0, 0, 0, bx * blockSize, by * blockSize, bz * blockSize, blockSize);
                    return;
                }
            }
        }
      private boolean placeBlock(Player player, int selectedSlot, String[] hotbarBlockTypes) {
    String blockType = hotbarBlockTypes[selectedSlot];
    if (blockType == null || blockType.equals("Empty")) {
        System.out.println("No block in this slot!");
        return false;
    }

    // Compute eye position and direction
    float eyeX = player.x;
    float eyeY = player.getEyeY();
    float eyeZ = player.z;

    float yawRad = (float) Math.toRadians(player.yaw);
    float pitchRad = (float) Math.toRadians(player.pitch);

    float dirX = (float) (-Math.sin(yawRad) * Math.cos(pitchRad));
    float dirY = (float) Math.sin(pitchRad);
    float dirZ = (float) (-Math.cos(yawRad) * Math.cos(pitchRad));

    float len = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
    dirX /= len; dirY /= len; dirZ /= len;

    float step = 0.05f;
    float maxDist = 5f;
    float startOffset = 0.3f;
    eyeX += dirX * startOffset;
    eyeY += dirY * startOffset;
    eyeZ += dirZ * startOffset;

    for (float t = 0f; t <= maxDist; t += step) {
        float px = eyeX + dirX * t;
        float py = eyeY + dirY * t;
        float pz = eyeZ + dirZ * t;

        int bx = (int) Math.floor(px);
        int by = (int) Math.floor(py);
        int bz = (int) Math.floor(pz);

        // Skip out-of-bounds blocks when raycasting
        if (bx < 0 || bx >= worldSizeX || by < 0 || by >= worldSizeY || bz < 0 || bz >= worldSizeZ)
            continue;

        Block target = world[bx][by][bz];
        if (target == null || target.getType().equals("Air"))
            continue;

        // Find side to place on
        float dx = px - bx;
        float dy = py - by;
        float dz = pz - bz;

        int placeX = bx;
        int placeY = by;
        int placeZ = bz;

        float min = Math.min(Math.min(Math.abs(dx - 0), Math.abs(dx - 1)),
                Math.min(Math.min(Math.abs(dy - 0), Math.abs(dy - 1)),
                        Math.min(Math.abs(dz - 0), Math.abs(dz - 1))));

        if (min == Math.abs(dx - 0)) placeX--;
        else if (min == Math.abs(dx - 1)) placeX++;
        else if (min == Math.abs(dy - 0)) placeY--;
        else if (min == Math.abs(dy - 1)) placeY++;
        else if (min == Math.abs(dz - 0)) placeZ--;
        else if (min == Math.abs(dz - 1)) placeZ++;

        // ✅ Bounds check before accessing world array
        if (placeX < 0 || placeX >= worldSizeX || 
            placeY < 0 || placeY >= worldSizeY || 
            placeZ < 0 || placeZ >= worldSizeZ) {

            // Print helpful message instead of crashing
            if (placeY >= worldSizeY)
                System.out.println("⚠️ Height limit reached! Can't place block above world.");
            else if (placeY < 0)
                System.out.println("⚠️ Cannot place below world floor.");
            else
                System.out.println("⚠️ Out of world bounds!");
            return false;
        }

        // Prevent placing inside player
        if (placeX + 1 > player.x && placeX < player.x &&
            placeY + 1 > player.y && placeY < player.y + 1.8f &&
            placeZ + 1 > player.z && placeZ < player.z)
            return false;

        // Only place if Air
        if (world[placeX][placeY][placeZ].getType().equals("Air")) {
            int topTex, sideTex, bottomTex;

            switch (blockType) {
                case "Grass":
                    topTex = grassTopTex;
                    sideTex = grassSideTex;
                    bottomTex = dirtTex;
                    break;
                case "Dirt":
                    topTex = dirtTex;
                    sideTex = dirtTex;
                    bottomTex = dirtTex;
                    break;
                case "Stone":
                    topTex = stoneTex;
                    sideTex = stoneTex;
                    bottomTex = stoneTex;
                    break;
                case "Bedrock":
                    topTex = bedrockTex;
                    sideTex = bedrockTex;
                    bottomTex = bedrockTex;
                    break;
                case "plank":
                    topTex = woodTopTex;
                    sideTex = woodSideTex;
                    bottomTex = woodBottomTex;
                    break;
                default:
                    return false;
            }

            world[placeX][placeY][placeZ] = new Block(
                    blockType, topTex, sideTex, bottomTex,
                    placeX * blockSize, placeY * blockSize, placeZ * blockSize, blockSize
            );

            glColor3f(1f, 1f, 1f);
            return true;
        }
        return false;
    }
    return false;
}

   private void generateWorld() {
    int stoneDepth = 4;

    // 1️⃣ Initialize world to Air
    for (int x = 0; x < worldSizeX; x++) {
        for (int y = 0; y < worldSizeY; y++) {
            for (int z = 0; z < worldSizeZ; z++) {
                world[x][y][z] = new Block(
                        "Air", 0, 0, 0,
                        x * blockSize, y * blockSize, z * blockSize, blockSize
                );
            }
        }
    }

    // 2️⃣ Generate terrain
    for (int x = 0; x < worldSizeX; x++) {
        for (int z = 0; z < worldSizeZ; z++) {
            int height = TerrainGenerator.getHeight(x, z);

            if (height >= worldSizeY - 1) height = worldSizeY - 2;
            if (height < 1) height = 1;

            for (int y = 0; y < worldSizeY; y++) {
                if (y == 0) {
                    world[x][y][z] = new Block("Bedrock", bedrockTex, bedrockTex, bedrockTex,
                            x * blockSize, y * blockSize, z * blockSize, blockSize);
                } else if (y < height - stoneDepth) {
                    world[x][y][z] = new Block("Stone", stoneTex, stoneTex, stoneTex,
                            x * blockSize, y * blockSize, z * blockSize, blockSize);
                } else if (y < height) {
                    world[x][y][z] = new Block("Dirt", dirtTex, dirtTex, dirtTex,
                            x * blockSize, y * blockSize, z * blockSize, blockSize);
                } else if (y == height) {
                    world[x][y][z] = new Block("Grass", grassTopTex, grassSideTex, dirtTex,
                            x * blockSize, y * blockSize, z * blockSize, blockSize);
                }
                // Above terrain remains Air from initialization
            }
            world[49][10][49]=new Block("plank",plankTex,plankTex,plankTex,49*blockSize,10*blockSize,49*blockSize,blockSize);

            // 3️⃣ Random tree placement
            if (TerrainGenerator.shouldPlaceWood(x, z) && height < worldSizeY - 5) {
                int treeHeight = 4 + (int)(Math.random() * 3); // 4–6 blocks tall

                // Trunk
                for (int h = 1; h <= treeHeight; h++) {
                    int wy = height + h;
                    if (wy >= worldSizeY) continue;

                    world[x][wy][z] = new Block(
                            "Wood", woodTopTex, woodSideTex, woodBottomTex,
                            x * blockSize, wy * blockSize, z * blockSize, blockSize
                    );
                }

                // Leaves (3x3x3 cube around top)
                int leafStart = height + treeHeight - 1;
                for (int lx = -1; lx <= 1; lx++) {
                    for (int lz = -1; lz <= 1; lz++) {
                        for (int ly = 0; ly <= 2; ly++) {
                            int wx = x + lx;
                            int wy = leafStart + ly;
                            int wz = z + lz;

                            // Bounds check
                            if (wx < 0 || wx >= worldSizeX || wy < 0 || wy >= worldSizeY || wz < 0 || wz >= worldSizeZ)
                                continue;

                            // Don't overwrite trunk
                            if (lx == 0 && lz == 0 && ly < treeHeight) continue;

                            // Place leaves only if empty or Air
                            if (world[wx][wy][wz] == null || world[wx][wy][wz].getType().equals("Air")) {
                                world[wx][wy][wz] = new Block(
                                        "Leaves", leavesTex, leavesTex, leavesTex,
                                        wx * blockSize, wy * blockSize, wz * blockSize, blockSize
                                );
                            }
                        }
                    }
                }
            }
        }
    }     
}

        private void updateMouse(Player player) {
            double[] mx = new double[1], my = new double[1];
            glfwGetCursorPos(window, mx, my);

            if (firstMouse) {
                lastMouseX = mx[0];
                lastMouseY = my[0];
                firstMouse = false;
            }

            double dx = mx[0] - lastMouseX;
            double dy = my[0] - lastMouseY;
            lastMouseX = mx[0];
            lastMouseY = my[0];

            player.yaw -= dx * mouseSensitivity;
            player.pitch -= dy * mouseSensitivity;

            if (player.pitch > 89) player.pitch = 89;
            if (player.pitch < -89) player.pitch = -89;
        }

    private void renderCrosshair() {
        glDisable(GL_DEPTH_TEST);

        // --- Get actual framebuffer size ---
        int[] width = new int[1], height = new int[1];
        glfwGetFramebufferSize(window, width, height);

        // --- Setup orthographic projection ---
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, width[0], 0, height[0], -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        // --- Draw crosshair ---
        float cx = width[0] / 2f;
        float cy = height[0] / 2f;
        float size = 10f;

        glLineWidth(2);
        glColor3f(1f, 1f, 1f);
        glBegin(GL_LINES);
            glVertex2f(cx - size, cy);
            glVertex2f(cx + size, cy);
            glVertex2f(cx, cy - size);
            glVertex2f(cx, cy + size);
        glEnd();

        // --- Restore matrices ---
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_DEPTH_TEST);
    }


        public static void main(String[] args) {
            new Main().run();
        }
    }



