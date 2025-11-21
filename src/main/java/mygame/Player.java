package mygame;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    public float x, y, z;
    public float yaw = 0;
    public float pitch = 0;
    private int textureId;

    private boolean isFlying = true;
    private boolean onGround = false;
    private float velocityY = 0.0f;

    // Physics constants
    private final float gravity = -0.008f;
    private final float jumpStrength = 0.15f;
    private final float playerHeight = 1.8f;
    private final float playerWidth = 0.6f;

    public Player(float x, float y, float z, int textureId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.textureId = textureId;
    }

    public void update(long window, Block[][][] world, int worldSizeX, int worldSizeY, int worldSizeZ, float blockSize) {
    float speed = isFlying ? 0.1f : 0.1f;

    float radYaw = (float) Math.toRadians(yaw);
    float forwardX = (float) Math.sin(radYaw);
    float forwardZ = (float) Math.cos(radYaw);
    float rightX = (float) Math.sin(radYaw - Math.PI / 2);
    float rightZ = (float) Math.cos(radYaw - Math.PI / 2);

    // Toggle flying
    if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
        isFlying = !isFlying;
        try { Thread.sleep(150); } catch (Exception ignored) {}
    }

    // Movement input
    float moveX = 0, moveZ = 0;
    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) { moveX -= forwardX * speed; moveZ -= forwardZ * speed; }
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) { moveX += forwardX * speed; moveZ += forwardZ * speed; }
    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) { moveX += rightX * speed; moveZ += rightZ * speed; }
    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) { moveX -= rightX * speed; moveZ -= rightZ * speed; }

    if (isFlying) {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) y += speed;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) y -= speed;
    } else {
        // Gravity and jump
        velocityY += gravity;
        float newY = y + velocityY;

        if (!collides(x, newY, z, world, blockSize)) {
            y = newY;
            onGround = false;
        } else {
            // Land on block
            if (velocityY < 0) {
                y = (float) Math.floor(y / blockSize) * blockSize;
                onGround = true;
            }
            velocityY = 0;
        }

        // Jump
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && onGround) {
            velocityY = jumpStrength;
            onGround = false;
        }
    }

    // Horizontal movement (X)
    float newX = x + moveX;
    if (!collides(newX, y, z, world, blockSize)) {
        x = newX;
    }

    // Horizontal movement (Z)
    float newZ = z + moveZ;
    if (!collides(x, y, newZ, world, blockSize)) {
        z = newZ;
    }
}


    // --- Collision detection ---
    private boolean collides(float px, float py, float pz, Block[][][] world, float blockSize) {
        int minX = (int)Math.floor((px - playerWidth / 2) / blockSize);
        int maxX = (int)Math.floor((px + playerWidth / 2) / blockSize);
        int minY = (int)Math.floor(py / blockSize);
        int maxY = (int)Math.floor((py + playerHeight) / blockSize);
        int minZ = (int)Math.floor((pz - playerWidth / 2) / blockSize);
        int maxZ = (int)Math.floor((pz + playerWidth / 2) / blockSize);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x < 0 || y < 0 || z < 0 || 
                        x >= world.length || y >= world[0].length || z >= world[0][0].length)
                        continue;

                    Block b = world[x][y][z];
                    if (b != null && !b.getType().equals("Air")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public float getEyeY() {
        return y + 1.62f; // Eye height
    }

    public float getX(){ return x; }
    public float getY(){ return y; }
    public float getZ(){ return z; }
}
