//
//  CommandLine.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CommandLine : NSObject
{
    int cyclesCount;
    NSString* configPath;
}

@property(readonly) int cyclesCount;
@property(readonly) NSString* configPath;

- (id) initWithArgc: (int) argc andArgv: (const char*[]) argv;

@end
