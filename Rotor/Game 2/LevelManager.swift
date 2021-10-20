//
//  LevelManager.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-05-12.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class LevelManager {
    
    static func loadElements(forLevel levelId: Int, screenSize: CGSize) -> [Element] {
        //returns all Element objects required for a level
        var rect = [Vector]()
        rect.append(Vector(from: CGPoint(x: -0.5, y: -0.5), to: CGPoint(x: -0.5, y: 0.5)))
        rect.append(Vector(from: CGPoint(x: -0.5, y: 0.5), to: CGPoint(x: 0.5, y: 0.5)))
        rect.append(Vector(from: CGPoint(x: 0.5, y: 0.5), to: CGPoint(x: 0.5, y: -0.5)))
        rect.append(Vector(from: CGPoint(x: 0.5, y: -0.5), to: CGPoint(x: -0.5, y: -0.5)))
        var diamond = [Vector]()
        diamond.append(Vector(from: CGPoint(x: 0, y: -0.5), to: CGPoint(x: -0.5, y: 0)))
        diamond.append(Vector(from: CGPoint(x: -0.5, y: 0), to: CGPoint(x: 0, y: 0.5)))
        diamond.append(Vector(from: CGPoint(x: 0, y: 0.5), to: CGPoint(x: 0.5, y: 0)))
        diamond.append(Vector(from: CGPoint(x: 0.5, y: 0), to: CGPoint(x: 0, y: -0.5)))
        var triangleRight = [Vector]()
        triangleRight.append(Vector(from: CGPoint(x: -0.5, y: -0.5), to: CGPoint(x: 0.5, y: 0)))
        triangleRight.append(Vector(from: CGPoint(x: 0.5, y: 0), to: CGPoint(x: -0.5, y: 0.5)))
        triangleRight.append(Vector(from: CGPoint(x: -0.5, y: 0.5), to: CGPoint(x: -0.5, y: -0.5)))
        var triangleLeft = [Vector]()
        triangleLeft.append(Vector(from: CGPoint(x: 0.5, y: -0.5), to: CGPoint(x: -0.5, y: 0)))
        triangleLeft.append(Vector(from: CGPoint(x: -0.5, y: 0), to: CGPoint(x: 0.5, y: 0.5)))
        triangleLeft.append(Vector(from: CGPoint(x: 0.5, y: 0.5), to: CGPoint(x: 0.5, y: -0.5)))
        var triangleDown = [Vector]()
        triangleDown.append(Vector(from: CGPoint(x: 0, y: -0.5), to: CGPoint(x: 0.5, y: 0.5)))
        triangleDown.append(Vector(from: CGPoint(x: 0.5, y: 0.5), to: CGPoint(x: -0.5, y: 0.5)))
        triangleDown.append(Vector(from: CGPoint(x: -0.5, y: 0.5), to: CGPoint(x: 0, y: -0.5)))
        var octagon = [Vector]()
        octagon.append(Vector(from: CGPoint(x: -1/6, y: 0.5), to: CGPoint(x: 1/6, y: 0.5)))
        octagon.append(Vector(from: CGPoint(x: 1/6, y: 0.5), to: CGPoint(x: 0.5, y: 1/6)))
        octagon.append(Vector(from: CGPoint(x: 0.5, y: 1/6), to: CGPoint(x: 0.5, y: -1/6)))
        octagon.append(Vector(from: CGPoint(x: 0.5, y: -1/6), to: CGPoint(x: 1/6, y: -0.5)))
        octagon.append(Vector(from: CGPoint(x: 1/6, y: -0.5), to: CGPoint(x: -1/6, y: -0.5)))
        octagon.append(Vector(from: CGPoint(x: -1/6, y: -0.5), to: CGPoint(x: -0.5, y: -1/6)))
        octagon.append(Vector(from: CGPoint(x: -0.5, y: -1/6), to: CGPoint(x: -0.5, y: 1/6)))
        octagon.append(Vector(from: CGPoint(x: -0.5, y: 1/6), to: CGPoint(x: -1/6, y: 0.5)))
        var vectorTypes = ["rect": rect, "diamond": diamond, "triangleLeft": triangleLeft, "triangleRight": triangleRight, "triangleDown": triangleDown, "octagon": octagon]
        
        var elementArray = [Element]()
        let colourScheme = getColourScheme(for: levelId)
        
        let wallLeftElement = StaticElement(screenSize: screenSize, size: CGSize(width: screenSize.width/25, height: screenSize.height*1.5), vectors: vectorTypes["rect"]!, colour: colourScheme.primaryColour, positionInLevel: CGPoint(x: screenSize.width*0.5, y: 0))
        wallLeftElement.positionIsStatic = true
        elementArray.append(wallLeftElement)
        let wallRightElement = StaticElement(screenSize: screenSize, size: CGSize(width: screenSize.width/25, height: screenSize.height*1.5), vectors: vectorTypes["rect"]!, colour: colourScheme.primaryColour, positionInLevel: CGPoint(x: -screenSize.width*0.5, y: 0))
        wallRightElement.positionIsStatic = true
        elementArray.append(wallRightElement)
        
        if levelId == 0 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level1", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 1 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level2", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 2 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level3", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 3 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level4", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 4 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level5", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 5 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level6", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 6 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level7", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 7 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level8", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 8 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level9", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 9 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level10", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 10 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level11", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        } else if levelId == 11 {
            var newElements = [Element]()
            (newElements, _) = loadLevelFromFile("level12", screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: screenSize.width)
            elementArray += newElements
        }
        return elementArray
    }
    
    private static func loadLevelFromFile(_ levelFile: String, screenSize: CGSize, vectorTypes: [String: [Vector]], colourScheme: (backgroundColour: UIColor, primaryColour: UIColor, secondaryColour: UIColor), endOfLevel: CGFloat) -> ([Element], CGFloat) {
        var elementArray = [Element]()
        var endOfLevel = endOfLevel
        if let path = Bundle.main.path(forResource: levelFile, ofType: "plist") {
            if let elements = NSArray(contentsOfFile: path) as? [NSDictionary] {
                for element in elements {
                    let type = element["type"] as! String
                    if type == "subSection" {
                        let file = element["file"] as! String
                        var newElements = [Element]()
                        (newElements, endOfLevel) = loadLevelFromFile(file, screenSize: screenSize, vectorTypes: vectorTypes, colourScheme: colourScheme, endOfLevel: endOfLevel)
                        elementArray += newElements
                    } else if type == "static" || type == "finishLine" {
                        let colourType = element["colour"] as! String
                        var colour = UIColor()
                        if colourType == "primary" {
                            colour = colourScheme.primaryColour
                        } else if colourType == "secondary" {
                            colour = colourScheme.secondaryColour
                        }
                        let vectorType = element["vectors"] as! String
                        let vectors = vectorTypes[vectorType]!
                        let relativeSize = element["size"] as! [CGFloat]
                        let size = CGSize(width: relativeSize[0]*screenSize.width, height: relativeSize[1]*screenSize.width)
                        let relativePosition = element["position"] as! [CGFloat]
                        endOfLevel += relativePosition[1]*screenSize.width
                        let position = CGPoint(x: relativePosition[0]*screenSize.width, y: endOfLevel)
                        if type == "static" {
                            elementArray.append(StaticElement(screenSize: screenSize, size: size, vectors: vectors, colour: colour, positionInLevel: position))
                        } else if type == "finishLine" {
                            elementArray.append(FinishLineElement(screenSize: screenSize, size: size, vectors: vectors, colour: colour, positionInLevel: position))
                        }
                    } else if type == "spacer" {
                        let space = element["space"] as! CGFloat
                        endOfLevel += space*screenSize.width
                    } else if type == "moving" {
                        let colourType = element["colour"] as! String
                        var colour = UIColor()
                        if colourType == "primary" {
                            colour = colourScheme.primaryColour
                        } else if colourType == "secondary" {
                            colour = colourScheme.secondaryColour
                        }
                        let vectorType = element["vectors"] as! String
                        let vectors = vectorTypes[vectorType]!
                        let relativeSize = element["size"] as! [CGFloat]
                        let size = CGSize(width: relativeSize[0]*screenSize.width, height: relativeSize[1]*screenSize.width)
                        let relativeFromPosition = element["fromPosition"] as! [CGFloat]
                        endOfLevel += relativeFromPosition[1]*screenSize.width
                        let fromPosition = CGPoint(x: relativeFromPosition[0]*screenSize.width, y: endOfLevel)
                        let relativeToPosition = element["toPosition"] as! [CGFloat]
                        endOfLevel += relativeToPosition[1]*screenSize.width
                        let toPosition = CGPoint(x: relativeToPosition[0]*screenSize.width, y: endOfLevel)
                        let period = element["period"] as! CGFloat
                        let reverse = element["reverse"] as! Bool
                        elementArray.append(MovingElement(screenSize: screenSize, size: size, vectors: vectors, colour: colour, fromPositionInLevel: fromPosition, toPositionInLevel: toPosition, movementPeriod: period, reverseDirection: reverse))
                    } else if type == "move" {
                        let colourType = element["colour"] as! String
                        var colour = UIColor()
                        if colourType == "primary" {
                            colour = colourScheme.primaryColour
                        } else if colourType == "secondary" {
                            colour = colourScheme.secondaryColour
                        }
                        let vectorType = element["vectors"] as! String
                        let vectors = vectorTypes[vectorType]!
                        let relativeSize = element["size"] as! [CGFloat]
                        let size = CGSize(width: relativeSize[0]*screenSize.width, height: relativeSize[1]*screenSize.width)
                        let relativeFromPosition = element["fromPosition"] as! [CGFloat]
                        endOfLevel += relativeFromPosition[1]*screenSize.width
                        let fromPosition = CGPoint(x: relativeFromPosition[0]*screenSize.width, y: endOfLevel)
                        let relativeToPosition = element["toPosition"] as! [CGFloat]
                        endOfLevel += relativeToPosition[1]*screenSize.width
                        let toPosition = CGPoint(x: relativeToPosition[0]*screenSize.width, y: endOfLevel)
                        let period = element["period"] as! CGFloat
                        elementArray.append(MoveElement(screenSize: screenSize, size: size, vectors: vectors, colour: colour, fromPositionInLevel: fromPosition, toPositionInLevel: toPosition, movementPeriod: period))
                    }
                }
            } else {
                print("error: could not load \(levelFile)")
            }
        } else {
            print("error: could not load \(levelFile)")
        }
        return (elementArray, endOfLevel)
    }

    
    static func getColourScheme(for level: Int) -> (backgroundColour: UIColor, primaryColour: UIColor, secondaryColour: UIColor) {
        //returns appropriate colour scheme for a level
        if level == 0 {
            return (backgroundColour: UIColor(red: 0.45, green: 0.69, blue: 0.97, alpha: 1.0), primaryColour: UIColor.white, secondaryColour: UIColor.white)
        } else if level == 1 {
            return (backgroundColour: UIColor(red: 0.9, green: 0.9, blue: 0.45, alpha: 1.0), primaryColour: UIColor.white, secondaryColour: UIColor.white)
        } else if level == 2 {
            return (backgroundColour: UIColor(red: 1.0, green: 0.51, blue: 0.45, alpha: 1.0), primaryColour: UIColor(red: 1.0, green: 1.0, blue: 0.80, alpha: 1.0), secondaryColour: UIColor(red: 1.0, green: 1.0, blue: 0.80, alpha: 1.0))
        } else if level == 3 {
            return (backgroundColour: UIColor(red: 0.2, green: 0.5, blue: 0.6, alpha: 1.0), primaryColour: UIColor(red: 0.7, green: 1.0, blue: 0.7, alpha: 1.0), secondaryColour: UIColor(red: 0.7, green: 1.0, blue: 0.7, alpha: 1.0))
        } else if level == 4 {
            return (backgroundColour: UIColor(red: 0.85, green: 0.65, blue: 0.98, alpha: 1.0), primaryColour: UIColor(red: 1.0, green: 0.85, blue: 1.0, alpha: 1.0), UIColor(red: 1.0, green: 0.85, blue: 1.0, alpha: 1.0))
        } else if level == 5 {
            return (backgroundColour: UIColor(red: 0.5, green: 0.7, blue: 0.3, alpha: 1.0), primaryColour: UIColor(red: 0.7, green: 0.9, blue: 0.6, alpha: 1.0), secondaryColour: UIColor(red: 0.9, green: 0.7, blue: 0.5, alpha: 1.0))
        } else if level == 6 {
            return (backgroundColour: UIColor(red: 0.92, green: 0.47, blue: 0.25, alpha: 1.0), primaryColour: UIColor.white, secondaryColour: UIColor.white)
        } else if level == 7 {
            return (backgroundColour: UIColor(red: 0.85, green: 0.2, blue: 0.2, alpha: 1.0), primaryColour: UIColor.white, secondaryColour: UIColor.white)
        } else if level == 8 {
            return (backgroundColour: UIColor(red: 0.2, green: 0.2, blue: 0.85, alpha: 1.0), primaryColour: UIColor.white, secondaryColour: UIColor.white)
        } else if level == 9 {
            return (backgroundColour: UIColor.black, primaryColour: UIColor(red: 0.15, green: 0.80, blue: 0.15, alpha: 1.0), secondaryColour: UIColor(red: 0.15, green: 0.80, blue: 0.15, alpha: 1.0))
        } else if level == 10 {
            return (backgroundColour: UIColor.white, primaryColour: UIColor(red: 0.9, green: 0.9, blue: 0.9, alpha: 1.0), secondaryColour: UIColor(red: 0.9, green: 0.9, blue: 0.9, alpha: 1.0))
        } else {
            return (backgroundColour: UIColor.darkGray, primaryColour: UIColor.white, secondaryColour: UIColor.white)
        }
    }
    
    static func getVelocity(for level: Int, screenSize: CGSize) -> CGFloat {
        //returns appropriate velocity for a level
        if level == 0 {
            return screenSize.width*0.000014
        } else if level == 1 {
            return screenSize.width*0.000016
        } else if level == 2 {
            return screenSize.width*0.000018
        } else if level == 3 {
            return screenSize.width*0.000022
        } else if level == 4 {
            return screenSize.width*0.000024
        } else if level == 5 {
            return screenSize.width*0.000026
        } else if level == 6 {
            return screenSize.width*0.000028
        } else if level == 7 {
            return screenSize.width*0.000028
        } else if level == 8 {
            return screenSize.width*0.000030
        } else if level == 9 {
            return screenSize.width*0.000032
        } else if level == 10 {
            return screenSize.width*0.000036
        } else if level == 11 {
            return screenSize.width*0.000038
        } else {
            return screenSize.width*0.000012
        }
    }
    
    static func getMusicSpeed(for level: Int) -> Double {
        //returns appropriate velocity for a level
        if level == 0 {
            return 0.7
        } else if level == 1 {
            return 0.75
        } else if level == 2 {
            return 0.8
        } else if level == 3 {
            return 0.85
        } else if level == 4 {
            return 0.9
        } else if level == 5 {
            return 0.95
        } else if level == 6 {
            return 1.0
        } else if level == 7 {
            return 1.05
        } else if level == 8 {
            return 1.1
        } else if level == 9 {
            return 1.15
        } else if level == 10 {
            return 1.2
        } else if level == 11 {
            return 1.25
        } else {
            return 1.0
        }
    }
    
    static func getMaxLevel() -> Int {
        return 11
    }
}
