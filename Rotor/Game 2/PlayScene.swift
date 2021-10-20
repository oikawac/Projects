//
//  PlayScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-19.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class PlayScene: GameScene, GameViewDelegate {
    
    var gameView: GameView
    
    var previousTime: TimeInterval = 0
    var timeElapsed: TimeInterval = 0
    
    var levelNumber: Int
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize, levelNumber: Int) {
        self.levelNumber = levelNumber
        gameView = GameView(screenSize: screenSize, levelNumber: levelNumber)
        super.init(size: screenSize)
        gameView.delegate = self
        addChild(gameView)
    }
    
    override func sceneFinishedInit() {
        gameManager?.audioManager.playBackgroundMusic(speedMultiplyer: LevelManager.getMusicSpeed(for: levelNumber))
    }

    override func update(_ currentTime: TimeInterval) {
        //updates game view
        if previousTime == 0 {
            previousTime = currentTime
        }
        timeElapsed = currentTime-previousTime
        previousTime = currentTime
        gameView.update(timeElapsed)
    }
    
    func gameViewWillComplete(_ gameView: GameView) {
        //prepares to change scenes
        gameManager?.audioManager.stopBackgroundMusic()
        gameManager?.audioManager.playLoseSound()
    }
    func gameViewComplete(_ gameView: GameView) {
        //changes to feedback scene
        gameManager?.changeScene(to: "feedbackScene", withTransition: (type: "pushUp", duration: 0.5))
    }
}
