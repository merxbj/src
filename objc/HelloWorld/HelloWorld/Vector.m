//
//  Vector.m
//  HelloWorld
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Vector.h"

@implementation Vector

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
    return self;
}

@synthesize x;
@synthesize y;

- (id)initWithX:(int)newX andY:(int)newY
{
    self = [self init];
    if (self)
    {
        self.x = newX;
        self.y = newY;
    }
    
    return self;
}

- (Vector*) negative
{
    Vector* negative = [[Vector alloc] initWithX:(-self.x) andY:(-self.y)];
    return negative;
}

- (Vector*) add:(Vector *)other
{
    Vector* result = [[Vector alloc] initWithX:(self.x + other.x) andY:(self.y + other.y)];
    return result;
}

- (Vector*) substract:(Vector *)other
{
    Vector* result = [self add: [self negative]];
    return result;
}

- (NSString*) toString
{
    return [NSString stringWithFormat:@"x = %d and y = %d", self.x, self.y];
}

@end
