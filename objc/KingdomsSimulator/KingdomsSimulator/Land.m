//
//  Land.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 02.10.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Land.h"

@implementation Land

@synthesize income;
@synthesize name;
@synthesize quantity;
@synthesize startingPrice;

- (id)init
{
    self = [super init];
    if (self) {
        quantity = 0;
        startingPrice = 0;
    }

    return self;
}

- (id) initWithName:(NSString *)newName andStartingPrice:(double)newStartingPrice andQuantity:(int)newQuantity andIncome:(double)newIncome {
    self = [super init];
    if (self) {
        name = newName;
        startingPrice = newStartingPrice;
        quantity = newQuantity;
        startingPrice = newStartingPrice;
    }

    return self;
}

- (void) incQuantity {
    quantity++;
}

- (NSString*) description {
    return [NSString stringWithFormat:@"%-17s  %3i  %9.2f", name, quantity, [self getIncomePerPrice]];
}

- (double) getCurrentPrice {
    double addition = startingPrice * 0.1 * quantity;
    return startingPrice + addition;
}

- (double) getIncomePerPrice {
    return ([self getCurrentPrice] / income);
}

- (double) getTotalIncome {
    return (income * quantity);
}

@end
