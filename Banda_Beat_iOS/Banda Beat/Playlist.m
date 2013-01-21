//
//  Playlist.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 30/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "Playlist.h"
#import "Track.h"
#import "NSString+IMAddition.h"


@implementation Playlist

@dynamic downloaded;
@dynamic idPlaylist;
@dynamic name;
@dynamic token;
@dynamic songCount;
@dynamic songToken;
@dynamic tracks;
@dynamic dirty;


-(void)setDataWithdictionary:(NSDictionary*)data
{
    NSString *name= [data objectForKey:@"name"];
    
    if ([NSString isEmptyString:name]) {
        name = @"Sin nombre";
    }
    
    [self setName:name];
    [self setIdPlaylist:[data objectForKey:@"idPlaylist"]];
    [self setToken:[data objectForKey:@"token"]];
    [self setSongToken:[data objectForKey:@"songToken"]];
    [self setSongCount:[data objectForKey:@"songCount"]];
    [self setDownloaded:[NSNumber numberWithBool:NO]];
    [self setDirty:[NSNumber numberWithBool:NO]];
}

@end
