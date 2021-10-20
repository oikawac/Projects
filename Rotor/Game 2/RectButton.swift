//
//  RectButton.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-05.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class RectButton: Button {
    
    var screenSize: CGSize
    var colour: UIColor
    var text: String
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(size: CGSize, text: String, colour: UIColor, screenSize: CGSize, id: String) {
        self.text = text
        self.colour = colour
        self.screenSize = screenSize
        let texture = ShapeGenerator.generateRectButton(size: size, text: text, outlineWidth: screenSize.width/200, textColour: UIColor.white, outlineColour: UIColor.white)
        super.init(texture: texture, size: texture.size(), colour: colour, hitbox: texture.size(), id: id)
    }
    
    override func touch() {
        buttonSprite.texture = ShapeGenerator.generateRectButton(size: size, text: text, outlineWidth: screenSize.width/100, textColour: UIColor.white, outlineColour: UIColor.white)
        self.run(SKAction.scale(to: 1.1, duration: 0.05))
    }
    
    override func detouch() {
        buttonSprite.texture = ShapeGenerator.generateRectButton(size: size, text: text, outlineWidth: screenSize.width/200, textColour: UIColor.white, outlineColour: UIColor.white)
        self.run(SKAction.scale(to: 1.0, duration: 0.05))
    }
    
    override func select() {
        if shouldDismissOnSelect {
            self.run(SKAction.fadeOut(withDuration: 0.1))
            self.run(SKAction.sequence([SKAction.scale(to: 0, duration: 0.15), SKAction.run(activate)]))
        } else {
            detouch()
            activate()
        }
    }
    
    override func dismiss() {
        self.run(SKAction.fadeOut(withDuration: 0.1))
        self.run(SKAction.scale(to: 0, duration: 0.15))
    }
}
