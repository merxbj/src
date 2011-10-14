//
//  LandFactory.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LandFactory.h"

@interface LandFactory()

- (Land*) createLandOfType: (LandType) landType withQuantity: (int) quantity;

@end

@implementation LandFactory

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
    return self;
}

- (Land*) newLandFromType:(LandType)type {
    return [self newLandFromType:type quantity:1];
}

- (Land*) newLandFromType:(LandType)type quantity:(int)quantity {
    return [self createLandOfType:type withQuantity:quantity];
}

- (Land*) createLandOfType:(LandType)landType withQuantity:(int)quantity {
    Land* land;
    
    switch (landType) {
        case FARM:
            land = [[Land alloc] initWithName:@"Farm" andStartingPrice:50 andQuantity:quantity andIncome:1];
            break;
        case LUMBER_MILL:
            land = [[Land alloc] initWithName:@"Lumber Mill" andStartingPrice:250 andQuantity:quantity andIncome:5];
            break;
        case TAVERN:
            land = [[Land alloc] initWithName:@"Tavern" andStartingPrice:650 andQuantity:quantity andIncome:12];
            break;
        case STONE_QUARRY:
            land = [[Land alloc] initWithName:@"Stone Quarry" andStartingPrice:2800 andQuantity:quantity andIncome:50];
            break;
        case BARRAKS:
            land = [[Land alloc] initWithName:@"Barraks" andStartingPrice:9000 andQuantity:quantity andIncome:150];
            break;
        case BLACKSMITH:
            land = [[Land alloc] initWithName:@"Blacksmith" andStartingPrice:14000 andQuantity:quantity andIncome:250];
            break;
        case TEMPLE:
            land = [[Land alloc] initWithName:@"Temple" andStartingPrice:50000 andQuantity:quantity andIncome:800];
            break;
        case VILLAGE:
            land = [[Land alloc] initWithName:@"Village" andStartingPrice:100000 andQuantity:quantity andIncome:1400];
            break;
        case SHIPYARD:
            land = [[Land alloc] initWithName:@"Shipyard" andStartingPrice:180000 andQuantity:quantity andIncome:2200];
            break;
        case CASTLE_KEEP:
            land = [[Land alloc] initWithName:@"Castle Keep" andStartingPrice:320000 andQuantity:quantity andIncome:3200];
            break;
        case ROYAL_CASTLE:
            land = [[Land alloc] initWithName:@"Royal Castle" andStartingPrice:540000 andQuantity:quantity andIncome:4500];
            break;
        case ROYAL_TRADE_ROUTE:
            land = [[Land alloc] initWithName:@"Royal Trade Route" andStartingPrice:1250000 andQuantity:quantity andIncome:6200];
            break;
        case GOLD_MINE:
            land = [[Land alloc] initWithName:@"Gold Mine" andStartingPrice:4000000 andQuantity:quantity andIncome:15000];
            break;
        default:
            land = nil;
    }
    
    return [land autorelease];
}

@end
