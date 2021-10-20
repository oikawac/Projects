#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "cmdscreen.h"
#include "planephysics.h"

//global filename variable
char FILENAME[30];


//MASS LIST FUNCTIONS & STRUCTS
//linked list structure to manage adding and deleteing masses
typedef struct MassList MassList;
struct MassList {
	int deleted;
	Mass* mass;
	MassList* next;
};
//init empty linked list
MassList* emptyMassList() {
	MassList* masses = (MassList *)malloc(sizeof(MassList));
	masses->deleted = 0;
	masses->mass = (Mass *)malloc(sizeof(Mass));
	masses->next = NULL;
	return masses;
}
//add mass to end of linked list
void addMass(MassList* masses, double mass, Vector position, Vector velocity) {
	while(masses->next != NULL) {
		masses = masses->next;
	}
	masses->next = emptyMassList();
	masses->mass->mass = mass;
	masses->mass->position = position;
	masses->mass->velocity = velocity;
}
//save mass list to file
int saveToFile(MassList* masses) {
	FILE *f = fopen(FILENAME, "wt");
	if (f == NULL) return 1;
	while (masses->next != NULL) {
		if (masses->deleted == 0) {
			fprintf(f, "%lf %lf %lf %lf %lf\n", masses->mass->mass, masses->mass->position.x, masses->mass->position.y, masses->mass->velocity.x, masses->mass->velocity.y);
		}
		masses = masses->next;
	}
	fclose(f);
	return 0;
}
//open file and return linked list of masses
MassList* openFromFile() {
	FILE *f = fopen(FILENAME, "rt");
	MassList* masses = emptyMassList();
	if (f == NULL) return masses;
	long int lastLinePosition = ftell(f)-1;
	while (!feof(f)) {
		double m, px, py, vx, vy;
		char line[100];
		fgets(line, 99, f);
		int parsedVars = sscanf(line, "%lf %lf %lf %lf %lf\n", &m, &px, &py, &vx, &vy);
		Vector position = {.x = px, .y = py};
		Vector velocity = {.x = vx, .y = vy};
		if (ftell(f) != lastLinePosition && parsedVars == 5) {
			addMass(masses, m, position, velocity); 
		}
		lastLinePosition = ftell(f);
	}
	fclose(f);
	return masses;
}


//MENU FUNCTIONS & STRUCTS
//struct defining menu items
typedef struct MenuPage MenuPage;
struct MenuPage {
	int height;
	char*** lineSelectionCmds;
	char** line;
	int* link;
};
//create menu page of width and height
MenuPage createMenuPage(int width, int height) {
	MenuPage menuPage;
	menuPage.height = height;
	menuPage.line = (char **)malloc((height-2)*sizeof(char *));
	menuPage.lineSelectionCmds = (char ***)malloc((height-2)*sizeof(char**));
	for (int i=0;i<height-2;i++) {
		menuPage.line[i] = (char *)malloc((width-10)*sizeof(char));
		menuPage.lineSelectionCmds[i] = (char **)malloc(2*sizeof(char *));
		menuPage.lineSelectionCmds[i][0] = (char *)malloc((width-10)*sizeof(char));
		menuPage.lineSelectionCmds[i][1] = (char *)malloc((width-10)*sizeof(char));
		for (int j=0;j<width-10;j++) {
			menuPage.line[i][j]=' ';
			menuPage.lineSelectionCmds[i][0][j]=' ';
			menuPage.lineSelectionCmds[i][1][j]=' ';	
		}
	}
	menuPage.link = (int *)malloc((height-2)*sizeof(int));
	for (int i=0;i<height-2;i++) {
		menuPage.link[i]=-1;
	}
	return menuPage;
}
//free up memory used for menu page
void deleteMenuPage(MenuPage page) {
	for (int i=0;i<page.height-2;i++) {
		free(page.line[i]);
		free(page.lineSelectionCmds[i][0]);
		free(page.lineSelectionCmds[i][1]);
		free(page.lineSelectionCmds[i]);
	}
	free(page.line);
	free(page.lineSelectionCmds);
	free(page.link);
}
//paste menu page onto surface
void displayMenuPage(Surface *menuSurface, MenuPage *page) {
	Frame bar = {.x=0, .y=0, .width=menuSurface->frame.width, .height=1};
	clearSurface(menuSurface);
	fillFrameInSurface(bar, '=', menuSurface);
	printOnSurface(1, 2, "command:", menuSurface);
	for (int i=0;i<menuSurface->frame.height-2;i++) {
		printOnSurface(10, i+3, page->line[i], menuSurface);
	}
}


//INPUT FUNCTIONS
//get float input from stdin
int getRangedFloatInput(float low, float high, float* variable) {	
	int parsedVars = scanf("%f", variable);
	if (*variable < low) {
		*variable = low;
	} else if (*variable > high) {
		*variable = high;
	}
	return parsedVars;
}
//get vector input from stdin
int getVectorInput(Vector* vector) {	
	int parsedVars = scanf("%lf, %lf", &vector->x, &vector->y);
	return parsedVars;
}


//DISPLAY MASSES
//paste masses from linked list onto surface
void displayMasses(MassList *masses, MassList* focusedMass, Vector viewPositionOffset, Surface *genSurface) {
	while (masses != NULL) {
		if (masses->next != NULL && masses->deleted == 0) {
			printCharOnSurface((int)((masses->mass->position.x-viewPositionOffset.x+genSurface->frame.width/4)*2), 
			(int)(-masses->mass->position.y+viewPositionOffset.y)+genSurface->frame.height/2,
			'O', genSurface);
		}
		masses = masses->next;
	}
	if (focusedMass->next != NULL) {
		int x = (int)((focusedMass->mass->position.x-viewPositionOffset.x+genSurface->frame.width/4)*2);
		int y = (int)(-focusedMass->mass->position.y+viewPositionOffset.y)+genSurface->frame.height/2;
		printCharOnSurface(x, y, 'X', genSurface);
		char infoMass[30];
		char infoPosition[30];
		char infoVelocity[30];
		sprintf(infoMass, "mass: %.4lf", focusedMass->mass->mass);
		sprintf(infoPosition, "pos: %.2lf, %.2lf", focusedMass->mass->position.x, focusedMass->mass->position.y);
		sprintf(infoVelocity, "vel: %.2lf, %.2lf", focusedMass->mass->velocity.x, focusedMass->mass->velocity.y);
		printOnSurface(x+2, y, infoMass, genSurface);
		printOnSurface(x+2, y+1, infoPosition, genSurface);
		printOnSurface(x+2, y+2, infoVelocity, genSurface);
	}
}

//Start Generator
//-open file
//-variable definition for all generator params and cmdscreen structs
//while loop for duration of program
//-paste surfaces and refresh screen
//-parse user input
//end while
//-save to file
//-free up all memory allocation
void startGenerator(int width, int height) {
	//width and height of screen
	int screenWidth = width;
	int screenHeight = height;

	//camera offset vector
	Vector viewPositionOffset = zeroVector;
	
	//masses linked list
	MassList* masses = openFromFile();
	MassList* focusedMass = masses;
	while (focusedMass->next != NULL) {
		focusedMass = focusedMass->next;
	}
	
	//while loop conditions for keep running program
	short unsigned int programIsRunning = 1;

	//init screen struct
	Screen screen;
	initializeScreen(screenWidth, screenHeight, &screen);
	clearScreen(&screen);

	//init each surface struct 
	Frame titleFrame = {.x=0, .y=0, .width=screenWidth, .height = 3};
	Frame generatorFrame = {.x=0, .y=3, .width=screenWidth, .height=30};
	Frame menuFrame = {.x=0, .y=32, .width=screenWidth, .height=10};
	Surface titleSurface;
	Surface generatorSurface;
	Surface menuSurface;
	initializeSurface(titleFrame, &titleSurface);
	initializeSurface(generatorFrame, &generatorSurface);
	initializeSurface(menuFrame, &menuSurface);
	clearSurface(&titleSurface);
	Frame bar = {.x=0, .y=0, .width=screenWidth, .height=1};
	fillFrameInSurface(bar, '=', &titleSurface);
	bar.y=2;
	fillFrameInSurface(bar, '=', &titleSurface);
	printOnSurface(1, 1, "ADD MASSES", &titleSurface);
	printOnSurface(screenWidth-strlen(FILENAME)-1, 1, FILENAME, &titleSurface);
	clearSurface(&generatorSurface);

	//define menu pages
	char input[32];
	struct MenuPage main = createMenuPage(screenWidth, menuFrame.height);
	strcpy(main.line[0], "(s)imulator");
	strcpy(main.lineSelectionCmds[0][0], "s");
	strcpy(main.lineSelectionCmds[0][1], "simulator");
	main.link[0] = -2;
	strcpy(main.line[1], "(a)dd mass");
	strcpy(main.lineSelectionCmds[1][0], "a");
	strcpy(main.lineSelectionCmds[1][1], "add");
	main.link[1] = -3;
	strcpy(main.line[2], "(d)elete mass");
	strcpy(main.lineSelectionCmds[2][0], "d");
	strcpy(main.lineSelectionCmds[2][1], "delete");
	main.link[2] = -4;
	strcpy(main.line[3], "(f)ocus next mass");
	strcpy(main.lineSelectionCmds[3][0], "f");
	strcpy(main.lineSelectionCmds[3][1], "focus");
	main.link[3] = -5;
	strcpy(main.line[4], "(c)hange mass parameters");
	strcpy(main.lineSelectionCmds[4][0], "c");
	strcpy(main.lineSelectionCmds[4][1], "change");
	main.link[4] = -100;
	struct MenuPage params = createMenuPage(screenWidth, menuFrame.height);
	strcpy(params.line[0], "(b)ack");
	strcpy(params.lineSelectionCmds[0][0], "b");
	strcpy(params.lineSelectionCmds[0][1], "back");
	params.link[0] = 0;
	strcpy(params.line[1], "mass");
	strcpy(params.lineSelectionCmds[1][0], "mass");
	params.link[1] = -101;
	strcpy(params.line[2], "position");
	strcpy(params.lineSelectionCmds[2][0], "position");
	params.link[2] = -102;
	strcpy(params.line[3], "velocity");
	strcpy(params.lineSelectionCmds[3][0], "velocity");
	params.link[3] = -103;
	MenuPage menuPage[2] = {main, params};
	MenuPage* currentMenuPage = &menuPage[0];
	displayMenuPage(&menuSurface, currentMenuPage);	

	//loop checking for user input
	while (programIsRunning) {
		//add title
		pasteToScreen(&titleSurface, &screen);
		//update generator surface
		clearSurface(&generatorSurface);
		displayMasses(masses, focusedMass, viewPositionOffset, &generatorSurface);
		pasteToScreen(&generatorSurface, &screen);
		//update menu surface
		pasteToScreen(&menuSurface, &screen);
		//refresh screen
		refresh(&screen);
		//get and handle input
		moveCursor(10, -8);
		scanf("%31s", input);
		for (int l=0;l<menuFrame.height-2;l++) {
			if (strcmp(currentMenuPage->lineSelectionCmds[l][0], input)==0 || strcmp(currentMenuPage->lineSelectionCmds[l][1], input)==0) {
				if (currentMenuPage->link[l] >= 0) {
					currentMenuPage = &menuPage[currentMenuPage->link[l]];
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -100 && focusedMass->next != NULL) {	
					currentMenuPage = &menuPage[1];
					sprintf(params.line[1], "mass: %.4lf", focusedMass->mass->mass);
					sprintf(params.line[2], "position: %.2lf, %.2lf", focusedMass->mass->position.x, focusedMass->mass->position.y);
					sprintf(params.line[3], "velocity: %.2lf, %.2lf", focusedMass->mass->velocity.x, focusedMass->mass->velocity.y);
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -101) {	
					moveCursor(0, -1);
					printf(" mass:           ");
					moveCursor(-10, 0);
					float mass = 1;
					getRangedFloatInput(0.01, 1000, &mass);
					focusedMass->mass->mass = mass;
					sprintf(params.line[1], "mass: %.4lf", focusedMass->mass->mass);
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -102) {	
					moveCursor(0, -1);
					printf(" position x,y:           ");
					moveCursor(-10, 0);
					Vector position = zeroVector;
					getVectorInput(&position);
					focusedMass->mass->position = position;
					sprintf(params.line[2], "position: %.2lf, %.2lf", focusedMass->mass->position.x, focusedMass->mass->position.y);
					viewPositionOffset = focusedMass->mass->position;
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -103) {	
					moveCursor(0, -1);
					printf(" speed:           ");
					moveCursor(-10, 0);
					float speed = 0;
					getRangedFloatInput(-10, 10, &speed);
					moveCursor(0, -1);
					printf(" degrees:           ");
					moveCursor(-10, 0);
					float angle = 0;
					getRangedFloatInput(-360, 360, &angle);
					focusedMass->mass->velocity.x = speed*cos((double)(angle/57.296));
					focusedMass->mass->velocity.y = speed*sin((double)(angle/57.296));
					sprintf(params.line[3], "velocity: %.2lf, %.2lf", focusedMass->mass->velocity.x, focusedMass->mass->velocity.y);
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -2) {
					programIsRunning = 0;
				} else if (currentMenuPage->link[l] == -3) {	
					moveCursor(0, -1);
					printf(" mass:           ");
					moveCursor(-10, 0);
					float mass = 1;
					getRangedFloatInput(0.01, 1000, &mass);
					moveCursor(0, -1);
					printf(" speed:           ");
					moveCursor(-10, 0);
					float speed = 0;
					getRangedFloatInput(-10, 10, &speed);
					moveCursor(0, -1);
					printf(" degrees:           ");
					moveCursor(-10, 0);
					float angle = 0;
					getRangedFloatInput(-360, 360, &angle);
					moveCursor(0, -1);
					printf(" position x,y:           ");
					moveCursor(-10, 0);
					Vector position = zeroVector;
					getVectorInput(&position);
					moveCursor(0,-1);
					printf("\n add mass %.4lf                         \n at %.2lf,%.2lf                \n with velocity %.2f [%.2f deg] ? (y/n) ", mass, position.x, position.y, speed, angle);
					char c = getchar();
					while (c=='\n') {
						c = getchar();
					}
					moveCursor(0, -3);
					if (c == 'y') {
						Vector velocity;
						velocity.x = speed*cos((double)(angle/57.296));
						velocity.y = speed*sin((double)(angle/57.296));
						addMass(masses, mass, position, velocity);
					}
				} else if (currentMenuPage->link[l] == -4) {
					if (focusedMass->next != NULL) {
						focusedMass->deleted = 1;
					}
				}
				if (currentMenuPage->link[l] == -5 || currentMenuPage->link[l] == -4) {
					do {
						if (focusedMass->next == NULL) {
							focusedMass = masses;
						} else {
							focusedMass = focusedMass->next;
						}
						if (focusedMass->next != NULL) {
							viewPositionOffset = focusedMass->mass->position;
						}
					} while (focusedMass->deleted == 1);
				}
			}
		}
		moveCursor(0, 7);
	}
	//save to file
	saveToFile(masses);
	//wipe screen and set cursor at top of screen
	clearScreen(&screen);
	refresh(&screen);
	moveCursor(0, -screenHeight);
	//free up memory from masses, menu pages, and surfaces
	while (masses != NULL) {
		free(masses->mass);
		MassList* temp = masses;
		masses = masses->next;
		free(temp);
	}
	deleteMenuPage(main);
	deinitializeSurface(&titleSurface);
	deinitializeSurface(&generatorSurface);
	deinitializeSurface(&menuSurface);
	deinitializeScreen(&screen);
}

int main(int argc, char* argv[]) {
	//copy filename from arguments
	strcpy(FILENAME, argv[1]);
	//either width and height are provided in arguments or defaults are used
	if (argc == 4) {
		int w;
		sscanf(argv[2], "%d", &w);
		int h;
		sscanf(argv[3], "%d", &h);
		startGenerator(w, h);
	} else {
		startGenerator(60, 42);
	}
	return 0;
}
