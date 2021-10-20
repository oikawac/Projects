//
//  Element.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-22.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class Element: SKSpriteNode {
    
    var positionInLevel: CGPoint
    
    var positionIsStatic = false
    
    var playerWinsOnContact = false
    
    var playerDiesOnContact = true
    
    var vectors = [Vector]()
    
    var buildLevel = 0

    var screenSize: CGSize
    
    var textureSize: CGSize
    
    var colour: UIColor
    
    var shouldDestructIn: TimeInterval?
    
    var playerPosition: CGPoint?
    
    var requestsPlayerPosition = false
    
    var currentlyAttachedToScene = false
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, size: CGSize, vectors: [Vector], colour: UIColor, positionInLevel: CGPoint) {
        self.screenSize = screenSize
        self.vectors = vectors
        textureSize = size
        self.colour = colour
        self.positionInLevel = positionInLevel
        let texture = ShapeGenerator.generateElement(screenSize: screenSize, size: textureSize, vectors: vectors, colour: colour, buildLevel: buildLevel)
        super.init(texture: texture, color: UIColor(), size: texture.size())
    }
    
    init(screenSize: CGSize, size: CGSize, vectors: [Vector], texture: SKTexture, colour: UIColor, positionInLevel: CGPoint) {
        self.screenSize = screenSize
        self.vectors = vectors
        textureSize = size
        self.colour = colour
        self.positionInLevel = positionInLevel
        super.init(texture: texture, color: UIColor(), size: texture.size())
    }
    
    func update(heightOnScreen: CGFloat, timeElapsed: TimeInterval) {
        //override method should manage the visual components of the element
        //called by GameView object once per frame
    }
    
    func move(heightOnScreen: CGFloat, timeElapsed: TimeInterval) {
        //override method should handle movement of the element
        //called by GameView object once per frame
    }
    
    func prepareToDestruct() {
        //override method should set up element to destruct
        //called by GameView before the level destructs
    }
    
    func reloadTexture() {
        self.texture = ShapeGenerator.generateElement(screenSize: screenSize, size: textureSize, vectors: vectors, colour: colour, buildLevel: buildLevel)
    }
    
    func checkCollision(player: PlayerArrow) -> Bool {
        //checks for collision with player in level
        //called by GameView once per frame
        var hitboxCollision = false
        var point = player.rightTip
        if point.x >= positionInLevel.x-size.width && point.x <= positionInLevel.x+size.width && point.y >= positionInLevel.y-size.height && point.y <= positionInLevel.y+size.height {
            hitboxCollision = true
        }
        point = player.leftTip
        if point.x >= positionInLevel.x-size.width && point.x <= positionInLevel.x+size.width && point.y >= positionInLevel.y-size.height && point.y <= positionInLevel.y+size.height {
            hitboxCollision = true
        }
        point = player.frontTip
        if point.x >= positionInLevel.x-size.width && point.x <= positionInLevel.x+size.width && point.y >= positionInLevel.y-size.height && point.y <= positionInLevel.y+size.height {
            hitboxCollision = true
        }
        
        if hitboxCollision {
            var vectorCollision = false
            let playerVectorLeft = Vector(from: player.frontTip, to: player.leftTip)
            let playerVectorRight = Vector(from: player.frontTip, to: player.rightTip)
            for vector in vectors {
                let levelVector = Vector(from: CGPoint(x: positionInLevel.x+(vector.point1.x*size.width), y: positionInLevel.y+(vector.point1.y*size.height)), to: CGPoint(x: positionInLevel.x+(vector.point2.x*size.width), y: positionInLevel.y+(vector.point2.y*size.height)))
                if levelVector.checkIntercept(vector: playerVectorLeft) == true {
                    vectorCollision = true
                }
                if levelVector.checkIntercept(vector: playerVectorRight) == true {
                    vectorCollision = true
                }
            }
            return vectorCollision
        } else {
            return false
        }
    }
}

