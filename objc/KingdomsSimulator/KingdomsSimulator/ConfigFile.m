//
//  ConfigFile.m
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "ConfigFile.h"

@interface ConfigFile () {
    BOOL landCollectionSetupElementOccured;
    BOOL targetIncomeElementOccured;
    BOOL startingAmountElementOccured;
}
- (void) parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict;
- (void) parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;
- (void) parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;
- (void) parseLandCollectionSetupFromString: (NSString *) string;
- (void) parseStartingAmountFromString: (NSString *) string;
- (void) parseTargetIncomeFromString: (NSString *) string;
@end

@implementation ConfigFile

@synthesize landCounts;
@synthesize targetIncome;
@synthesize startingAmount;

- (id) init
{
    self = [super init];
    if (self) {
        landCounts = [[NSMutableArray alloc] init];
        landCollectionSetupElementOccured = FALSE;
        targetIncomeElementOccured = FALSE;
        startingAmountElementOccured = FALSE;
    }
    
    return self;
}

- (void) dealloc {
    [landCounts release];
    [super dealloc];
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

- (void) parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict {
    
    if ([elementName isEqualToString:@"LandCollectionSetUp"]) {
        landCollectionSetupElementOccured = TRUE;
    } else if ([elementName isEqualToString:@"TargetIncome"]) {
        targetIncomeElementOccured = TRUE;
    } else if ([elementName isEqualToString:@"StartingAmount"]) {
        startingAmountElementOccured = TRUE;
    }
    
}

- (void) parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
    if ([elementName isEqualToString:@"LandCollectionSetUp"]) {
        landCollectionSetupElementOccured = FALSE;
    } else if ([elementName isEqualToString:@"TargetIncome"]) {
        targetIncomeElementOccured = FALSE;
    } else if ([elementName isEqualToString:@"StartingAmount"]) {
        startingAmountElementOccured = FALSE;
    }
}

- (void) parser:(NSXMLParser *)parser foundCharacters:(NSString *)string {
    if (landCollectionSetupElementOccured) {
        [self parseLandCollectionSetupFromString:string];
    } else if (targetIncomeElementOccured) {
        [self parseTargetIncomeFromString:string];
    } else if (startingAmountElementOccured) {
        [self parseStartingAmountFromString:string];
    }
}

- (void) parseLandCollectionSetupFromString: (NSString *) string {
    [landCounts removeAllObjects];
    NSArray* components = [string componentsSeparatedByString:@","];
    for (NSString* component in components) {
        [landCounts addObject:[NSNumber numberWithInt:[component intValue]]];
    }
}

- (void) parseStartingAmountFromString: (NSString *) string {
    startingAmount = [string doubleValue];
}

- (void) parseTargetIncomeFromString: (NSString *) string {
    targetIncome = [string doubleValue];
}


@end
