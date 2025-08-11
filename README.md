# Tanks Project


**Tanks Project** is a 2D turn-based artillery game built in **Java 8** using the **Processing** graphics library and **Gradle 8.6**.  

Developed as part of the **INFO1113** assignment (S1, 2024) at **University of Sydney**, the game challenges players to control tanks, aim accurately, and outscore their opponents across multiple levels.  

Players take turns moving their tank, adjusting turret angle and power, and firing projectiles affected by wind and terrain. Levels, backgrounds, colours, and other settings are loaded from external configuration and layout files. The player with the highest total score after all levels wins.

## Requirements

- **Java 8** 
- **Gradle 8.x** (wrapper included, no need to install separately)
- **Processing** core library (managed via Gradle dependencies)

## How to Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/devilshadow04/Tanks-Project.git
   cd Tanks-Project
   ```

2. **Run the game**:
   ```bash
   gradle run
   ```

## Gameplay Instructions

### 🎯 Goal
Control your tank to destroy opponents and survive across all levels.  
Earn points by damaging other tanks — the player with the highest total score at the end wins.

### 🕹 Controls
- **UP** – Rotate turret left  
- **DOWN** – Rotate turret right  
- **LEFT** – Move tank left (uses fuel)  
- **RIGHT** – Move tank right (uses fuel)  
- **W** – Increase firing power  
- **S** – Decrease firing power  
- **SPACE** – Fire a projectile (ends your turn)  
- **R** – Use repair kit (+20 HP, costs 20 points)  
- **F** – Use extra fuel (+200 fuel, costs 10 points)  

### 🌍 Playing the Game
- Levels are made of different terrains and obstacles.  
- Each player starts at a set position and takes turns in alphabetical order.  
- You have a limited amount of fuel each level, plan your movement wisely.  
- Wind changes each turn and affects your shot’s direction and distance.  
- Adjust your aim and power to hit opponents while avoiding obstacles.  

### 💥 Damage & Survival
- Projectiles cause explosions that damage tanks in range.  
- Direct hits cause more damage than near misses.  
- If your tank’s health reaches 0, it’s destroyed and you’re out for the rest of the level.  

### 🪂 Parachutes
- Each player starts with 3 parachutes for the entire game.  
- If the ground beneath you is destroyed, a parachute will deploy automatically to prevent damage.  
- Without a parachute, falling will cause health loss.

### 🛠 Powerups
- **Repair Kit (R)** – Restore health (+20 HP)  
- **Extra Fuel (F)** – Add more movement fuel (+200)  
- Powerups cost score points — only buy them if you can afford it.

### 📊 Scoring
- Gain points by damaging enemy tanks.  
- No points are awarded for damaging yourself.  
- Scores carry over between levels and are shown on the scoreboard.

### 🏆 Winning
- A level ends when only one tank remains.  
- The game ends after all levels are played.  
- The player with the highest total score is declared the winner.

## License

This project is for educational purposes as part of the University of Sydney coursework.  
