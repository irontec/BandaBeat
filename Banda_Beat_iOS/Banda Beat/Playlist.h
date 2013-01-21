//
//  Playlist.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 30/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Track;

@interface Playlist : NSManagedObject

@property (nonatomic, retain) NSNumber * downloaded;
@property (nonatomic, retain) NSNumber * idPlaylist;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * token;
@property (nonatomic, retain) NSNumber * songCount;
@property (nonatomic, retain) NSString * songToken;
@property (nonatomic, retain) NSSet *tracks;
//Si el songToken a cambiado se pone la playlist como dirty para indicar que hay que actualizar las canciones
@property (nonatomic, retain) NSNumber * dirty;

-(void)setDataWithdictionary:(NSDictionary*)data;
@end

@interface Playlist (CoreDataGeneratedAccessors)

- (void)addTracksObject:(Track *)value;
- (void)removeTracksObject:(Track *)value;
- (void)addTracks:(NSSet *)values;
- (void)removeTracks:(NSSet *)values;

@end
