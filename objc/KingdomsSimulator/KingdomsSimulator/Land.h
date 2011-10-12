//
//  Land.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    FARM,
    BARRAKS,
    BLACKSMITH,
    CASTLE_KEEP,
    LUMBER_MILL,
    ROYAL_CASTLE,
    ROYAL_TRADE_ROUTE,
    SHIPYARD,
    STONE_QUARRY,
    TAVERN,
    TEMPLE,
    VILLAGE,
    GOLD_MINE
} LandType;

@interface Land : NSObject {
    double startingPrice;
    int quantity;
    double income;
    NSString* name;
}

@property double startingPrice;
@property int quantity;
@property double income;
@property NSString* name;

- (id) initWithName:(NSString *)newName andStartingPrice:(double)newStartingPrice andQuantity:(int)newQuantity andIncome:(double)newIncome;
- (void) incQuantity;
- (double) getCurrentPrice;
- (double) getIncomePerPrice;
- (double) getTotalIncome;
- (NSComparisonResult) compareLandBasedOnIncomePerPrice: (Land*) other;

@end
