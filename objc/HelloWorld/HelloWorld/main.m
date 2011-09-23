//
//  main.m
//  HelloWorld
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Robot.h"
#import "Vector.h"

int main (int argc, const char * argv[])
{

    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];

    Robot* robot = [[Robot alloc] initWithPosition: [[Vector alloc] initWithX:0 andY:0]];
    
    while (robot.position.x < 10 && robot.position.y < 10)
    {
        [robot moveByVector:[[Vector alloc] initWithX:1 andY:1]];
        NSLog(@"%@", [robot toString]);
    }

    [pool drain];
    return 0;
}

