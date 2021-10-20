#ifndef cmdscreen_h
#define cmdscreen_h

typedef struct Screen Screen;

//structure for implementing screen in terminal
struct Screen {
	int width;
	int height;
	char **buffer;
};

typedef struct Frame Frame;

//structure to define location and size of a rectangle
struct Frame {
	int x;
	int y;
	int width;
	int height;
};

typedef struct Surface Surface;

//char array image structure
struct Surface {
	Frame frame;
	char **lines;
};



//move the terminal cursor relative to current position
void moveCursor(int x, int y);

//deallocate memory used for the screen buffer
void deinitializeScreen(Screen *screen);

//deallocate memory used for the surface
void deinitializeSurface(Surface *surface);

//allocate memory for the screen buffer
void initializeScreen(int width, int height, Screen *screen);

//allocate memory for the surface
void initializeSurface(Frame frame, Surface *surface);

//paste surface directly onto screen buffer overwriting anything within the frame of the surface
void pasteToScreen(Surface *surface, Screen *screen);

//paste surface directly onto surfaceOnto overwriting anything within the frame of surface
void paste(Surface *surface, Surface *surfaceOnto);

//print string onto surface
void printOnSurface(unsigned int x, unsigned int y, char *string, Surface *surface);

//print char onto surface
void printCharOnSurface(unsigned int x, unsigned int y, char c, Surface *surface);

//fill surface with character
void fillSurface(char fill, Surface *surface);

//fill surface with whitespace
void clearSurface(Surface *surface);

//fill surface within frame with character
void fillFrameInSurface(Frame frame, char fill, Surface *surface);

//fill screen buffer with character
void fillScreen(char fill, Screen *screen);

//fill screen buffer with whitespace
void clearScreen(Screen *screen);

//print screen to STDOUT
void refresh(Screen *screen);

#endif


