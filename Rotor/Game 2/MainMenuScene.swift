//
//  MainMenuScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-02.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class MainMenuScene: GameScene, ButtonDelegate {
    
    var buttonArray = [Button]()
    
    var title: SKLabelNode
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize) {
        title = SKLabelNode(text: "Pgy!")
        super.init(size: screenSize)
        backgroundColor = UIColor.darkGray
        title.fontName = "HemiHeadRg-BoldItalic"
        title.fontColor = UIColor.white
        title.horizontalAlignmentMode = .center
        title.verticalAlignmentMode = .center
        title.fontSize = 10
        let scalingFactor = (size.height*0.2) / title.frame.height
        title.fontSize *= scalingFactor
        title.text = "Rotor"
        title.position = CGPoint(x: size.width/2, y: size.height*0.9)
        title.alpha = 0
        addChild(title)
        title.run(SKAction.sequence([SKAction.wait(forDuration: 1.0), SKAction.fadeIn(withDuration: 0.75)]))
        let playButton = SlideOutButton(size: CGSize(width: size.width*12/10, height: size.width/5), colour: UIColor.white, screenSize: screenSize, height: 0.7*size.height, text: "Play", id: "playButton")
        playButton.delegate = self
        addChild(playButton)
        buttonArray.append(playButton)
        let settingsButton = SlideOutButton(size: CGSize(width: size.width, height: size.width/7), colour: UIColor.white, screenSize: screenSize, height: 0.7*size.height-playButton.size.height, text: "Settings", id: "settingsButton")
        settingsButton.delegate = self
        addChild(settingsButton)
        buttonArray.append(settingsButton)
        let highscoresButton = SlideOutButton(size: CGSize(width: size.width*12/10, height: size.width/7), colour: UIColor.white, screenSize: screenSize, height: 0.2*size.height, text: "Highscores", id: "highscoresButton")
        highscoresButton.delegate = self
        addChild(highscoresButton)
        buttonArray.append(highscoresButton)
    }
    
    func buttonPressed(_ button: Button) {
        if button.id == "playButton" {
            gameManager?.changeScene(to: "levelMenuScene", withTransition: nil)
        } else if button.id == "settingsButton" {
            gameManager?.changeScene(to: "settingsMenuScene", withTransition: nil)
        } else if button.id == "highscoresButton" {
            gameManager?.changeScene(to: "highscoresMenuScene", withTransition: nil)
        }
    }
    func buttonSelected(_ button: Button) {
        gameManager?.audioManager.playButtonPress()
        for button in buttonArray {
            button.dismiss()
            title.run(SKAction.move(to: CGPoint(x: -size.width, y: title.position.y), duration: 0.25))
        }
    }
}
