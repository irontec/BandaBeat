//
//  NSString+IMAddition.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 30/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "NSString+IMAddition.h"

@implementation NSString (IMAddition)
+ (BOOL)isEmptyString:(NSString *)string;
// Returns YES if the string is nil or equal to @""
{
    // Note that [string length] == 0 can be false when [string isEqualToString:@""] is true, because these are Unicode strings.
    
    if (((NSNull *) string == [NSNull null]) || (string == nil) ) {
        return YES;
    }
    string = [string stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceAndNewlineCharacterSet]];
    
    if ([string isEqualToString:@""]) {
        return YES;
    }
    
    return NO;
}
@end
