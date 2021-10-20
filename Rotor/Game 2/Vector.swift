//
//  Vector.swift
//  Game 2
//
//  Created by Cailean Oikawa on 2017-05-03.
//  Copyright © 2017 Cailean Oikawa. All rights reserved.
//

import SpriteKit

class Vector {
    
    var point1: CGPoint
    
    var point2: CGPoint
    
    init(from p1: CGPoint, to p2: CGPoint) {
        point1 = p1
        point2 = p2
    }
    
    func checkIntercept(vector: Vector) -> Bool {
        //checks if a vector intercepts with itself
        if point1.x == point2.x {
            point1.x += 0.1
        }
        if point1.y == point2.y {
            point1.y += 0.1
        }
        if vector.point1.x == vector.point2.x {
            vector.point1.x += 0.1
        }
        if vector.point1.y == vector.point2.y {
            vector.point1.y += 0.1
        }
        
        let selfSlope = (self.point1.y-self.point2.y)/(self.point1.x-self.point2.x)
        let selfYInt = self.point1.y-selfSlope*self.point1.x
        
        let vectorSlope = (vector.point1.y-vector.point2.y)/(vector.point1.x-vector.point2.x)
        let vectorYInt = vector.point1.y-vectorSlope*vector.point1.x
        
        let xIntercept = (selfYInt-vectorYInt)/(vectorSlope-selfSlope)
        let yIntercept = selfSlope*xIntercept+selfYInt
        
        var selfLowerX: CGFloat
        var selfUpperX: CGFloat
        var selfLowerY: CGFloat
        var selfUpperY: CGFloat
        if self.point1.x < self.point2.x {
            selfLowerX = self.point1.x
            selfUpperX = self.point2.x
        } else {
            selfLowerX = self.point2.x
            selfUpperX = self.point1.x
        }
        if self.point1.y < self.point2.y {
            selfLowerY = self.point1.y
            selfUpperY = self.point2.y
        } else {
            selfLowerY = self.point2.y
            selfUpperY = self.point1.y
        }
        
        var vectorLowerX: CGFloat
        var vectorUpperX: CGFloat
        var vectorLowerY: CGFloat
        var vectorUpperY: CGFloat
        if vector.point1.x < vector.point2.x {
            vectorLowerX = vector.point1.x
            vectorUpperX = vector.point2.x
        } else {
            vectorLowerX = vector.point2.x
            vectorUpperX = vector.point1.x
        }
        if vector.point1.y < vector.point2.y {
            vectorLowerY = vector.point1.y
            vectorUpperY = vector.point2.y
        } else {
            vectorLowerY = vector.point2.y
            vectorUpperY = vector.point1.y
        }
        if xIntercept > selfLowerX && xIntercept < selfUpperX && yIntercept > selfLowerY && yIntercept < selfUpperY {
            if xIntercept > vectorLowerX && xIntercept < vectorUpperX && yIntercept > vectorLowerY && yIntercept < vectorUpperY {
                return true
            }
        }
        return false
    }
}
