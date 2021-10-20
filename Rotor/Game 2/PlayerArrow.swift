//
//  PlayerArrow.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-27.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class PlayerArrow: SKSpriteNode {
    
    var rotationIncrement: CGFloat = 0
    var rotationDirection: CGFloat = 1.0
    
    var levelPosition = CGPoint(x: 0, y: 0)
    
    var velocity: CGFloat = 0
    
    var screenSize: CGSize
    
    var frontTip = CGPoint()
    var leftTip = CGPoint()
    var rightTip = CGPoint()
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, colour: UIColor) {
        self.screenSize = screenSize
        let playerArrowSize = CGSize(width: screenSize.width/16, height: screenSize.width/10)
        let texture = ShapeGenerator.generatePlayerArrow(size: playerArrowSize, fillColour: colour)
        super.init(texture: texture, color: UIColor(), size: texture.size())
        position = CGPoint(x: 0, y: -screenSize.height*1/8)
        zPosition = 2
    }
    
    func update(_ timeElapsed: TimeInterval) {
        //moves player in level and updates vector points
        levelPosition.x -= sin(zRotation)*screenSize.width*velocity
        position.x = levelPosition.x
        levelPosition.y += cos(zRotation)*screenSize.width*velocity
        frontTip = CGPoint(x: levelPosition.x-sin(zRotation)*size.height/2, y: levelPosition.y+cos(zRotation)*size.height/2)
        rightTip = CGPoint(x: levelPosition.x+sin(zRotation)*size.height/2+sin(((zRotation*180/CGFloat.pi)+90)/180*CGFloat.pi)*size.width/2, y: levelPosition.y-cos(zRotation)*size.height/2-cos(((zRotation*180/CGFloat.pi)+90)/180*CGFloat.pi)*size.width/2)
        leftTip = CGPoint(x: levelPosition.x+sin(zRotation)*size.height/2+sin(((zRotation*180/CGFloat.pi)-90)/180*CGFloat.pi)*size.width/2, y: levelPosition.y-cos(zRotation)*size.height/2-cos(((zRotation*180/CGFloat.pi)-90)/180*CGFloat.pi)*size.width/2)

    }
}
