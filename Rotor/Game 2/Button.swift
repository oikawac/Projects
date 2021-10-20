//
//  Button.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-18.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

protocol ButtonDelegate: class {
    func buttonPressed(_ button: Button)
    //signals to parent that button was pressed
    func buttonSelected(_ button: Button)
    //signals to parent that button was selected and will be pressed
}

import SpriteKit

class Button: SKSpriteNode {
    
    var id: String
    var hitbox: CGSize
    var buttonSprite: SKSpriteNode
    var shouldDismissOnSelect = true
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    weak var delegate: ButtonDelegate?
    
    init(texture: SKTexture, size: CGSize, colour: UIColor, hitbox: CGSize, id: String) {
        self.id = id
        self.hitbox = hitbox
        buttonSprite = SKSpriteNode(texture: texture, color: colour, size: texture.size())
        buttonSprite.colorBlendFactor = 1.0
        super.init(texture: nil, color: UIColor(), size: hitbox)
        addChild(buttonSprite)
        isUserInteractionEnabled = true
    }
    
    func touch() {
        //button touched animation
    }
    
    func detouch() {
        //button detouched animation
    }
    
    func select() {
        //button was selected
    }
    
    func dismiss() {
        //dismiss button animation
    }
    
    func activate() {
        //signals to parent that button has been pressed
        delegate?.buttonPressed(self)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if touches.count == 1 {
            let location = touches.first!.location(in: self)
            if location.x > -hitbox.width/2 && location.x < hitbox.width/2 {
                if location.y > -hitbox.height/2 && location.y < hitbox.height/2 {
                    touch()
                }
            }
        }
    }
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if touches.count == 1 {
            let location = touches.first!.location(in: self)
            if location.x < -hitbox.width/2 || location.x > hitbox.width/2 || location.y < -hitbox.height/2 || location.y > hitbox.height/2 {
                detouch()
            }
            if location.x > -hitbox.width/2 && location.x < hitbox.width/2 {
                if location.y > -hitbox.height/2 && location.y < hitbox.height/2 {
                    touch()
                }
            }
        }
    }
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if touches.count == 1 {
            let location = touches.first!.location(in: self)
            if location.x > -hitbox.width/2 && location.x < hitbox.width/2 {
                if location.y > -hitbox.height/2 && location.y < hitbox.height/2 {
                    select()
                    delegate?.buttonSelected(self)
                }
            }
        }
    }
}
