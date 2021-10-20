//
//  GameView.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-19.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

protocol GameViewDelegate: class {
    func gameViewComplete(_ gameView: GameView)
    func gameViewWillComplete(_ gameView: GameView)
}

class GameView: SKSpriteNode {
    
    var playerArrow: PlayerArrow
    
    var particleLayer: ParticleLayer
    
    var touch: UITouch?
    
    var levelElements = [Element]()
    
    var levelNumber: Int
    
    var levelDestructing = false
    var levelHasBeenDestructingFor: TimeInterval?
    
    var playerWon = false
    
    weak var delegate: GameViewDelegate?

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, levelNumber: Int) {
        self.levelNumber = levelNumber
        playerArrow = PlayerArrow(screenSize: screenSize, colour: LevelManager.getColourScheme(for: levelNumber).primaryColour)
        playerArrow.velocity = LevelManager.getVelocity(for: levelNumber, screenSize: screenSize)
        playerArrow.rotationIncrement = (360/((CGFloat.pi*(screenSize.width/15))/playerArrow.velocity))/CGFloat.pi*180
        particleLayer = ParticleLayer(screenSize: screenSize, playerTrailColour: LevelManager.getColourScheme(for: levelNumber).primaryColour)
        super.init(texture: nil, color: LevelManager.getColourScheme(for: levelNumber).backgroundColour, size: CGSize(width: screenSize.width, height: screenSize.height))
        position = CGPoint(x: screenSize.width/2, y: screenSize.height/2)
        isUserInteractionEnabled = true
        addChild(playerArrow)
        addChild(particleLayer)
        let elements = LevelManager.loadElements(forLevel: levelNumber, screenSize: size)
        for element in elements {
            addLevelElement(element)
        }
    }
    
    func update(_ timeElapsed: TimeInterval) {
        //updates level elements, player, particles and checks win/lose conditions
        if playerWon {
            SettingsManager.setLatestPercentage(1.0)
            playerWon = false
            var finishLine: Element?
            if levelElements.count > 0 {
                for x in 0...levelElements.count-1 {
                    let element = levelElements[x]
                    if !element.playerWinsOnContact {
                        element.removeFromParent()
                    } else {
                        finishLine = element
                    }
                }
            }
            levelElements = [Element]()
            if let finish = finishLine {
                levelElements.append(finish)
            }
            playerArrow.run(SKAction.fadeOut(withDuration: 1.0))
            particleLayer.alpha = 0
        }
        if playerArrow.alpha == 0 {
            delegate?.gameViewComplete(self)
        }
        if !levelDestructing {
            playerArrow.update(timeElapsed)
            if touch != nil {
                if let touchForce = touch?.force {
                    playerArrow.zRotation += playerArrow.rotationIncrement*(touchForce/2)/180*CGFloat.pi*playerArrow.rotationDirection
                } else {
                    playerArrow.zRotation += playerArrow.rotationIncrement/180*CGFloat.pi*playerArrow.rotationDirection
                }
            }
            particleLayer.playerPositionInLevel = playerArrow.levelPosition
            particleLayer.playerPositionOnScreen = playerArrow.position
            particleLayer.update(timeElapsed)
        } else {
            if levelHasBeenDestructingFor != nil {
                levelHasBeenDestructingFor! += timeElapsed
                if levelHasBeenDestructingFor! > 1.0 {
                    delegate?.gameViewComplete(self)
                }
            }
        }
        updateLevelElements(timeElapsed)
    }
    
    func addLevelElement(_ element: Element) {
        levelElements.append(element)
        element.position.x = element.positionInLevel.x
    }
    
    func updateLevelElements(_ timeElapsed: TimeInterval) {
        //updates level elements
        if levelDestructing {
            if levelElements.count > 0 {
                for x in 0...levelElements.count-1 {
                    let element = levelElements[x]
                    if element.shouldDestructIn != nil {
                        element.shouldDestructIn! -= timeElapsed
                    }
                    element.update(heightOnScreen: (element.position.y-element.size.height/2+size.height/2)/size.height, timeElapsed: timeElapsed)
                }
            }
        } else {
            if levelElements.count > 0 {
                for x in 0...levelElements.count-1 {
                    let element = levelElements[x]
                    if element.positionIsStatic {
                        element.positionInLevel.y = playerArrow.levelPosition.y
                    }
                    if element.requestsPlayerPosition {
                        element.playerPosition = playerArrow.levelPosition
                    }
                    element.position.x = element.positionInLevel.x
                    element.position.y = (element.positionInLevel.y-playerArrow.levelPosition.y)-size.height*1/8
                    if element.position.y > -size.height/2-element.size.height/2 && element.position.y < size.height/2+element.size.height {
                        if !element.currentlyAttachedToScene {
                            addChild(element)
                            element.currentlyAttachedToScene = true
                        }
                        if element.checkCollision(player: playerArrow) {
                            if !element.playerWinsOnContact {
                                if element.playerDiesOnContact {
                                    destructLevel()
                                }
                            } else {
                                playerWon = true
                            }
                        }
                        element.update(heightOnScreen: (element.position.y-element.size.height/2+size.height/2)/size.height, timeElapsed: timeElapsed)
                        element.move(heightOnScreen: (element.position.y-element.size.height/2+size.height/2)/size.height, timeElapsed: timeElapsed)
                    } else {
                        if element.currentlyAttachedToScene {
                            element.removeFromParent()
                            element.currentlyAttachedToScene = false
                        }
                    }
                }
            }
        }
    }
    
    func destructLevel() {
        //called to destruct level
        levelDestructing = true
        delegate?.gameViewWillComplete(self)
        levelHasBeenDestructingFor = 0
        if levelElements.count > 0 {
            for x in 0...levelElements.count-1 {
                let element = levelElements[x]
                element.shouldDestructIn = 1
                element.prepareToDestruct()
                if element.playerWinsOnContact {
                    let playerProgress = playerArrow.levelPosition.y/element.positionInLevel.y
                    SettingsManager.setLatestPercentage(playerProgress)
                }
            }
        }
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        touch = touches.first
    }
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        touch = nil
    }
}
