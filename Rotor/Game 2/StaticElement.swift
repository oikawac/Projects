//
//  StaticElement.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-22.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class StaticElement: Element {
    
    var totalTimeElapsed: TimeInterval = 0
    
    override func update(heightOnScreen: CGFloat, timeElapsed: TimeInterval) {
        totalTimeElapsed += timeElapsed
        if shouldDestructIn == nil {
            if !positionIsStatic {
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
                buildLevel = 2
                if totalTimeElapsed < 1.5 {
                    if totalTimeElapsed > 0.75 {
                        buildLevel = 4
                    }
                    if Int(arc4random_uniform(UInt32(100))) < 30 {
                        reloadTexture()
                    }
                } else {
                    buildLevel = 5
                    reloadTexture()
                }
            }
        } else {
            if !positionIsStatic {
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
            } else {
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
    
    override func prepareToDestruct() {
        buildLevel = 4
    }
}
