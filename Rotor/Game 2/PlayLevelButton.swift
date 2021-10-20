//
//  PlayLevelButton.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-04.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class PlayLevelButton: Button {
    
    var screenSize: CGSize
    var radius: CGFloat
    var colour: UIColor
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(radius: CGFloat, colour: UIColor, screenSize: CGSize, id: String) {
        self.colour = colour
        self.screenSize = screenSize
        self.radius = radius
        let texture = ShapeGenerator.generatePlayLevelButton(radius: radius, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
        super.init(texture: texture, size: texture.size(), colour: colour, hitbox: texture.size(), id: id)
        shouldDismissOnSelect = false
    }
    
    override func touch() {
        buttonSprite.texture = ShapeGenerator.generatePlayLevelButton(radius: radius, outlineWidth: screenSize.width/200, fillColour: UIColor.white, outlineColour: UIColor.white)

    }
    
    override func detouch() {
        buttonSprite.texture = ShapeGenerator.generatePlayLevelButton(radius: radius, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
    }
    
    override func select() {
        activate()
    }
    
    override func dismiss() {
    }
}
