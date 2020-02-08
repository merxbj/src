//
//  CommandLine.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "CommandLine.h"

@implementation CommandLine

- (id)init {
    self = [super init];
    if (self) {
        cyclesCount = INT_MAX;
        configPath = [[NSString alloc] initWithString:@"GASConfig.xml"];
    }
    return self;
}

- (void) dealloc {
    [configPath release];
    [super dealloc];
}

- (id) initWithArgc:(int)argc andArgv:(const char *[])argv {
    self = [self init];
    if (self) {
        for (int i = 1; i < argc; i++) {
            NSString* arg = [[NSString alloc] initWithUTF8String:argv[i]];
            if ([arg isEqualToString:@"-config"]) {
                configPath = [[NSString alloc] initWithUTF8String:argv[i + 1]]; 
            }
            else if ([arg isEqualToString:@"-c"]) {
                cyclesCount = [[NSString stringWithUTF8String:argv[i + 1]] intValue];
            }
            [arg release];
        }

    }
    return self;
}

@synthesize cyclesCount;
@synthesize configPath;

@end
