//
//  FeedbackScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-05.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class FeedbackScene: GameScene, ButtonDelegate {
    
    var buttonArray = [Button]()
    
    var colourScheme: (backgroundColour: UIColor, primaryColour: UIColor, secondaryColour: UIColor)
    var progress: Int
    var progressLabel: SKLabelNode
    var progressLabelValue = 0
    var progressBar: SKSpriteNode
    var antiProgressBar: SKSpriteNode
    
    var levelNumber: Int
    
    var playerWon: Bool
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, levelNumber: Int, progress: CGFloat) {
        self.levelNumber = levelNumber
        if progress > SettingsManager.getFurthestPercentage(levelNumber) {
            SettingsManager.setFurthestPercentage(levelNumber, percentage: progress)
        }
        if progress == 1.0 {
            SettingsManager.setLevelComplete(levelNumber, complete: true)
            playerWon = true
        } else {
            playerWon = false
        }
        self.progress = Int(floor(progress*100))
        if self.progress < 0 {
            self.progress = 0
        }
        colourScheme = LevelManager.getColourScheme(for: levelNumber)
        progressLabel = SKLabelNode(text: "\(self.progress)%")
        progressLabel.fontName = "HemiHeadRg-BoldItalic"
        progressLabel.fontColor = colourScheme.primaryColour
        progressLabel.horizontalAlignmentMode = .right
        progressLabel.verticalAlignmentMode = .top
        progressLabel.fontSize = 10
        var scalingFactor = (screenSize.height/4.5) / progressLabel.frame.height
        progressLabel.fontSize *= scalingFactor
        progressLabel.position = CGPoint(x: screenSize.width/2+progressLabel.frame.width/2, y: screenSize.height*9/10)
        var path = CGMutablePath()
        path.move(to: CGPoint())
        path.addLine(to: CGPoint(x: 1, y: 0))
        var shape = SKShapeNode(path: path)
        shape.lineWidth = screenSize.width/200
        shape.strokeColor = colourScheme.primaryColour
        var texture = SKView().texture(from: shape)
        progressBar = SKSpriteNode(texture: texture, color: UIColor(), size: texture!.size())
        progressBar.anchorPoint = CGPoint(x: 0, y: 0.5)
        progressBar.position = CGPoint(x: screenSize.width/5, y: screenSize.height/2)
        path = CGMutablePath()
        path.move(to: CGPoint())
        path.addLine(to: CGPoint(x: screenSize.width*(3.0/5.0)-1.0, y: 0))
        shape = SKShapeNode(path: path)
        shape.lineWidth = screenSize.width/200
        shape.strokeColor = colourScheme.primaryColour
        texture = SKView().texture(from: shape)
        antiProgressBar = SKSpriteNode(texture: texture, color: UIColor(), size: texture!.size())
        antiProgressBar.anchorPoint = CGPoint(x: 1, y: 0.5)
        antiProgressBar.position = CGPoint(x: screenSize.width*4/5, y: screenSize.height/2)
        antiProgressBar.alpha = 0.6
        super.init(size: screenSize)
        let title = SKLabelNode(text: "completed")
        title.fontName = "HemiHeadRg-BoldItalic"
        title.fontColor = colourScheme.primaryColour
        title.horizontalAlignmentMode = .center
        title.verticalAlignmentMode = .top
        title.fontSize = 10
        scalingFactor = (screenSize.height/9) / title.frame.height
        title.fontSize *= scalingFactor
        title.position = CGPoint(x: screenSize.width/2, y: screenSize.height*11/16)
        addChild(title)
        let startLabel = SKLabelNode(text: "Pgy!")
        startLabel.fontName = "HemiHeadRg-BoldItalic"
        startLabel.fontColor = colourScheme.primaryColour
        startLabel.horizontalAlignmentMode = .right
        startLabel.verticalAlignmentMode = .center
        startLabel.fontSize = 10
        scalingFactor = (screenSize.height/25) / startLabel.frame.height
        startLabel.fontSize *= scalingFactor
        startLabel.text = "start"
        startLabel.position = CGPoint(x: screenSize.width*1/5, y: screenSize.height/2)
        addChild(startLabel)
        let endLabel = SKLabelNode(text: "Pgy!")
        endLabel.fontName = "HemiHeadRg-BoldItalic"
        endLabel.fontColor = colourScheme.primaryColour
        endLabel.horizontalAlignmentMode = .left
        endLabel.verticalAlignmentMode = .center
        endLabel.fontSize = 10
        scalingFactor = (screenSize.height/25) / endLabel.frame.height
        endLabel.fontSize *= scalingFactor
        endLabel.text = "end"
        endLabel.position = CGPoint(x: screenSize.width*4/5, y: screenSize.height/2)
        addChild(endLabel)
        backgroundColor = colourScheme.backgroundColour
        if progress < 1.0 {
            let retryButton = RectButton(size: CGSize(width: screenSize.width/2, height: screenSize.width/8), text: "retry", colour: colourScheme.primaryColour, screenSize: screenSize, id: "retryButton")
            retryButton.delegate = self
            retryButton.position = CGPoint(x: screenSize.width/2, y: screenSize.height/3)
            addChild(retryButton)
            buttonArray.append(retryButton)
        } else if levelNumber != LevelManager.getMaxLevel() {
            let nextLevelButton = RectButton(size: CGSize(width: screenSize.width/2, height: screenSize.width/8), text: "next level", colour: colourScheme.primaryColour, screenSize: screenSize, id: "nextLevelButton")
            nextLevelButton.delegate = self
            nextLevelButton.position = CGPoint(x: screenSize.width/2, y: screenSize.height/3)
            addChild(nextLevelButton)
            buttonArray.append(nextLevelButton)
        } else {
            //todo: more levels coming soon label
        }
        let levelsButton = RectButton(size: CGSize(width: screenSize.width/2, height: screenSize.width/8), text: "levels", colour: colourScheme.primaryColour, screenSize: screenSize, id: "levelsButton")
        levelsButton.delegate = self
        levelsButton.position = CGPoint(x: screenSize.width/2, y: screenSize.height/3-0.75*screenSize.height/8)
        addChild(levelsButton)
        buttonArray.append(levelsButton)
        addChild(progressLabel)
        addChild(progressBar)
        addChild(antiProgressBar)
        animateProgressLabelAndGraphic()
    }
    
    func animateProgressLabelAndGraphic() {
        if progressLabelValue < progress {
            progressLabelValue += 1
            progressLabel.text = "\(progressLabelValue)%"
            progressLabel.run(SKAction.sequence([SKAction.wait(forDuration: 0.01), SKAction.run(animateProgressLabelAndGraphic)]))
            var path = CGMutablePath()
            path.move(to: CGPoint())
            var point = CGPoint(x: size.width*CGFloat(progressLabelValue)/100.0*3.0/5.0, y: 0)
            path.addLine(to: point)
            var shape = SKShapeNode(path: path)
            shape.lineWidth = size.width/200
            shape.strokeColor = colourScheme.primaryColour
            var texture = SKView().texture(from: shape)
            progressBar.texture = texture!
            progressBar.size = texture!.size()
            if progressLabelValue < 100 {
                path = CGMutablePath()
                path.move(to: CGPoint())
                point = CGPoint(x: size.width*CGFloat(100-progressLabelValue)/100.0*3.0/5.0, y: 0)
                path.addLine(to: point)
                shape = SKShapeNode(path: path)
                shape.lineWidth = size.width/200
                shape.strokeColor = colourScheme.primaryColour
                texture = SKView().texture(from: shape)
                antiProgressBar.texture = texture!
                antiProgressBar.size = texture!.size()
            } else {
                antiProgressBar.removeFromParent()
            }
        }
    }
    
    func buttonPressed(_ button: Button) {
        if playerWon {
            gameManager?.audioManager.stopBackgroundMusic()
        }
        if button.id == "retryButton" {
            gameManager?.changeScene(to: "playScene", withTransition: (type: "pushDown", duration: 0.5))
        } else if button.id == "levelsButton" {
            gameManager?.changeScene(to: "levelMenuScene", withTransition: (type: "fade", duration: 1.0))
        } else if button.id == "nextLevelButton" {
            SettingsManager.setCurrentLevel(levelNumber+1)
            gameManager?.changeScene(to: "playScene", withTransition: (type: "pushDown", duration: 0.5))
        }
    }
    func buttonSelected(_ button: Button) {
        gameManager?.audioManager.playButtonPress()
        for button in buttonArray {
            button.dismiss()
        }
    }
}
