//
//  LandFactory.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Land.h"

@interface LandFactory : NSObject

- (Land*) newLandFromType: (LandType) type;
- (Land*) newLandFromType: (LandType) type quantity: (int) quantity;

@end
