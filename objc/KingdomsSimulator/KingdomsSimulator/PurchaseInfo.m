//
//  PurchaseInfo.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "PurchaseInfo.h"

@implementation PurchaseInfo

- (id)init
{
    self = [super init];
    if (self) {
        purchases = [[NSMutableDictionary alloc] init];
    }
    
    return self;
}

- (void) purchaseLand:(Land *)land {
    if ([purchases doesContain:land]) {
        LandPurchaseInfo* lpi = [purchases objectForKey:land];
        [lpi boughtOneMore];
    } else {
        LandPurchaseInfo* lpi = [[LandPurchaseInfo alloc] initWithLand:land];
        [purchases setObject:lpi forKey:land];
    }
}

- (void) print {
    for (LandPurchaseInfo* lpi in purchases) {
        NSLog(@"%@", [lpi description]);
    }
}

- (void) clear {
    [purchases removeAllObjects];
}

@end

@implementation LandPurchaseInfo

- (id)initWithLand:(Land *)newLand {
    self = [super init];
    if (self) {
        land = newLand;
        quantity = 1;
        totalPrice = [land getCurrentPrice];
        totalIncomeIncrease = [land income];
    }
    return self;
}

- (void) boughtOneMore {
    quantity++;
    totalPrice += [land getCurrentPrice];
    totalIncomeIncrease += [land income];
}

- (NSString*) description {
    return [NSString stringWithFormat:@"Bought %3d %-17s for %9.0f gold! Income incrsd by %5.0f gold!", quantity, [land name], totalPrice, totalIncomeIncrease];
}

@end
