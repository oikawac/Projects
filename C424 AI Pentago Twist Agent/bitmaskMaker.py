import copy

position = [
[0,0,0,0,0,0],
[0,0,0,0,0,0],
[0,0,0,0,0,0],
[0,0,0,0,0,0],
[0,0,0,0,0,0],
[0,0,0,0,0,0]]

def allWinningPositions():
	positions = []
	#horizontals
	for row in range(6):
		win1 = copy.deepcopy(position)
		win1[row] = [1,1,1,1,1,0]
		win2 = copy.deepcopy(position)
		win2[row] = [0,1,1,1,1,1]
		positions.append(win1)
		positions.append(win2)
	#verticals
	for column in range(6):	
		win1 = copy.deepcopy(position)
		for row in range(0,5):
			win1[row][column] = 1
		win2 = copy.deepcopy(position)
		for row in range(1,6):
			win2[row][column] = 1
		positions.append(win1)
		positions.append(win2)
	#diagonals
	for start in [[1,0],[0,0],[0,1],[1,1]]:
		win = copy.deepcopy(position)
		for i in range(5):
			win[start[0]+i][start[1]+i] = 1
		positions.append(win)	
	for start in [[4,0],[5,0],[4,1],[5,1]]:
		win = copy.deepcopy(position)
		for i in range(5):
			win[start[0]-i][start[1]+i] = 1
		positions.append(win)
	return positions



def playerPositionBitmask(position):
	absoluteBitPosition = 0;
	bitmask = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
	row_offset = [0,0,3,3]
	column_offset = [0,3,0,3]
	for q in range(4):
		for r in range(3):
			for c in range(3):
				value = position[r+row_offset[q]][c+column_offset[q]]
				bitmask[absoluteBitPosition] = value
				absoluteBitPosition += 1;
	return ''.join(str(b) for b in bitmask)

for win in allWinningPositions():
	#for row in win:
		#print(row)
	#print(playerPositionBitmask(win)[0:9]+" "+playerPositionBitmask(win)[9:18]+" "+playerPositionBitmask(win)[18:27]+" "+playerPositionBitmask(win)[27:36])
	print("0b"+playerPositionBitmask(win)+"L,")	
			
