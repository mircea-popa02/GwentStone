# GwentStone 
*Popa Mircea 323CD*


## 📝 Table of Contents
#### [Intro](#intro)
#### [Description](#description)
#### [Implementation](#implementation)
#### [Conclusion](#conclusion)


## Intro <a name = "intro"></a>
The task was to implement a static, terminal-based card game between two
fictional players which shares some features with Gwent and Hearthstone.

## 🔧 Description <a name = "description"></a>
The program uses a number of classes located in the *fileio* package
which are used to model input and output data. The data is formatted using the
JSON standard and is located inside the *input* folder. The output 
data is held inside an ArrayNode object which is then copied to a JSON file.

## 🏁 Implementation <a name = "implementation"></a>
* The main game logic is handled inside the GameLogic class (part of the logic
package). The **playGames()** method is responsible for looping through
the actions, writing to output and working with raw input data. For each game
played between the two players, the program simply reads the current command,
accounts for the players' turn, frozen cards and other factors and writes to
output (in case of a debugging commands or possible errors), or applies
effects to the cards placed on the game table.

* The game **table** is a 4x5 2D ArrayList of GenericCard instances. The
GenericCard class is parent to the **MinionCard**, **EnvironmentCard** and
**Hero** classes, which are all the types of cards used in the game. All of them
share common fields such as mana, description, colors and name, but minion 
and hero cards also have attack and health and other methods associated.

* The player decks and the cards in hand are modelled using the **Deck** class.
It contains a GenericCard Arraylist and some debugging methods. The decks are
generally modified using the default add() and remove() methods inside GameLogic.

* The frozen dynamic is handled using two 2x5 int arrays, one for each player.
Each field can either be 1 (in case a card is frozen), or 0 if the card can be
used to attack the enemy. Each 2D array corresponds to one of the two sides of
the game table. Unfreezing occurs after a player ends their turn in which they
had a frozen card.

* Keeping track of player mana is done using a PlayerMana class instance which
is modified accordingly after using cards abilities or at the end of a round.

## 🎈 Conclusion <a name = "conclusion"></a>
To conclude, the implementation handles most types of commands, but does not
account for some invalid inputs and errors. The time spent for this homework
is around 36 hours.

**GitHub link**: https://github.com/mircea-popa02/GwentStone

*The repository will be made public after the hard deadline
