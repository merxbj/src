//
//  ConfigFile.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "ConfigFile.h"

@interface ConfigFile ()
    - (BOOL) parseConfigWithXmlParser: (NSXMLParser*) parser;
@end

@implementation ConfigFile

@synthesize landCounts;
@synthesize targetIncome;
@synthesize startingAmount;

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
    return self;
}

- (id) initWithConfigFilePath:(NSString *)path {
    self = [self init];
    if (self) {
        NSInputStream* configXmlInputStream = [[NSInputStream alloc] initWithFileAtPath:path];
        if (configXmlInputStream) {
            NSXMLParser* configXmlParser = [[NSXMLParser alloc] initWithStream:configXmlInputStream];
            if (configXmlParser) {
                [configXmlParser setDelegate:self];
                if ([configXmlParser parse]) {
                    return self;
                }
                [configXmlParser release];
            }

            [configXmlInputStream close];
            [configXmlInputStream release];
        }
    }
    return nil;
}

- (BOOL) parseConfigWithXmlParser:(NSXMLParser *)parser {
    [parser 
}

@end
