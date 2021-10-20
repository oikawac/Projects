#!/bin/bash

gcc -o generator generator.c cmdscreen.c planephysics.c -lm 2> errorlog.txt
set `wc -l errorlog.txt`
if [ $1 != 0 ]
then
	head -n4 errorlog.txt
fi
