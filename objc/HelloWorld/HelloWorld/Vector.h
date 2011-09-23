//
//  Vector.h
//  HelloWorld
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Vector : NSObject
{
    int x;
    int y;
}

@property int x;
@property int y;

- (id) initWithX: (int) newX andY: (int) newY;
- (Vector*) add: (Vector*) other;
- (Vector*) substract: (Vector*) other;
- (Vector*) negative;
- (NSString*) toString;

@end
