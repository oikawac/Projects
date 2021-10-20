//
//  AudioManager.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-08-10.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import AudioKit

class AudioManager {
    
    var backgroundMusicPlayer: AKAudioPlayer?
    var speedVariator: AKVariSpeed?
    var loseSoundPlayer: AKAudioPlayer?
    var buttonPressPlayer: AKAudioPlayer?
    
    var mixer: AKMixer
    
    init() {
        mixer = AKMixer()
       
        backgroundMusicPlayer = loadSoundFile(file: "bg_music", fileExtension: "mp3", looping: true)
        loseSoundPlayer = loadSoundFile(file: "lose_sound", fileExtension: "mp3", looping: false)
        buttonPressPlayer = loadSoundFile(file: "button_press", fileExtension: "wav", looping: false)
        
        
        if let backgroundMusicPlayer = backgroundMusicPlayer {
            backgroundMusicPlayer.fadeInTime = 0.4
            speedVariator = AKVariSpeed(backgroundMusicPlayer, rate: 0.8)
            mixer.connect(speedVariator)
        }
        if let loseSoundPlayer = loseSoundPlayer {
            mixer.connect(loseSoundPlayer)
        }
        if let buttonPressPlayer = buttonPressPlayer {
            mixer.connect(buttonPressPlayer)
        }
        
        AudioKit.output = mixer
        AudioKit.start()
        if let speedVariator = speedVariator {
            speedVariator.start()
        }
        
        resetVolumes()
    }
    
    func resetVolumes() {
        if let backgroundMusicPlayer = backgroundMusicPlayer {
            backgroundMusicPlayer.volume = Double(SettingsManager.getMusicVolume())
        }
        if let loseSoundPlayer = loseSoundPlayer {
            loseSoundPlayer.volume = Double(SettingsManager.getSFXVolume())
        }
        if let buttonPressPlayer = buttonPressPlayer {
            buttonPressPlayer.volume = Double(SettingsManager.getSFXVolume())
        }
    }
    
    func loadSoundFile(file: String, fileExtension: String, looping: Bool) -> AKAudioPlayer? {
        var player: AKAudioPlayer?
        do {
            let fileURL = Bundle.main.url(forResource: file, withExtension: fileExtension)
            let file = try AKAudioFile(forReading: fileURL!)
            player = try AKAudioPlayer(file: file, looping: looping, completionHandler: nil)
        } catch {
            print("error: audio \(file) could not be loaded")
        }
        return player
    }
    
    func playBackgroundMusic(speedMultiplyer: Double) {
        if let speedVariator = speedVariator {
            speedVariator.rate = speedMultiplyer
        }
        if let backgroundMusicPlayer = backgroundMusicPlayer {
            backgroundMusicPlayer.start()
        }
    }
    
    func stopBackgroundMusic() {
        if let backgroundMusicPlayer = backgroundMusicPlayer {
            backgroundMusicPlayer.stop()
        }
    }
    
    func playLoseSound() {
        if let loseSoundPlayer = loseSoundPlayer {
            loseSoundPlayer.play()
        }
    }
    
    func playButtonPress() {
        if let buttonPressPlayer = buttonPressPlayer {
            buttonPressPlayer.play()
        }
    }
}
