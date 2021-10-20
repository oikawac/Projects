package student_player;

import pentago_twist.PentagoBoardState;

import java.util.Scanner;

public class MyTools {

    /**Function to pack
     * 26 bit parentIndex (0-~67 million)
     * with 26 bit childrenIndex
     * with 12 bit numberOfChildren
     * into single 64 bit long
     *
     * @param parentIndex
     * @param childrenIndex
     * @param numberOfChildren
     * @return
     */
    public static long packIndexData(long parentIndex, long childrenIndex, long numberOfChildren) {
        parentIndex = parentIndex << 38;
        childrenIndex = childrenIndex << 12;
        return numberOfChildren | childrenIndex | parentIndex;
    }
    /**unpacks data from packed 64 bit index data
     *
     * @param indexData
     * @return
     * ->long[]{parentIndex, chilrenIndex, numberOfChildren}
     */
    public static long[] unpackIndexData(long indexData) {
        long numberOfChildren = indexData & 0b0111111111111;
        long childrenIndex = (indexData >>> 12) & 0b011111111111111111111111111;
        long parentIndex = (indexData >>> 38) & 0b011111111111111111111111111;
        return new long[]{parentIndex, childrenIndex, numberOfChildren};
    }

    /**Function to pack node parameters into 64 bit long
     * visited 26 bits
     * wins 27 bits
     * ( we consider number of wins to equal wins/2 since we need to account for draw=0.5 )
     *
     * @param visited
     * @param wins
     * @param termWin
     * @param termLoss
     * @param termDraw
     * @param implWin
     * @param implLoss
     * @param resolved
     * @return
     */
    public static long packNodeData(long visited, long wins, boolean termWin, boolean termLoss, boolean termDraw, boolean implWin, boolean implLoss, boolean resolved) {
        visited = visited << 38;
        wins = wins << 11;
        long termWinLong = termWin ? 0b0100000 : 0b0;
        long termLossLong = termLoss ? 0b010000 : 0b0;
        long termDrawLong = termDraw ? 0b01000 : 0b0;
        long implWinLong = implWin ? 0b0100 : 0b0;
        long implLossLong = implLoss ? 0b010 : 0b0;
        long resolvedLong = resolved ? 0b01 : 0b0;
        return visited | wins | termWinLong | termLossLong | termDrawLong | implWinLong | implLossLong | resolvedLong;
    }
    public static long packNodeData(long[] nodeData) {
        nodeData[0] = nodeData[0] << 38;
        nodeData[1] = nodeData[1] << 11;
        nodeData[2] = nodeData[2] << 5;
        nodeData[3] = nodeData[3]  << 4;
        nodeData[4] = nodeData[4]  << 3;
        nodeData[5] = nodeData[5]  << 2;
        nodeData[6] = nodeData[6] << 1;
        return nodeData[0] | nodeData[1] | nodeData[2] | nodeData[3] | nodeData[4] | nodeData[5] | nodeData[6] | nodeData[7];
    }

    /**unpacks data from packed 64 bit node data
     *
     * @param nodeData
     * @return
     * ->long[]{visited, wins, termWin, termLoss, termDraw, implWin, implLoss, resolved}
     * boolean values represented by 0b0 and 0b01
     */
    public static long[] unpackNodeData(long nodeData) {
        long visited = (nodeData >>> 38) & 0b011111111111111111111111111;
        long wins = (nodeData >>> 11) & 0b0111111111111111111111111111;

        long termWin = (nodeData & 0b0100000) == 0b0 ? 0 : 1;
        long termLoss = (nodeData & 0b010000) == 0b0 ? 0 : 1;
        long termDraw = (nodeData & 0b01000) == 0b0 ? 0 : 1;
        long implWin = (nodeData & 0b0100) == 0b0 ? 0 : 1;
        long implLoss = (nodeData & 0b010) == 0b0 ? 0 : 1;
        long resolved = (nodeData & 0b01) == 0b0 ? 0 : 1;
        return new long[]{visited, wins, termWin, termLoss, termDraw, implWin, implLoss, resolved};
    }

    /**Function to allocate array of longs which will store nodes in an upper confidence tree according to the following construction
     *
     * each node is 4 sequential 64bit longs
     * 64 bits - indexData
     *  hi->lo bits : 24 bits index of parent, 24 bits index of first child, 16 bits number of children
     * 64 bits - nodeData
     *  hi->lo bits : 24 bits number of visits to node, 24 bits number of wins, (10 bits blank),
     *                6 boolean bits {terminal win, terminal loss, terminal draw, implied win, implied loss, resolved}
     * 64 bits - player to move position bitBoard
     * 64 bits - player last moved position bitBoard
     *
     * root of tree is stored from index 0 to index 3
     *
     * a children index of zero is considered to mean the node has no children yet
     * if children need to be added they must all be added at once and are appended
     * in order to the end of the array with the index of the first child stored into
     * the 64 bit index data of the parent node
     *
     * @param maxNumberOfNodes
     * @param startingPosition
     * @return
     */
    private static int nextAvailableIndex;
    private static boolean parentNodeWasUpdatedByChild;
    private static final int maxNumberOfNodes = 7500000;
    //theoretically we can push this to 11000000 nodes before OutOfMemory error,
    //but the JVM runs like a fat pig if you don't leave it a good amount of unused heap space,
    //not sure why, probably pressures stack or forces more frequent garbage collection
    private static long[] upperConfidenceTree = new long[maxNumberOfNodes*4];
    private static void initUpperConfidenceTree(long[] startingPosition) {
        long rootIndexData = packIndexData(0, 4, 0);
        long rootNodeData = packNodeData(0, 0, false, false, false, false, false, false);
        upperConfidenceTree[0] = rootIndexData;
        upperConfidenceTree[1] = rootNodeData;
        upperConfidenceTree[2] = startingPosition[0];
        upperConfidenceTree[3] = startingPosition[1];
        nextAvailableIndex = 4;
    }

    public static double evaluationCoeff = 1.414;
    private static double evaluateUCTNode(long[] childNodeData, long[] parentNodeData) {
        if (childNodeData[7] == 0b01) {//check if flagged as resolved (is terminal state or implied terminal state)
            return Double.MIN_VALUE;
        }
        return (childNodeData[0] == 0) ? Double.MAX_VALUE : (childNodeData[0]-(childNodeData[1]/2.0))/((double)childNodeData[0])+evaluationCoeff*squareRootLog((int)parentNodeData[0])/squareRoot((int)childNodeData[0]);
    }

    /**Function to visit UCT nodes
     *
     * @param index
     * @return
     * 0 - player 0 won
     * 1 - player 1 won
     * 2 - game was drawn
     * 3 - ignore result
     * 4 - stop growing tree (out of memory or root node completely resolved)
     */
    private static int visitUCTNode(int index) {
        long[] indexData = unpackIndexData(upperConfidenceTree[index]);
        long[] nodeData = unpackNodeData(upperConfidenceTree[index+1]);
        boolean terminalWin = nodeData[2] != 0b0;
        boolean terminalLoss = nodeData[3] != 0b0;
        boolean terminalDraw = nodeData[4] != 0b0;
        boolean impliedWin = nodeData[5] != 0b0;
        boolean impliedLoss = nodeData[6] != 0b0;
        boolean resolved = nodeData[7] != 0b0;
        long[] position = new long[]{upperConfidenceTree[index+2], upperConfidenceTree[index+3]};
        int[] metaData = getMetaData(position[0]);
        if (index == 0 && resolved) {
            return 4;
        }
        nodeData[0]+=1;//increment node visits
        if (nodeData[0] == 1) {//first visit (no need to generate children yet)
            long[] copyOfPosition = new long[2];
            copyOfPosition[0] = position[0];
            copyOfPosition[1] = position[1];
            int winner = gameRollOutFromPosition(copyOfPosition);
            nodeData[1] += (winner == metaData[1]) ? 2 : 0;//check if player id matches winner
            nodeData[1] += (winner == 2) ? 1 : 0;//check if draw
            long updatedNodeData = packNodeData(nodeData[0], nodeData[1], terminalWin, terminalLoss, terminalDraw, impliedWin, impliedLoss, resolved);
            upperConfidenceTree[index+1] = updatedNodeData;
            return winner;
        } else {
            if (nodeData[0] == 2) {//second visit (we need to generate children)
                indexData[1] = nextAvailableIndex;//record first child index
                long[][] positions = fastAllValidBoardTransitions(position);
                indexData[2] = positions.length;//record number of children
                if (maxNumberOfNodes *4-nextAvailableIndex<indexData[2]*4) {
                    return 4;//stop growing tree when we run out of memory
                }
                long childIndexData;
                long childNodeData;
                boolean childTerminalWin;
                boolean childImpliedWin;
                boolean childTerminalLoss;
                boolean childImpliedLoss;
                boolean childTerminalDraw;
                boolean childResolved;
                boolean win0;
                boolean win1;
                for (int i = 0; i < positions.length; i++) {
                    //check for terminal positions
                    childTerminalWin = false;
                    childImpliedWin = false;
                    childTerminalLoss = false;
                    childImpliedLoss = false;
                    childTerminalDraw = false;
                    childResolved = false;
                    win0 = positionIsWon(positions[i][0]);
                    win1 = positionIsWon(positions[i][1]);
                    if (((~(positions[i][0] | positions[i][1])) & removeMetaDataBitmask) == 0b0L) {
                        childTerminalDraw = true;
                        childResolved = true;
                    } else if (win0 && win1) {
                        childTerminalDraw = true;
                        childResolved = true;
                    } else if (win0) {
                        childTerminalWin = true;
                        childImpliedWin = true;
                        childResolved = true;
                    } else if (win1) {
                        childTerminalLoss = true;
                        childImpliedLoss = true;
                        childResolved = true;
                        impliedWin = true;//parent is implied won if child is terminally lost
                        resolved = true;
                    }
                    childIndexData = packIndexData(index, 0, 0);
                    childNodeData = packNodeData(0, 0, childTerminalWin, childTerminalLoss, childTerminalDraw, childImpliedWin, childImpliedLoss, childResolved);
                    upperConfidenceTree[nextAvailableIndex+i*4] = childIndexData;
                    upperConfidenceTree[nextAvailableIndex+i*4+1] = childNodeData;
                    upperConfidenceTree[nextAvailableIndex+i*4+2] = positions[i][0];
                    upperConfidenceTree[nextAvailableIndex+i*4+3] = positions[i][1];
                }
                nextAvailableIndex += positions.length*4;
                long updatedIndexData = packIndexData(indexData[0], indexData[1], indexData[2]);//update node being visited with children index and number of children
                upperConfidenceTree[index] = updatedIndexData;
            }
            //now search through children for next node to visit
            int childrenIndex = (int) indexData[1];
            int numberOfImpliedWon = 0;
            long numberOfChildren = indexData[2];
            int bestChildAccordingToTreePolicyIndex = childrenIndex;
            long[] childNodeData = unpackNodeData(upperConfidenceTree[childrenIndex+1]);
            if (childNodeData[5] == 0b01) numberOfImpliedWon++;
            double bestChildScoreAccordingToTreePolicy = evaluateUCTNode(childNodeData, nodeData);
            double childScoreAccordingToTreePolicy;

            for (int i = 4; i<numberOfChildren*4; i+=4) {
                childNodeData = unpackNodeData(upperConfidenceTree[childrenIndex+i+1]);
                if (childNodeData[5] == 0b01) numberOfImpliedWon++;
                childScoreAccordingToTreePolicy = evaluateUCTNode(childNodeData, nodeData);
                if (childScoreAccordingToTreePolicy > bestChildScoreAccordingToTreePolicy) {
                    bestChildAccordingToTreePolicyIndex = childrenIndex+i;
                    bestChildScoreAccordingToTreePolicy = childScoreAccordingToTreePolicy;
                }
            }
            if (bestChildScoreAccordingToTreePolicy == Double.MIN_VALUE) {//all children are resolved
                resolved = true;
                if (numberOfImpliedWon == numberOfChildren) { //if all descendents are implied won this child is implied lost
                    impliedLoss = true;
                    int parentIndex = (int) indexData[0];
                    long[] parentNodeData = unpackNodeData(upperConfidenceTree[parentIndex+1]);
                    parentNodeData[5] = 0b01;
                    parentNodeData[7] = 0b01;
                    upperConfidenceTree[parentIndex+1] = packNodeData(parentNodeData);
                    parentNodeWasUpdatedByChild = true;
                }
                long updatedNodeData = packNodeData(nodeData[0], nodeData[1], terminalWin, terminalLoss, terminalDraw, impliedWin, impliedLoss, resolved);
                upperConfidenceTree[index+1] = updatedNodeData;
                return 3;//resolved nodes abort
            }
            int winner = visitUCTNode(bestChildAccordingToTreePolicyIndex);
            if (parentNodeWasUpdatedByChild) {
                parentNodeWasUpdatedByChild = false;
                long[] updatedNodeData = unpackNodeData(upperConfidenceTree[index+1]);
                terminalWin = updatedNodeData[2] != 0b0;
                terminalLoss = updatedNodeData[3] != 0b0;
                terminalDraw = updatedNodeData[4] != 0b0;
                impliedWin = updatedNodeData[5] != 0b0;
                impliedLoss = updatedNodeData[6] != 0b0;
                resolved = updatedNodeData[7] != 0b0;
            }
            nodeData[1] += (winner == metaData[1]) ? 2 : 0;//add to wins if winner matches playerId
            nodeData[1] += (winner == 2) ? 1 : 0;//add 0.5 to wins if draw
            long updatedNodeData = packNodeData(nodeData[0], nodeData[1], terminalWin, terminalLoss, terminalDraw, impliedWin, impliedLoss, resolved);
            upperConfidenceTree[index+1] = updatedNodeData;
            return winner;
        }
    }

    public static long[] positionOfBestMove = new long[2];
    public static int[] bestMoveFromUCTTree() {
        //get index data of root
        long[] rootIndexData = unpackIndexData(upperConfidenceTree[0]);
        //get data from first child
        long[] childNodeData = unpackNodeData(upperConfidenceTree[5]);
        positionOfBestMove[0] = upperConfidenceTree[6];
        positionOfBestMove[1] = upperConfidenceTree[7];
        int[] childMetaData = getMetaData(upperConfidenceTree[7]);
        boolean childImpliedWin = childNodeData[5] != 0b0;
        boolean childImpliedLoss = childNodeData[6] != 0b0;
        boolean childTerminalDraw = childNodeData[4] != 0b0;
        double bestWinRate = (childNodeData[0] == 0) ? 0: (childNodeData[0]-(childNodeData[1]/2.0))/(double)childNodeData[0];
        long mostPredictedLosingMoveVisits = 0;
        int[] bestPredictedLosingMove = new int[]{childMetaData[2], childMetaData[3], childMetaData[4], childMetaData[5]};
        if (childImpliedWin) {
            bestWinRate = Double.MIN_VALUE;
        } else if (childImpliedLoss) {
            bestWinRate = Double.MAX_VALUE;
            mostPredictedLosingMoveVisits = childNodeData[0];
        } else if (childTerminalDraw) {
            bestWinRate = 0.5;
        }
        int[] bestMove = new int[]{childMetaData[2], childMetaData[3], childMetaData[4], childMetaData[5]};
        for (int i = 8; i<rootIndexData[2]*4; i+=4) {//iterate over children
            childNodeData = unpackNodeData(upperConfidenceTree[i+1]);
            childMetaData = getMetaData(upperConfidenceTree[i+3]);
            childImpliedWin = childNodeData[5] != 0b0;
            childImpliedLoss = childNodeData[6] != 0b0;
            childTerminalDraw = childNodeData[4] != 0b0;
            double winRate = (childNodeData[0] == 0) ? 0: (childNodeData[0]-(childNodeData[1]/2.0))/(double)childNodeData[0];
            if (childImpliedWin) {
                winRate = Double.MIN_VALUE;
                //we need to keep track of best losing move otherwise algorithm just plays top left corner
                if (childNodeData[0] > mostPredictedLosingMoveVisits) {
                    mostPredictedLosingMoveVisits = childNodeData[0];
                    bestPredictedLosingMove = new int[]{childMetaData[2], childMetaData[3], childMetaData[4], childMetaData[5]};
                }
            } else if (childImpliedLoss) {
                winRate = Double.MAX_VALUE;
            } else if (childTerminalDraw) {
                winRate = 0.5;
            }
            if (winRate > bestWinRate) {
                bestMove = new int[]{childMetaData[2], childMetaData[3], childMetaData[4], childMetaData[5]};
                positionOfBestMove[0] = upperConfidenceTree[i+2];
                positionOfBestMove[1] = upperConfidenceTree[i+3];
                bestWinRate = winRate;
            }
        }
        if (bestWinRate == Double.MIN_VALUE) {
            return bestPredictedLosingMove;
        }
        return bestMove;
    }

    /**Debug function to traverse tree nodes and examine children
     *
     * @param index
     */
    public static void exploreTree(int index) {
        long[] indexData = unpackIndexData(upperConfidenceTree[index]);
        long[] nodeData = unpackNodeData(upperConfidenceTree[index+1]);
        boolean terminalWin = nodeData[2] != 0b0;
        boolean terminalLoss = nodeData[3] != 0b0;
        boolean terminalDraw = nodeData[4] != 0b0;
        boolean impliedWin = nodeData[5] != 0b0;
        boolean impliedLoss = nodeData[6] != 0b0;
        boolean resolved = nodeData[7] != 0b0;
        long[] position = new long[]{upperConfidenceTree[index+2], upperConfidenceTree[index+3]};
        int[] metaData = getMetaData(position[0]);
        int parentIndex = (int) indexData[0];
        long[] parentNodeData = unpackNodeData(upperConfidenceTree[parentIndex+1]);
        boolean parentTerminalWin = parentNodeData[2] != 0b0;
        boolean parentTerminalLoss = parentNodeData[3] != 0b0;
        boolean parentTerminalDraw = parentNodeData[4] != 0b0;
        boolean parentImpliedWin = parentNodeData[5] != 0b0;
        boolean parentImpliedLoss = parentNodeData[6] != 0b0;
        boolean parentResolved = parentNodeData[7] != 0b0;
        long[] parentPosition = new long[]{upperConfidenceTree[parentIndex+2], upperConfidenceTree[parentIndex+3]};
        int[] parentMetaData = getMetaData(parentPosition[0]);
        System.out.format("playing as %d\n",metaData[1]);
        printBitBoard(position);
        if (index != 0) {
            System.out.format("-1 parent: %f wins %d visited (%f)\n", (double) parentNodeData[1]/2, parentNodeData[0], parentNodeData[1] / (double) parentNodeData[0] / 2.0);
        }
        System.out.format("node: %f wins %d visited (%f)", (double) nodeData[1]/2, nodeData[0], nodeData[1]/(double) nodeData[0] / 2.0);
        if (resolved) {
            System.out.print(" [resolved] ");
        } if (impliedLoss) {
            System.out.println(" (implied loss)");
        } else if (impliedWin) {
            System.out.println(" (implied win)");
        } else {
            System.out.println();
        }
        if (indexData[1] != 0) {
            int childrenIndex = (int) indexData[1];
            for (int i = 0; i < indexData[2]; i++) {
                long[] childNodeData = unpackNodeData(upperConfidenceTree[childrenIndex+i*4+1]);
                boolean childTerminalWin = childNodeData[2] != 0b0;
                boolean childTerminalLoss = childNodeData[3] != 0b0;
                boolean childTerminalDraw = childNodeData[4] != 0b0;
                boolean childImpliedWin = childNodeData[5] != 0b0;
                boolean childImpliedLoss = childNodeData[6] != 0b0;
                boolean childResolved = childNodeData[7] != 0b0;
                long[] childPosition = new long[]{upperConfidenceTree[childrenIndex+i*4+2], upperConfidenceTree[childrenIndex+i*4+3]};
                int[] childMetaData = getMetaData(childPosition[1]);
                int[] move = new int[]{childMetaData[2], childMetaData[3], childMetaData[4],childMetaData[5]};
                if (!childTerminalWin && !childTerminalLoss && !childTerminalDraw) {
                    System.out.format("%d child %d %d %d %d: %f wins %d visited (%f)", i, move[0], move[1], move[2], move[3], (double) childNodeData[1]/2, childNodeData[0], childNodeData[1] / (double) childNodeData[0] / 2.0);
                    System.out.format(" <%d nodes lost> ", allValidBoardTransitions(childPosition).length-fastAllValidBoardTransitions(childPosition).length);
                    if (childResolved) {
                        System.out.print(" [resolved] ");
                    } if (childImpliedWin) {
                        System.out.println(" (implied win)");
                    } else if (childImpliedLoss) {
                        System.out.println(" (implied loss)");
                    } else {
                        System.out.println();
                    }
                } else {
                    if (childTerminalWin) {
                        System.out.format("%d child %d %d %d %d: Terminal Win\n", i, move[0], move[1], move[2], move[3]);
                    } else if (childTerminalLoss) {
                        System.out.format("%d child %d %d %d %d: Terminal Loss\n", i, move[0], move[1], move[2], move[3]);
                    } else if (childTerminalDraw) {
                        System.out.format("%d child %d %d %d %d: Terminal Draw\n", i, move[0], move[1], move[2], move[3]);
                    }
                }
            }
        }
        System.out.print("(q to quit) next node: ");
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        if (s.equals("q")) {
            return;
        }
        int input = Integer.parseInt(s);
        if (input == -1) {
            exploreTree((int) indexData[0]);
        } else {
            exploreTree((int) indexData[1]+input*4);
        }
    }


    public static final int boardSize = 6;
    public static final int quadSize = 3;
    public static final int numQuads = 4;


    private static final long[] quadBitmasks = new long[]{
            0b0000000000000000000000000000111111111000000000000000000000000000L,
            0b0000000000000000000000000000000000000111111111000000000000000000L,
            0b0000000000000000000000000000000000000000000000111111111000000000L,
            0b0000000000000000000000000000000000000000000000000000000111111111L
    };

    private static final long metaDataBitmask       = 0b1111111111111111111111111111000000000000000000000000000000000000L;
    private static final long removeMetaDataBitmask = 0b0000000000000000000000000000111111111111111111111111111111111111L;

    private static final long playerMoveDataBitmask = 0b1111111111111111111110000000000000000000000000000000000000000000L;
    private static final long playerTurnDataBitmask = 0b0000000000000000000001111111000000000000000000000000000000000000L;

    //these won position bitmasks can be automatically generated by functions in bitmaskMaker.py
    public static final long[] fiveInARowBitMasks = new long[]{
            0b111000000110000000000000000000000000L,
            0b011000000111000000000000000000000000L,
            0b000111000000110000000000000000000000L,
            0b000011000000111000000000000000000000L,
            0b000000111000000110000000000000000000L,
            0b000000011000000111000000000000000000L,
            0b000000000000000000111000000110000000L,
            0b000000000000000000011000000111000000L,
            0b000000000000000000000111000000110000L,
            0b000000000000000000000011000000111000L,
            0b000000000000000000000000111000000110L,
            0b000000000000000000000000011000000111L,
            0b100100100000000000100100000000000000L,
            0b000100100000000000100100100000000000L,
            0b010010010000000000010010000000000000L,
            0b000010010000000000010010010000000000L,
            0b001001001000000000001001000000000000L,
            0b000001001000000000001001001000000000L,
            0b000000000100100100000000000100100000L,
            0b000000000000100100000000000100100100L,
            0b000000000010010010000000000010010000L,
            0b000000000000010010000000000010010010L,
            0b000000000001001001000000000001001000L,
            0b000000000000001001000000000001001001L,
            0b000100010000000000001000000000100010L,
            0b100010001000000000000000000100010000L,
            0b010001000000000100000000000010001000L,
            0b000010001000000000000000000100010001L,
            0b000000001010100000010100000000000000L,
            0b000000000000010100001010100000000000L,
            0b000000000001010100001010000000000000L,
            0b000000000000001010000001010100000000L
    };

    public static final int[][] positionBit2BoardCoords = new int[][]{
            {5,5}, {5,4}, {5,3}, {4,5}, {4,4}, {4,3}, {3,5}, {3,4}, {3,3},
            {5,2}, {5,1}, {5,0}, {4,2}, {4,1}, {4,0}, {3,2}, {3,1}, {3,0},
            {2,5}, {2,4}, {2,3}, {1,5}, {1,4}, {1,3}, {0,5}, {0,4}, {0,3},
            {2,2}, {2,1}, {2,0}, {1,2}, {1,1}, {1,0}, {0,2}, {0,1}, {0,0}
    };

    /** Function to implement George Marsaglia's XOR shift random number generator
     * https://en.wikipedia.org/wiki/Xorshift
     * @return random 64 bit long
     */
    public static long seed = 123;
    public static int random_positive_int() {
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        return (((int) seed) >>> 1);
    }
    public static void seed(long newSeed) {
        seed = newSeed;
    }

    /**
     * Since these functions are called on the same value or same narrow range of values many times in a row
     * we save a lot of time by caching results in a direct mapping
     *
     * most of the time is required for logarithm rather than square root
     */
    public static final int cacheSize = 1024;
    private static int[] squareRootCacheTags = new int[cacheSize];
    private static double[] squareRootCache = new double[cacheSize];
    public static double squareRoot(int x) {
        int index = x % cacheSize;
        if (x != squareRootCacheTags[index]) {
            squareRootCacheTags[index] = x;
            squareRootCache[index] = Math.sqrt(x);
        }
        return squareRootCache[index];
    }
    private static int[] squareRootLogCacheTags = new int[cacheSize];
    private static double[] squareRootLogCache = new double[cacheSize];
    public static double squareRootLog(int x) {
        int index = x % cacheSize;
        if (x != squareRootLogCacheTags[index]) {
            squareRootLogCacheTags[index] = x;
            squareRootLogCache[index] = Math.sqrt(Math.log(x));
        }
        return squareRootLogCache[index];
    }


    /**
     * Function to extract meta data from bit board representation
     * (see bit representation in boardState2BitBoard function below)
     * @param playerBitBoard
     * @return
     */
    public static int[] getMetaData(long playerBitBoard) {
        long metaData = playerBitBoard & metaDataBitmask;
        metaData = metaData >>> 36;
        long turnNumber = metaData & 0b011111;
        long playerId = (metaData >>> 5) & 0b01;
        long moveX = (metaData >>> 6) & 0b0111;
        long moveY = (metaData >>> 9) & 0b0111;
        long quad = (metaData >>> 12) & 0b011;
        long r_f = (metaData >>> 14) & 0b01;
        return new int[]{(int) turnNumber, (int) playerId, (int) moveX, (int) moveY, (int) quad, (int) r_f};
    }
    /** Function to update metadata of player position encoding
     * @param playerBitBoard
     * @param metaData
     * @return
     */
    public static long updateMetaData(long playerBitBoard, int[] metaData) {
        long metaDataEncoding = metaData[5] << 2;
        metaDataEncoding = (metaDataEncoding | metaData[4]) << 3;
        metaDataEncoding = (metaDataEncoding | metaData[3]) << 3;
        metaDataEncoding = (metaDataEncoding | metaData[2]) << 1;
        metaDataEncoding = (metaDataEncoding | metaData[1]) << 5;
        metaDataEncoding = (metaDataEncoding | metaData[0]) << 36;
        return (playerBitBoard & removeMetaDataBitmask) | metaDataEncoding;
    }

    /**
     * Functions to print out bit board data to console
     */
    public static String formattedBitPosition(long position) {
        position = position & removeMetaDataBitmask;
        String s = String.format("%36s",Long.toBinaryString(position)).replace(' ', '0');
        return s.substring(0,9)+" "+s.substring(9,18)+" "+s.substring(18,27)+" "+s.substring(27,36);
    }
    public static void printBitBoard(long[] bitBoard) {
        int[] metaData = getMetaData(bitBoard[0]);
        System.out.format("turn: %d\n", metaData[0]);
        System.out.format("player %d: ", metaData[1]);
        System.out.println(formattedBitPosition(bitBoard[0]));
        System.out.format("player %d: ", (metaData[1] == 0) ? 1 : 0);
        System.out.println(formattedBitPosition(bitBoard[1]));
        long coordinateBitPosition = 1L;
        coordinateBitPosition = coordinateBitPosition << 36;
        char[][] quadString = new char[4][9];
        char ourPieceCharacter = '/';
        char oppPieceCharacter = '/';
        if (metaData[1] == 0) {
            ourPieceCharacter = '0';
            oppPieceCharacter = '1';
        } else {
            ourPieceCharacter = '1';
            oppPieceCharacter = '0';
        }
        for (int q=0; q<numQuads; q++) {
            int index = 0;
            for (int r = 0; r < quadSize; r++) {
                for (int c = 0; c < quadSize; c++) {
                    coordinateBitPosition = coordinateBitPosition >>> 1;
                    if ((coordinateBitPosition & bitBoard[0]) != 0) {
                        quadString[q][index] = ourPieceCharacter;
                    } else if ((coordinateBitPosition & bitBoard[1]) != 0) {
                        quadString[q][index] = oppPieceCharacter;
                    } else {
                        quadString[q][index] = '.';
                    }
                    index += 1;
                }
            }
        }
        char[][] rows = new char[6][6];
        for (int r=0; r<quadSize; r++) {
            rows[r][0] = quadString[0][3*r+0];
            rows[r][1] = quadString[0][3*r+1];
            rows[r][2] = quadString[0][3*r+2];
            rows[r][3] = quadString[1][3*r+0];
            rows[r][4] = quadString[1][3*r+1];
            rows[r][5] = quadString[1][3*r+2];
        }
        for (int r=0; r<quadSize; r++) {
            rows[3+r][0] = quadString[2][3*r+0];
            rows[3+r][1] = quadString[2][3*r+1];
            rows[3+r][2] = quadString[2][3*r+2];
            rows[3+r][3] = quadString[3][3*r+0];
            rows[3+r][4] = quadString[3][3*r+1];
            rows[3+r][5] = quadString[3][3*r+2];
        }
        for (char[] row: rows) {
            for (char c: row) {
                System.out.print(c);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    /**
     * Function for converting PentagoBoardState instance into two representative 64 bit longs
     * @param boardState
     * @return
     */
    public static long[] boardState2BitBoard(PentagoBoardState boardState) {
        /* Schema for encoding state
            2 64-bit longs (1 per player) represent state

            64 bit long representing one player's pieces and metadata
            from low to high bits:
            9 bits q3
            9 bits q2
            9 bits q1
            9 bits q0
            6 bits turnNumber
            1 bit playerId (player of this position)
            3 bits move x  (last move taken by this player)
            3 bits move y
            2 bits quad
            1 bit  rotate/flip

            total: 52 bits

            each quad is represented as a linear sequence abcdefghi mapped to:
                                                                                abc
                                                                                def
                                                                                ghi

         */
        int playerId = boardState.getTurnPlayer();
        long ourPieces = playerId;
        ourPieces = ourPieces << 5;
        ourPieces = ourPieces | boardState.getTurnNumber();
        ourPieces = ourPieces << 36;
        long opponentsPieces = (playerId == 0) ? 1 : 0;
        opponentsPieces = opponentsPieces << 5;
        opponentsPieces = opponentsPieces | boardState.getTurnNumber();
        opponentsPieces = opponentsPieces << 36;
        int[] row_offset = new int[]{0,0,3,3};
        int[] column_offset = new int[]{0,3,0,3};
        long coordinateBitPosition = 1L;
        coordinateBitPosition = coordinateBitPosition << 36;
        for (int q=0; q<numQuads; q++) {
            for (int r=0; r<quadSize; r++) {
                for (int c = 0; c < quadSize; c++) {
                    coordinateBitPosition = coordinateBitPosition >>> 1;
                    PentagoBoardState.Piece piece = boardState.getPieceAt(r+row_offset[q], c+column_offset[q]);
                    if (piece == PentagoBoardState.Piece.BLACK) {
                        if (playerId == 0) {
                            //we are playing white so add to opponents pieces
                            opponentsPieces = opponentsPieces | coordinateBitPosition;
                        } else {
                            //we are playing black so add to our pieces
                            ourPieces = ourPieces | coordinateBitPosition;
                        }
                    } else if (piece == PentagoBoardState.Piece.WHITE) {
                        if (playerId == 0) {
                            //we are playing white so add to our pieces
                            ourPieces = ourPieces | coordinateBitPosition;
                        } else {
                            //we are playing black so add to opponents pieces
                            opponentsPieces = opponentsPieces | coordinateBitPosition;
                        }
                    }
                }
            }
        }
        return new long[]{ourPieces, opponentsPieces};
    }

    /**
     * Function to determine if a players position has won the game
     * @param position (64 bit single player current position)
     * @return true if player has a five in a row, false otherwise
     */
    public static boolean positionIsWon(long position) {
        for (int i=0; i<32; i++) {
            if (((position & removeMetaDataBitmask) & fiveInARowBitMasks[i]) == fiveInARowBitMasks[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function to create a bitmask of a flipped quadrant
     * @param bitBoard
     * @param quad
     * @return
     */
    private static long[] flipQuadBitMasksToShift = new long[]{0b100100100L, 0b010010010, 0b001001001L};
    private static int[] flipQuadBitMasksToShiftShiftAmounts = new int[]{2, 0, -2};
    public static long[] flippedBitMaskOfQuad(long[] bitBoard, int quad) {
        long[] newQuad = new long[]{0L,0L};
        long bitMask;
        int shiftAmount;
        for (int i=0; i<flipQuadBitMasksToShift.length; i++) {
            bitMask = (flipQuadBitMasksToShift[i] << ((3-quad) * 9));
            shiftAmount = flipQuadBitMasksToShiftShiftAmounts[i];
            if (shiftAmount > 0) {
                newQuad[0] = newQuad[0] | ((bitBoard[0] & bitMask) >>> shiftAmount);
                newQuad[1] = newQuad[1] | ((bitBoard[1] & bitMask) >>> shiftAmount);
            } else if (shiftAmount < 0) {
                newQuad[0] = newQuad[0] | ((bitBoard[0] & bitMask) << shiftAmount*-1);
                newQuad[1] = newQuad[1] | ((bitBoard[1] & bitMask) << shiftAmount*-1);
            } else {
                newQuad[0] = newQuad[0] | (bitBoard[0] & bitMask);
                newQuad[1] = newQuad[1] | (bitBoard[1] & bitMask);
            }
        }
        return newQuad;
    }
    /**
     * Function to create a bitmask of a rotated quadrant
     * @param bitBoard
     * @param quad
     * @return
     */
    private static final int[][] indexPermutationsToRotateQuad = new int[][]{
            {0,2},{2,8},{8,6},{6,0},{1,5},{5,7},{7,3},{3,1},{4,4}
    };
    public static long[] rotatedBitMaskOfQuad(long[] bitBoard, int quad) {
        long[] copyOfBitBoard = new long[]{0L,0L};
        long[] newQuad = new long[]{0L,0L};
        copyOfBitBoard[0] = (bitBoard[0] & quadBitmasks[quad]) >>> (9*(3-quad));
        copyOfBitBoard[1] = (bitBoard[1] & quadBitmasks[quad]) >>> (9*(3-quad));
        for (int[] shift : indexPermutationsToRotateQuad) {
            long bitToShift = 0b100000000 >>> shift[0];
            if ((bitToShift & copyOfBitBoard[0]) != 0) {
                newQuad[0] = newQuad[0] | (0b100000000 >>> shift[1]);
            }
            if ((bitToShift & copyOfBitBoard[1]) != 0) {
                newQuad[1] = newQuad[1] | (0b100000000 >>> shift[1]);
            }
        }
        newQuad[0] = newQuad[0] << (9*(3-quad));
        newQuad[1] = newQuad[1] << (9*(3-quad));
        return newQuad;
    }

    /** DEPRECATED - slower than fast version but kept around because it is guaranteed not to hash collide
     * Function to generate and return a list of all unique valid board transitions as well as associated move coordinate specification
     * @param position
     * @return
     * returns Object[] of length 2, elements at indices 0 and 1 can be cast to long[][] and int[][] respectively
     */
    public static long[][] allValidBoardTransitions(long[] position) {
        long[] newPosition = new long[2];
        //retrieve metadata from current player
        //flip bit board so as to return game boards from opponents perspective
        int[] metaData0 = getMetaData(position[0]);
        int[] metaData1 = getMetaData(position[1]);
        //number of moves is turn number * 2 + playerId
        int numberOfMoves = 2*metaData0[0]+metaData0[1];
        if (metaData0[1] == 1) {
            //increase turn number for each transition if playerId is 1
            metaData0[0]++;
            metaData1[0]++;
            newPosition[0] = updateMetaData(position[1], metaData1);
            newPosition[1] = updateMetaData(position[0], metaData0);
        } else {
            newPosition[0] = position[1];
            newPosition[1] = position[0];
        }
        //get all taken squares by either player
        long allPieces = newPosition[0] | newPosition[1];
        long[][] allTransitions = new long[(36 - numberOfMoves) * 8][2];
        int allTransitionsIndex = 0;
        //precompute flip of each current quad
        long[][] flippedQuadBitmasks = new long[][]{
                flippedBitMaskOfQuad(newPosition, 0),
                flippedBitMaskOfQuad(newPosition, 1),
                flippedBitMaskOfQuad(newPosition, 2),
                flippedBitMaskOfQuad(newPosition, 3)
        };
        //precompute rotate of each current quad
        long[][] rotatedQuadBitmasks = new long[][]{
                rotatedBitMaskOfQuad(newPosition, 0),
                rotatedBitMaskOfQuad(newPosition, 1),
                rotatedBitMaskOfQuad(newPosition, 2),
                rotatedBitMaskOfQuad(newPosition, 3)
        };
        int absolutePositionBit = 35; //linear position of coordinate in bit board
        for (int q=0; q<numQuads; q++) {
            int indexOfLastMoveModifyingPreviousQuad = allTransitionsIndex-1; //this points to furthest element back we need to check for duplicates with
            long quad = (allPieces & quadBitmasks[q]) >>> ((3-q)*9);//quad representation of both players
            for (int i=0; i<quadSize*quadSize; i++) {
                int[] moveCoordinates = positionBit2BoardCoords[absolutePositionBit];
                absolutePositionBit--;
                long coordinateBitPosition = (0b100000000 >>> i);
                if ((coordinateBitPosition & quad) == 0) {
                    //space is available place a piece (in second bitboard which is current player after flip)
                    long[] newPlacedBitBoard = new long[2];
                    newPlacedBitBoard[0] = newPosition[0];
                    newPlacedBitBoard[1] = newPosition[1] | (coordinateBitPosition << ((3-q)*9));
                    long[] newPlacedBitBoardFlippedQuadBitmask = flippedBitMaskOfQuad(newPlacedBitBoard, q);
                    long[] newPlacedBitBoardRotatedQuadBitmask = rotatedBitMaskOfQuad(newPlacedBitBoard, q);
                    for (int quadToModify=0;quadToModify<numQuads;quadToModify++) {
                        long[] newFlippedBitBoard = new long[2];
                        long[] newRotatedBitBoard = new long[2];
                        //remove current quad
                        newFlippedBitBoard[0] = newPlacedBitBoard[0] & ~quadBitmasks[quadToModify];
                        newFlippedBitBoard[1] = newPlacedBitBoard[1] & ~quadBitmasks[quadToModify];
                        newRotatedBitBoard[0] = newPlacedBitBoard[0] & ~quadBitmasks[quadToModify];
                        newRotatedBitBoard[1] = newPlacedBitBoard[1] & ~quadBitmasks[quadToModify];
                        if (quadToModify == q) {
                            //replace quad with flipped quad
                            newFlippedBitBoard[0] = newFlippedBitBoard[0] | newPlacedBitBoardFlippedQuadBitmask[0];
                            newFlippedBitBoard[1] = newFlippedBitBoard[1] | newPlacedBitBoardFlippedQuadBitmask[1];
                            //replace quad with rotated quad
                            newRotatedBitBoard[0] = newRotatedBitBoard[0] | newPlacedBitBoardRotatedQuadBitmask[0];
                            newRotatedBitBoard[1] = newRotatedBitBoard[1] | newPlacedBitBoardRotatedQuadBitmask[1];
                        } else {
                            //replace quad with flipped quad
                            newFlippedBitBoard[0] = newFlippedBitBoard[0] | flippedQuadBitmasks[quadToModify][0];
                            newFlippedBitBoard[1] = newFlippedBitBoard[1] | flippedQuadBitmasks[quadToModify][1];
                            //replace quad with rotated quad
                            newRotatedBitBoard[0] = newRotatedBitBoard[0] | rotatedQuadBitmasks[quadToModify][0];
                            newRotatedBitBoard[1] = newRotatedBitBoard[1] | rotatedQuadBitmasks[quadToModify][1];
                        }
                        boolean shouldAddNewFlippedBoard = true;
                        boolean shouldAddNewRotatedBoard = true;
                        if ((newRotatedBitBoard[0] & removeMetaDataBitmask) == (newFlippedBitBoard[0] & removeMetaDataBitmask)
                          &&(newRotatedBitBoard[1] & removeMetaDataBitmask) == (newFlippedBitBoard[1] & removeMetaDataBitmask)) {
                            shouldAddNewRotatedBoard = false;
                        }
                        int pointer = allTransitionsIndex-1;
                        while (pointer > indexOfLastMoveModifyingPreviousQuad) {
                            //check all board states generated within current quad for potential redundant moves to consider
                            if (shouldAddNewFlippedBoard && (allTransitions[pointer][0] & removeMetaDataBitmask) == (newFlippedBitBoard[0] & removeMetaDataBitmask)
                                                         && (allTransitions[pointer][1] & removeMetaDataBitmask) == (newFlippedBitBoard[1] & removeMetaDataBitmask)) {
                                shouldAddNewFlippedBoard = false;
                                if (!shouldAddNewRotatedBoard) {
                                    break;
                                }
                            }
                            if (shouldAddNewRotatedBoard && (allTransitions[pointer][0] & removeMetaDataBitmask) == (newRotatedBitBoard[0] & removeMetaDataBitmask)
                                                         && (allTransitions[pointer][1] & removeMetaDataBitmask) == (newRotatedBitBoard[1] & removeMetaDataBitmask)) {
                                shouldAddNewRotatedBoard = false;
                                if (!shouldAddNewFlippedBoard) {
                                    break;
                                }
                            }
                            pointer--;
                        }
                        if (shouldAddNewFlippedBoard) {
                            //encode the move data onto new position bit board
                            newFlippedBitBoard[1] = updateMetaData(newFlippedBitBoard[1],
                                    new int[]{metaData0[0], metaData0[1], moveCoordinates[0], moveCoordinates[1], quadToModify, 1});
                            allTransitions[allTransitionsIndex] = newFlippedBitBoard;
                            allTransitionsIndex++;
                        }
                        if (shouldAddNewRotatedBoard) {
                            //encode the move data onto new position bit board
                            newRotatedBitBoard[1] = updateMetaData(newRotatedBitBoard[1],
                                    new int[]{metaData0[0], metaData0[1], moveCoordinates[0], moveCoordinates[1], quadToModify, 0});
                            allTransitions[allTransitionsIndex] = newRotatedBitBoard;
                            allTransitionsIndex++;
                        }
                    }
                }
            }
        }
        long[][] uniqueTransitions = new long[allTransitionsIndex][2];
        for (int i=0; i<allTransitionsIndex; i++) {
            uniqueTransitions[i] = allTransitions[i];
        }
        return uniqueTransitions;
    }

    private static long[][] hashSetOfNewPositions = new long[(997+1)*(997+1)][2];// 997 is prime... this helps avoid hash collisions i think
    private static long[][] validTransitionsArray = new long[288][2];
    public static long[][] fastAllValidBoardTransitions(long[] position) {
        long[] newPosition = new long[2];
        //retrieve metadata from current player
        //flip bit board so as to return game boards from opponents perspective
        int[] metaData0 = getMetaData(position[0]);
        int[] metaData1 = getMetaData(position[1]);
        //number of moves is turn number * 2 + playerId
        int numberOfMoves = 2*metaData0[0]+metaData0[1];
        if (metaData0[1] == 1) {
            //increase turn number for each transition if playerId is 1
            metaData0[0]++;
            metaData1[0]++;
            newPosition[0] = updateMetaData(position[1], metaData1);
            newPosition[1] = updateMetaData(position[0], metaData0);
        } else {
            newPosition[0] = position[1];
            newPosition[1] = position[0];
        }
        //get all taken squares by either player
        long allPieces = newPosition[0] | newPosition[1];
        //long[][] validTransitionsArray = new long[(36 - numberOfMoves) * 8][2];
        int allTransitionsIndex = 0;
        //precompute flip of each current quad
        long[][] flippedQuadBitmasks = new long[][]{
                flippedBitMaskOfQuad(newPosition, 0),
                flippedBitMaskOfQuad(newPosition, 1),
                flippedBitMaskOfQuad(newPosition, 2),
                flippedBitMaskOfQuad(newPosition, 3)
        };
        //precompute rotate of each current quad
        long[][] rotatedQuadBitmasks = new long[][]{
                rotatedBitMaskOfQuad(newPosition, 0),
                rotatedBitMaskOfQuad(newPosition, 1),
                rotatedBitMaskOfQuad(newPosition, 2),
                rotatedBitMaskOfQuad(newPosition, 3)
        };
        int absolutePositionBit = 35; //linear position of coordinate in bit board
        for (int q=0; q<numQuads; q++) {
            long quad = (allPieces & quadBitmasks[q]) >>> ((3-q)*9);//quad representation of both players
            for (int i=0; i<quadSize*quadSize; i++) {
                int[] moveCoordinates = positionBit2BoardCoords[absolutePositionBit];
                absolutePositionBit--;
                long coordinateBitPosition = (0b100000000 >>> i);
                if ((coordinateBitPosition & quad) == 0) {
                    //space is available place a piece (in second bitboard which is current player after flip)
                    long[] newPlacedBitBoard = new long[2];
                    newPlacedBitBoard[0] = newPosition[0];
                    newPlacedBitBoard[1] = newPosition[1] | (coordinateBitPosition << ((3-q)*9));
                    long[] newPlacedBitBoardFlippedQuadBitmask = flippedBitMaskOfQuad(newPlacedBitBoard, q);
                    long[] newPlacedBitBoardRotatedQuadBitmask = rotatedBitMaskOfQuad(newPlacedBitBoard, q);
                    for (int quadToModify=0;quadToModify<numQuads;quadToModify++) {
                        long[] newFlippedBitBoard = new long[2];
                        long[] newRotatedBitBoard = new long[2];
                        //remove current quad
                        newFlippedBitBoard[0] = newPlacedBitBoard[0] & ~quadBitmasks[quadToModify];
                        newFlippedBitBoard[1] = newPlacedBitBoard[1] & ~quadBitmasks[quadToModify];
                        newRotatedBitBoard[0] = newPlacedBitBoard[0] & ~quadBitmasks[quadToModify];
                        newRotatedBitBoard[1] = newPlacedBitBoard[1] & ~quadBitmasks[quadToModify];
                        if (quadToModify == q) {
                            //replace quad with flipped quad
                            newFlippedBitBoard[0] = newFlippedBitBoard[0] | newPlacedBitBoardFlippedQuadBitmask[0];
                            newFlippedBitBoard[1] = newFlippedBitBoard[1] | newPlacedBitBoardFlippedQuadBitmask[1];
                            //replace quad with rotated quad
                            newRotatedBitBoard[0] = newRotatedBitBoard[0] | newPlacedBitBoardRotatedQuadBitmask[0];
                            newRotatedBitBoard[1] = newRotatedBitBoard[1] | newPlacedBitBoardRotatedQuadBitmask[1];
                        } else {
                            //replace quad with flipped quad
                            newFlippedBitBoard[0] = newFlippedBitBoard[0] | flippedQuadBitmasks[quadToModify][0];
                            newFlippedBitBoard[1] = newFlippedBitBoard[1] | flippedQuadBitmasks[quadToModify][1];
                            //replace quad with rotated quad
                            newRotatedBitBoard[0] = newRotatedBitBoard[0] | rotatedQuadBitmasks[quadToModify][0];
                            newRotatedBitBoard[1] = newRotatedBitBoard[1] | rotatedQuadBitmasks[quadToModify][1];
                        }
                        boolean shouldAddNewFlippedBoard = false;
                        boolean shouldAddNewRotatedBoard = false;
                        int hashOfNewFlippedBoard = (int) ((((newFlippedBitBoard[1]&removeMetaDataBitmask) % 997)+1) * (((newFlippedBitBoard[0]&removeMetaDataBitmask) % 997)+1));
                        if (hashSetOfNewPositions[hashOfNewFlippedBoard][0] == 0) {
                            hashSetOfNewPositions[hashOfNewFlippedBoard][0] = newFlippedBitBoard[1];
                            shouldAddNewFlippedBoard = true;
                        } else if (hashSetOfNewPositions[hashOfNewFlippedBoard][1] == 0 && hashSetOfNewPositions[hashOfNewFlippedBoard][0] != newFlippedBitBoard[1]) {
                            hashSetOfNewPositions[hashOfNewFlippedBoard][1] = newFlippedBitBoard[1];
                            shouldAddNewFlippedBoard = true;
                        }
                        int hashOfNewRotatedBoard = (int) ((((newRotatedBitBoard[1]&removeMetaDataBitmask) % 997)+1) * (((newRotatedBitBoard[0]&removeMetaDataBitmask) % 997)+1));
                        if (hashSetOfNewPositions[hashOfNewRotatedBoard][0] == 0) {
                            hashSetOfNewPositions[hashOfNewRotatedBoard][0] = newRotatedBitBoard[1];
                            shouldAddNewRotatedBoard = true;
                        } else if (hashSetOfNewPositions[hashOfNewRotatedBoard][1] == 0 && hashSetOfNewPositions[hashOfNewRotatedBoard][0] != newRotatedBitBoard[1]) {
                            hashSetOfNewPositions[hashOfNewRotatedBoard][1] = newRotatedBitBoard[1];
                            shouldAddNewRotatedBoard = true;
                        }

                        if (shouldAddNewFlippedBoard) {
                            //encode the move data onto new position bit board
                            newFlippedBitBoard[1] = updateMetaData(newFlippedBitBoard[1],
                                    new int[]{metaData0[0], metaData0[1], moveCoordinates[0], moveCoordinates[1], quadToModify, 1});
                            validTransitionsArray[allTransitionsIndex] = newFlippedBitBoard;
                            allTransitionsIndex++;
                        }
                        if (shouldAddNewRotatedBoard) {
                            //encode the move data onto new position bit board
                            newRotatedBitBoard[1] = updateMetaData(newRotatedBitBoard[1],
                                    new int[]{metaData0[0], metaData0[1], moveCoordinates[0], moveCoordinates[1], quadToModify, 0});
                            validTransitionsArray[allTransitionsIndex] = newRotatedBitBoard;
                            allTransitionsIndex++;
                        }
                    }
                }
            }
        }
        long[][] uniqueTransitions = new long[allTransitionsIndex][2];
        for (int i=0; i<allTransitionsIndex; i++) {
            uniqueTransitions[i] = validTransitionsArray[i];
            int hashOfBoard = (int) ((((uniqueTransitions[i][1]&removeMetaDataBitmask) % 997)+1) * (((uniqueTransitions[i][0]&removeMetaDataBitmask) % 997)+1));
            hashSetOfNewPositions[hashOfBoard][0] = 0;
            hashSetOfNewPositions[hashOfBoard][1] = 0;
        }
        return uniqueTransitions;
    }

    /**Function randomly rolls out game (recursive)
     * @return
     */
    public static int gameRollOutFromPosition(long[] position) {
        int[] metaData0 = getMetaData(position[0]);
        int[] metaData1 = getMetaData(position[1]);
        long allFreeSpaces = (~(position[0] | position[1])) & removeMetaDataBitmask;
        if (allFreeSpaces == 0b0L) {
            return 2;
        }
        long[] newPosition = new long[2];
        if (metaData0[1] == 1) {
            //increase turn number for each transition if playerId is 1
            metaData0[0]++;
            metaData1[0]++;
        }
        int numberOfMovesLeft = 36-2*metaData0[0]+metaData0[1];
        newPosition[0] = updateMetaData(position[1], metaData1);
        newPosition[1] = updateMetaData(position[0], metaData0);
        int randomMoveIndex = random_positive_int();
        //a ghost haunted this code on this line so the line has been removed
        randomMoveIndex %= numberOfMovesLeft;//causes core dump if number of moves left==0
        long bitToFlip = 1L;
        int index = 0;
        while (index != randomMoveIndex || allFreeSpaces % 2 == 0) {
            while (allFreeSpaces % 2 == 0) {
                allFreeSpaces = allFreeSpaces >>> 1;
                bitToFlip = bitToFlip << 1;
            }
            if (index < randomMoveIndex) {
                index++;
                allFreeSpaces = allFreeSpaces >>> 1;
                bitToFlip = bitToFlip << 1;
            }
        }
        newPosition[1] = newPosition[1] | bitToFlip;
        int random = random_positive_int();
        int rotateOrFlip = (random % 2 == 0) ? 0 : 1;
        random = random_positive_int();
        int quadToModify = random % numQuads;
        long[] bitMaskOfModifiedQuad;
        if (rotateOrFlip == 0) {
            //rotate
            bitMaskOfModifiedQuad = rotatedBitMaskOfQuad(newPosition, quadToModify);
        } else {
            //flip
            bitMaskOfModifiedQuad = flippedBitMaskOfQuad(newPosition, quadToModify);
        }
        newPosition[0] = newPosition[0] & ~quadBitmasks[quadToModify];
        newPosition[1] = newPosition[1] & ~quadBitmasks[quadToModify];
        newPosition[0] = newPosition[0] | bitMaskOfModifiedQuad[0];
        newPosition[1] = newPosition[1] | bitMaskOfModifiedQuad[1];
        boolean oppPOVWon = positionIsWon(newPosition[1]);
        boolean ourPOVWon = positionIsWon(newPosition[0]);
        if (oppPOVWon && !ourPOVWon) {
            return metaData0[1];
        } else if (!oppPOVWon && ourPOVWon) {
            return metaData1[1];
        } else if (oppPOVWon && ourPOVWon) {
            return 2;
        } else if (0b0 == ((~(newPosition[0] | newPosition[1])) & removeMetaDataBitmask)) {
            return 2;
        }
        return gameRollOutFromPosition(newPosition);
    }

    public static int[] upperConfidenceTreeDeterminedBestPosition(long[] currentPosition, long milliSecondsToCompute, boolean freeMemory) {
        long start = System.currentTimeMillis();
        initUpperConfidenceTree(currentPosition);
        int iterations = 100000;
        while(true) {//loop is structured such that compute time is only checked frequently as we approach the compute time limit
            for (int i=0;i<iterations;i++) {
                if (visitUCTNode(0) == 4) {
                    break;
                }
            }
            long timeRemaining = milliSecondsToCompute-(System.currentTimeMillis()-start);
            iterations = (int)timeRemaining*10;
            if (timeRemaining < 5) {
                break;
            }
        };
        int[] move = bestMoveFromUCTTree();
        if (freeMemory) upperConfidenceTree = null;//free memory
        return move;
    }
}

