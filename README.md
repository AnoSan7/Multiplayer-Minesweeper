# Multiplayer Minesweeper

Multiplayer Minesweeper is a modern, Java-based adaptation of the classic Minesweeper game enriched with real-time multiplayer functionality that runs on different laptops connected under the same network. In this turn-based game, two players compete by strategically uncovering cells while a custom networking layer synchronizes every move between a host and a joining player.



## Demo
![image (2)](https://github.com/user-attachments/assets/8480142a-ca3e-4f52-b84f-dbe0d5bf1ff7)
![Untitled video - Made with Clipchamp (1) (1)](https://github.com/user-attachments/assets/fec4cfc9-86d5-4042-b7ee-a10304a8ccc6)


## Features
- **Modern Dark-Themed UI:** Crafted with Java Swing to deliver an engaging visual experience.
- **Turn-Based Multiplayer:** Enables real-time competitive gameplay between two players.
- **Classic Game Mechanics:** Implements traditional Minesweeper rules with added strategic elements.

## Technologies & Libraries
- **Java AWT & Swing:** Used for building a responsive and detailed graphical user interface.
- **Java Networking (java.net):** Utilized for handling low-level network communication and ensuring reliable multiplayer connections.
- **Maven:** For project management and build automation.
- Additional libraries handling threading and event management to support smooth gameplay.

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven

### Installation
1. **Clone the Repository:**
   ```sh
   git clone https://github.com/AnoSan7/Multiplayer-Minesweeper.git
   ```
2. **Build the Project:**
   ```sh
   mvn clean install
   ```
3. **Run the Game:**
   ```sh
   java -cp target/classes Main
   ```

## Team Roles

**Aayush Sachan**  
- **UI/UX Design & Development:**  
  Responsible for designing and implementing the user interface, ensuring a sleek and intuitive dark-themed experience. Tasks include creating responsive layouts, managing event handling, and integrating visual effects to enhance gameplay immersion.

**Anomitra Santra**  
- **Core Game Logic & Mechanics:**  
  Focuses on the fundamental game mechanics by developing and refining the board configuration, cell behavior, and overall game state management. This role involves ensuring that the game rules are correctly implemented and optimizing the logic for a smooth gaming experience.

**Ayush Bhagat**  
- **Networking & Multiplayer Synchronization:**  
  Develops and maintains the custom networking framework that supports real-time, turn-based multiplayer functionality. Responsibilities include establishing reliable communication between players, handling network latency, and ensuring seamless synchronization of both players’ actions.
