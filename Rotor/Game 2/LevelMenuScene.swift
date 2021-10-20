//
//  LevelMenuScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-02.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

//todo: give credit https://soundcloud.com/parijat-mishra/8-bits-of-a-dream?in=parijat-mishra/sets/original-instrumentals
//todo: acutal volume control

import SpriteKit

class LevelMenuScene: GameScene, ButtonDelegate, LevelSelectViewDelegate {
    
    var levelSelectView: LevelSelectView
    var backButton: Button
    
    var bottomBarDivider: SKSpriteNode
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize) {
        levelSelectView = LevelSelectView(screenSize: screenSize)
        levelSelectView.position = CGPoint(x: 0, y: screenSize.height*0.575)
        levelSelectView.alpha = 0
        backButton = ArrowButton(size: CGSize(width: screenSize.height/10, height: screenSize.height/10), colour: UIColor.white, screenSize: screenSize, direction: "left", id: "backButton")
        backButton.alpha = 0
        backButton.position = CGPoint(x: screenSize.height*0.075, y: screenSize.height*0.075)
        let path = CGMutablePath()
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: screenSize.width, y: 0))
        let shape = SKShapeNode(path: path)
        shape.lineWidth = screenSize.width/200
        let texture = SKView().texture(from: shape)!
        bottomBarDivider = SKSpriteNode(texture: texture, color: UIColor.white, size: texture.size())
        bottomBarDivider.position = CGPoint(x: -screenSize.width/2, y: screenSize.height*0.15)
        bottomBarDivider.colorBlendFactor = 1.0
        super.init(size: screenSize)
        backButton.delegate = self
        addChild(backButton)
        backgroundColor = UIColor.darkGray
        levelSelectView.delegate = self
        addChild(levelSelectView)
        addChild(bottomBarDivider)
        animateMenu()
    }
    
    override func update(_ timeElapsed: TimeInterval) {
        levelSelectView.update()
    }
    
    func animateMenu() {
        bottomBarDivider.run(SKAction.move(to: CGPoint(x: size.width/2, y: size.height*0.15), duration: 0.35))
        backButton.run(SKAction.sequence([SKAction.wait(forDuration: 0.5), SKAction.fadeIn(withDuration: 0.1)]))
        backButton.color = UIColor.darkGray
        levelSelectView.run(SKAction.sequence([SKAction.wait(forDuration: 0.6), SKAction.fadeIn(withDuration: 0.1)]))
    }
    
    func levelSelectViewSelectedLevel(_ levelSelectView: LevelSelectView, level: Int) {
        SettingsManager.setCurrentLevel(level)
        gameManager?.changeScene(to: "playScene", withTransition: (type: "pushDown", duration: 0.5))
    }
    
    func levelSelectViewBlendedColourChanged(_ levelSelectView: LevelSelectView, backgroundColour: UIColor, primaryColour: UIColor) {
        backButton.buttonSprite.color = primaryColour
        backButton.color = backgroundColour
        bottomBarDivider.color = primaryColour
        backgroundColor = backgroundColour
    }
    
    func buttonPressed(_ button: Button) {
        if button.id == "backButton" {
            gameManager?.changeScene(to: "mainMenuScene", withTransition: nil)
        }
    }
    func buttonSelected(_ button: Button) {
        gameManager?.audioManager.playButtonPress()
    }
}

