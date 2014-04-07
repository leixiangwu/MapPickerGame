Map-Picker-Game
===============

Map-Picker-Game Map picker game application was a course project for Spring 2013 Computer Science III (CSE 219) at Stony Brook University.
The purpose of this game was to help people learning region names, flags, leaders and capitals.

Author
===============
* Leixiang Wu
* Stony Brook University Student <return>
* leixiang5@yahoo.com <return>

Install Direction
===============
1. You need to have both Java SDK and NetBeans:
2. Download all the zip files
3. Open the NetBeans
4. Click File, Import, and choose ZIP
5. Import the ZIP file
6. Now you should see the list of projects
7. Click on RambleOn project
8. Click Run Button
9. Now you should be able to play the game.
10. Have fun!!!!!!!

Play Game Direction
===============
1. Click on on Welcome image.
2. Either select an existing account or create a new account to record the game history.
3. Left side displays a tree of regions. Click on each region will display the game history on right side for that particular region (Played Times, Leader Mode, Fastest Time). Note that you can navigate into subregions by clicking on key icon
4. Click Right Arrow at top to entern the game or left arrow to go back to account screen. 
5. The bar has 6 icons. The game will begin after you select one of icons. Earth icon is for region name mode, Flag icon is for region flag mode, Peron icon is for leader mode, House icon is for capital mode. Arraw icon is for going back to the precious screen. Last button is to exit the game.
6. When the game starts, a region statck and game status will be displayed. 
7. Select corresponding region to the top one of the stack (green box). Correct matching a region will change the region to green and you should hear a sound correct matching, wrong selection will mark the region to red and another sound to indicate wrong matching. The status will be updated as well.
8. The stack goes down slowly to next region after a correct selection.
9. When you finish all regions, a winning message will pop up. It will contain the game status for this game. 
10. If you wonder the picture in the winning message, guess who? he is my professor Richard Mckenna!! Click on X button to go back to game screen to play another game

Notes
===============
* It includes a Software Design Description (SDD) for the RambleOnTM (Region Picker) game application.
* You may realize nothing happens after click on a white region. Some regions don't have data associated with them and it was there for pratice of fool design proof in which doesn't allow user to do illegal actions.
* Unplayable modes will be in white color. 
* Some regions are not playable because it has enough regions to demonstrate the idea behind this game. Those regions have white colors.

Game Modes
===============
* Region Name Mode: Select corresponding region map to the region name on stack
* Flag Mode: Select corresponding region map to the region flag on stack
* Leader Mode: Select corresponding region map to the region leader picture on stack
* Capital Mode: Select corresponding region map to the region capital name on stack
 
Game Roles
===============
If you want to cheat, press keyboard C. It was there for my grader to grade my assignment more easily.

Suggestions
===============
If you want to give some suggestions to me, feel free to email me (leixiang5@yahoo.com).

Credits
===============
* Map data were collected from students who took CSE 219 in Spring 2013.
* AudioManager, XMLUtilities, and MiniGame frameworks are provided by professor Richard Mckenna.
