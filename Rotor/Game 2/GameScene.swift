//
//  GameScene.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-04-02.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class GameScene: SKScene {
    
    weak var gameManager: GameManager?
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override init(size: CGSize) {
        super.init(size: size)
        self.size = size
        self.scaleMode = .aspectFit
    }
    
    func sceneFinishedInit() {
        //called once scene loads
    }
}
