//
//  KingdomsSimulator.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LandCollection.h"

typedef enum {
    TARGET_INCOME, TOTAL_TIME, CYCLES_PASSED, FORCED
} EndingConditionType;

@interface KingdomsSimulator : NSObject {
    LandCollection* lands;
    double accountBalance;
    long totalTimeElapsed;
    int tickDuration;
    int targetIncome;
    NSMutableArray* endingConditions;
    int targetCyclesCount;
}

@property int targetIncome;
@property int startingAmount;
@property int tickDuration;
@property int targetCyclesCount;

- (void) createCommonLandList;
- (void) createLandListFromArraySetup: (NSArray*) arraySetup;
- (void) addEndingConditionType: (EndingConditionType) endingConditionType;
- (void) removeEndingConditionType: (EndingConditionType) endingConditionType;
- (void) run;

@end
