//
//  Slider.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-08-10.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

protocol SliderDelegate: class {
    func sliderPressed(_ slider: Slider, value: CGFloat)
    //signals to parent that button was pressed
    func sliderReleased(_ slider: Slider, value: CGFloat)
    //signals to parent that button was selected and will be pressed
}

import SpriteKit

class Slider: SKSpriteNode {
    
    var id: String
    
    var sliderBar: SKSpriteNode
    var sliderCircle: SKSpriteNode
    
    weak var delegate: SliderDelegate?
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, length: CGFloat, radius: CGFloat, id: String) {
        self.id = id
        let sliderBarPath = CGMutablePath()
        sliderBarPath.move(to: CGPoint())
        sliderBarPath.addLine(to: CGPoint(x: length, y: 0))
        let sliderBarShape = SKShapeNode(path: sliderBarPath)
        sliderBarShape.lineWidth = screenSize.width/200
        let sliderBarTexture = SKView().texture(from: sliderBarShape)
        sliderBar = SKSpriteNode(texture: sliderBarTexture, color: UIColor(), size: sliderBarTexture!.size())
        let sliderCircleTexture = ShapeGenerator.generateCircle(ofRadius: radius, outlineWidth: screenSize.width/200, fillColour: UIColor.white, outlineColour: UIColor.white)
        sliderCircle = SKSpriteNode(texture: sliderCircleTexture, color: UIColor(), size: sliderCircleTexture.size())
        super.init(texture: nil, color: UIColor(), size: CGSize(width: length, height: radius*2))
        addChild(sliderBar)
        addChild(sliderCircle)
        isUserInteractionEnabled = true
    }
    
    func setValue(_ value: CGFloat) {
        sliderCircle.position.x = value*size.width
    }
    func getValue() -> CGFloat {
        return sliderCircle.position.x/size.width
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if touches.count == 1 {
            let location = touches.first!.location(in: self)
            if location.x > -size.width/2 && location.x < size.width/2 {
                if location.y > -size.height/2 && location.y < size.height/2 {
                    delegate?.sliderPressed(self, value: location.x/size.width)
                }
            }
        }
    }
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if touches.count == 1 {
            let location = touches.first!.location(in: self)
            if location.x > -size.width/2 && location.x < size.width/2 {
                sliderCircle.position.x = location.x
                delegate?.sliderPressed(self, value: sliderCircle.position.x/size.width)
            }
        }
    }
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if touches.count == 1 {
            delegate?.sliderReleased(self, value: sliderCircle.position.x/size.width)
        }
    }
}
