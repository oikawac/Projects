//
//  SettingsManager.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-06-04.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//


/*
 2017-06-10 22:04:31.238109 Game 2[7752:2991196] [User Defaults] Failed to write value for key completeLevels in CFPrefsPlistSource<0x17011fb60> (Domain: com.Stephen-Oikawa.Game-2, User: kCFPreferencesCurrentUser, ByHost: No, Container: (null)): Path not accessible, switching to read-only
 2017-06-10 22:04:31.238150 Game 2[7752:2990447] [User Defaults] Failed to write value for key latestPercentage in CFPrefsPlistSource<0x17011fb60> (Domain: com.Stephen-Oikawa.Game-2, User: kCFPreferencesCurrentUser, ByHost: No, Container: (null)): Path not accessible, switching to read-only
 2017-06-10 22:04:31.238453 Game 2[7752:2990433] [User Defaults] Failed to write value for key furthestPercentages in CFPrefsPlistSource<0x17011fb60> (Domain: com.Stephen-Oikawa.Game-2, User: kCFPreferencesCurrentUser, ByHost: No, Container: (null)): Path not accessible, switching to read-only
 2017-06-10 22:04:36.919361 Game 2[7752:2990386] [User Defaults] attempt to set 0 for key in currentLevel in read-only (due to a previous failed write) preferences domain CFPrefsPlistSource<0x17011fb60> (Domain: com.Stephen-Oikawa.Game-2, User: kCFPreferencesCurrentUser, ByHost: No, Container: (null))
 */
//todo: prevent critical failure when userdefaults fails to write

import SpriteKit

class SettingsManager {
    
    static func registerDefaultSettings() {
        //registers the default values for UserDefaults
        UserDefaults.standard.register(defaults: ["currentLevel": 0])
        UserDefaults.standard.register(defaults: ["completeLevels": [false, false, false, false, false, false, false, false, false, false, false, false]])
        UserDefaults.standard.register(defaults: ["latestPercentage": 0.0])
        UserDefaults.standard.register(defaults: ["furthestPercentages": [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]])
        UserDefaults.standard.register(defaults: ["sfxVolume": 0.5])
        UserDefaults.standard.register(defaults: ["musicVolume": 0.5])
    }
    
    static func resetUserDefaults() {
        UserDefaults.standard.set(0, forKey: "currentLevel")
        UserDefaults.standard.set([false, false, false, false, false, false, false, false, false, false, false, false], forKey: "completeLevels")
        UserDefaults.standard.set(0.0, forKey: "latestPercentage")
        UserDefaults.standard.set([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], forKey: "furthestPercentages")
        UserDefaults.standard.set(0.5, forKey: "sfxVolume")
        UserDefaults.standard.set(0.5, forKey: "musicVolume")
    }
    
    static func setCurrentLevel(_ level: Int) {
        //sets the currentLevel setting
        UserDefaults.standard.set(level, forKey: "currentLevel")
    }
    static func getCurrentLevel() -> Int {
        //returns the currentLevel setting
        return UserDefaults.standard.integer(forKey: "currentLevel")
    }
    
    static func setLevelComplete(_ level: Int, complete: Bool) {
        //sets a level as complete in the completeLevels setting
        var completedArray = UserDefaults.standard.array(forKey: "completeLevels") as! [Bool]
        completedArray[level] = complete
        UserDefaults.standard.set(completedArray, forKey: "completeLevels")
    }
    static func getLevelComplete(_ level: Int) -> Bool {
        //returns if a level is complete in the completeLevels setting
        let completedArray = UserDefaults.standard.array(forKey: "completeLevels") as! [Bool]
        return completedArray[level]
    }
    static func getLevelUnlocked(_ level: Int) -> Bool {
        //returns if a level is unlocked
        if level == 0 {
            return true
        } else {
            return getLevelComplete(level-1)
        }
    }
    
    static func setFurthestPercentage(_ level: Int, percentage: CGFloat) {
        //sets the furthest percentage reached for a level  in the furthestPercentages setting
        var furthestPercentages = UserDefaults.standard.array(forKey: "furthestPercentages") as! [CGFloat]
        furthestPercentages[level] = percentage
        UserDefaults.standard.set(furthestPercentages, forKey: "furthestPercentages")
    }
    static func getFurthestPercentage(_ level: Int) -> CGFloat {
        //returns furthest percentage reached for level in the furthestPercentages setting
        let furthestPercentages = UserDefaults.standard.array(forKey: "furthestPercentages") as! [CGFloat]
        return furthestPercentages[level]
    }
    
    static func setLatestPercentage(_ percentage: CGFloat) {
        //sets the percentage reached of the last level played in the latestProgress setting
        UserDefaults.standard.set(percentage, forKey: "latestPercentage")
    }
    static func getLatestPercentage() -> CGFloat {
        //returns the percentage reached of the last level played in the latestProgress setting
        return CGFloat(UserDefaults.standard.double(forKey: "latestPercentage"))
    }
    
    static func setSFXVolume(_ volume: CGFloat) {
        //sets the SFX volume
        UserDefaults.standard.set(volume, forKey: "sfxVolume")
    }
    static func getSFXVolume() -> CGFloat {
        //returns the SFX volume
        return CGFloat(UserDefaults.standard.double(forKey: "sfxVolume"))
    }
    
    static func setMusicVolume(_ volume: CGFloat) {
        //sets the music volume
        UserDefaults.standard.set(volume, forKey: "musicVolume")
    }
    static func getMusicVolume() -> CGFloat {
        //returns the music volume
        return CGFloat(UserDefaults.standard.double(forKey: "musicVolume"))
    }
}
