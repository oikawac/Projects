#!/bin/bash

echo "existing save files: "
echo 
ls | grep '.save$' | sed 's/.save//'
echo 
input=""
echo -n "filename: "
read input
echo 

tput civis
if [ $input != "" ]
then
	FILENAME=$input
	FILENUM=""
	FILEEXT=".save"
else
	FILENAME="UNTITLED"
	FILENUM=0
	FILEEXT=".save"
	while [ -f $FILENAME$FILENUM$FILEEXT ]
	do
		FILENUM=$(( $FILENUM + 1 ))
	done
fi

EXITCODE=1
while [ $EXITCODE != 0 ]
do
	./simulator $FILENAME$FILENUM$FILEEXT 80 50 2> debugsimulator.txt
	EXITCODE=$?
	if [ $EXITCODE == 1 ]
	then 
		./generator $FILENAME$FILENUM$FILEEXT 80 42 2> debuggenerator.txt
	fi
done

tput cnorm
