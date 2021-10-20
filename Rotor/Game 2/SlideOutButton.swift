//
//  SlideOutButton.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-15.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class SlideOutButton: Button {
    
    var text: String
    var screenSize: CGSize
    var colour: UIColor
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(size: CGSize, colour: UIColor, screenSize: CGSize, height: CGFloat, text: String, id: String) {
        self.colour = colour
        self.text = text
        self.screenSize = screenSize
        let texture = ShapeGenerator.generateSlideButton(width: size.width, height: size.height, text: text, textColour: UIColor.white, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
        super.init(texture: texture, size: texture.size(), colour: colour, hitbox: texture.size(), id: id)
        position = CGPoint(x: -size.width, y: height)
        self.run(SKAction.move(to: CGPoint(x: 0, y: height), duration: 0.75))
    }
    
    override func touch() {
        buttonSprite.texture = ShapeGenerator.generateSlideButton(width: buttonSprite.size.width, height: buttonSprite.size.height, text: text, textColour: UIColor.white, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
        self.run(SKAction.move(to: CGPoint(x: size.width/6, y: position.y), duration: 0.1))
    }
    
    override func detouch() {
        buttonSprite.texture = ShapeGenerator.generateSlideButton(width: buttonSprite.size.width, height: buttonSprite.size.height, text: text, textColour: UIColor.white, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
        self.run(SKAction.move(to: CGPoint(x: 0, y: position.y), duration: 0.1))
    }
    
    override func select() {
        if shouldDismissOnSelect {
            self.run(SKAction.sequence([SKAction.move(to: CGPoint(x: -size.width, y: position.y), duration: 0.2), SKAction.run(activate)]))
        } else {
            detouch()
            activate()
        }
    }
    
    override func dismiss() {
        self.run(SKAction.move(to: CGPoint(x: -size.width, y: position.y), duration: 0.2))
    }
}
