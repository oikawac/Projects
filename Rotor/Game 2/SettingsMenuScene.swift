//
//  SettingsMenuScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-05-14.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class SettingsMenuScene: GameScene, ButtonDelegate, SliderDelegate {
    
    var buttonArray = [Button]()
    
    var sfxLabel: SKLabelNode
    var musicLabel: SKLabelNode
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(screenSize: CGSize) {
        sfxLabel = SKLabelNode(text: "Pgy!")
        musicLabel = SKLabelNode(text: "Pgy!")
        super.init(size: screenSize)
        backgroundColor = UIColor.darkGray
        let backButton = SlideOutButton(size: CGSize(width: size.width*8/10, height: size.width/7), colour: UIColor.white, screenSize: screenSize, height: 0.2*size.height, text: "Back", id: "backButton")
        backButton.delegate = self
        addChild(backButton)
        buttonArray.append(backButton)
        let resetButton = RectButton(size: CGSize(width: screenSize.width/1.4, height: screenSize.width/8), text: "reset game content", colour: UIColor.white, screenSize: screenSize, id: "resetButton")
        resetButton.delegate = self
        resetButton.shouldDismissOnSelect = false
        resetButton.position = CGPoint(x: screenSize.width/2, y: screenSize.height*0.4)
        addChild(resetButton)
        buttonArray.append(resetButton)
        let sfxVolumeSlider = Slider(screenSize: screenSize, length: screenSize.width*8/10, radius: screenSize.width/30, id: "sfx")
        sfxVolumeSlider.position = CGPoint(x: screenSize.width/2, y: screenSize.height*0.6)
        sfxVolumeSlider.delegate = self
        sfxVolumeSlider.setValue(SettingsManager.getSFXVolume()-0.5)
        addChild(sfxVolumeSlider)
        let musicVolumeSlider = Slider(screenSize: screenSize, length: screenSize.width*8/10, radius: screenSize.width/30, id: "music")
        musicVolumeSlider.position = CGPoint(x: screenSize.width/2, y: screenSize.height*0.8)
        musicVolumeSlider.delegate = self
        musicVolumeSlider.setValue(SettingsManager.getMusicVolume()-0.5)
        addChild(musicVolumeSlider)
        sfxLabel.fontName = "HemiHeadRg-BoldItalic"
        sfxLabel.fontColor = UIColor.white
        sfxLabel.horizontalAlignmentMode = .left
        sfxLabel.verticalAlignmentMode = .center
        sfxLabel.fontSize = 10
        var scalingFactor = (screenSize.height/25) / sfxLabel.frame.height
        sfxLabel.fontSize *= scalingFactor
        sfxLabel.text = "SFX volume: \(Int(SettingsManager.getSFXVolume()*100))%"
        sfxLabel.position = CGPoint(x: screenSize.width*1/10, y: screenSize.height*0.65)
        addChild(sfxLabel)
        musicLabel.fontName = "HemiHeadRg-BoldItalic"
        musicLabel.fontColor = UIColor.white
        musicLabel.horizontalAlignmentMode = .left
        musicLabel.verticalAlignmentMode = .center
        musicLabel.fontSize = 10
        scalingFactor = (screenSize.height/25) / musicLabel.frame.height
        musicLabel.fontSize *= scalingFactor
        musicLabel.text = "music volume: \(Int(SettingsManager.getMusicVolume()*100))%"
        musicLabel.position = CGPoint(x: screenSize.width*1/10, y: screenSize.height*0.85)
        addChild(musicLabel)
    }
    
    func buttonPressed(_ button: Button) {
        if button.id == "backButton" {
            gameManager?.changeScene(to: "mainMenuScene", withTransition: nil)
        } else if button.id == "resetButton" {
            SettingsManager.resetUserDefaults()
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
    
    func sliderPressed(_ slider: Slider, value: CGFloat) {
        if slider.id == "sfx" {
            sfxLabel.text = "sfx volume: \(Int((value+0.5)*100))%"
        } else if slider.id == "music" {
            musicLabel.text = "music volume: \(Int((value+0.5)*100))%"
        }
    }
    func sliderReleased(_ slider: Slider, value: CGFloat) {
        if slider.id == "sfx" {
            SettingsManager.setSFXVolume(value+0.5)
            sfxLabel.text = "sfx volume: \(Int((value+0.5)*100))%"
        } else if slider.id == "music" {
            SettingsManager.setMusicVolume(value+0.5)
            musicLabel.text = "music volume: \(Int((value+0.5)*100))%"
        }
        gameManager?.audioManager.resetVolumes()
    }
}

