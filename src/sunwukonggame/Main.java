/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 * Main game class for Sun Wukong game
 * Handles game loop, state management, and drawing.
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;
import processing.core.PImage;

// Used Deepseek to do fonts
import processing.core.PFont;

//Used: https://www.w3schools.com/java/java_arraylist.asp
import java.util.ArrayList;

public class Main extends PApplet {
    //Game state
    private static final int STATE_START = 0;          
    private static final int STATE_CONTROLS = 1;       
    private static final int STATE_REBEL_BATTLE = 2;   
    private static final int STATE_PEACH_DECISION = 3;
    private static final int STATE_REDEMPTION = 4;     
    private static final int STATE_BATTLE = 5;       
    private static final int STATE_WIN = 6;        
    private static final int STATE_LOSE = 7;           
    
    private int currentState = STATE_START; 

    //Game objects
    private Player player;                
    private MonkTang monk;             
    private BullDemonKing boss;              
    private ArrayList<Projectile> projectiles = new ArrayList<>();      
    private ArrayList<HeavenlySoldier> soldiers = new ArrayList<>();   
    private ArrayList<FireProjectile> fireProjectiles = new ArrayList<>(); 

    //Game stats
    private int rebelWave = 1;               
    private int soldiersDefeated = 0;      
    private int playerLives = 3;            
    private int monkHealth = 5;            
    private int score = 0;                  
    private static int highScore = 0;       

    // Ability states and timers
    private boolean lostToSoldiers = false; 
    private boolean shieldActive = false;   
    private boolean pushActive = false;      
    private long shieldEndTime = 0;         
    private long lastShieldDeactivationTime = 0; 
    private long lastProjectileTime = 0;     
    private long pushEndTime = 0;           
    private long lastPushTime = 0;         
    private long peachDecisionStartTime = 0; 
    private long lastBossAbilityTime = 0;    
    private float shieldEnergy = 1.0f;      
    
    //Ability cooldowns all in ms
    private final int shieldDuration = 7000;    
    private final int shieldCooldown = 15000;    
    private final int projectileCooldown = 1000;  
    private final int pushCooldown = 2000;      
    private final int bossAbilityCooldown = 5000; 
    
    //Movement states
    private boolean movingUp = false;       
    private boolean movingDown = false;    
    private boolean movingLeft = false;      
    private boolean movingRight = false;    
    private final int playerSpeed = 5;       

    //Images and fonts
    private PImage backgroundImage;          
    private PImage peachImage;               
    private PImage templeImage;              
    private PImage victoryImage;             
    private PFont titleFont;                 
    private PFont subtitleFont;              
    private PFont regularFont;               
    private PFont helpFont;                  

    /**
     * Start program
     * @param args Inputs from command line
     */
    public static void main(String[] args) {
        highScore = FileUtils.loadHighScore();
        PApplet.main("sunwukonggame.Main");
    }

    /**
     * Setup window size.
     */
    public void settings() {
        size(800, 600);
    }

    /**
     * Loads game objects and resources.
     */
    public void setup() {
       
        
        // Create game objects with their images
        player = new Player(this, 400, 300, "images/wukong.png");
        monk = new MonkTang(this, 600, 300, "images/monktang.png");
        boss = new BullDemonKing(this, 100, 100, "images/bullking.png");

        // Load background images
        backgroundImage = loadImage("images/background.png");
        peachImage = loadImage("images/peach.png");
        templeImage = loadImage("images/temple.png");
        victoryImage = loadImage("images/victory.png");

        // Setup fonts
        titleFont = createFont("SansSerif", 36);
        subtitleFont = createFont("SansSerif", 24);
        regularFont = createFont("SansSerif", 18);
        helpFont = createFont("SansSerif", 20);
    }

    /**
     * Run main game loop
     */
    public void draw() {
        background(0);  //Clear screen with black
        
        //Handle game process
        handlePlayerMovement();
        handleShieldState();
        
        //Update current game state
        switch (currentState) {
            case STATE_REBEL_BATTLE: updateRebelBattle(); break;
            case STATE_PEACH_DECISION: updatePeachDecision(); break;
            case STATE_BATTLE: updateBattle(); break;
        }

        drawGame(); 
    }

    /**
     * Draw the current game state.
     */
    private void drawGame() {
        //Draw the state content
        switch (currentState) {
            case STATE_START: drawStartScreen(); break;
            case STATE_CONTROLS: drawControlsScreen(); break;
            case STATE_REBEL_BATTLE: drawRebelBattle(); break;
            case STATE_PEACH_DECISION: drawPeachDecision(); break;
            case STATE_REDEMPTION: drawRedemptionPath(); break;
            case STATE_BATTLE: drawBattle(); break;
            case STATE_WIN: drawWinScreen(); break;
            case STATE_LOSE: drawLoseScreen(); break;
        }
        
        //Draw ability effects if it is active
        if (shieldActive && currentState == STATE_BATTLE) drawShield();
        if (currentState == STATE_BATTLE) drawCooldowns();
        if (pushActive && currentState == STATE_BATTLE) drawPushEffect();
    }

    /**
     * Draw the player's shield effect.
     */
    private void drawShield() {
        noFill();
        stroke(0, 255, 255, 100);
        strokeWeight(3);
        ellipse(player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2, 
                player.getWidth() + 20, player.getHeight() + 20);
        
        if (monkHealth > 0) {
            ellipse(monk.getX() + monk.getWidth()/2, monk.getY() + monk.getHeight()/2, 
                    monk.getWidth() + 20, monk.getHeight() + 20);
        }
    }
    
    /**
     * Draw all cooldown indicators.
     */
    private void drawCooldowns() {
        drawShieldCooldown();
        drawPushCooldown();
    }
    
    /**
     * Draw the shield cooldown/energy indicator.
     */
    private void drawShieldCooldown() {
        long currentTime = millis();
        fill(50);
        rect(20, height - 40, 100, 20);
        
        if (shieldActive) {
            long remaining = shieldEndTime - currentTime;
            float durationPercentage = (float) remaining / shieldDuration;
            fill(0, 255, 255);
            rect(20, height - 40, 100 * durationPercentage, 20);
            
            fill(255);
            text("Shield: " + (remaining/1000) + "s", 20, height - 25);
        } else {
            long timeSinceDeactivation = currentTime - lastShieldDeactivationTime;
            float regenPercentage = Math.min(1.0f, (float) timeSinceDeactivation / shieldCooldown);
            float currentEnergy = shieldEnergy + (1.0f - shieldEnergy) * regenPercentage;
            
            fill(0, 255, 255);
            rect(20, height - 40, 100 * currentEnergy, 20);
            
            fill(255);
            if (currentEnergy < 0.999f) {
                text("Shield: " + (int)(currentEnergy * 100) + "%", 20, height - 25);
            } else {
                text("Shield READY (E)", 20, height - 25);
            }
        }
        
        noFill();
        stroke(255);
        rect(20, height - 40, 100, 20);
    }
    
    /**
     * Draw the push ability cooldown indicator.
     */
    private void drawPushCooldown() {
        long currentTime = millis();
        long timeSinceLastPush = currentTime - lastPushTime;
        float cooldownPercentage = Math.min(1.0f, (float) timeSinceLastPush / pushCooldown);
        
        fill(50);
        rect(20, height - 70, 100, 20);
        
        fill(255, 0, 255);
        rect(20, height - 70, 100 * cooldownPercentage, 20);
        
        fill(255);
        if (cooldownPercentage < 1.0) {
            int secondsLeft = (int)((pushCooldown - timeSinceLastPush) / 1000) + 1;
            text("Push CD: " + secondsLeft + "s", 20, height - 55);
        } else {
            text("Push READY (F)", 20, height - 55);
        }
        
        noFill();
        stroke(255);
        rect(20, height - 70, 100, 20);
    }
    
    /**
     * Draw the push animation.
     */
    private void drawPushEffect() {
        noFill();
        stroke(255, 0, 255, 100);
        strokeWeight(5);
        ellipse(boss.getX() + boss.getWidth()/2, boss.getY() + boss.getHeight()/2, 
                boss.getWidth() + 40, boss.getHeight() + 40);
    }

    /**
     * Draw start screen.
     */
    private void drawStartScreen() {
        background(0);
        textFont(titleFont);
        fill(255, 255, 0);
        text("Journey to the West", 250, 100);

        textFont(subtitleFont);
        fill(255);
        text("Sun Wukong's Adventure", 280, 150);

        textFont(regularFont);
        text("Choose Your Path:", 350, 250);
        text("1. Rebel Against Heaven (Press A)", 300, 300);
        text("2. Join Monk Tang's Journey (Press D)", 300, 350);
        text("3. View Controls (Press C)", 300, 400);
        text("High Score: " + highScore, 350, 500);
    }
    
    /**
     * Draw controls screen.
     */
    private void drawControlsScreen() {
        background(0);
        textFont(titleFont);
        fill(255, 255, 0);
        text("Game Controls", 280, 80);
        
        textFont(helpFont);
        fill(255);
        int y = 150;
        String[] controls = {
            "Movement:", "WASD - Move Monkey King",
            "Combat:", "Mouse Click - Shoot Projectile (1s cooldown)",
            "", "E Key - Activate Shield (Partial energy allowed)",
            "", "F Key - Push Boss Away (2s cooldown)",
            "Game Actions:", "A Key - Rebel Path",
            "", "D Key - Monk Path",
            "", "Space - Start Final Battle",
            "", "R Key - Restart from Win/Lose Screen",
            "Menu Controls:", "C - Show Controls",
            "", "ESC - Return to Menu"
        };
        
        for (int i = 0; i < controls.length; i += 2) {
            text(controls[i], 50, y);
            text(controls[i+1], 100, y += 30);
            if (controls[i].isEmpty()) y += 10;
            else y += 10;
        }
        
        textFont(subtitleFont);
        fill(0, 255, 255);
        text("Press ESC to return to menu", 220, height - 50);
    }

    /**
     * Draw rebel battle screen.
     */
    private void drawRebelBattle() {
        background(0);
        if (backgroundImage != null) {
            tint(255, 220);
            image(backgroundImage, 0, 0, width, height);
            noTint();
        }

        for (HeavenlySoldier s : soldiers) s.draw(this);
        player.draw(this);
        for (Projectile p : projectiles) p.draw(this);

        fill(255);
        text("Defeated: " + soldiersDefeated + "/10", 20, 30);
        text("Player Lives: " + playerLives, 20, 60);
        text("Wave: " + rebelWave, 20, 90);
        
        drawAttackCooldown(height - 40);
    }
    
    /**
     * Draw attack cooldown indicator
     * @param yPos Vertical position of where to draw indicator
     */
    private void drawAttackCooldown(int yPos) {
        long currentTime = millis();
        long timeSinceLast = currentTime - lastProjectileTime;
        float cooldownPercent = Math.min(1.0f, (float) timeSinceLast / projectileCooldown);
        
        fill(50);
        rect(20, yPos, 100, 20);
        
        fill(255, 165, 0);
        rect(20, yPos, 100 * cooldownPercent, 20);
        
        fill(255);
        noFill();
        stroke(255);
        rect(20, yPos, 100, 20);
        text("Attack", 50, yPos + 15);
    }

    /**
     * Draw peach decision screen
     */
    private void drawPeachDecision() {
        if (peachImage != null) image(peachImage, 0, 0, width, height);
        else background(255, 200, 200);

        fill(0, 0, 0, 150);
        rect(0, 0, width, height);
        
        textFont(subtitleFont);
        fill(255, 255, 0);
        text("You defeated the Heavenly Army!", 220, 100);
        fill(255);
        text("The Immortal Peach tempts you...", 200, 150);
    }

    /**
     * Draw redemption path screen.
     */
    private void drawRedemptionPath() {
        if (templeImage != null) image(templeImage, 0, 0, width, height);
        else background(100, 150, 100);
        
        fill(0, 0, 0, 180);
        rect(0, 0, width, height);
        
        textFont(titleFont);
        fill(255, 215, 0);
        text("Path of Redemption", 250, 100);
        
        textFont(subtitleFont);
        fill(255);
        text("You chose the path of enlightenment", 200, 300);
        
        textFont(regularFont);
        text("Press SPACE to begin your journey", 250, 350);
        text("with Monk Tang", 350, 380);
    }

    /**
     * Draw boss battle screen.
     */
    private void drawBattle() {
        background(0);
        if (backgroundImage != null) {
            tint(255, 220);
            image(backgroundImage, 0, 0, width, height);
            noTint();
        }
            
        player.draw(this);
        if (monkHealth > 0) monk.draw(this);
        boss.draw(this);
        for (Projectile p : projectiles) p.draw(this);
        for (FireProjectile fp : fireProjectiles) fp.draw(this);

        fill(255);
        text("Boss HP: " + boss.getHealth() + "/300", 20, 30);
        text("Monk Tang HP: " + monkHealth, 20, 60);
        text("High Score: " + highScore, 20, 90);
        
        if (boss.isFireBreathing()) text("FIRE BREATH!", boss.getX(), boss.getY() - 20);
        
        drawAttackCooldown(height - 100);
    }

    /**
     * Draw win screen
     */
    private void drawWinScreen() {
        if (victoryImage != null) image(victoryImage, 0, 0, width, height);
        else background(0, 100, 0);
        
        fill(0, 0, 0, 180);
        rect(0, 0, width, height);
        
        textFont(titleFont);
        fill(255, 215, 0);
        text("Victory!", 330, 100);

        textFont(subtitleFont);
        fill(255);
        text("You helped Monk Tang complete his journey", 150, 150);
        text("Peace has returned to the realm", 230, 200);

        textFont(regularFont);
        text("Final Score: " + score, 330, 250);
        text("High Score: " + highScore, 330, 280);
        text("Press R to Restart", 330, 310);
    }

    /**
     * Draw lose screen
     */
    private void drawLoseScreen() {
        if (lostToSoldiers) {
            background(70, 70, 70);
            fill(0, 0, 0, 150);
            rect(0, 0, width, height);

            textFont(titleFont);
            fill(255, 0, 0);
            text("GAME OVER", 300, 100);

            textFont(regularFont);
            fill(255);
            text("You have been punished for your rebellion!", 220, 200);
            text("Trapped under Buddha's mountain for eternity", 200, 230);
            text("Press R to Restart", 330, 280);
        } else {
            background(0);
            textFont(titleFont);
            fill(255, 0, 0);
            text("GAME OVER", 300, 100);
            
            textFont(regularFont);
            fill(255);
            text("You failed to protect Monk Tang", 250, 200);
            text("and yourself against the Bull Demon King", 200, 230);
            text("Press R to Restart", 330, 280);
        }
    }

    /**
     * Process player movement input and updates position
     */
    private void handlePlayerMovement() {
        if (currentState != STATE_REBEL_BATTLE && currentState != STATE_BATTLE) return;
        
        int newX = player.getX();
        int newY = player.getY();
        
        if (movingUp) newY -= playerSpeed;
        if (movingDown) newY += playerSpeed;
        if (movingLeft) newX -= playerSpeed;
        if (movingRight) newX += playerSpeed;
        
        newX = constrain(newX, 0, width - player.getWidth());
        newY = constrain(newY, 0, height - player.getHeight());
        
        player.setX(newX);
        player.setY(newY);
    }
    
    /**
     * Update shield state and will deactivate when time expires
     */
    private void handleShieldState() {
        if (shieldActive && millis() >= shieldEndTime) {
            shieldActive = false;
            lastShieldDeactivationTime = millis();
            shieldEnergy = 0.0f;
        }
    }
    
    /**
     * Update rebel battle state
     */
    private void updateRebelBattle() {
        // Update projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.move();
            
            if (p.getX() < 0 || p.getX() > width || p.getY() < 0 || p.getY() > height) {
                projectiles.remove(i);
                continue;
            }
            
            for (int j = soldiers.size() - 1; j >= 0; j--) {
                HeavenlySoldier s = soldiers.get(j);
                if (p.isColliding(s)) {
                    soldiers.remove(j);
                    projectiles.remove(i);
                    soldiersDefeated++;
                    if (soldiers.isEmpty() && soldiersDefeated < 10) {
                        spawnSoldierWave();
                        rebelWave++;
                    }
                    break;
                }
            }
        }

        //Update soldiers
        for (HeavenlySoldier s : soldiers) {
            s.moveToward(player.getX(), player.getY());
            if (s.isColliding(player) && !player.isInvulnerable()) {
                playerLives--;
                player.setX(400);
                player.setY(300);
                player.setInvulnerable(2000);
                if (playerLives <= 0) {
                    lostToSoldiers = true;
                    currentState = STATE_LOSE;
                }
                break;
            }
        }

        if (soldiersDefeated >= 10) {
            peachDecisionStartTime = millis();
            currentState = STATE_PEACH_DECISION;
            lostToSoldiers = true;
        }
    }
    
    /**
     * Update peach decision state.
     */
    private void updatePeachDecision() {
        if (millis() - peachDecisionStartTime > 3000) {
            currentState = STATE_LOSE;
            lostToSoldiers = true;
        }
    }
    
    /**
     * Update boss battle state.
     */
    private void updateBattle() {
        if (monkHealth <= 0) {
            lostToSoldiers = false;
            currentState = STATE_LOSE;
            return;
        }
        
        updateBossAbilities();
        handleFireProjectiles();
        handleBossMovement();
        handleProjectiles();
        checkWinCondition();
    }
    
    /**
     * Update boss special abilities.
     */
    private void updateBossAbilities() {
        // Fire a big fireball every 5 seconds
        if (millis() - lastBossAbilityTime >= 5000) {
            boss.startFireBreath();
            createFireProjectile();  // Create single big fireball
            lastBossAbilityTime = millis();
        }
    }
    
    /**
     * Create new fire projectile aimed at Monk Tang.
     */
    private void createFireProjectile() {
        if (monkHealth <= 0) return;
        
        // Create a big fireball aimed at Monk Tang
        fireProjectiles.add(new FireProjectile(
            boss.getX() + boss.getWidth()/2,
            boss.getY() + boss.getHeight()/2,
            monk.getX() + monk.getWidth()/2,
            monk.getY() + monk.getHeight()/2
        ));
    }
    
    /**
     * Handle all fire projectile updates and collisions.
     */
    private void handleFireProjectiles() {
        for (int i = fireProjectiles.size() - 1; i >= 0; i--) {
            FireProjectile fp = fireProjectiles.get(i);
            fp.move();
            
            // Remove when close to target
            int distance = abs(fp.getX() - fp.targetX) + abs(fp.getY() - fp.targetY);
            if (distance < 10) {
                fireProjectiles.remove(i);
                continue;
            }
            
            if (fp.getX() < -100 || fp.getX() > width + 100 || 
                fp.getY() < -100 || fp.getY() > height + 100) {
                fireProjectiles.remove(i);
                continue;
            }
            
            if (fp.isColliding(player) && !player.isInvulnerable()) {
                if (!shieldActive) {
                    playerLives--;
                    player.setInvulnerable(2000);
                    if (playerLives <= 0) {
                        lostToSoldiers = false;
                        currentState = STATE_LOSE;
                    }
                }
                fireProjectiles.remove(i);
            }
            else if (monkHealth > 0 && fp.isColliding(monk)) {
                if (!shieldActive) {
                    monkHealth--;
                    if (monkHealth <= 0) {
                        lostToSoldiers = false;
                        currentState = STATE_LOSE;
                    }
                }
                fireProjectiles.remove(i);
            }
        }
        
        if (pushActive && millis() > pushEndTime) pushActive = false;
    }
    
    /**
     * Handle boss movement and collisions.
     */
    private void handleBossMovement() {
        int targetX = monkHealth > 0 ? monk.getX() : player.getX();
        int targetY = monkHealth > 0 ? monk.getY() : player.getY();
        
        boss.moveToward(targetX, targetY);

        if (monkHealth > 0 && boss.isColliding(monk) && !shieldActive) {
            monkHealth--;
            if (monkHealth <= 0) {
                lostToSoldiers = false;
                currentState = STATE_LOSE;
            }
        }

        if (boss.isColliding(player) && !player.isInvulnerable() && !shieldActive) {
            playerLives--;
            player.setInvulnerable(2000);
            if (playerLives <= 0) {
                lostToSoldiers = false;
                currentState = STATE_LOSE;
            }
        }
    }
    
    /**
     * Handle player projectiles updates and collisions.
     */
    private void handleProjectiles() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.move();
            
            if (p.getX() < 0 || p.getX() > width || p.getY() < 0 || p.getY() > height) {
                projectiles.remove(i);
                continue;
            }
            
            if (p.isColliding(boss)) {
                boss.takeDamage(10);
                projectiles.remove(i);
            }
        }
    }
    
    /**
     * Check if player has defeated the boss
     */
    private void checkWinCondition() {
        if (boss.getHealth() <= 0) {
            score += 100;
            if (score > highScore) {
                highScore = score;
                FileUtils.saveHighScore(highScore);
            }
            currentState = STATE_WIN;
        }
    }

    /**
     * Spawn new wave of enemy soldiers
     */
    private void spawnSoldierWave() {
        soldiers.clear();
        int count = min(3, 10 - soldiersDefeated);
        
        for (int i = 0; i < count; i++) {
            int side = (int) random(4);
            int x = 0, y = 0;
            
            if (side == 0) { x = (int) random(width); y = -50; }
            else if (side == 1) { x = (int) random(width); y = height + 50; }
            else if (side == 2) { x = -50; y = (int) random(height); }
            else { x = width + 50; y = (int) random(height); }
            
            soldiers.add(new HeavenlySoldier(this, x, y, "images/soldier.png"));
        }
    }

    /**
     * Handle key press events
     */
    public void keyPressed() {
        char keyLower = Character.toLowerCase(key);
        
        // Movement keys
        if (keyLower == 'w') movingUp = true;
        else if (keyLower == 's') movingDown = true;
        else if (keyLower == 'a') movingLeft = true;
        else if (keyLower == 'd') movingRight = true;
        else if (keyLower == 'e') activateShield();
        else if (keyLower == 'f') activatePush();
        
        //Game state transitions
        if (keyLower == 'a') {
            if (currentState == STATE_START) {
                resetGame();
                currentState = STATE_REBEL_BATTLE;
                spawnSoldierWave();
            } 
            else if (currentState == STATE_LOSE || currentState == STATE_WIN) {
                resetGame();
                currentState = STATE_START;
            }
        }
        else if (keyLower == 'd') {
            if (currentState == STATE_START) {
                resetGame();
                currentState = STATE_REDEMPTION;
            }
        }
        else if (key == ' ') {
            if (currentState == STATE_REDEMPTION) currentState = STATE_BATTLE;
        }
        else if (keyLower == 'c') {
            if (currentState == STATE_START) currentState = STATE_CONTROLS;
        }
        else if (keyLower == 'r') {
            if (currentState == STATE_LOSE || currentState == STATE_WIN) {
                resetGame();
                currentState = STATE_START;
            }
        }
        
        //ESC key handling
        if (keyCode == ESC && currentState == STATE_CONTROLS) {
            key = 0;
            currentState = STATE_START;
        }
    }
    
    /**
     * Handle key release events
     */
    public void keyReleased() {
        char keyLower = Character.toLowerCase(key);
        
        if (keyLower == 'w') movingUp = false;
        else if (keyLower == 's') movingDown = false;
        else if (keyLower == 'a') movingLeft = false;
        else if (keyLower == 'd') movingRight = false;
    }
    
    /**
     * Activate the shield ability if available.
     */
    private void activateShield() {
        if (currentState != STATE_BATTLE) return;
        
        long currentTime = millis();
        
        if (shieldActive) {
            shieldActive = false;
            lastShieldDeactivationTime = currentTime;
            shieldEnergy = (float)(shieldEndTime - currentTime) / shieldDuration;
        } 
        else if (shieldEnergy > 0.01f) {
            shieldActive = true;
            shieldEndTime = currentTime + (long)(shieldDuration * shieldEnergy);
            shieldEnergy = 0.0f;
        }
    }
    
    /**
     * Activate push ability if there is no more cooldown
     */
    private void activatePush() {
        if (currentState != STATE_BATTLE) return;
        if (millis() - lastPushTime <= pushCooldown) return;
        
        pushActive = true;
        pushEndTime = millis() + 1000;
        lastPushTime = millis();
        
        int dx = boss.getX() - player.getX();
        int dy = boss.getY() - player.getY();
        int pushDistance = 200;
        
        int newX = boss.getX() + (dx > 0 ? pushDistance : -pushDistance);
        int newY = boss.getY() + (dy > 0 ? pushDistance : -pushDistance);
        
        boss.setX(constrain(newX, 0, width - boss.getWidth()));
        boss.setY(constrain(newY, 0, height - boss.getHeight()));
    }

    /**
     * Reset all game state to initial values.
     */
    private void resetGame() {
        player = new Player(this, 400, 300, "images/wukong.png");
        monk = new MonkTang(this, 600, 300, "images/monktang.png");
        boss = new BullDemonKing(this, 100, 100, "images/bullking.png");
        monkHealth = 5;
        playerLives = 3;
        soldiers.clear();
        projectiles.clear();
        fireProjectiles.clear();
        score = 0;
        soldiersDefeated = 0;
        rebelWave = 1;
        shieldActive = false;
        pushActive = false;
        lastProjectileTime = 0;
        shieldEnergy = 1.0f;
        lastShieldDeactivationTime = 0;
        movingUp = movingDown = movingLeft = movingRight = false;
        lostToSoldiers = false;
    }

    /**
     * Handle mouse press events
     */
    public void mousePressed() {
        if (currentState != STATE_REBEL_BATTLE && currentState != STATE_BATTLE) return;
        if (millis() - lastProjectileTime < projectileCooldown) return;
        
        Projectile p = new Projectile(this, 
            player.getX() + player.getWidth()/2,
            player.getY() + player.getHeight()/2,
            mouseX, mouseY,
            "images/projectile.png"
        );
        projectiles.add(p);
        lastProjectileTime = millis();
    }
}