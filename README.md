# Java-AWT-Solitaire-Game
Solitaire game example using Java AWT. Includes basic game logic, localization and a save/load system.

# Screenshots
![Screenshot-solitaire](http://i.imgur.com/P7055e6.png)

# Program architecture

The program main class is **Game**, which contains the main function and also creates singular instances of the **GUI** and **Engine**.

The game, as it should, does not use any **global variables**. All variables are members of some class. The GUI instances can access public members and methods from the Engine instance because the Engine is passed as an argument to the GUI’s constructor.

The **Engine** class contains functions which enable the interaction between one or two **Piles**.

The **GUI** class, except for display all user interface elements and positioning the game objects, it also handles all the interactions done by the user using the mouse or keyboard input.

The **Pile** class is a container class which hold zero, one or more Cards. This class contains solitairespecific logic to make sure only legal moves can be performed.

The **Card** class holds information about a single playing card (including the image) and the **Deck** class contains a list of 52 cards.

To easily handle the dragging event, when a drag event is started the pile that was clicked gets split into two piles (one that remains still, and another temporarily pile that is absolute positioned). The temporarily file gets merged with another pile when the drag event is complete, or is appended back to the original pile.

**The GUI implementation** uses the root content pane to display all game content and the default frame layered pane to display the top menu bar. The layered pane is also used to display the absolutepositioned temp pile while it is being dragged.

# License


[![cc-by](https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/CC-BY_icon.svg/88px-CC-BY_icon.svg.png)](https://creativecommons.org/licenses/by/2.0/)

Licensees may copy, distribute, display and perform the work and make derivative works and remixes based on it only if they give the author or licensor the credits (attribution) in the manner specified by these
