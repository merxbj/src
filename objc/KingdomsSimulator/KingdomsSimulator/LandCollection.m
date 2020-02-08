//
//  LandCollection.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LandCollection.h"

@implementation LandCollection

- (id)init
{
    self = [super init];
    if (self) {
        factory = [[LandFactory alloc] init];
        lands = [[NSMutableArray alloc] init];
        receipt = [[PurchaseInfo alloc] init];
    }
    
    return self;
}

- (void) dealloc {
    [factory release];
    [lands release];
    [receipt release];
}

- (void) createCommonLandCollection {
    [lands addObject:[factory newLandFromType:FARM]];
    [lands addObject:[factory newLandFromType:BARRAKS]];
    [lands addObject:[factory newLandFromType:BLACKSMITH]];
    [lands addObject:[factory newLandFromType:CASTLE_KEEP]];
    [lands addObject:[factory newLandFromType:LUMBER_MILL]];
    [lands addObject:[factory newLandFromType:ROYAL_CASTLE]];
    [lands addObject:[factory newLandFromType:ROYAL_TRADE_ROUTE]];
    [lands addObject:[factory newLandFromType:SHIPYARD]];
    [lands addObject:[factory newLandFromType:STONE_QUARRY]];
    [lands addObject:[factory newLandFromType:TAVERN]];
    [lands addObject:[factory newLandFromType:TEMPLE]];
    [lands addObject:[factory newLandFromType:VILLAGE]];
    [lands addObject:[factory newLandFromType:GOLD_MINE]];
}

- (void) createLandCollectionFromArraySetup:(NSArray *)arraySetup {
    for (int i = 0; i < [arraySetup count]; i++) {
        NSNumber* count = (NSNumber*) [arraySetup objectAtIndex:i];
        [lands addObject:[factory newLandFromType:i quantity:[count intValue]]];
    }
}

- (void) printLandList {
    for (Land* land in lands) {
        printf("%s", [[land description] UTF8String]);
    }
}

- (Land*) getBestLand {
    [lands sortUsingSelector:@selector(compareLandBasedOnIncomePerPrice:)];
    return [lands objectAtIndex:0];
}

- (double) buyLand: (Land*) land withAvailableBudget: (double) budget {
    double balance = budget - [land getCurrentPrice];
    if (balance > 0.0) {
        [receipt purchaseLand: land];
        [land incQuantity];
    }
    return balance;
}

- (double) getTotalIncome {
    double income = 0.0;
    for (Land* land in lands) {
        income += [land getTotalIncome];
    }
    return income;
}

- (Land*) getEffortableLandWithBudget: (double) budget {
    Land* effortableLand = nil;
    [lands sortUsingSelector:@selector(compareLandBasedOnCurrentPrice:)];
    for (Land* land in lands) {
        if ([land getCurrentPrice] <= budget) {
            effortableLand = land;
            break;
        }
    }
    return effortableLand;
}

- (void) printRecentlyBought {
    [receipt print];
    [receipt clear];
    printf("--------------------------------------------------------------------------------\n");
}

@end
