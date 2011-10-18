//
//  LandCollection.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LandFactory.h"
#import "PurchaseInfo.h"
#import "Land.h"

@interface LandCollection : NSObject {
    NSMutableArray* lands;
    LandFactory* factory;
    PurchaseInfo* receipt;
}

- (void) createCommonLandCollection;
- (void) createLandCollectionFromArraySetup: (NSArray*) arraySetup;
- (void) printLandList;
- (Land*) getBestLand;
- (double) buyLand: (Land*) land withAvailableBudget: (double) budget;
- (double) getTotalIncome;
- (Land*) getEffortableLandWithBudget: (double) budget;
- (void) printRecentlyBought;

@end
