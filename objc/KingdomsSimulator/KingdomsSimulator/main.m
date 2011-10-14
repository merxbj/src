//
//  main.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CommandLine.h"
#import "ConfigFile.h"
#import "KingdomsSimulator.h"

int main (int argc, const char * argv[])
{

    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];

    CommandLine* cl = [[CommandLine alloc] initWithArgc:argc andArgv:argv];
    ConfigFile* cf = [[ConfigFile alloc] initWithConfigFilePath:cl.configPath];
    KingdomsSimulator* ks = [[KingdomsSimulator alloc] init];
    
    [ks createLandListFromArraySetup:cf.landCounts];
    [ks setTargetIncome:cf.targetIncome];
    [ks addEndingConditionType:TARGET_INCOME];
    [ks setStartingAmount:cf.startingAmount];
    
    if (cl.cyclesCount != NSIntegerMax) {
        [ks addEndingConditionType:CYCLES_PASSED];
        [ks setTargetCyclesCount:cl.cyclesCount];
    }
    
    [ks run];

    [pool drain];
    return 0;
}

