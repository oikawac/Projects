#include <math.h>
#include <stdio.h>
#include "planephysics.h"

double G = 1;
Vector zeroVector = {.0, .0};

//return sum of two vectors
Vector sumOfVectors(Vector v1, Vector v2) {
	Vector sum = {.x=v1.x+v2.x, .y=v1.y+v2.y};
	return sum; 
}
//return difference between two vectors
Vector diffOfVectors(Vector v1, Vector v2) {
	Vector diff = {.x=v1.x-v2.x, .y=v1.y-v2.y};
	return diff; 
}
//return angle of vector in radians
double angleOfVector(Vector v) {
	return atan2(v.y, v.x);
}
//return scalar multiplication of vector
Vector scaleVector(Vector v, double k) {
	v.x*=k;
	v.y*=k;
	return v;
}

double distanceBetweenVectors(Vector v1, Vector v2) {
	return sqrt(pow((v1.x-v2.x), 2)+pow((v1.y-v2.y), 2));
}

double magnitudeOfVector(Vector v) {
	return distanceBetweenVectors(zeroVector, v);
}

Vector gravitationalForce(Mass *m1, Mass *m2) {
	double magnitude = (G*m1->mass*m2->mass)/pow(distanceBetweenVectors(m1->position, m2->position), 2);
	double angle = angleOfVector(diffOfVectors(m1->position, m2->position));
	Vector force = {.x=magnitude*cos(angle), .y=magnitude*sin(angle)};
	return force;
}
