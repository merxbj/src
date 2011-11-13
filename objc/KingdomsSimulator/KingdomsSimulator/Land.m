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
        name = [newName retain];

        quantity = newQuantity;
        startingPrice = newStartingPrice;
        income = newIncome;
    }

    return self;
}

- (void) dealloc {
    [name release];
    [super dealloc];
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

- (NSComparisonResult) compareLandBasedOnIncomePerPrice:(Land *)other {
    NSNumber* ipp1 = [NSNumber numberWithDouble:[self getIncomePerPrice]];
    NSNumber* ipp2 = [NSNumber numberWithDouble:[other getIncomePerPrice]];
    return [ipp1 compare:ipp2];
}

- (NSComparisonResult) compareLandBasedOnCurrentPrice:(Land *)other {
    NSNumber* ipp1 = [NSNumber numberWithDouble:[self getCurrentPrice]];
    NSNumber* ipp2 = [NSNumber numberWithDouble:[other getCurrentPrice]];
    return [ipp1 compare:ipp2];
}

- (id) copyWithZone:(NSZone *) zone {
    Land* copy = [[[self class] allocWithZone:zone] init];
    copy.quantity = self.quantity;
    copy.income = self.income;
    copy.startingPrice = self.startingPrice;
    [copy setName:[self.name copy]];
    return copy;
}

- (NSUInteger) hash {
    return [name hash];
}

- (BOOL) isEqual: (id) object {
    if (object == nil) {
        return false;
    }

    Land* other = (Land*) object;
    return ([other.name isEqual:self.name]);
}

@end
