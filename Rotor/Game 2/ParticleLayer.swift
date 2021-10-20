//
//  ParticleLayer.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-30.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class ParticleLayer: SKSpriteNode {
    
    var screenSize: CGSize
    
    var playerPositionInLevel = CGPoint()
    
    var playerPositionOnScreen: CGPoint
    
    var playerTrailPoints = [CGPoint]()
    
    var playerTrailNode = SKSpriteNode()
    
    var playerTrailColour: UIColor
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, playerTrailColour: UIColor) {
        self.playerTrailColour = playerTrailColour
        self.screenSize = screenSize
        playerPositionOnScreen = CGPoint(x: 0, y: -screenSize.height*1/8)
        super.init(texture: nil, color: UIColor(), size: screenSize)
        zPosition = 1
        playerTrailNode.position = playerPositionOnScreen
        playerTrailPoints.append(playerPositionOnScreen)
        playerTrailPoints.append(CGPoint(x: 0, y: -screenSize.height/2))
        updatePlayerTrailNode()
        addChild(playerTrailNode)
    }
    
    func update(_ timeElapsed: TimeInterval) {
        //manages player trail and updates player trail texture
        if playerTrailPoints.count < 500 {
            playerTrailPoints.append(playerPositionInLevel)
        } else {
            playerTrailPoints.remove(at: 0)
            playerTrailPoints.append(playerPositionInLevel)
        }
        updatePlayerTrailNode()
    }
    private func updatePlayerTrailNode() {
        //updates player trail texture
        playerTrailNode.position = playerPositionOnScreen
        if playerTrailPoints.count > 2 {
            playerTrailNode.texture = ShapeGenerator.generateTrail(points: playerTrailPoints, trailColour: playerTrailColour, trailWidth: screenSize.width/200)
            playerTrailNode.size = playerTrailNode.texture!.size()
            var leftMostPoint = playerTrailPoints[0]
            var bottomMostPoint = playerTrailPoints[0]
            for point in playerTrailPoints {
                if point.x < leftMostPoint.x {
                    leftMostPoint = point
                }
                if point.y < bottomMostPoint.y {
                    bottomMostPoint = point
                }
            }
            playerTrailNode.anchorPoint = CGPoint(x: (playerTrailPoints.last!.x+(screenSize.width/200)/2-leftMostPoint.x)/playerTrailNode.size.width, y: (playerTrailPoints.last!.y+(screenSize.width/200)/2-bottomMostPoint.y)/playerTrailNode.size.height)
        }
    }
}
