//
//  ConfigFile.h
//  KingdomsSimulator
//
//  Created by Merxbauer Jaroslav on 24.09.11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ConfigFile : NSObject <NSXMLParserDelegate> {
    NSDictionary* landCounts;
    NSNumber* targetIncome;
    NSNumber* startingAmount;
}

@property(readonly) NSDictionary* landCounts;
@property(readonly) NSNumber* targetIncome;
@property(readonly) NSNumber* startingAmount;

- (id) initWithConfigFilePath: (NSString*) path;

@end
