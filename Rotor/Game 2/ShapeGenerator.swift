//
//  ShapeGenerator.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-02.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class ShapeGenerator {
    
    static func generateCircle(ofRadius radius: CGFloat, outlineWidth: CGFloat, fillColour: UIColor, outlineColour: UIColor) -> SKTexture {
        let shape = SKShapeNode(circleOfRadius: radius)
        shape.lineWidth = outlineWidth
        shape.fillColor = fillColour
        shape.strokeColor = outlineColour
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateRectangle(width: CGFloat, height: CGFloat, cornerRadius: CGFloat, outlineWidth: CGFloat, fillColour: UIColor, outlineColour: UIColor) -> SKTexture {
        let shape = SKShapeNode(rectOf: CGSize(width: width, height: height), cornerRadius: cornerRadius)
        shape.lineWidth = outlineWidth
        shape.fillColor = fillColour
        shape.strokeColor = outlineColour
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateLock(width: CGFloat, height: CGFloat, outlineWidth: CGFloat, fillColour: UIColor, outlineColour: UIColor) -> SKTexture {
        var path = UIBezierPath()
        path.move(to: CGPoint(x: -width/2, y: -height/2))
        path.addLine(to: CGPoint(x: -width/2, y: 0))
        path.addLine(to: CGPoint(x: -width*4/10, y: 0))
        path.addLine(to: CGPoint(x: -width*4/10, y: width*1/10))
        path.addArc(withCenter: CGPoint(x: 0, y: width*1/10), radius: width*4/10, startAngle: CGFloat.pi, endAngle: 0, clockwise: false)
        path.addLine(to: CGPoint(x: width*4/10, y: 0))
        path.addLine(to: CGPoint(x: width/2, y: 0))
        path.addLine(to: CGPoint(x: width/2, y: -height/2))
        path.addLine(to: CGPoint(x: -width/2, y: -height/2))
        let shape = SKShapeNode(path: path.cgPath)
        shape.lineWidth = outlineWidth
        shape.fillColor = fillColour
        shape.strokeColor = outlineColour
        path = UIBezierPath()
        path.move(to: CGPoint(x: -width*2/10, y: 0))
        path.addLine(to: CGPoint(x: -width*2/10, y: width*1/10))
        path.addArc(withCenter: CGPoint(x: 0, y: width*1/10), radius: width*2/10, startAngle: CGFloat.pi, endAngle: 0, clockwise: false)
        path.addLine(to: CGPoint(x: width*2/10, y: 0))
        path.addLine(to: CGPoint(x: -width*2/10, y: 0))
        let interiorShape = SKShapeNode(path: path.cgPath)
        interiorShape.lineWidth = outlineWidth
        interiorShape.fillColor = UIColor()
        interiorShape.strokeColor = outlineColour
        let interiorTexture = SKView().texture(from: interiorShape)
        let sprite = SKSpriteNode(texture: interiorTexture, color: UIColor(), size: interiorTexture!.size())
        sprite.anchorPoint = CGPoint(x: 0.5, y: 0)
        shape.addChild(sprite)
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateSlideButton(width: CGFloat, height: CGFloat, text: String, textColour: UIColor, outlineWidth: CGFloat, fillColour: UIColor, outlineColour: UIColor) -> SKTexture {
        let path = CGMutablePath()
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: 0+width, y: 0))
        path.addLine(to: CGPoint(x: 0+width-(height/sqrt(3)), y: 0-height))
        path.addLine(to: CGPoint(x: 0, y: 0-height))
        path.addLine(to: CGPoint(x: 0, y: 0))
        let shape = SKShapeNode(path: path)
        shape.lineWidth = outlineWidth
        shape.fillColor = fillColour
        shape.strokeColor = outlineColour
        let label = SKLabelNode(text: "Pgy!")
        label.fontName = "HemiHeadRg-BoldItalic"
        label.fontColor = textColour
        label.horizontalAlignmentMode = .right
        label.verticalAlignmentMode = .center
        label.fontSize = 10
        let scalingFactor = (shape.frame.height*0.6) / label.frame.height
        label.fontSize *= scalingFactor
        label.position = CGPoint(x: width-(height/sqrt(3)), y: -height/2)
        label.text = text
        shape.addChild(label)
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateArrowButton(width: CGFloat, height: CGFloat, direction: String, outlineWidth: CGFloat, fillColour: UIColor, outlineColour: UIColor) -> SKTexture {
        let path = CGMutablePath()
        if direction == "left" {
            path.move(to: CGPoint(x: -width/2, y: 0))
            path.addLine(to: CGPoint(x: 0, y: -height/2))
            path.addLine(to: CGPoint(x: width/12, y: -height*5/12))
            path.addLine(to: CGPoint(x: -width*2/6, y: 0))
            path.addLine(to: CGPoint(x: width/12, y: height*5/12))
            path.addLine(to: CGPoint(x: 0, y: height/2))
            path.addLine(to: CGPoint(x: -width/2, y: 0))
        } else {
            path.move(to: CGPoint(x: width/2, y: 0))
            path.addLine(to: CGPoint(x: 0, y: -height/2))
            path.addLine(to: CGPoint(x: -width/12, y: -height*5/12))
            path.addLine(to: CGPoint(x: width*2/6, y: 0))
            path.addLine(to: CGPoint(x: -width/12, y: height*5/12))
            path.addLine(to: CGPoint(x: 0, y: height/2))
            path.addLine(to: CGPoint(x: width/2, y: 0))
        }
        let shape = SKShapeNode(path: path)
        shape.lineWidth = outlineWidth
        shape.fillColor = fillColour
        shape.strokeColor = outlineColour
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generatePlayLevelButton(radius: CGFloat, outlineWidth: CGFloat, fillColour: UIColor, outlineColour: UIColor) -> SKTexture {
        let shape = SKShapeNode(circleOfRadius: radius)
        shape.lineWidth = outlineWidth
        shape.fillColor = UIColor()
        shape.strokeColor = outlineColour
        let path = CGMutablePath()
        path.move(to: CGPoint(x: cos(0/180*CGFloat.pi)*radius/2, y: sin(0/180*CGFloat.pi)*radius/2))
        path.addLine(to: CGPoint(x: cos(120/180*CGFloat.pi)*radius/2, y: sin(120/180*CGFloat.pi)*radius/2))
        path.addLine(to: CGPoint(x: cos(240/180*CGFloat.pi)*radius/2, y: sin(240/180*CGFloat.pi)*radius/2))
        path.addLine(to: CGPoint(x: cos(0/180*CGFloat.pi)*radius/2, y: sin(0/180*CGFloat.pi)*radius/2))
        let triangleShape = SKShapeNode(path: path)
        triangleShape.lineWidth = outlineWidth
        triangleShape.fillColor = fillColour
        triangleShape.strokeColor = outlineColour
        shape.addChild(triangleShape)
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateRectButton(size: CGSize, text: String, outlineWidth: CGFloat, textColour: UIColor, outlineColour: UIColor) -> SKTexture {
        let rectTexture = generateRectangle(width: size.width, height: size.height, cornerRadius: size.width*0.05, outlineWidth: outlineWidth, fillColour: UIColor(), outlineColour: outlineColour)
        let rect = SKSpriteNode(texture: rectTexture, color: UIColor(), size: rectTexture.size())
        let label = SKLabelNode(text: "Pgy!")
        label.fontName = "HemiHeadRg-BoldItalic"
        label.fontColor = textColour
        label.horizontalAlignmentMode = .center
        label.verticalAlignmentMode = .center
        label.fontSize = 10
        let scalingFactor = (size.height*0.6) / label.frame.height
        label.fontSize *= scalingFactor
        label.text = text
        rect.addChild(label)
        let texture = SKView().texture(from: rect)
        return texture!
    }
    
    static func generatePlayerArrow(size: CGSize, fillColour: UIColor) -> SKTexture {
        let path = CGMutablePath()
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: -size.width*1/2, y: -size.height))
        path.addLine(to: CGPoint(x: 0, y: -size.height*3/4))
        path.addLine(to: CGPoint(x: size.width*1/2, y: -size.height))
        path.addLine(to: CGPoint(x: 0, y: 0))
        let shape = SKShapeNode(path: path)
        shape.lineWidth = 1
        shape.fillColor = fillColour
        shape.strokeColor = fillColour
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateTrail(points: [CGPoint], trailColour: UIColor, trailWidth: CGFloat) -> SKTexture {
        let path = CGMutablePath()
        path.move(to: points[0])
        for x in 1...points.count-1 {
            path.addLine(to: points[x])
        }
        let shape = SKShapeNode(path: path)
        shape.lineWidth = trailWidth
        shape.strokeColor = trailColour
        let texture = SKView().texture(from: shape)
        return texture!
    }
    
    static func generateElement(screenSize: CGSize, size: CGSize, vectors: [Vector], colour: UIColor, buildLevel: Int) -> SKTexture {
        let sprite = SKSpriteNode(texture: nil, color: UIColor(), size: size)
        if buildLevel < 4 {
            for vector in vectors {
                let dot = SKSpriteNode(texture: nil, color: colour, size: CGSize(width: screenSize.width/200, height: screenSize.width/200))
                dot.position = CGPoint(x: vector.point2.x*size.width, y: vector.point2.y*size.height)
                sprite.addChild(dot)
            }
        }
        let path = CGMutablePath()
        var lineDrawProbability = 0
        if buildLevel == 1 {
            lineDrawProbability = 25
        } else if buildLevel == 2 {
            lineDrawProbability = 50
        } else if buildLevel == 3 {
            lineDrawProbability = 75
        } else if buildLevel == 4 {
            lineDrawProbability = 100
        } else if buildLevel == 5 {
            lineDrawProbability = 100
        }
        path.move(to: CGPoint(x: vectors[0].point1.x*size.width, y: vectors[0].point1.y*size.height))
        for vector in vectors {
            if Int(arc4random_uniform(UInt32(100))) < lineDrawProbability {
                path.addLine(to: CGPoint(x: vector.point2.x*size.width, y: vector.point2.y*size.height))
            } else {
                path.move(to: CGPoint(x: vector.point2.x*size.width, y: vector.point2.y*size.height))
            }
        }
        let shape = SKShapeNode(path: path)
        shape.lineWidth = screenSize.width/200
        shape.strokeColor = colour
        if buildLevel == 5 {
            shape.fillColor = colour
        } else {
            shape.fillColor = UIColor()
        }
        sprite.addChild(shape)
        let texture = SKView().texture(from: sprite)
        return texture!
    }
    
    static func generateFinishLine(screenSize: CGSize, size: CGSize, colour: UIColor) -> SKTexture {
        let sprite = SKSpriteNode(texture: nil, color: UIColor(), size: size)
        let path = CGMutablePath()
        path.move(to: CGPoint(x: -size.width/2, y: -size.height/2))
        path.addLine(to: CGPoint(x: size.width/2, y: -size.height/2))
        path.move(to: CGPoint(x: size.width/2, y: size.height/2))
        path.addLine(to: CGPoint(x: -size.width/2, y: size.height/2))
        let shape = SKShapeNode(path: path)
        shape.lineWidth = screenSize.width/200
        shape.strokeColor = colour
        sprite.addChild(shape)
        let label = SKLabelNode(text: "end of level")
        label.fontName = "HemiHeadRg-BoldItalic"
        label.fontColor = colour
        label.horizontalAlignmentMode = .center
        label.verticalAlignmentMode = .center
        label.fontSize = 10
        let scalingFactor = (sprite.size.height*0.6) / label.frame.height
        label.fontSize *= scalingFactor
        label.position = CGPoint(x: 0, y: 0)
        sprite.addChild(label)
        let texture = SKView().texture(from: sprite)
        return texture!
    }
}
