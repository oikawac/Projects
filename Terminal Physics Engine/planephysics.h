#ifndef planephysics_h
#define planephysics_h

//gravitational constant
extern double G;

typedef struct Vector Vector;

//two dimensional vector
struct Vector {
	double x;
	double y;
};

//zero vector
extern Vector zeroVector;

typedef struct Mass Mass;

//defines attributes of a mass relevant to newtonian physics
struct Mass {
	double mass;
	Vector position;
	Vector velocity;
	Vector acceleration;
	Vector fNet;
};

//add two vectors
Vector sumOfVectors(Vector v1, Vector v2);

//difference of two vectors
Vector diffOfVectors(Vector v1, Vector v2);

//angle of vector in rads
double angleOfVector(Vector v);

//scalar multiplication of vector
Vector scaleVector(Vector v, double k);

//return distance between two vectors
double distanceBetweenVectors(Vector v1, Vector v2);

//return magnitude of a vector
double magnitudeOfVector(Vector v);

//calculate the net force on the m1 based on gravitational attraction to m2
Vector gravitationalForce(Mass *m1, Mass *m2);

#endif
