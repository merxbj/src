//
//  ConfigFile.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ConfigFile : NSObject <NSXMLParserDelegate> {
    NSMutableArray* landCounts;
    double targetIncome;
    double startingAmount;
}

@property(readonly) NSArray* landCounts;
@property(readonly) double targetIncome;
@property(readonly) double startingAmount;

- (id) initWithConfigFilePath: (NSString*) path;

@end
