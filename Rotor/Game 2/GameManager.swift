//
//  GameManager.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-02.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class GameManager {
    
    //the GameScene object displayed on the root view controller
    var currentScene: GameScene
    
    var size: CGSize
    
    var audioManager: AudioManager
    
    weak var viewController: GameViewController?
    
    init(size: CGSize) {
        self.size = size
        audioManager = AudioManager()
        currentScene = MainMenuScene(screenSize: size)
        currentScene.gameManager = self
        SettingsManager.registerDefaultSettings()
    }
    
    func changeScene(to sceneName: String, withTransition transitionParams: (type: String, duration: TimeInterval)?) {
        //changes current scene displayed on root view controller
        var transition: SKTransition
        if let transitionType = transitionParams {
            if transitionType.type == "pushLeft" {
                transition = SKTransition.push(with: .left, duration: transitionType.duration)
            } else if transitionType.type == "pushRight" {
                transition = SKTransition.push(with: .right, duration: transitionType.duration)
            } else if transitionType.type == "pushDown" {
                    transition = SKTransition.push(with: .down, duration: transitionType.duration)
            } else if transitionType.type == "pushUp" {
                transition = SKTransition.push(with: .up, duration: transitionType.duration)
            } else if transitionType.type == "fade" {
                transition = SKTransition.fade(withDuration: 0.25)
            } else {
                transition = SKTransition.fade(withDuration: 0.0)
            }
        } else {
            transition = SKTransition.fade(withDuration: 0.0)
        }
        var cancel = false
        if sceneName == "mainMenuScene" {
            currentScene = MainMenuScene(screenSize: size)
            currentScene.gameManager = self
            currentScene.sceneFinishedInit()
        } else if sceneName == "levelMenuScene" {
            currentScene = LevelMenuScene(screenSize: size)
            currentScene.gameManager = self
            currentScene.sceneFinishedInit()
        } else if sceneName == "settingsMenuScene" {
            currentScene = SettingsMenuScene(screenSize: size)
            currentScene.gameManager = self
            currentScene.sceneFinishedInit()
        } else if sceneName == "highscoresMenuScene" {
            currentScene = HighscoresMenuScene(screenSize: size)
            currentScene.gameManager = self
            currentScene.sceneFinishedInit()
        } else if sceneName == "playScene" {
            currentScene = PlayScene(screenSize: size, levelNumber: SettingsManager.getCurrentLevel())
            currentScene.gameManager = self
            currentScene.sceneFinishedInit()
        } else if sceneName == "feedbackScene" {
            currentScene = FeedbackScene(screenSize: size, levelNumber: SettingsManager.getCurrentLevel(), progress: SettingsManager.getLatestPercentage())
            currentScene.gameManager = self
            currentScene.sceneFinishedInit()
        } else {
            print("error: no scene could be loaded with name \(sceneName)")
            cancel = true
        }
        if viewController != nil && !cancel{
            if let view = viewController!.view as! SKView? {
                view.presentScene(currentScene, transition: transition)
                view.ignoresSiblingOrder = true
                view.preferredFramesPerSecond = 60
            }
        }
    }
}
