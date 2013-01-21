//
//  Track.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 30/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "Track.h"
#import "NSString+IMAddition.h"

@implementation Track

@dynamic album;
@dynamic grupo;
@dynamic idTrack;
@dynamic imageBig;
@dynamic imageThumb;
@dynamic imageProfile;
@dynamic imageMini;
@dynamic imageIphone;
@dynamic titulo;
@dynamic url;
@dynamic token;
@dynamic favorite;
@dynamic playlist;
@dynamic duration;
@synthesize order;

-(void)setDataWithdictionary:(NSDictionary*)data order:(NSInteger)ord
{
    NSString *name= [data objectForKey:@"titulo"];
    
    if ([NSString isEmptyString:name]) {
        name = @"";
    }
    
    [self setTitulo:name];
    [self setAlbum:[data objectForKey:@"album"]];
    [self setGrupo:[data objectForKey:@"grupo"]];
    [self setIdTrack:[data objectForKey:@"idTrack"]];
    [self setImageBig:[data objectForKey:@"imageBig"]];
    [self setImageProfile:[data objectForKey:@"imageProfile"]];
    [self setImageMini:[data objectForKey:@"imageMini"]];
    [self setImageThumb:[data objectForKey:@"imageThumb"]];
    [self setImageIphone:[data objectForKey:@"imageiPhone"]];
    [self setUrl:[data objectForKey:@"url"]];
    [self setToken:[data objectForKey:@"token"]];
    [self setDuration:[data objectForKey:@"duration"]];
    [self setOrder:[NSNumber numberWithInteger:ord]];
    [self setFavorite:[NSNumber numberWithBool:NO]];
}


@end
