//
//  FinishLineElement.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-01.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class FinishLineElement: Element {
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override init(screenSize: CGSize, size: CGSize, vectors: [Vector], colour: UIColor, positionInLevel: CGPoint) {
        let texture = ShapeGenerator.generateFinishLine(screenSize: screenSize, size: size, colour: colour)
        super.init(screenSize: screenSize, size: size, vectors: vectors, texture: texture, colour: colour, positionInLevel: positionInLevel)
        playerWinsOnContact = true
        playerDiesOnContact = false
    }
}
