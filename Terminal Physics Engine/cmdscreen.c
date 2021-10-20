#include <stdio.h>
#include <stdlib.h>
#include "cmdscreen.h"

//move cursor using ascii codes
void moveCursor(int x, int y) {
	if (x < 0) {
		printf("\033[%dD", -x);
	} else if (x > 0) {
		printf("\033[%dC", x);
	}
	if (y < 0) {
		printf("\033[%dA", -y);
	} else if (y > 0) {
		printf("\033[%dB", y);
	}
}

//free up memory used for screen
void deinitializeScreen(Screen *screen) {
	for (int r=0; r<screen->height; r++) {
		free(screen->buffer[r]);
	}	
	free(screen->buffer);
}

//free up memory used for surface
void deinitializeSurface(Surface *surface) {
	for (int r=0; r<surface->frame.height; r++) {
		free(surface->lines[r]);
	}
	free(surface->lines);
}

//allocate memory for screen
void initializeScreen(int width, int height, Screen *screen) {
	screen->width = width;
	screen->height = height;
	screen->buffer = (char **)malloc(screen->height*sizeof(char *));
	for (int r=0; r<screen->height; r++) {
		screen->buffer[r] = (char *)malloc(screen->width*sizeof(char));
		printf("\n");
	}	
}

//allocate memory for surface
void initializeSurface(Frame frame, Surface *surface) {
	surface->frame = frame;
	surface->lines = (char **)malloc(frame.height*sizeof(char *));
	for (int r=0; r<frame.height; r++) {
		surface->lines[r] = (char *)malloc(frame.width*sizeof(char));
	}
}

//paste surface onto screen
void pasteToScreen(Surface *surface, Screen *screen) {
	if (surface->frame.x >= 0 && 
	    surface->frame.x < screen->width && 
	    surface->frame.y >= 0 && 
	    surface->frame.y < screen->height) {
		int rowBoundary = surface->frame.y+surface->frame.height;
		if (rowBoundary > screen->height) {
			rowBoundary = screen->height;
		}
		for (int r=surface->frame.y; r<rowBoundary; r++) {
			int columnBoundary = surface->frame.x+surface->frame.width;
			if (columnBoundary > screen->width) {
				columnBoundary = screen->width;
			}
			for (int c=surface->frame.x; c<columnBoundary; c++) {

				screen->buffer[r][c]=surface->lines[r-surface->frame.y][c-surface->frame.x];
			}
		}
	}
}

//paste surface onto surfaceOnto
void paste(Surface *surface, Surface *surfaceOnto) {	
	if (surface->frame.x >= 0 && 
	    surface->frame.x < surfaceOnto->frame.width && 
	    surface->frame.y >= 0 && 
	    surface->frame.y < surfaceOnto->frame.height) {
		int rowBoundary = surface->frame.y+surface->frame.height;
		if (rowBoundary > surfaceOnto->frame.height) {
			rowBoundary = surfaceOnto->frame.height;
		}
		for (int r=surface->frame.y; r<rowBoundary; r++) {
			int columnBoundary = surface->frame.x+surface->frame.width;
			if (columnBoundary > surfaceOnto->frame.width) {
				columnBoundary = surfaceOnto->frame.width;
			}
			for (int c=surface->frame.x; c<columnBoundary; c++) {

				surfaceOnto->lines[r][c]=surface->lines[r-surface->frame.y][c-surface->frame.x];
			}
		}
	}
}

//print string onto surface
void printOnSurface(unsigned int x, unsigned int y, char *string, Surface *surface) {
	if (x < surface->frame.width && y < surface->frame.height) {
		int r = y;
		for (int c=x; string[c-x] != 0 && c<surface->frame.width; c++) {
			surface->lines[r][c] = string[c-x];
		}
	}
}

//print char onto surface
void printCharOnSurface(unsigned int x, unsigned int y, char c, Surface *surface) {
	if (x < surface->frame.width && y < surface->frame.height) {
		surface->lines[y][x] = c;
	}
}

//fill surface with char
void fillSurface(char fill, Surface *surface) {
	for (int r=0; r<surface->frame.height; r++) {	
		for (int c=0; c<surface->frame.width; c++) {
			surface->lines[r][c] = fill;
		}
	}	
}

//fill surface with whitespace
void clearSurface(Surface *surface) {
	fillSurface(32, surface);
}

//fill a frame in a surface with a char
void fillFrameInSurface(Frame frame, char fill, Surface *surface) {
	if (frame.x < 0) frame.x = 0;
	if (frame.y < 0) frame.y = 0;
	int rowBoundary, columnBoundary;
	if (frame.x < surface->frame.width && frame.y < surface->frame.height) {
		rowBoundary = frame.y+frame.height;
		if (rowBoundary > surface->frame.height) {
			rowBoundary = surface->frame.height;
		}
		for (int r=frame.y; r<rowBoundary; r++) {	
			columnBoundary = frame.x+frame.width;
			if (columnBoundary > surface->frame.width) {
				columnBoundary = surface->frame.width;
			}
			for (int c=frame.x; c<columnBoundary; c++) {
				surface->lines[r][c] = fill;
			}
		}	
	}
}

//fill screen with char
void fillScreen(char fill, Screen *screen) {
	for (int r=0; r<screen->height; r++) {
		for (int c=0; c<screen->width; c++) {
			screen->buffer[r][c] = fill;
		}
	}
}

//fill screen with whitespace
void clearScreen(Screen *screen) {
	fillScreen(32, screen);
}

//print screen to stdout and bring cursor back to top
void refresh(Screen *screen) {
	moveCursor(0, -screen->height);
	for (int r=0;r<screen->height;r++) {
		for (int c=0;c<screen->width;c++) {
			putchar(screen->buffer[r][c]);
		}
		putchar(10);
	}
}
