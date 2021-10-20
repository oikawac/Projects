#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <string.h>
#include "cmdscreen.h"
#include "planephysics.h"

//global variable exit code 
//-0 indicates program should exit 
//-1 indicates generator programs should launch after exit
int EXITCODE=0;
//global variable filename
char FILENAME[30];


//MENU FUNCTIONS & STRUCTS
//struct defining menu items
typedef struct MenuPage MenuPage;
struct MenuPage {
	int height;
	char*** lineSelectionCmds;
	char** line;
	int* link;
};
//create empty menu page
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
//free up memory allocated to menu page
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
//paste menu page to screen
void displayMenuPage(Surface *menuSurface, MenuPage *page) {
	Frame bar = {.x=0, .y=0, .width=menuSurface->frame.width, .height=1};
	clearSurface(menuSurface);
	fillFrameInSurface(bar, '=', menuSurface);
	printOnSurface(1, 2, "command:", menuSurface);
	for (int i=0;i<menuSurface->frame.height-2;i++) {
		printOnSurface(10, i+3, page->line[i], menuSurface);
	}
}


//FILE I/O
//save array of masses to file
int saveToFile(int numMasses, Mass *masses) {
	FILE *f = fopen(FILENAME, "wt");
	if (f == NULL) return 1;
	for (int i=0;i<numMasses;i++) {
		fprintf(f, "%lf %lf %lf %lf %lf\n", masses[i].mass, masses[i].position.x, masses[i].position.y, masses[i].velocity.x, masses[i].velocity.y);
	}
	fclose(f);
	return 0;
}
//open file and return number of masses to load
int getNumberOfMassesToLoad() {
	FILE *f = fopen(FILENAME, "rt");
	if (f == NULL) return 0;
	int numLines = 0;
	while (!feof(f)) {
		if (fgetc(f) == '\n') {
			numLines += 1;
		}
	}
	fclose(f);
	return numLines;
}
//load number of masses from file and return as array
int openFromFile(int numMasses, Mass* masses) {
	FILE *f = fopen(FILENAME, "rt");
	if (f == NULL) return 1;
	for (int i=0;i<numMasses;i++) {
		double m, px, py, vx, vy;
		char line[100];
		fgets(line, 99, f);
		sscanf(line, "%lf %lf %lf %lf %lf\n", &m, &px, &py, &vx, &vy);	
		Mass mass;
		mass.mass = m;
		mass.position.x = px;
		mass.position.y = py;
		mass.velocity.x = vx;
		mass.velocity.y = vy;
		masses[i] = mass;
	}
	fclose(f);
	return 0;
}

//UPDATE PHYSICS & DISPLAY MASSES
//paste masses onto surface
void displayMasses(int numMasses, Mass *masses, Mass* focusedMass, Vector viewPositionOffset, Surface *simSurface) {
	for (int i=0;i<numMasses;i++) {
		printCharOnSurface((int)((masses[i].position.x-viewPositionOffset.x+simSurface->frame.width/4)*2), (int)(-masses[i].position.y+viewPositionOffset.y)+simSurface->frame.height/2, 'O', simSurface);
	}
	if (focusedMass != NULL) {
		printCharOnSurface((int)((focusedMass->position.x-viewPositionOffset.x+simSurface->frame.width/4)*2), (int)(-focusedMass->position.y+viewPositionOffset.y)+simSurface->frame.height/2, 'X', simSurface);
	}
}
//update forces & acceleration & velocity & position of each mass
void updateMasses(int numMasses, Mass *masses, double timeStep) {
	for (int m1=0;m1<numMasses;m1++) {
		masses[m1].fNet = zeroVector;
		for (int m2=0;m2<m1;m2++) {
			masses[m1].fNet=diffOfVectors(masses[m1].fNet, gravitationalForce(&masses[m1], &masses[m2]));
		} for (int m2=m1+1;m2<numMasses;m2++) {
			masses[m1].fNet=diffOfVectors(masses[m1].fNet, gravitationalForce(&masses[m1], &masses[m2]));
		}
		masses[m1].acceleration = scaleVector(masses[m1].fNet, 1/masses[m1].mass);
		masses[m1].velocity = sumOfVectors(masses[m1].velocity, scaleVector(masses[m1].acceleration, timeStep));
		masses[m1].position = sumOfVectors(masses[m1].position, scaleVector(masses[m1].velocity, timeStep));	
	}
}

//INPUT HELPER FUNCTIONS
//convert USecs to HOUR:MINUTE:SECOND
void updateTimeVals(int *hours, int *minutes, int *seconds, unsigned long int USecs) {
	*hours = USecs/3600000000;
	long int remainder = USecs%3600000000;
	*minutes = remainder/60000000;
	remainder = remainder%60000000;
	*seconds = remainder/1000000;
}
//get integer input from stdin
int getRangedIntegerInput(int low, int high, int* variable) {	
	int parsedVars = scanf("%d", variable);
	if (*variable < low) {
		*variable = low;
	} else if (*variable > high) {
		*variable = high;
	}
	return parsedVars;
}
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
//get HOUR:MINUTE:SECOND input from stdin
int getTimeVals(int *hours, int *minutes, int *seconds) {
	int parsedVars = scanf("%d:%d:%d", hours, minutes, seconds);
	if (*hours > 99) {
		*hours = 99;
	} else if (*hours < 0) {
		*hours = 0;
	}
	if (*minutes > 99) {
		*minutes = 99;
	} else if (*minutes < 0) {
		*minutes = 0;
	}
	if (*seconds > 99) {
		*seconds = 99;
	} else if (*seconds < 0) {
		*seconds = 0;
	}
	return parsedVars;
}


//Start Simulation
//-open file
//-variable definition for all sim params and cmdscreen structs
//while loop for duration of program
//-paste surfaces and refresh screen
//-parse user input
//while loop for duration of sim
//-display masses and update simstate surface
//-refresh screen
//while loop for duration of frame
//-calculate physical params of masses in plane
//-calculate frame duration thus far
//end while
//-pause cpu execution for remaining frame duration
//end while
//end while
//-save to file
//-free up all memory allocation
void startSimulation(int width, int height) {
	//width and height of screen
	int screenWidth = width;
	int screenHeight = height;

	//simulator parameters
	int currentHour = 0;
	int currentMinute = 0;
	int currentSecond = 0;
	int durationOfSimHour = 0;
	int durationOfSimMinute = 0;
	int durationOfSimSecond = 0;
	unsigned long int durationOfSimulationUSecs = 1000000*60;
	unsigned long int totalElapsedSimulationTimeUSecs=0;
	unsigned long int startOfSimulationTotalElapsedSimulationTimeUSecs=0;
	float simulationSpeed = 10;

	//physics variables	
	int numberOfMasses = getNumberOfMassesToLoad();
	Mass masses[numberOfMasses];
	openFromFile(numberOfMasses, masses);
	Mass* focusedMass = NULL;
	int focusedMassIndex = numberOfMasses;

	//variables for tracking computation time of simulator
	int desiredFrameRate = 60;
	unsigned long int USecsPerFrame = 1000000/desiredFrameRate;
	unsigned long int lastCalculationCycleLengthUSecs = 0;
	unsigned long int averageCalculationCycleLengthUSecs = 0;
	struct timeval startOfFrameTimeUSecs, currentTimeUSecs;

	//while loop conditions for keep running program and keep running simulator
	short unsigned int programIsRunning = 1;
	short unsigned int simulationIsRunning = 0;

	//init screen struct
	Screen screen;
	initializeScreen(screenWidth, screenHeight, &screen);
	clearScreen(&screen);

	//init each surface struct 
	Frame titleFrame = {.x=0, .y=0, .width=screenWidth, .height = 3};
	Frame simulationStateFrame = {.x=0,.y=3, .width=screenWidth, .height = 8};
	Frame simulationStateTextFrame = {.x=0, .y=1, .width = screenWidth, .height=6};
	Frame focusedMassFrame = { .x=40, .y=1, .width=30,.height=4};
	Frame simulationFrame = {.x=0, .y=11, .width=screenWidth, .height=30};
	Frame menuFrame = {.x=0, .y=41, .width=screenWidth, .height=10};
	Surface titleSurface;
	Surface simulationStateSurface;
	Surface focusedMassSurface;
	Surface simulationSurface;
	Surface menuSurface;
	initializeSurface(titleFrame, &titleSurface);
	initializeSurface(simulationStateFrame, &simulationStateSurface);
	initializeSurface(focusedMassFrame, &focusedMassSurface);
	initializeSurface(simulationFrame, &simulationSurface);
	initializeSurface(menuFrame, &menuSurface);
	clearSurface(&titleSurface);
	Frame bar = {.x=0, .y=0, .width=screenWidth, .height=1};
	fillFrameInSurface(bar, '=', &titleSurface);
	bar.y=2;
	fillFrameInSurface(bar, '=', &titleSurface);
	printOnSurface(1, 1, "CAILEAN OIKAWA - GRAVITY SIMULATOR", &titleSurface);
	printOnSurface(screenWidth-strlen(FILENAME)-1, 1, FILENAME, &titleSurface);
	clearSurface(&simulationStateSurface);
	bar.y=7;
	fillFrameInSurface(bar, '=', &simulationStateSurface);
	clearSurface(&simulationSurface);
	
	//camera offset vector
	Vector viewPositionOffset = zeroVector;

	//define menu pages
	char input[32];
	struct MenuPage main = createMenuPage(screenWidth, menuFrame.height);
	strcpy(main.line[0], "(e)xit");
	strcpy(main.lineSelectionCmds[0][0], "e");
	strcpy(main.lineSelectionCmds[0][1], "exit");
	main.link[0] = -2;
	strcpy(main.line[1], "(r)un simulation");
	strcpy(main.lineSelectionCmds[1][0], "r");
	strcpy(main.lineSelectionCmds[1][1], "run");
	main.link[1] = -3;
	strcpy(main.line[2], "(l)ist variables");
	strcpy(main.lineSelectionCmds[2][0], "l");
	strcpy(main.lineSelectionCmds[2][1], "list");
	main.link[2] = 1;
	strcpy(main.line[3], "(a)dd masses");
	strcpy(main.lineSelectionCmds[3][0], "a");
	strcpy(main.lineSelectionCmds[3][1], "add");
	main.link[3] = -4;
	strcpy(main.line[4], "(f)ocus next mass");
	strcpy(main.lineSelectionCmds[4][0], "f");
	strcpy(main.lineSelectionCmds[4][1], "focus");
	main.link[4] = -5;
	strcpy(main.line[5], "(q)uicksave as");
	strcpy(main.lineSelectionCmds[5][0], "q");
	strcpy(main.lineSelectionCmds[5][1], "quicksave");
	main.link[5] = -6;
	struct MenuPage list = createMenuPage(screenWidth, menuFrame.height);
	strcpy(list.line[0], "(b)ack to menu");
	strcpy(list.lineSelectionCmds[0][0], "b");
	strcpy(list.lineSelectionCmds[0][1],"back");
	list.link[0] = 0;
	updateTimeVals(&durationOfSimHour, &durationOfSimMinute, &durationOfSimSecond, durationOfSimulationUSecs);
	sprintf(list.line[1], "duration <%02d:%02d:%02d>", durationOfSimHour, durationOfSimMinute, durationOfSimSecond);
	strcpy(list.lineSelectionCmds[1][0], "duration");
	list.link[1] = -100;
	sprintf(list.line[2], "framerate <%02d>", desiredFrameRate);
	strcpy(list.lineSelectionCmds[2][0], "framerate");
	list.link[2] = -101;
	sprintf(list.line[3], "speed <%.2f>", simulationSpeed);
	strcpy(list.lineSelectionCmds[3][0], "speed");
	list.link[3] = -102;
	MenuPage menuPage[2] = {main, list};
	MenuPage* currentMenuPage = &menuPage[0];
	displayMenuPage(&menuSurface, currentMenuPage);	
	
	//simulation state strings
	char currentTimeString[50];
        char durationOfSimulationString[50];
	char simulationSpeedString[50];
	//focused mass strings
	char focusedMassStringMass[25];
	char focusedMassStringPos[25];
	char focusedMassStringVel[25];
	char focusedMassStringAcc[25];
	//loop checking for user input
	while (programIsRunning) {
		//add title
		pasteToScreen(&titleSurface, &screen);
		//update simulation state surface
		fillFrameInSurface(simulationStateTextFrame, ' ', &simulationStateSurface);
		sprintf(currentTimeString, "time elapsed: %02d:%02d:%02d", currentHour, currentMinute, currentSecond);	
		updateTimeVals(&durationOfSimHour, &durationOfSimMinute, &durationOfSimSecond, durationOfSimulationUSecs);
		sprintf(durationOfSimulationString, "simulate masses for: %02d:%02d:%02d", durationOfSimHour, durationOfSimMinute, durationOfSimSecond);
		sprintf(simulationSpeedString, "simulation speed: %.2f", simulationSpeed);
		printOnSurface(1, 1, currentTimeString, &simulationStateSurface);
		printOnSurface(1, 2, durationOfSimulationString, &simulationStateSurface);
		printOnSurface(1, 3, simulationSpeedString, &simulationStateSurface);
		//update focused mass surface
		fillSurface('.', &focusedMassSurface);
		if (focusedMass == NULL) {
			printOnSurface(0, 0, "no mass in focus", &focusedMassSurface);
		} else {
			sprintf(focusedMassStringMass, "mass: %.4lf", focusedMass->mass);
			sprintf(focusedMassStringPos, "pos: %.2lf, %.2lf", focusedMass->position.x, focusedMass->position.y);
			sprintf(focusedMassStringVel, "vel: %.2lf, %.2lf", focusedMass->velocity.x, focusedMass->velocity.y);
			sprintf(focusedMassStringAcc, "acc: %.2lf, %.2lf", focusedMass->acceleration.x, focusedMass->acceleration.y);
			printOnSurface(0, 0, focusedMassStringMass, &focusedMassSurface);
			printOnSurface(0, 1, focusedMassStringPos, &focusedMassSurface);
			printOnSurface(0, 2, focusedMassStringVel, &focusedMassSurface);
			printOnSurface(0, 3, focusedMassStringAcc, &focusedMassSurface);
		}
		paste(&focusedMassSurface, &simulationStateSurface);
		pasteToScreen(&simulationStateSurface, &screen);
		//update simulation surface
		clearSurface(&simulationSurface);
		displayMasses(numberOfMasses, masses, focusedMass, viewPositionOffset, &simulationSurface);
		pasteToScreen(&simulationSurface, &screen);
		//update menu surface
		pasteToScreen(&menuSurface, &screen);
		//refresh screen
		refresh(&screen);
		//get and handle input
		moveCursor(10, -7);
		scanf("%31s", input);
		for (int l=0;l<menuFrame.height-2;l++) {
			if (strcmp(currentMenuPage->lineSelectionCmds[l][0], input)==0 || strcmp(currentMenuPage->lineSelectionCmds[l][1], input)==0) {
				if (currentMenuPage->link[l] >= 0) {
					currentMenuPage = &menuPage[currentMenuPage->link[l]];
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -2) {
					programIsRunning = 0;
				} else if (currentMenuPage->link[l] == -3) {
					simulationIsRunning = 1;
				} else if (currentMenuPage->link[l] == -4) {
					programIsRunning = 0;
					EXITCODE = 1;
				} else if (currentMenuPage->link[l] == -5) {
					focusedMassIndex = (focusedMassIndex+1)%(numberOfMasses+1);
					if (focusedMassIndex == numberOfMasses) {
						focusedMass = NULL;
					} else {
						focusedMass = &masses[focusedMassIndex];
						viewPositionOffset = focusedMass->position;
					}
				} else if (currentMenuPage->link[l] == -6) {	
					moveCursor(0, -1);
					printf(" name:          ");
					moveCursor(-10, 0);
					char temp[30];
					strcpy(temp, FILENAME);
					scanf("%24s", FILENAME);
					strcat(FILENAME, ".save");
					saveToFile(numberOfMasses, masses);
					strcpy(FILENAME, temp);
				} else if (currentMenuPage->link[l] == -100) {
					moveCursor(0, -1);
					printf(" duration:          ");
					moveCursor(-10, 0);
					getTimeVals(&durationOfSimHour, &durationOfSimMinute, &durationOfSimSecond);
					durationOfSimulationUSecs = 3600000000*durationOfSimHour+60000000*durationOfSimMinute+1000000*durationOfSimSecond;
					sprintf(list.line[1], "duration <%02d:%02d:%02d>", durationOfSimHour, durationOfSimMinute, durationOfSimSecond);
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -101) {
					moveCursor(0, -1);
					printf(" framerate:           ");
					moveCursor(-10, 0);
					getRangedIntegerInput(10, 300, &desiredFrameRate);
					USecsPerFrame = 1000000/desiredFrameRate;
					sprintf(list.line[2], "framerate <%02d>", desiredFrameRate);
					displayMenuPage(&menuSurface, currentMenuPage);
				} else if (currentMenuPage->link[l] == -102) {
					moveCursor(0, -1);
					printf(" speed:           ");
					moveCursor(-10, 0);
					getRangedFloatInput(1, 99, &simulationSpeed);
					sprintf(list.line[3], "speed: %.2f", simulationSpeed);
					displayMenuPage(&menuSurface, currentMenuPage);
				}
			}
		}
		moveCursor(0, 7);
		//loop running simulation
		startOfSimulationTotalElapsedSimulationTimeUSecs = totalElapsedSimulationTimeUSecs;
		while (simulationIsRunning) {
			//start time elapsed tracking
			gettimeofday(&startOfFrameTimeUSecs, NULL);
			gettimeofday(&currentTimeUSecs, NULL);
			//simulation state surface updates
			updateTimeVals(&currentHour, &currentMinute, &currentSecond, totalElapsedSimulationTimeUSecs);
			sprintf(currentTimeString, "time elapsed: %02d:%02d:%02d", currentHour, currentMinute, currentSecond);
			fillFrameInSurface(simulationStateTextFrame, ' ', &simulationStateSurface);
			printOnSurface(1, 1, currentTimeString, &simulationStateSurface);
			//update focused mass surface
			clearSurface(&focusedMassSurface);
			if (focusedMass == NULL) {
				printOnSurface(0, 0, "no mass in focus", &focusedMassSurface);
			} else {
				sprintf(focusedMassStringMass, "mass: %.2lf", focusedMass->mass);
				sprintf(focusedMassStringPos, "pos: %.2lf, %.2lf", focusedMass->position.x, focusedMass->position.y);
				sprintf(focusedMassStringVel, "vel: %.2lf, %.2lf", focusedMass->velocity.x, focusedMass->velocity.y);
				sprintf(focusedMassStringAcc, "acc: %.2lf, %.2lf", focusedMass->acceleration.x, focusedMass->acceleration.y);
				printOnSurface(0, 0, focusedMassStringMass, &focusedMassSurface);
				printOnSurface(0, 1, focusedMassStringPos, &focusedMassSurface);
				printOnSurface(0, 2, focusedMassStringVel, &focusedMassSurface);
				printOnSurface(0, 3, focusedMassStringAcc, &focusedMassSurface);
			}
			paste(&focusedMassSurface, &simulationStateSurface);
			pasteToScreen(&simulationStateSurface, &screen);
			//draw masses
			fillSurface(' ', &simulationSurface);
			if (focusedMass != NULL) {
				viewPositionOffset = focusedMass->position;
			}
			displayMasses(numberOfMasses, masses, focusedMass, viewPositionOffset, &simulationSurface);
			pasteToScreen(&simulationSurface, &screen);
			refresh(&screen);
			//physics loop
			int calculationCycles = 0;
			lastCalculationCycleLengthUSecs = 0;
			averageCalculationCycleLengthUSecs = 0;
			while(currentTimeUSecs.tv_usec-startOfFrameTimeUSecs.tv_usec+averageCalculationCycleLengthUSecs < USecsPerFrame) {	
				calculationCycles += 1;
				//physics calculation
				updateMasses(numberOfMasses, masses, simulationSpeed*(float)averageCalculationCycleLengthUSecs/1000000.0);
				//calculate time elapsed
				lastCalculationCycleLengthUSecs = currentTimeUSecs.tv_usec;
				gettimeofday(&currentTimeUSecs, NULL);
				lastCalculationCycleLengthUSecs = currentTimeUSecs.tv_usec-lastCalculationCycleLengthUSecs;
				averageCalculationCycleLengthUSecs = ((calculationCycles-1)*averageCalculationCycleLengthUSecs+lastCalculationCycleLengthUSecs)/calculationCycles;
				if (averageCalculationCycleLengthUSecs == 0) {
					averageCalculationCycleLengthUSecs = 1;
				}
			}
			//pause to maintain framerate
			gettimeofday(&currentTimeUSecs, NULL);
			long int remainingTimeUSecs = USecsPerFrame-(currentTimeUSecs.tv_usec-startOfFrameTimeUSecs.tv_usec);
			if (remainingTimeUSecs > 0 && currentTimeUSecs.tv_usec-startOfFrameTimeUSecs.tv_usec > 0) {
				usleep(remainingTimeUSecs);
			}
			totalElapsedSimulationTimeUSecs += simulationSpeed*USecsPerFrame;
			if (totalElapsedSimulationTimeUSecs-startOfSimulationTotalElapsedSimulationTimeUSecs>=durationOfSimulationUSecs) {
				simulationIsRunning=0;
			}
		}
	}
	//save to file
	saveToFile(numberOfMasses, masses);
	//switching to generator program
	if (EXITCODE != 0) {
		//wipe screen and set cursor to top of screen
		clearScreen(&screen);
		refresh(&screen);
		moveCursor(0, -screenHeight);
	}
	//free up memory from menu pages and surfaces
	deleteMenuPage(main);
	deleteMenuPage(list);
	deinitializeSurface(&titleSurface);
	deinitializeSurface(&simulationStateSurface);
	deinitializeSurface(&simulationSurface);
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
		startSimulation(w, h);
	} else {
		startSimulation(60, 50);
	}
	return EXITCODE;
}
