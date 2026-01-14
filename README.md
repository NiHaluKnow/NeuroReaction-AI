# Chain Reaction Game 

A strategic grid-based game implementation in Java featuring an intelligent AI opponent powered by Minimax algorithm with Alpha-Beta pruning.

![Game Mode](https://img.shields.io/badge/Mode-PvP%20%7C%20PvAI%20%7C%20AI%20vs%20AI-blue)
![Java](https://img.shields.io/badge/Java-11+-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## 📖 Overview

Chain Reaction is a deterministic combinatorial game where players take turns placing orbs on a grid. When a cell reaches its critical mass, it explodes, distributing orbs to adjacent cells and potentially triggering spectacular chain reactions. The last player with orbs on the board wins!

### Game Rules

- **Grid**: 9×6 board (customizable)
- **Critical Mass**: 
  - Corners: 2 orbs
  - Edges: 3 orbs
  - Center: 4 orbs
- **Explosion**: When a cell reaches critical mass, it explodes
- **Chain Reactions**: Explosions can trigger cascading reactions
- **Conversion**: Enemy orbs are captured during explosions
- **Victory**: Eliminate all opponent orbs from the board

## 🎮 Features

### Game Modes
- 🧑‍🤝‍🧑 **Human vs Human**: Local multiplayer
- 🤖 **Human vs AI**: Challenge the intelligent AI
- 🤖🆚🤖 **AI vs AI**: Watch AI opponents battle

### AI Intelligence
- **Minimax Algorithm** with Alpha-Beta pruning
- **Configurable Difficulty**: Adjustable search depth
- **Multiple Heuristics**:
  - **Orb Count**: Material advantage based on total orbs
  - **Critical Mass**: Weights cells near explosion threshold
  - **Strategic Position**: Values corners (2×) and edges (1.5×) higher
  - **Killer Move**: Rewards cells about to explode near enemy orbs
  - **Conversion Potential**: Evaluates opportunities to capture opponent's orbs
  - **Growth Potential**: Assesses expansion and safe positioning
  - **Combined**: Weighted combination of all heuristics (default)

### User Interfaces
- **GUI Mode**: Beautiful graphical interface with:
  - Smooth animations
  - Particle effects
  - Visual explosion chains
  - Highlighted moves
  - Game state save/load
  
- **CLI Mode**: Terminal-based gameplay for classic experience

## 🚀 Quick Start

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Terminal/Command Prompt

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/chain-reaction-game.git
cd chain-reaction-game

# Make scripts executable (Linux/Mac)
chmod +x run.sh dltClass.sh
```

### Running the Game

#### GUI Mode (Recommended)
```bash
./run.sh
```

#### CLI Mode
```bash
javac *.java
java ChainReactionLauncher
```

#### Manual Compilation
```bash
# Compile
javac GaMe/*.java Ai_Player/*.java Heuristics/*.java *.java

# Run GUI
java ChainReactionGUI

# Run CLI
java ChainReactionLauncher
```

### Cleaning Build Files
```bash
./dltClass.sh
```

## 📁 Project Structure

```
chain-reaction-game/
├── GaMe/
│   ├── Cell.java                 # Cell data structure
│   ├── ChainReactionGame.java    # Core game logic
│   └── GameState.java            # Game state wrapper
├── Ai_Player/
│   ├── AIPlayer.java             # AI implementation
│   └── MinimaxResult.java        # Minimax result structure
├── Heuristics/
│   └── Heuristics.java           # Evaluation functions
├── ChainReactionGUI.java         # Graphical interface
├── ChainReactionLauncher.java    # CLI interface
├── run.sh                        # Quick run script
├── dltClass.sh                   # Clean build files
└── README.md
```

## 🎯 How to Play

### GUI Mode
1. Launch the game using `./run.sh`
2. Select game mode from the menu
3. Click on cells to place orbs
4. Watch chain reactions unfold!

### CLI Mode
1. Choose game mode (1-3)
2. Enter row and column coordinates when prompted
3. Follow the game state displayed in terminal

### Strategy Tips
- 💡 Corners and edges have lower critical mass - easier to explode
- 💡 Build up orbs strategically before triggering chains
- 💡 Convert enemy orbs by exploding adjacent cells
- 💡 Control key positions to dominate the board

## 🧠 AI Implementation

### Minimax with Alpha-Beta Pruning

The AI uses a sophisticated search algorithm:

```
1. Generate all legal moves
2. Recursively evaluate game tree up to specified depth
3. Maximize AI score, minimize opponent score
4. Prune branches that won't improve the result
5. Select move with highest evaluation
```

### Heuristic Evaluation

**Combined Heuristic Formula:**
```
Score = 0.2×OrbCount + 0.3×CriticalMass + 0.2×StratPosition 
        + 0.2×ConversionPotential + 0.1×GrowthPotential
```

**Available Heuristics:**
- `"orb_count"`: Simple material advantage (player orbs - opponent orbs)
- `"critical_mass"`: Weights cells based on proximity to explosion (mass/criticalMass ratio)
- `"strategic_position"`: Values corners (2.0×) and edges (1.5×) over center cells (1.0×)
- `"killer_move"`: Rewards cells 1-2 orbs away from critical mass near enemy positions
- `"conversion_potential"`: Evaluates cells about to explode that can capture enemy orbs
- `"growth_potential"`: Assesses empty cells adjacent to player orbs and safe single-orb cells
- `"combined"`: Weighted combination (default)

### Performance
- Configurable search depth (default: 3 levels)
- Efficient pruning reduces search space
- Real-time node evaluation statistics
- Typical response time: < 2 seconds

## 🛠️ Customization

### AI Difficulty
```java
// In ChainReactionLauncher.java or ChainReactionGUI.java
AIPlayer ai = new AIPlayer('B', depth, heuristic);
// depth: 2 (easy), 3 (medium), 4+ (hard)
// heuristic: "combined", "orb_count", "critical_mass", etc.
```

### Board Size
```java
// In ChainReactionGame.java
ChainReactionGame game = new ChainReactionGame(rows, cols);
// Default: 9 rows × 6 columns
```

### AI Delay (GUI)
```java
// In ChainReactionGUI.java
public int aiDelay = 2000; // milliseconds
```

## 📊 Technical Details

### Core Algorithms
- **BFS Queue**: For explosion chain propagation
- **Minimax**: Game tree search (depth-limited)
- **Alpha-Beta Pruning**: Search space optimization
- **State Cloning**: For move simulation without mutation

### Time Complexity
- **Move Generation**: O(rows × cols)
- **Minimax**: O(b^d) where b = branching factor, d = depth
- **With Pruning**: Reduces to ~O(b^(d/2)) in best case

## 🤝 Contributing

Contributions are welcome! Areas for improvement:
- Additional heuristics
- Machine learning integration
- Network multiplayer
- Mobile version
- Tournament mode

## 📝 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

This project was created as a part of the course **CSE-318: Artificial Intelligence**

## 🙏 Acknowledgments

- Inspired by the classic Chain Reaction mobile game

---

**Enjoy the game!** ⚡️ If you find this project interesting, please give it a ⭐ on GitHub!
