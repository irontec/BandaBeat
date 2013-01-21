//
//  Track.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 30/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "Playlist.h"


@interface Track : NSManagedObject

@property (nonatomic, retain) NSString * album;
@property (nonatomic, retain) NSString * grupo;
@property (nonatomic, retain) NSNumber * idTrack;
@property (nonatomic, retain) NSString * imageMini;
@property (nonatomic, retain) NSString * imageBig;
@property (nonatomic, retain) NSString * imageProfile;
@property (nonatomic, retain) NSString * imageThumb;
@property (nonatomic, retain) NSString * imageIphone;
@property (nonatomic, retain) NSString * titulo;
@property (nonatomic, retain) NSString * url;
@property (nonatomic, retain) NSString * token;
@property (nonatomic, retain) NSString * duration;
@property (nonatomic, retain) NSNumber * favorite;
@property (nonatomic, retain) NSNumber * order;
@property (nonatomic, retain) Playlist *playlist;

-(void)setDataWithdictionary:(NSDictionary*)data order:(NSInteger)order;
@end
