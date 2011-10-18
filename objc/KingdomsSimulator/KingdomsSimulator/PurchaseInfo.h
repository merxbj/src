//
//  PurchaseInfo.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Land.h"

@interface PurchaseInfo : NSObject {
    NSMutableDictionary* purchases;
}

- (void) purchaseLand: (Land*) land;
- (void) print;
- (void) clear;

@end

@interface LandPurchaseInfo : NSObject {
    Land* land;
    int quantity;
    double totalPrice;
    double totalIncomeIncrease;
}

- (id) initWithLand: (Land*) land;
- (void) boughtOneMore;

@end
