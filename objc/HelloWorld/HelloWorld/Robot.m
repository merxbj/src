//
//  Robot.m
//  HelloWorld
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Robot.h"

@implementation Robot

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
    return self;
}

@synthesize position;

- (id) initWithPosition:(Vector *)newPosition
{
    self = [self init];
    if (self) {
        self.position = newPosition;
    }
    
    return self;
}

- (void) moveByVector:(Vector *)vector
{
    self.position = [self.position add:vector];
}

- (NSString*) toString
{
    NSString* description = [NSString stringWithFormat:@"Robot at position: %@", [position toString]];
    return description;
}

@end
