//
//  main.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CommandLine.h"

int main (int argc, const char * argv[])
{

    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
    
    CommandLine cl = [[[CommandLine alloc] initWithArgc:argc andArgv:argv]];
    

    [pool drain];
    return 0;
}

