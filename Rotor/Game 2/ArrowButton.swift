//
//  ArrowButton.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-02.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class ArrowButton: Button {
    
    var direction: String
    var screenSize: CGSize
    var colour: UIColor
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(size: CGSize, colour: UIColor, screenSize: CGSize, direction: String, id: String) {
        self.colour = colour
        self.direction = direction
        self.screenSize = screenSize
        let texture = ShapeGenerator.generateArrowButton(width: size.width, height: size.height, direction: direction, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
        super.init(texture: texture, size: texture.size(), colour: colour, hitbox: CGSize(width: screenSize.width*2, height: screenSize.height*0.15), id: id)
        shouldDismissOnSelect = false
    }
    
    override func touch() {
        buttonSprite.texture = ShapeGenerator.generateArrowButton(width: buttonSprite.size.width, height: buttonSprite.size.height, direction: direction, outlineWidth: screenSize.width/200, fillColour: UIColor.white, outlineColour: UIColor.white)
    }
    
    override func detouch() {
        buttonSprite.texture = ShapeGenerator.generateArrowButton(width: buttonSprite.size.width, height: buttonSprite.size.height, direction: direction, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
    }
    
    override func select() {
        activate()
    }
    
    override func dismiss() {
        
    }
}
