//
//  MovingElement.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-22.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class MovingElement: Element {
    
    var fromPosition: CGPoint
    var toPosition: CGPoint
    var middlePosition: CGPoint
    
    var movementPeriod: CGFloat
    var direction: CGFloat
    
    var totalTimeElapsed: TimeInterval = 0
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, size: CGSize, vectors: [Vector], colour: UIColor, fromPositionInLevel: CGPoint, toPositionInLevel: CGPoint, movementPeriod: CGFloat, reverseDirection: Bool) {
        if reverseDirection {
            direction = -1
        } else {
            direction = 1
        }
        fromPosition = fromPositionInLevel
        toPosition = toPositionInLevel
        self.movementPeriod = movementPeriod
        middlePosition = fromPosition.midPoint(to: toPosition)
        super.init(screenSize: screenSize, size: size, vectors: vectors, colour: colour, positionInLevel: middlePosition)
    }
    
    override func update(heightOnScreen: CGFloat, timeElapsed: TimeInterval) {
        if shouldDestructIn == nil {
            if (buildLevel != 0) && (heightOnScreen > 0.95 || heightOnScreen < 0.05) {
                buildLevel = 0
                reloadTexture()
            } else if (buildLevel != 1) && (heightOnScreen < 0.95 && heightOnScreen > 0.90 || heightOnScreen < 0.10 && heightOnScreen > 0.05) {
                buildLevel = 1
                reloadTexture()
            } else if (buildLevel != 2) && (heightOnScreen < 0.90 && heightOnScreen > 0.85 || heightOnScreen < 0.15 && heightOnScreen > 0.10) {
                buildLevel = 2
                reloadTexture()
            } else if (buildLevel != 3) && (heightOnScreen < 0.85 && heightOnScreen > 0.80 || heightOnScreen < 0.20 && heightOnScreen > 0.15) {
                buildLevel = 3
                reloadTexture()
            } else if (buildLevel != 4) && (heightOnScreen < 0.80 && heightOnScreen > 0.75 || heightOnScreen < 0.25 && heightOnScreen > 0.20) {
                buildLevel = 4
                reloadTexture()
            } else if (buildLevel != 5) && (heightOnScreen < 0.75 && heightOnScreen > 0.25) {
                buildLevel = 5
                reloadTexture()
            }
        } else {
            if heightOnScreen < 1.25 && heightOnScreen > -0.25 {
                if shouldDestructIn! < 0.9 && shouldDestructIn! > 0.8 && buildLevel == 4 {
                    buildLevel = 3
                    reloadTexture()
                }  else if shouldDestructIn! < 0.7 && shouldDestructIn! > 0.6 && buildLevel == 3 {
                    buildLevel = 2
                    reloadTexture()
                }  else if shouldDestructIn! < 0.5 && shouldDestructIn! > 0.4 && buildLevel == 2 {
                    buildLevel = 1
                    reloadTexture()
                }  else if shouldDestructIn! < 0.3 && shouldDestructIn! > 0.2 && buildLevel == 1 {
                    buildLevel = 0
                    reloadTexture()
                }  else if shouldDestructIn! < 0.1 && shouldDestructIn! > 0.0 && buildLevel == 0 {
                    removeFromParent()
                    reloadTexture()
                }
            }
        }
    }
    
    override func move(heightOnScreen: CGFloat, timeElapsed: TimeInterval) {
        if buildLevel > 4 && shouldDestructIn == nil {
            totalTimeElapsed += timeElapsed
            positionInLevel.x = middlePosition.x+direction*(abs(fromPosition.x-toPosition.x)/2)*CGFloat(sin(totalTimeElapsed*Double.pi*2/Double(movementPeriod)))
            positionInLevel.y = middlePosition.y+direction*(abs(fromPosition.y-toPosition.y)/2)*CGFloat(sin(totalTimeElapsed*Double.pi*2/Double(movementPeriod)))
        }
    }
    
    override func prepareToDestruct() {
        buildLevel = 4
    }
}
