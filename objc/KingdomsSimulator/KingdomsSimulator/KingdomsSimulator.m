//
//  KingdomsSimulator.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "KingdomsSimulator.h"

@interface KingdomsSimulator() {
    
}

- (void) updateAccount;
- (void) printResults;
- (bool) passedEndingCondition;

@end

@implementation KingdomsSimulator

@synthesize startingAmount;
@synthesize targetCyclesCount;
@synthesize targetIncome;
@synthesize tickDuration;

- (id)init
{
    self = [super init];
    if (self) {
        lands = [[LandCollection alloc] init];
        accountBalance = 0;
        endingConditions = [[NSMutableArray alloc] init];
        targetIncome = 0;
        totalTimeElapsed = 0;
        tickDuration = 50;
        targetCyclesCount = INT_MAX;
    }
    
    return self;
}

- (void) dealloc {
    [lands release];
    [endingConditions release];
    [super release];
}

- (void) run {
    printf("Simulation started ...");
    while (![self passedEndingCondition]) {
        [self updateAccount];
    }
    [self printResults];
}

- (void) updateAccount {
    
    // buy new land(s)
    bool buyMore = true;
    
    while (buyMore) {
        buyMore = false;
        
        if ([lands getTotalIncome] > 0) {
            double balance = [lands buyLand:[lands getBestLand] withAvailableBudget:accountBalance];
            if (balance > 0) {
                accountBalance = balance;
                buyMore = true;
            }
        } else {
            Land* land = [lands getEffortableLandWithBudget:accountBalance];
            if (land != nil) {
                accountBalance = [lands buyLand:land withAvailableBudget:accountBalance];
            } else {
                // ouch! we don't have enough money to buy a first land
                [endingConditions addObject:[NSNumber numberWithInt:FORCED]];
            }
        }
    }
}

- (void) printResults {
    printf("Simulation ended ...");
    printf("Total time elapsed: %ld days %ld hours %ld minutes", totalTimeElapsed / 1440, (totalTimeElapsed % 1440) / 60, (totalTimeElapsed % 1440) % 60);
}

- (bool) passedEndingCondition {
    bool passedEndingCondition = false;
    
    for (NSNumber* num in endingConditions) {
        EndingConditionType type = (EndingConditionType) [num intValue];
        switch (type) {
            case TARGET_INCOME:
                passedEndingCondition = targetIncome <= lands.getTotalIncome;
                break;
            case CYCLES_PASSED:
                passedEndingCondition = (totalTimeElapsed / tickDuration) == targetCyclesCount;
                break;
            case FORCED:
            default:
                passedEndingCondition = true;
                break;
        }
    }
    
    return passedEndingCondition;
}

- (void) createCommonLandList {
    [lands createCommonLandCollection];
}

- (void) createLandListFromArraySetup: (NSArray*) arraySetup {
    [lands createLandCollectionFromArraySetup:arraySetup];
}

- (void) addEndingConditionType: (EndingConditionType) endingConditionType {
    [endingConditions addObject:[NSNumber numberWithInt:endingConditionType]];
}

- (void) removeEndingConditionType: (EndingConditionType) endingConditionType {
    [endingConditions removeObject:[NSNumber numberWithInt:endingConditionType]];
}


@end
