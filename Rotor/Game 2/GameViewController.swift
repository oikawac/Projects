//
//  GameViewController.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-03-31.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

extension CGPoint {
    func distance(to point: CGPoint) -> CGFloat {
        let xDiff = x-point.x
        let yDiff = y-point.y
        return sqrt(xDiff*xDiff+yDiff*yDiff)
    }
    
    func angle(to point: CGPoint) -> CGFloat {
        let xDiff = point.x-x
        let yDiff = point.y-y
        return atan2(yDiff, xDiff)
    }
    
    func midPoint(to point: CGPoint) -> CGPoint {
        return CGPoint(x: (x+point.x)/2, y: (y+point.y)/2)
    }
}


import SpriteKit

class GameViewController: UIViewController {
    
    //GameManager object - manages current scene displayed on root view controller
    let gameManager = GameManager(size: CGSize(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height))
    
    override func viewDidLoad() {
        gameManager.viewController = self
        super.viewDidLoad()
        if let view = self.view as! SKView? {
            let scene = gameManager.currentScene
            scene.scaleMode = .aspectFit
            view.presentScene(scene)
            view.ignoresSiblingOrder = true
            view.preferredFramesPerSecond = 60
            view.showsFPS = true
            view.showsNodeCount = true
        }
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
}
