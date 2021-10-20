//
//  HighscoresMenuScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-08-07.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class HighscoresMenuScene: GameScene, ButtonDelegate {
    
    var buttonArray = [Button]()
    
    
    var progressLabelArray = [SKLabelNode]()
    var progressBarArray = [SKSpriteNode]()
    var antiProgressBarArray = [SKSpriteNode]()
    
    var highscores = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    
    var screenSize: CGSize
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize) {
        self.screenSize = screenSize
        super.init(size: screenSize)
        progressBarArray = loadProgressBars()
        antiProgressBarArray = loadAntiProgressBars()
        progressLabelArray = loadProgressLabels()
        backgroundColor = UIColor.darkGray
        let backButton = SlideOutButton(size: CGSize(width: size.width*8/10, height: size.width/7), colour: UIColor.white, screenSize: screenSize, height: 0.2*size.height, text: "Back", id: "backButton")
        backButton.delegate = self
        addChild(backButton)
        buttonArray.append(backButton)
        updateHighscores()
        
        let caileanOikawaLabel = SKLabelNode(text: "Pgy!")
        caileanOikawaLabel.fontName = "HemiHeadRg-BoldItalic"
        caileanOikawaLabel.fontColor = UIColor.white
        caileanOikawaLabel.horizontalAlignmentMode = .center
        caileanOikawaLabel.verticalAlignmentMode = .center
        caileanOikawaLabel.fontSize = 10
        var scalingFactor = (screenSize.height/35) / caileanOikawaLabel.frame.height
        caileanOikawaLabel.fontSize *= scalingFactor
        caileanOikawaLabel.text = "game design & coding by Cailean Oikawa"
        caileanOikawaLabel.position = CGPoint(x: screenSize.width/2, y: screenSize.height*0.1)
        addChild(caileanOikawaLabel)
        
        let parijatMishraLabel = SKLabelNode(text: "Pgy!")
        parijatMishraLabel.fontName = "HemiHeadRg-BoldItalic"
        parijatMishraLabel.fontColor = UIColor.white
        parijatMishraLabel.horizontalAlignmentMode = .center
        parijatMishraLabel.verticalAlignmentMode = .center
        parijatMishraLabel.fontSize = 10
        scalingFactor = (screenSize.height/35) / parijatMishraLabel.frame.height
        parijatMishraLabel.fontSize *= scalingFactor
        parijatMishraLabel.text = "music '8 Bits Of A Dream' by Parijat Mishra"
        parijatMishraLabel.position = CGPoint(x: screenSize.width/2, y: screenSize.height*0.06)
        addChild(parijatMishraLabel)

    }
    
    func loadProgressBars() -> [SKSpriteNode] {
        var progressBars = [SKSpriteNode]()
        for x in 1...12 {
            let path = CGMutablePath()
            path.move(to: CGPoint())
            path.addLine(to: CGPoint(x: 1, y: 0))
            let shape = SKShapeNode(path: path)
            shape.lineWidth = screenSize.width/200
            let texture = SKView().texture(from: shape)
            let progressBar = SKSpriteNode(texture: texture, color: UIColor(), size: texture!.size())
            progressBar.anchorPoint = CGPoint(x: 0, y: 0.5)
            progressBar.position = CGPoint(x: screenSize.width/10, y: screenSize.height*CGFloat(1-0.05833*Double(x)))
            progressBar.alpha = 0
            progressBars.append(progressBar)
            addChild(progressBar)
        }
        return progressBars
    }
    func loadAntiProgressBars() -> [SKSpriteNode] {
        var antiProgressBars = [SKSpriteNode]()
        for x in 1...12 {
            let path = CGMutablePath()
            path.move(to: CGPoint())
            path.addLine(to: CGPoint(x: screenSize.width*(3.0/5.0)-1.0, y: 0))
            let shape = SKShapeNode(path: path)
            shape.lineWidth = screenSize.width/200
            let texture = SKView().texture(from: shape)
            let antiProgressBar = SKSpriteNode(texture: texture, color: UIColor(), size: texture!.size())
            antiProgressBar.anchorPoint = CGPoint(x: 1, y: 0.5)
            antiProgressBar.position = CGPoint(x: screenSize.width*7/10, y: screenSize.height*CGFloat(1-0.05833*Double(x)))
            antiProgressBar.alpha = 0.6
            antiProgressBars.append(antiProgressBar)
            addChild(antiProgressBar)
        }
        return antiProgressBars
    }
    func loadProgressLabels() -> [SKLabelNode] {
        var progressLabels = [SKLabelNode]()
        for x in 1...12 {
            let progressLabel = SKLabelNode(text: "0%")
            progressLabel.fontName = "HemiHeadRg-BoldItalic"
            progressLabel.horizontalAlignmentMode = .left
            progressLabel.verticalAlignmentMode = .center
            progressLabel.fontSize = 10
            let scalingFactor = (screenSize.height*0.7/12/2) / progressLabel.frame.height
            progressLabel.fontSize *= scalingFactor
            progressLabel.position = CGPoint(x: screenSize.width*16/20, y: screenSize.height*CGFloat(1-0.05833*Double(x)))
            progressLabels.append(progressLabel)
            addChild(progressLabel)
        }
        return progressLabels
    }
    
    func updateHighscores() {
        var allHighscoresUpdated = true
        var shouldNotMoveAlongToNextLevel = false
        for x in 0...11 {
            let currentPercentage = highscores[x]
            let furthestPercentage = SettingsManager.getFurthestPercentage(x)
            if Int(floor(furthestPercentage*100)) > Int(floor(currentPercentage*100)) {
                allHighscoresUpdated = false
                shouldNotMoveAlongToNextLevel = true
                if Int(floor(furthestPercentage*100))-Int(floor(currentPercentage*100)) < 3 {
                    progressLabelArray[x].text = "\(Int(floor(currentPercentage*100))+1)%"
                    highscores[x] += 0.01
                } else {
                    progressLabelArray[x].text = "\(Int(floor(currentPercentage*100))+3)%"
                    highscores[x] += 0.03
                }
                var path = CGMutablePath()
                path.move(to: CGPoint())
                path.addLine(to: CGPoint(x: (screenSize.width*3/5)*CGFloat(highscores[x]), y: 0))
                var shape = SKShapeNode(path: path)
                shape.lineWidth = screenSize.width/200
                var texture = SKView().texture(from: shape)
                progressBarArray[x].texture = texture
                progressBarArray[x].size = texture!.size()
                if (screenSize.width*3/5)*CGFloat(highscores[x]) > 1 {
                    progressBarArray[x].alpha = 1
                }
                path = CGMutablePath()
                path.move(to: CGPoint())
                if (screenSize.width*3/5)*CGFloat(1-highscores[x]) < 1 {
                    antiProgressBarArray[x].alpha = 0
                } else {
                    path.addLine(to: CGPoint(x: (screenSize.width*3/5)*CGFloat(1-highscores[x]), y: 0))
                    shape = SKShapeNode(path: path)
                    shape.lineWidth = screenSize.width/200
                    texture = SKView().texture(from: shape)
                    antiProgressBarArray[x].texture = texture
                    antiProgressBarArray[x].size = texture!.size()
                }
                if shouldNotMoveAlongToNextLevel {
                    break
                }
            } else {
                if shouldNotMoveAlongToNextLevel == true {
                    shouldNotMoveAlongToNextLevel = false
                }
            }
        }
        if !allHighscoresUpdated {
            self.run(SKAction.sequence([SKAction.wait(forDuration: 0.0), SKAction.run(updateHighscores)]))
        }
    }
    
    func buttonPressed(_ button: Button) {
        if button.id == "backButton" {
            gameManager?.changeScene(to: "mainMenuScene", withTransition: nil)
        }
    }
    func buttonSelected(_ button: Button) {
        gameManager?.audioManager.playButtonPress()
        if button.id == "backButton" {
            for button in buttonArray {
                button.dismiss()
            }
        }
    }
}

