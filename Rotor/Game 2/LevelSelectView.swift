//
//  LevelSelectView.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-04.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

protocol LevelSelectViewDelegate: class {
    func levelSelectViewSelectedLevel(_ levelSelectView: LevelSelectView, level: Int)
    func levelSelectViewBlendedColourChanged(_ levelSelectView: LevelSelectView, backgroundColour: UIColor, primaryColour: UIColor)
}


class LevelSelectView: SKSpriteNode {
    
    var screenSize: CGSize
    
    var lastTouchPoint: CGPoint?
    var lastTouch: UITouch?
    var velocity: CGFloat = 0
    
    var levelPreviewArray = [SKSpriteNode]()
    var verticalDividerArray = [SKSpriteNode]()
    var playerArrowArray = [SKSpriteNode]()
    var playerTrailArray = [SKSpriteNode]()
    var lockArray = [SKSpriteNode]()
    
    var levelSelected = false
    
    weak var delegate: LevelSelectViewDelegate?
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize) {
        self.screenSize = screenSize
        super.init(texture: nil, color: UIColor(), size: CGSize(width: screenSize.width*3, height: screenSize.height*0.85))
        isUserInteractionEnabled = true
        anchorPoint = CGPoint(x: 0, y: 0.5)
        generateLevelPreviews()
        generateVerticalDividers()
    }
    
    func generateVerticalDividers() {
        let path = CGMutablePath()
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: 0, y: screenSize.height*0.85))
        let shape = SKShapeNode(path: path)
        shape.lineWidth = screenSize.width/200
        shape.strokeColor = UIColor.white
        let texture = SKView().texture(from: shape)
        for x in 1...11 {
            let sprite = SKSpriteNode(texture: texture, color: UIColor.white, size: texture!.size())
            sprite.colorBlendFactor = 1.0
            sprite.position = CGPoint(x: CGFloat(x)*screenSize.width/4, y: 0)
            addChild(sprite)
            verticalDividerArray.append(sprite)
        }
    }
    
    func generateLevelPreviews() {
        for x in 0...11 {
            var colourScheme = (backgroundColour: UIColor.darkGray, primaryColour: UIColor.white, secondaryColour: UIColor.white)
            if SettingsManager.getLevelUnlocked(x) {
                colourScheme = LevelManager.getColourScheme(for: x)
            }
            let sprite = SKSpriteNode(texture: nil, color: colourScheme.backgroundColour, size: CGSize(width: screenSize.width/4, height: screenSize.height*2))
            let playerTexture = ShapeGenerator.generatePlayerArrow(size: CGSize(width: screenSize.width/16, height: screenSize.width/10), fillColour: UIColor.white)
            let playerArrow = SKSpriteNode(texture: playerTexture, color: colourScheme.primaryColour, size: playerTexture.size())
            playerArrow.colorBlendFactor = 1.0
            let path = CGMutablePath()
            path.move(to: CGPoint())
            path.addLine(to: CGPoint(x: 0, y: screenSize.height))
            let shape = SKShapeNode(path: path)
            shape.lineWidth = screenSize.width/200
            shape.strokeColor = UIColor.white
            let trailTexture = SKView().texture(from: shape)
            let trail = SKSpriteNode(texture: trailTexture, color: colourScheme.primaryColour, size: trailTexture!.size())
            trail.anchorPoint = CGPoint(x: 0.5, y: 1)
            trail.colorBlendFactor = 1.0
            if SettingsManager.getLevelUnlocked(x) {
                playerArrow.position = CGPoint(x: 0, y: size.height/2)
                trail.position = CGPoint(x: 0, y: size.height/2)
            } else {
                playerArrow.position = CGPoint(x: 0, y: size.height/3)
                trail.position = CGPoint(x: 0, y: size.height/3)
                let lockTexture = ShapeGenerator.generateLock(width: screenSize.width/8, height: screenSize.width/8, outlineWidth: screenSize.width/200, fillColour: UIColor(), outlineColour: UIColor.white)
                let lock = SKSpriteNode(texture: lockTexture, color: UIColor.white, size: lockTexture.size())
                lock.colorBlendFactor = 1.0
                lock.position = CGPoint(x: 0, y: size.height/2)
                lockArray.append(lock)
                sprite.addChild(lock)
            }
            playerTrailArray.append(trail)
            sprite.addChild(trail)
            playerArrowArray.append(playerArrow)
            sprite.addChild(playerArrow)
            sprite.position = CGPoint(x: CGFloat(x)*screenSize.width/4+screenSize.width/8, y: -screenSize.height/2)
            sprite.zPosition = -1
            levelPreviewArray.append(sprite)
            addChild(sprite)
        }
    }
    
    func update() {
        if lastTouchPoint != nil && lastTouch != nil && !levelSelected {
            let level = Int(floor(lastTouchPoint!.x/(screenSize.width/4)))
            let naturalFactor = (screenSize.height/10 - levelPreviewArray[level].position.y)/(screenSize.height/10 + screenSize.height/2)
            if SettingsManager.getLevelUnlocked(level) {
                levelPreviewArray[level].position.y += 3*lastTouch!.force
                for x in 0...11 {
                    let levelPreview = levelPreviewArray[x]
                    var naturalColour = UIColor.darkGray
                    if SettingsManager.getLevelUnlocked(x) {
                        naturalColour = LevelManager.getColourScheme(for: x).backgroundColour
                    }
                    let selectColour = LevelManager.getColourScheme(for: level).backgroundColour
                    let blendedColour = blendColours(naturalColour: naturalColour, selectColour: selectColour, naturalColourBlendFactor: naturalFactor)
                    levelPreview.color = blendedColour
                }
                
                let blendedColour = blendColours(naturalColour: UIColor.white, selectColour: LevelManager.getColourScheme(for: level).primaryColour, naturalColourBlendFactor: naturalFactor)
                for x in 0...10 {
                    let verticalDivider = verticalDividerArray[x]
                    verticalDivider.color = blendedColour
                }
                if lockArray.count > 0 {
                    for x in 0...lockArray.count-1 {
                        let lock = lockArray[x]
                        lock.color = blendedColour
                    }
                }
                for x in 0...11 {
                    let playerArrow = playerArrowArray[x]
                    var naturalColour = UIColor.white
                    if SettingsManager.getLevelUnlocked(x) {
                        naturalColour = LevelManager.getColourScheme(for: x).primaryColour
                    }
                    let selectColour = LevelManager.getColourScheme(for: level).primaryColour
                    let blendedColour = blendColours(naturalColour: naturalColour, selectColour: selectColour, naturalColourBlendFactor: naturalFactor)
                    playerArrow.color = blendedColour
                }
                for x in 0...11 {
                    let playerTrail = playerTrailArray[x]
                    var naturalColour = UIColor.white
                    if SettingsManager.getLevelUnlocked(x) {
                        naturalColour = LevelManager.getColourScheme(for: x).primaryColour
                    }
                    let selectColour = LevelManager.getColourScheme(for: level).primaryColour
                    let blendedColour = blendColours(naturalColour: naturalColour, selectColour: selectColour, naturalColourBlendFactor: naturalFactor)
                    playerTrail.color = blendedColour
                }
                let selectColourBackground = LevelManager.getColourScheme(for: level).backgroundColour
                let selectColourPrimary = LevelManager.getColourScheme(for: level).primaryColour
                let blendedBackgroundColour = blendColours(naturalColour: UIColor.darkGray, selectColour: selectColourBackground, naturalColourBlendFactor: naturalFactor)
                let blendedPrimaryColour = blendColours(naturalColour: UIColor.white, selectColour: selectColourPrimary, naturalColourBlendFactor: naturalFactor)
                delegate?.levelSelectViewBlendedColourChanged(self, backgroundColour: blendedBackgroundColour, primaryColour: blendedPrimaryColour)
            }
            if levelPreviewArray[level].position.y > screenSize.height/10 {
                levelSelected = true
                self.run(SKAction.sequence([SKAction.move(to: CGPoint(x: -levelPreviewArray[level].position.x+screenSize.width/2, y: position.y), duration: 0.25), SKAction.run(selectLevel)]))
            } else if levelPreviewArray[level].position.y < -screenSize.height/2 {
                levelPreviewArray[level].position.y = -screenSize.height/2
            }
        }
    }
    
    func blendColours(naturalColour: UIColor, selectColour: UIColor, naturalColourBlendFactor: CGFloat) -> UIColor {
        let naturalFactor = naturalColourBlendFactor
        let selectFactor = 1-naturalFactor
        var naturalR: CGFloat = 0
        var naturalG: CGFloat = 0
        var naturalB: CGFloat = 0
        var naturalA: CGFloat = 0
        var selectR: CGFloat = 0
        var selectG: CGFloat = 0
        var selectB: CGFloat = 0
        var selectA: CGFloat = 0
        naturalColour.getRed(&naturalR, green: &naturalG, blue: &naturalB, alpha: &naturalA)
        selectColour.getRed(&selectR, green: &selectG, blue: &selectB, alpha: &selectA)
        let r = naturalFactor * naturalR + selectFactor * selectR
        let g = naturalFactor * naturalG + selectFactor * selectG
        let b = naturalFactor * naturalB + selectFactor * selectB
        return UIColor(red: r, green: g, blue: b, alpha: 1.0)
    }
    
    func selectLevel() {
        var level = 0
        for x in 0...11 {
            if levelPreviewArray[x].position.y > screenSize.height/10 {
                level = x
            }
        }
        delegate?.levelSelectViewSelectedLevel(self, level: level)
    }
    
    func glide() {
        position.x += velocity
        if position.x < -size.width*2/3 {
            position.x = -size.width*2/3
        } else if position.x > 0 {
            position.x = 0
        }
        velocity *= 0.95
        if abs(velocity) > 0.2 && lastTouchPoint == nil {
            self.run(SKAction.sequence([SKAction.wait(forDuration: 0.01), SKAction.run(glide)]))
        } else {
            velocity = 0
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if !levelSelected {
            lastTouch = touches.first!
            lastTouchPoint = lastTouch!.location(in: self)
            velocity = 0
        }
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if !levelSelected {
            if let last = lastTouchPoint {
                let distanceTraveled = touches.first!.location(in: self).x - last.x
                position.x += distanceTraveled
                velocity = distanceTraveled
            }
            if position.x < -size.width*2/3 {
                position.x = -size.width*2/3
            } else if position.x > 0 {
                position.x = 0
            }
        }
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if !levelSelected {
            lastTouchPoint = nil
            lastTouch = nil
            glide()
            for levelPreview in levelPreviewArray {
                levelPreview.run(SKAction.move(to: CGPoint(x: levelPreview.position.x, y: -screenSize.height/2), duration: 0.2))
            }
            for x in 0...11 {
                let levelPreview = levelPreviewArray[x]
                levelPreview.color = UIColor.darkGray
                if SettingsManager.getLevelUnlocked(x) {
                    levelPreview.color = LevelManager.getColourScheme(for: x).backgroundColour
                }
                delegate?.levelSelectViewBlendedColourChanged(self, backgroundColour: UIColor.darkGray, primaryColour: UIColor.white)
            }
            for x in 0...10 {
                let verticalDivider = verticalDividerArray[x]
                verticalDivider.color = UIColor.white
            }
            if lockArray.count > 0 {
                for x in 0...lockArray.count-1 {
                    let lock = lockArray[x]
                    lock.color = UIColor.white
                }
            }
            for x in 0...11 {
                let playerArrow = playerArrowArray[x]
                var naturalColour = UIColor.white
                if SettingsManager.getLevelUnlocked(x) {
                    naturalColour = LevelManager.getColourScheme(for: x).primaryColour
                }
                playerArrow.color = naturalColour
            }
            for x in 0...11 {
                let playerTrail = playerTrailArray[x]
                var naturalColour = UIColor.white
                if SettingsManager.getLevelUnlocked(x) {
                    naturalColour = LevelManager.getColourScheme(for: x).primaryColour
                }
                playerTrail.color = naturalColour
            }
        }
    }
}
