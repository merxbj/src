//
//  Robot.h
//  HelloWorld
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Vector.h"

@interface Robot : NSObject
{
    Vector* position;
}

@property Vector* position;

- (id) initWithPosition:(Vector*) newPosition;
- (void) moveByVector:(Vector*) vector;
- (NSString*) toString;

@end
